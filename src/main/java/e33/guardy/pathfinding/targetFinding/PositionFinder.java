package e33.guardy.pathfinding.targetFinding;

import com.google.common.collect.Lists;
import net.minecraft.util.math.BlockPos;

import java.util.List;

public class PositionFinder extends AbstractTargetFinder {

    private BlockPos searchedPosition;
    private boolean positionFound = false;

    public PositionFinder(BlockPos startPosition, BlockPos searchedPosition) {
        super(startPosition);

        this.searchedPosition = searchedPosition;
    }

    @Override
    public boolean targetFound() {
        return this.positionFound;
    }

    @Override
    public List<BlockPos> getTargets() {
        if (this.targetFound()) {
            return Lists.newArrayList(this.searchedPosition);
        }

        double nearestDistance = Double.MAX_VALUE;
        BlockPos nearestPosition = null;

        for (int stepNumber : this.getStepHistory().getStepNumbers()) {
            List<BlockPos> stepPositions = this.getStepHistory().getStepPositions(stepNumber);

            for (BlockPos position : stepPositions) {
                if (position.distanceSq(this.searchedPosition) < nearestDistance) {
                    nearestDistance = position.distanceSq(this.searchedPosition);
                    nearestPosition = position;
                }
            }
        }

        this.positionFound = true;
        this.searchedPosition = nearestPosition;
        LOGGER.info(this.searchedPosition);

        return Lists.newArrayList(this.searchedPosition);
    }

    public void nextStep(List<BlockPos> blocksInStep, int stepNumber) {
        super.nextStep(blocksInStep, stepNumber);

        if (this.getStepHistory().getPositionStep(this.searchedPosition) != null) {
            this.positionFound = true;
        }
    }
}
