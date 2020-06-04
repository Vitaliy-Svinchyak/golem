package e33.guardy.pathfinding.pathBuilding;

import com.google.common.collect.Lists;
import e33.guardy.debug.TimeMeter;
import e33.guardy.pathfinding.MovementLimitations;
import e33.guardy.pathfinding.leafs.TreeLeaf;
import e33.guardy.util.ToStringHelper;
import net.minecraft.pathfinding.Path;
import net.minecraft.pathfinding.PathPoint;
import net.minecraft.util.math.BlockPos;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Collections;
import java.util.List;
import java.util.Map;

abstract class AbstractPathBuilder implements IPathBuilder {
    final static Logger LOGGER = LogManager.getLogger();

    Path createPathFromMeetingTree(TreeLeaf startLeaf, TreeLeaf endLeaf) {
        List<PathPoint> startPathPoints = this.getPathPositionsFromTree(startLeaf);
        Collections.reverse(startPathPoints);

        List<PathPoint> endPathPoints = this.getPathPositionsFromTree(endLeaf);
        startPathPoints.addAll(endPathPoints);

        PathPoint targetPathPoint = endPathPoints.get(endPathPoints.size() - 1);
        BlockPos target = new BlockPos(targetPathPoint.x, targetPathPoint.y, targetPathPoint.z);

        return new Path(startPathPoints, target, true);
    }

    Path createPathFromTree(TreeLeaf leaf) {
        List<PathPoint> pathPoints = this.getPathPositionsFromTree(leaf);
        Collections.reverse(pathPoints);
        BlockPos target = leaf.getBlockPos();

        return new Path(pathPoints, target, true);
    }

    List<BlockPos> getNextStepsFromCurrentPosition(BlockPos currentPosition, List<BlockPos> nextStepPoints, MovementLimitations limitations) {
        List<BlockPos> nextSteps = Lists.newArrayList();

        for (BlockPos nextPoint : nextStepPoints) {
            int xDiff = Math.abs(nextPoint.getX() - currentPosition.getX());
            int zDiff = Math.abs(nextPoint.getZ() - currentPosition.getZ());
            int yDiff = nextPoint.getY() - currentPosition.getY();

            // Because step is big circle
            if (xDiff <= 1 && zDiff <= 1 && yDiff <= limitations.jumHeight && yDiff >= -limitations.maxFallHeight) {
                nextSteps.add(nextPoint);
            }
        }

        return nextSteps;
    }

    List<BlockPos> filterByVisitedPoints(List<BlockPos> positions, Map<String, Boolean> visitedPoints) {
        List<BlockPos> filtered = Lists.newArrayList();

        for (BlockPos position : positions) {
            if (visitedPoints.get(ToStringHelper.toString(position)) == null) {
                filtered.add(position);
            }
        }

        return filtered;
    }

    private List<PathPoint> getPathPositionsFromTree(TreeLeaf leaf) {
        List<PathPoint> pathPoints = Lists.newArrayList();

        while (leaf != null) {
            BlockPos block = leaf.getBlockPos();
            pathPoints.add(new PathPoint(block.getX(), block.getY(), block.getZ()));
            leaf = leaf.getParent();
        }

        return pathPoints;
    }
}
