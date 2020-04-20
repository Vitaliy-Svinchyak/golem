package e33.guardy.debug;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.blaze3d.platform.GlStateManager;
import e33.guardy.entity.ShootyEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.debug.DebugRenderer;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public class UnwalkableBlocksDebugRenderer implements DebugRenderer.IDebugRenderer {
    final static Logger LOGGER = LogManager.getLogger();
    private final static List<ShootyEntity> entities = Lists.newArrayList();
    private final static Map<UUID, List<Float>> colors = Maps.newHashMap();
    private final Minecraft minecraft;

    public UnwalkableBlocksDebugRenderer(Minecraft minecraft) {
        this.minecraft = minecraft;
    }

    public static void addEntity(ShootyEntity entity) {
        entities.add(entity);
    }

    public static void removeEntity(ShootyEntity entity) {
        entities.remove(entity);
    }

    @Override
    public void render(long l) {
        if (entities.size() == 0) {
            return;
        }
        GlStateManager.pushMatrix();
        GlStateManager.enableBlend();
        GlStateManager.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        GlStateManager.color4f(0.0F, 1.0F, 0.0F, 0.75F);
        GlStateManager.disableTexture();
        GlStateManager.lineWidth(6.0F);

        for (ShootyEntity entity : entities) {
            if (entity.isAlive()) {
                this.renderBlocks(entity.pathBuilder.unwalkableBlocks);
                this.renderRoutes(entity.pathBuilder.routes, entity.getUniqueID());
            }
        }

        GlStateManager.enableTexture();
        GlStateManager.disableBlend();
        GlStateManager.popMatrix();
    }

    private void renderRoutes(Map<BlockPos, Map<UUID, Integer>> routes, UUID shooty) {
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
            } else {
                LOGGER.info(point);
            }

            if (steps.size() > 1) {
                int fastestEnemy = Integer.MAX_VALUE;
                for (UUID enemy : steps.keySet()) {
                    if (!enemy.equals(shooty) && steps.get(enemy) < fastestEnemy) {
                        fastestEnemy = steps.get(enemy);
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
                text += " (" + fastestEnemy + ")";
            } else {
                color = Color.ROUTE_VIOLET;
            }

            this.renderBlockWithColorAndNumber(point, color, text, 0.75F, x, y, z);
        }
    }

    private void renderPartialBlocksWithColorAndNumber(float offsetX, float offsetZ, float width, float height, BlockPos block, int number, List<Float> color, double x, double y, double z) {
        DebugRenderer.func_217732_a(
                number + ".", // to fix the bug with number 6
                (double) block.getX() + offsetX + width / 2,
                (double) block.getY() + 0.15D,
                (double) block.getZ() + offsetZ + height / 2,
                -1
        );
        DebugRenderer.func_217730_a(
                (
                        new AxisAlignedBB(
                                block.getX() - 0.01 + offsetX,
                                block.getY() - 1.01,
                                block.getZ() - 0.01 + offsetZ,
                                block.getX() + offsetX + width + 0.01,
                                block.getY() + 0.01,
                                block.getZ() + offsetZ + height + 0.01
                        )
                ).offset(-x, -y, -z),
                color.get(0),
                color.get(1),
                color.get(2),
                color.get(3)
        );
    }

    private void renderBlocks(List<BlockPos> blocks) {
        ActiveRenderInfo activeRenderInfo = this.getActiveRenderInfo();
        double x = activeRenderInfo.getProjectedView().x;
        double y = activeRenderInfo.getProjectedView().y;
        double z = activeRenderInfo.getProjectedView().z;

        this.renderBlocksWithColor(blocks, Color.UNWALKABLE_BLACK, x, y, z);
    }

    private void renderBlockWithColorAndNumber(BlockPos block, Color color, String text, float alpha, double x, double y, double z) {
        DebugRenderer.func_217730_a(
                (
                        new AxisAlignedBB(
                                block.getX() - 0.01,
                                block.getY() - 1.01,
                                block.getZ() - 0.01,
                                block.getX() + 1.01,
                                block.getY() + 0.01,
                                block.getZ() + 1.01
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
                (double) block.getY() + 0.35D,
                (double) block.getZ() + 0.5D,
                -1
        );
    }

    private void renderBlocksWithColor(List<BlockPos> blocks, Color color, double x, double y, double z) {
        for (BlockPos block : blocks) {
            //            if (this.getDiffInCoordinates(activeRenderInfo, block) <= 64.0F) {
            DebugRenderer.func_217730_a(
                    (
                            new AxisAlignedBB(
                                    block.getX() - 0.01,
                                    block.getY() - 1.01,
                                    block.getZ() - 0.01,
                                    block.getX() + 1.01,
                                    block.getY() + 0.01,
                                    block.getZ() + 1.01
                            )
                    ).offset(-x, -y, -z),
                    color.red,
                    color.green,
                    color.blue,
                    color.alpha
            );
        }
    }

    private ActiveRenderInfo getActiveRenderInfo() {
        return this.minecraft.gameRenderer.getActiveRenderInfo();
    }

}
