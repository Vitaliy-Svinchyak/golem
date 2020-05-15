package e33.guardy.debug;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.blaze3d.platform.GlStateManager;
import e33.guardy.entity.ShootyEntity;
import net.minecraft.block.BedBlock;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.debug.DebugRenderer;
import net.minecraft.pathfinding.Path;
import net.minecraft.pathfinding.PathPoint;
import net.minecraft.util.Direction;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;

public class PathFindingDebugRenderer implements DebugRenderer.IDebugRenderer {
    final static Logger LOGGER = LogManager.getLogger();
    private final static List<ShootyEntity> entities = Lists.newArrayList();
    private static List<ShootyEntity> entitiesToAdd = Lists.newArrayList();
    private final static Map<UUID, List<Float>> colors = Maps.newHashMap();
    private final Minecraft minecraft;

    public PathFindingDebugRenderer(Minecraft minecraft) {
        this.minecraft = minecraft;
    }

    public static void addEntity(ShootyEntity entity) {
        entitiesToAdd.add(entity);
    }

    private static void removeEntities(List<ShootyEntity> entitiesToRemove) {
        entities.removeAll(entitiesToRemove);
    }

    private static void addNewEntities() {
        entities.addAll(entitiesToAdd);
        entitiesToAdd = Lists.newArrayList();
    }

    @Override
    public void render(long l) {
        if (entities.size() == 0) {
            addNewEntities();
            return;
        }

        GlStateManager.pushMatrix();
        GlStateManager.enableBlend();
        GlStateManager.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        GlStateManager.color4f(0.0F, 1.0F, 0.0F, 0.75F);
        GlStateManager.disableTexture();
        GlStateManager.lineWidth(6.0F);

        List<ShootyEntity> entitiesToRemove = Lists.newArrayList();
        for (ShootyEntity entity : entities) {
            if (entity.isAlive()) {
                this.renderRoutes(entity.pathBuilder.speedTracker, entity.pathBuilder.safePoints, entity.pathBuilder.currentPath, entity.getUniqueID());
            } else {
                entitiesToRemove.add(entity);
            }
        }

        removeEntities(entitiesToRemove);
        addNewEntities();

        GlStateManager.enableTexture();
        GlStateManager.disableBlend();
        GlStateManager.popMatrix();
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
            this.renderBlockWithColorAndNumber(point, color, text, 1F, x, y, z);
//            }
        }
    }

    private void renderBlockWithColorAndNumber(BlockPos block, Color color, String text, float alpha, double x, double y, double z) {
        BlockState state = this.minecraft.world.getBlockState(block.down());
        double topY = block.getY() - 1 + state.getShape(this.minecraft.world, block).getEnd(Direction.Axis.Y);
        DebugRenderer.func_217730_a(
                (
                        new AxisAlignedBB(
                                block.getX() + 0.01,
                                block.getY() - 1.01,
                                block.getZ() + 0.01,
                                block.getX() + 0.99,
                                topY + 0.01,
                                block.getZ() + 0.99
                        )
                ).offset(-x, -y, -z),
                color.red,
                color.green,
                color.blue,
                alpha
        );

        DebugRenderer.func_217732_a(
                "." + text + ".",
                (double) block.getX() + 0.5D,
                (double) topY + 0.35D,
                (double) block.getZ() + 0.5D,
                -1
        );
    }

    private ActiveRenderInfo getActiveRenderInfo() {
        return this.minecraft.gameRenderer.getActiveRenderInfo();
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
