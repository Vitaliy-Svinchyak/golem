package e33.guardy.debug;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import e33.guardy.entity.ShootyEntity;
import e33.guardy.pathfinding.PathCreator;
import e33.guardy.pathfinding.StepHistoryKeeper;
import e33.guardy.pathfinding.targetFinding.ITargetFinder;
import e33.guardy.util.ToStringHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.debug.DebugRenderer;
import net.minecraft.pathfinding.Path;
import net.minecraft.pathfinding.PathPoint;
import net.minecraft.util.math.BlockPos;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;

public class PathFindingDebugRenderer extends AbstractDebugRenderer implements DebugRenderer.IDebugRenderer {

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
        List<BlockPos> pathBlocks = PathFindingDebugRenderer.turnToBlocks(path);
        Map<BlockPos, PositionStats> positionStats = this.createPositionStats(pathCreator, safePoints, pathBlocks);

        ActiveRenderInfo activeRenderInfo = this.getActiveRenderInfo();
        double x = activeRenderInfo.getProjectedView().x;
        double y = activeRenderInfo.getProjectedView().y;
        double z = activeRenderInfo.getProjectedView().z;

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

//            if (color != Color.SHOOTY && color != Color.ROUTE_VIOLET) {
            this.renderBlockWithColorAndNumber(point, color, text, x, y, z);
//            }
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

    @Nonnull
    private static List<BlockPos> turnToBlocks(@Nullable Path path) {
        List<BlockPos> accuratePath = Lists.newArrayList();
        if (path == null) {
            return accuratePath;
        }
        accuratePath.add(path.func_224770_k());

        for (int pathIndex = 0; pathIndex < path.getCurrentPathLength(); ++pathIndex) {
            PathPoint pathPoint = path.getPathPointFromIndex(pathIndex);
            accuratePath.add(new BlockPos(pathPoint.x, pathPoint.y, pathPoint.z));
        }

        return accuratePath;
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
