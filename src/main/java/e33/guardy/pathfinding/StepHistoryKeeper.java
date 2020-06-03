package e33.guardy.pathfinding;

import com.google.common.collect.Maps;
import e33.guardy.util.ToStringHelper;
import net.minecraft.util.math.BlockPos;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class StepHistoryKeeper {
    private Map<String, Integer> positionsToStep;
    private Map<Integer, List<BlockPos>> stepToPositions;

    public StepHistoryKeeper() {
        this.clear();
    }

    public Set<Integer> getStepNumbers() {
        return this.stepToPositions.keySet();
    }

    public List<BlockPos> getStepPositions(int stepNumber) {
        return this.stepToPositions.get(stepNumber);
    }

    public int getPositionStep(BlockPos position) {
        return this.positionsToStep.get(ToStringHelper.toString(position));
    }

    public Integer getPositionStep(String position) {
        return this.positionsToStep.get(position);
    }

    public void saveStep(List<BlockPos> positionsInStep, int stepNumber) {
        this.stepToPositions.put(stepNumber, positionsInStep);

        for (BlockPos blockPos : positionsInStep) {
            this.positionsToStep.put(ToStringHelper.toString(blockPos), stepNumber);
        }
    }

    public void clear() {
        this.positionsToStep = Maps.newHashMap();
        this.stepToPositions = Maps.newHashMap();
    }
}
