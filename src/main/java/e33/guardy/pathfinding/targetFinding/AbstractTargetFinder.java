package e33.guardy.pathfinding.targetFinding;

import e33.guardy.pathfinding.StepHistoryKeeper;
import net.minecraft.util.math.BlockPos;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

abstract public class AbstractTargetFinder implements ITargetFinder {
    final static Logger LOGGER = LogManager.getLogger();

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
