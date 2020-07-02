package e33.guardy.debug;

import com.google.common.collect.Maps;
import e33.guardy.entity.ShootyEntity;
import e33.guardy.pathfinding.PathCreator;
import e33.guardy.pathfinding.StepHistoryKeeper;
import e33.guardy.pathfinding.targetFinding.ITargetFinder;
import e33.guardy.util.ToStringHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.debug.DebugRenderer;
import net.minecraft.pathfinding.Path;
import net.minecraft.util.math.BlockPos;

import java.util.*;

public class PathFindingDebugRenderer extends AbstractDebugRenderer implements DebugRenderer.IDebugRenderer {
    private Map<Integer, Map<BlockPos, PositionStats>> statsCache = Maps.newHashMap();

    public PathFindingDebugRenderer(Minecraft minecraft) {
        super(minecraft);
    }

    @Override
    void renderEntities(List<ShootyEntity> entities) {
        for (ShootyEntity entity : entities) {
            if (entity.pathCreator.currentPath != null) {
                this.renderRoutes(entity.pathCreator);
            }
        }
    }

    private void renderRoutes(PathCreator pathCreator) {
        Path path = pathCreator.currentPath;
        List<BlockPos> safePoints = pathCreator.safestPoints;
        List<BlockPos> pathBlocks = this.turnPathToBlocksList(path);

        Map<BlockPos, PositionStats> positionStats;
        if (statsCache.get(path.hashCode()) != null) {
            positionStats = statsCache.get(path.hashCode());
        } else {
            positionStats = this.createPositionStats(pathCreator, safePoints, pathBlocks);
            statsCache.put(path.hashCode(), positionStats);
        }

        for (BlockPos point : positionStats.keySet()) {
            Color color = Color.SHOOTY;
            PositionStats stats = positionStats.get(point);
            String text = "";
            if (stats.shootySpeed != null) {
                text += stats.shootySpeed + "";
            }

            if (stats.fastestEnemySpeed != null || stats.shootySpeed == null) {
                if (stats.shootySpeed != null && stats.fastestEnemySpeed > stats.shootySpeed) {
                    color = Color.ROUTE_VIOLET;
                }

                if (stats.fastestEnemySpeed != null) {
                    switch (stats.fastestEnemySpeed) {
                        case 0:
                            color = Color.DANGEROUS_ZONE_RED;
                            break;
                        case 1:
                            color = Color.DANGEROUS_ZONE_ORANGE;
                            break;
                        case 2:
                            color = Color.DANGEROUS_ZONE_YELLOW;
                            break;
                        default:
                            break;
                    }
                }
                text += " (" + stats.fastestEnemySpeed + "/" + stats.totalEnemySpeed + ")";
            } else {
                color = Color.ROUTE_VIOLET;
            }

            if (stats.isSafe) {
                color = Color.SAFE_GREEN;
            }

            if (stats.isPath) {
                color = Color.PATH_GREEN;
            }

            if (color != Color.SHOOTY && color != Color.ROUTE_VIOLET) {
                this.renderBlockWithColorAndText(point, color, text);
            }
        }
    }

    private Map<BlockPos, PositionStats> createPositionStats(PathCreator pathCreator, List<BlockPos> safePoints, List<BlockPos> pathPositions) {
        Map<BlockPos, PositionStats> positionStats = Maps.newHashMap();
        StepHistoryKeeper stepHistory = pathCreator.safePlaceFinder.getStepHistory();

        for (int stepNumber : stepHistory.getStepNumbers()) {
            for (BlockPos position : stepHistory.getStepPositions(stepNumber)) {
                boolean isSafe = safePoints.contains(position);
                boolean isPath = pathPositions.contains(position);

                positionStats.put(position, this.createPositionStats(position, pathCreator, stepNumber, isSafe, isPath));
            }
        }

        for (ITargetFinder enemyScout : pathCreator.enemyScouts) {
            StepHistoryKeeper enemyStepHistory = enemyScout.getStepHistory();

            for (int stepNumber : enemyStepHistory.getStepNumbers()) {
                for (BlockPos position : enemyStepHistory.getStepPositions(stepNumber)) {
                    if (positionStats.get(position) == null) {
                        positionStats.put(position, this.createPositionStats(position, pathCreator, null, false, false));
                    }
                }
            }
        }

        return positionStats;
    }

    private PositionStats createPositionStats(BlockPos position, PathCreator pathCreator, Integer shootySpeed, boolean isSafe, boolean isPath) {
        String positionKey = ToStringHelper.toString(position);
        Integer fastestEnemySpeed = null;
        int totalEnemySpeed = 0;

        for (ITargetFinder enemyScout : pathCreator.enemyScouts) {
            Integer enemySpeed = enemyScout.getStepHistory().getPositionStep(positionKey);

            if (enemySpeed != null) {
                if (fastestEnemySpeed == null || enemySpeed < fastestEnemySpeed) {
                    fastestEnemySpeed = enemySpeed;
                }

                totalEnemySpeed += enemySpeed;
            }
        }

        return new PositionStats(shootySpeed, fastestEnemySpeed, totalEnemySpeed, isSafe, isPath);
    }
}

class PositionStats {
    final Integer shootySpeed;
    final Integer fastestEnemySpeed;
    final int totalEnemySpeed;
    final boolean isSafe;
    final boolean isPath;

    PositionStats(Integer shootySpeed, Integer fastestEnemySpeed, int totalEnemySpeed, boolean isSafe, boolean isPath) {
        this.shootySpeed = shootySpeed;
        this.fastestEnemySpeed = fastestEnemySpeed;
        this.totalEnemySpeed = totalEnemySpeed;
        this.isSafe = isSafe;
        this.isPath = isPath;
    }
}
