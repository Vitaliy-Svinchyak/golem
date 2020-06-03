package e33.guardy.pathfinding.pathBuilding;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import e33.guardy.pathfinding.MovementLimitations;
import e33.guardy.pathfinding.StepHistoryKeeper;
import e33.guardy.pathfinding.leafs.TreeLeaf;
import e33.guardy.pathfinding.targetFinding.ITargetFinder;
import e33.guardy.util.ToStringHelper;
import net.minecraft.pathfinding.Path;
import net.minecraft.util.math.BlockPos;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class PatrolPathBuilder extends AbstractPathBuilder implements IPathBuilder {

    @Override
    public Path build(MovementLimitations limitations, ITargetFinder finder) {
        StepHistoryKeeper stepHistory = finder.getStepHistory();
        BlockPos target = finder.getTargets().get(0);

        List<TreeLeaf> leafs = stepHistory.getStepPositions(0).stream().map(TreeLeaf::new).collect(Collectors.toList());
        Map<String, Boolean> visitedPoints = Maps.newHashMap();
        Iterator<Integer> stepIterator = stepHistory.getStepNumbers().iterator();
        stepIterator.next(); // skipping 0 step
        double nearestDistance = Double.MAX_VALUE;
        TreeLeaf nearestLeaf = null;

        while (stepIterator.hasNext()) {
            int stepNumber = stepIterator.next();
            List<TreeLeaf> currentLeafs = Lists.newArrayList();

            for (TreeLeaf leaf : leafs) {
                List<BlockPos> newSteps = this.getNextStepsFromCurrentPosition(
                        leaf.getBlockPos(),
                        this.filterByVisitedPoints(stepHistory.getStepPositions(stepNumber), visitedPoints),
                        limitations
                );

                if (newSteps.size() == 0) {
                    leaf.die();
                    visitedPoints.put(ToStringHelper.toString(leaf.getBlockPos()), true);
                    continue;
                }

                if (newSteps.contains(target)) {
                    TreeLeaf targetLeaf = new TreeLeaf(target);
                    leaf.addChild(targetLeaf);

                    return this.createPathFromTree(targetLeaf);
                }

                List<TreeLeaf> newLeafs = newSteps.stream().map(TreeLeaf::new).collect(Collectors.toList());
                if (newLeafs.size() != 0) {
                    for (TreeLeaf child : newLeafs) {
                        leaf.addChild(child);
                        visitedPoints.put(ToStringHelper.toString(child.getBlockPos()), true);

                        currentLeafs.add(child);
                        if (child.getBlockPos().distanceSq(target) < nearestDistance) {
                            nearestDistance = child.getBlockPos().distanceSq(target);
                            nearestLeaf = child;
                        }
                    }
                }
            }

            leafs = currentLeafs;

            if (leafs.size() == 0) {
                break;
            }
        }

        if (!finder.targetFound() && nearestLeaf != null) {
            return this.createPathFromTree(nearestLeaf);
        }

        return null;
    }

}
