package e33.guardy.pathfinding.targetFinding;

import e33.guardy.pathfinding.StepHistoryKeeper;
import net.minecraft.util.math.BlockPos;

import java.util.List;

abstract public class AbstractTargetFinder implements ITargetFinder {

    private final StepHistoryKeeper stepHistoryKeeper;

    public AbstractTargetFinder(BlockPos startPosition) {
        this.stepHistoryKeeper = new StepHistoryKeeper(startPosition);
    }

    public StepHistoryKeeper getStepHistory() {
        return this.stepHistoryKeeper;
    }

    public void nextStep(List<BlockPos> blocksInStep, int stepNumber) {
        this.stepHistoryKeeper.saveStep(blocksInStep, stepNumber);
    }

    public void clear() {
        this.stepHistoryKeeper.clear();
    }
}
