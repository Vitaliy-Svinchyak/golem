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

import java.util.*;
import java.util.stream.Collectors;

public class PatrolPathBuilder extends AbstractPathBuilder implements IPathBuilder {

    @Override
    public Path build(MovementLimitations limitations, ITargetFinder finder) {
        StepHistoryKeeper stepHistory = finder.getStepHistory();
        BlockPos target = finder.getTargets().get(0);

        Map<String, Boolean> startVisitedPoints = Maps.newHashMap();
        Map<String, Boolean> endVisitedPoints = Maps.newHashMap();
        List<Object> stepNumbers = Arrays.asList(stepHistory.getStepNumbers().toArray());
        int startIndex = 1;
        int endIndex = stepHistory.getPositionStep(target);

        List<TreeLeaf> startLeafs = stepHistory.getStepPositions(0).stream().map(TreeLeaf::new).collect(Collectors.toList());
        List<TreeLeaf> endLeafs = Lists.newArrayList(new TreeLeaf(target));

        while (startIndex <= endIndex) {
            int startStepNumber = (int) stepNumbers.get(startIndex);
            int endStepNumber = (int) stepNumbers.get(endIndex);

            startLeafs = this.getNexSteps(startLeafs, limitations, startVisitedPoints, stepHistory.getStepPositions(startStepNumber));
            endLeafs = this.getNexSteps(endLeafs, limitations, endVisitedPoints, stepHistory.getStepPositions(endStepNumber));

            if (startIndex == endIndex) {
                List<TreeLeaf> duplicates = this.getDuplicates(startLeafs, endLeafs);

                if (duplicates != null) {
                    return this.createPathFromMeetingTree(duplicates.get(0).getParent(), duplicates.get(1));
                }
            }

            if (startIndex == endIndex - 1) {
                endLeafs = this.getNexSteps(endLeafs, limitations, endVisitedPoints, stepHistory.getStepPositions(endStepNumber - 1));
                List<TreeLeaf> duplicates = this.getDuplicates(startLeafs, endLeafs);

                if (duplicates != null) {
                    return this.createPathFromMeetingTree(duplicates.get(0), duplicates.get(1));
                }
            }

            startIndex++;
            endIndex--;
        }

        return null;
    }

    private List<TreeLeaf> getDuplicates(List<TreeLeaf> a, List<TreeLeaf> b) {
        for (TreeLeaf aLeaf : a) {
            for (TreeLeaf bLeaf : b) {
                if (aLeaf.equals(bLeaf)) {
                    return Lists.newArrayList(aLeaf, bLeaf);
                }
            }
        }

        return null;
    }

    private List<TreeLeaf> getNexSteps(List<TreeLeaf> startLeafs, MovementLimitations limitations, Map<String, Boolean> visitedPoints, List<BlockPos> stepPositions) {
        List<TreeLeaf> iterationLeafs = Lists.newArrayList();

        for (TreeLeaf leaf : startLeafs) {
            List<BlockPos> newSteps = this.getNextStepsFromCurrentPosition(
                    leaf.getBlockPos(),
                    this.filterByVisitedPoints(stepPositions, visitedPoints),
                    limitations
            );

            if (newSteps.size() == 0) {
                leaf.die();
                visitedPoints.put(ToStringHelper.toString(leaf.getBlockPos()), true);
                continue;
            }

            for (BlockPos child : newSteps) {
                TreeLeaf childLeaf = new TreeLeaf(child);
                leaf.addChild(childLeaf);
                visitedPoints.put(ToStringHelper.toString(child), true);

                iterationLeafs.add(childLeaf);
            }
        }

        return iterationLeafs;
    }

}
