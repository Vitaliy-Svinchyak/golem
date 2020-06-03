package e33.guardy.pathfinding.targetFinding;

import e33.guardy.pathfinding.StepHistoryKeeper;
import net.minecraft.util.math.BlockPos;

import java.util.List;

public interface ITargetFinder {

    public StepHistoryKeeper getStepHistory();

    public boolean targetFound();

    public void nextStep(List<BlockPos> blocksInStep, int stepNumber);

    public List<BlockPos> getTargets();

    public void clear();
}

// монстры ничего не ищат
// шути ищет безопасные точки (ему надо знать о всех монстрах вокруг и их ходах в конце)
// шути ищет углы деревни