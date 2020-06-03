package e33.guardy.pathfinding.targetFinding;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import e33.guardy.pathfinding.StepHistoryKeeper;
import e33.guardy.util.ToStringHelper;
import net.minecraft.util.math.BlockPos;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class SafePlaceFinder extends AbstractTargetFinder {

    private final Collection<ITargetFinder> enemyScouts;

    public SafePlaceFinder(Collection<ITargetFinder> enemyScouts) {
        super();
        this.enemyScouts = enemyScouts;
    }

    @Override
    public boolean targetFound() {
        return false;
    }

    @Override
    public List<BlockPos> getTargets() {
        Map<Integer, List<BlockPos>> diffInSpeed = Maps.newHashMap();
        StepHistoryKeeper stepHistory = this.getStepHistory();

        for (int stepNumber : stepHistory.getStepNumbers()) {
            List<BlockPos> stepPositions = stepHistory.getStepPositions(stepNumber);

            for (BlockPos position : stepPositions) {
                String positionKey = ToStringHelper.toString(position);
                int fastestEnemySpeed = Integer.MAX_VALUE;

                for (ITargetFinder enemyScout : this.enemyScouts) {
                    Integer enemySpeed = enemyScout.getStepHistory().getPositionStep(positionKey);

                    if (enemySpeed != null && enemySpeed < fastestEnemySpeed) {
                        fastestEnemySpeed = enemySpeed;
                    }
                }

                diffInSpeed.computeIfAbsent(fastestEnemySpeed, k -> Lists.newArrayList());
                diffInSpeed.get(fastestEnemySpeed).add(position);
            }

        }

        List<Integer> sortedDiffs = diffInSpeed.keySet().stream().sorted().collect(Collectors.toList());

        if (sortedDiffs.size() > 0) {
            int maxDiff = sortedDiffs.get(sortedDiffs.size() - 1);

            return diffInSpeed.get(maxDiff);
        }

        return null;
    }
}
