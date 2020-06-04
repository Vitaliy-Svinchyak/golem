package e33.guardy.pathfinding.targetFinding;

import com.google.common.collect.Lists;
import net.minecraft.util.math.BlockPos;

import java.util.List;

public class PositionFinder extends AbstractTargetFinder {

    private final BlockPos searchedPosition;
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
        return Lists.newArrayList(this.searchedPosition);
    }

    public void nextStep(List<BlockPos> blocksInStep, int stepNumber) {
        super.nextStep(blocksInStep, stepNumber);

        if (this.getStepHistory().getPositionStep(this.searchedPosition) != null) {
            this.positionFound = true;
        }
    }
}
