package e33.guardy.debug;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.platform.GlStateManager;
import e33.guardy.entity.ShootyEntity;
import net.minecraft.block.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.debug.DebugRenderer;
import net.minecraft.util.Direction;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

public class PatrolRouteDebugRenderer implements DebugRenderer.IDebugRenderer {
    final static Logger LOGGER = LogManager.getLogger();
    private final static List<ShootyEntity> entities = Lists.newArrayList();
    private static List<ShootyEntity> entitiesToAdd = Lists.newArrayList();
    private final Minecraft minecraft;

    public PatrolRouteDebugRenderer(Minecraft minecraft) {
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
                if (entity.patrolVillageGoal != null && entity.patrolVillageGoal.patrolPoints != null) {
                    this.renderRoutes(entity.patrolVillageGoal.patrolPoints, entity.patrolVillageGoal.angularPoints);
                }
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

    private void renderRoutes(List<BlockPos> patrolPoints, List<BlockPos> angularPoints) {
        ActiveRenderInfo activeRenderInfo = this.getActiveRenderInfo();
        double offsetX = activeRenderInfo.getProjectedView().x;
        double offsetY = activeRenderInfo.getProjectedView().y;
        double offsetZ = activeRenderInfo.getProjectedView().z;

        for (BlockPos pos : patrolPoints) {
            this.renderBlockWithColorAndNumber(pos, Color.VILLAGE_BLACK, offsetX, offsetY, offsetZ);
        }

        for (BlockPos pos : angularPoints) {
            this.renderBlockWithColorAndNumber(pos, Color.VILLAGE_RED, offsetX, offsetY, offsetZ);
        }
    }

    private void renderBlockWithColorAndNumber(BlockPos block, Color color, double offsetX, double offsetY, double offsetZ) {
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
                ).offset(-offsetX, -offsetY, -offsetZ),
                color.red,
                color.green,
                color.blue,
                color.alpha
        );
    }

    private ActiveRenderInfo getActiveRenderInfo() {
        return this.minecraft.gameRenderer.getActiveRenderInfo();
    }
}
