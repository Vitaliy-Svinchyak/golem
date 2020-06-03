package e33.guardy.debug;

import com.google.common.collect.Lists;
import e33.guardy.entity.ShootyEntity;
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
            this.renderRoutes(entity.pathCreator.speedTracker, entity.pathCreator.safestPoints, entity.pathCreator.currentPath, entity.getUniqueID());
        }
    }

    private void renderRoutes(Map<BlockPos, Map<UUID, Integer>> routes, List<BlockPos> safePoints, Path path, UUID shooty) {
        List<BlockPos> pathBlocks = PathFindingDebugRenderer.turnToBlocks(path);
        ActiveRenderInfo activeRenderInfo = this.getActiveRenderInfo();
        double x = activeRenderInfo.getProjectedView().x;
        double y = activeRenderInfo.getProjectedView().y;
        double z = activeRenderInfo.getProjectedView().z;

        for (BlockPos point : routes.keySet()) {
            Color color = Color.SHOOTY;
            Map<UUID, Integer> steps = routes.get(point);
            String text = "";
            if (steps.get(shooty) != null) {
                text += steps.get(shooty).toString();
            }

            if (steps.size() > 1 || steps.get(shooty) == null) {
                int fastestEnemy = Integer.MAX_VALUE;
                int totalEnemySpeed = 0;
                for (UUID enemy : steps.keySet()) {
                    if (!enemy.equals(shooty) && steps.get(enemy) < fastestEnemy) {
                        fastestEnemy = steps.get(enemy);
                    }
                    if (!enemy.equals(shooty)) {
                        totalEnemySpeed += steps.get(enemy);
                    }
                }
                if (steps.get(shooty) != null && fastestEnemy > steps.get(shooty)) {
                    color = Color.ROUTE_VIOLET;
                }
                if (fastestEnemy == 0) {
                    color = Color.DANGEROUS_ZONE_RED;
                }
                if (fastestEnemy == 1) {
                    color = Color.DANGEROUS_ZONE_ORANGE;
                }
                if (fastestEnemy == 2) {
                    color = Color.DANGEROUS_ZONE_YELLOW;
                }
                text += " (" + fastestEnemy + "/" + totalEnemySpeed + ")";
            } else {
                color = Color.ROUTE_VIOLET;
            }

            if (safePoints.contains(point)) {
                color = Color.SAFE_GREEN;
            }

            if (pathBlocks.contains(point)) {
                color = Color.PATH_GREEN;
            }

//            if (color != Color.SHOOTY && color != Color.ROUTE_VIOLET) {
            this.renderBlockWithColorAndNumber(point, color, text, x, y, z);
//            }
        }
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
