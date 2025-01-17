package e33.guardy.pathfinding.targetFinding;

import com.google.common.collect.Lists;
import net.minecraft.util.math.BlockPos;

import java.util.List;

public class PositionFinder extends AbstractTargetFinder {

    private BlockPos originalSearchedPosition;
    private BlockPos searchedPosition;
    private boolean positionFound = false;

    public PositionFinder(BlockPos startPosition, BlockPos searchedPosition) {
        super(startPosition);

        this.searchedPosition = searchedPosition;
        this.originalSearchedPosition = searchedPosition;
    }

    @Override
    public boolean targetFound() {
        return this.positionFound;
    }

    @Override
    public List<BlockPos> getTargets() {
        return Lists.newArrayList(this.searchedPosition);
    }

    @Override
    public void finish() {
        if (this.targetFound()) {
            return;
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
        this.searchedPosition = nearestPosition; // Changing target to nearest point to make possible build next points
    }

    public void nextStep(List<BlockPos> blocksInStep, int stepNumber) {
        super.nextStep(blocksInStep, stepNumber);

        if (this.getStepHistory().getPositionStep(this.searchedPosition) != null) {
            this.positionFound = true;
        }
    }

    public void clear() {
        super.clear();
        this.positionFound = false;
        this.searchedPosition = this.originalSearchedPosition;
    }
}
