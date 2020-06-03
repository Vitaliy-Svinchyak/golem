package e33.guardy.debug;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.platform.GlStateManager;
import e33.guardy.entity.ShootyEntity;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.debug.DebugRenderer;
import net.minecraft.util.Direction;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

abstract public class AbstractDebugRenderer implements DebugRenderer.IDebugRenderer {
    final static Logger LOGGER = LogManager.getLogger();
    final static List<ShootyEntity> entities = Lists.newArrayList();
    static List<ShootyEntity> entitiesToAdd = Lists.newArrayList();
    protected final Minecraft minecraft;

    AbstractDebugRenderer(Minecraft minecraft) {
        this.minecraft = minecraft;
    }

    public static void addEntity(ShootyEntity entity) {
        entitiesToAdd.add(entity);
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
            if (!entity.isAlive()) {
                entitiesToRemove.add(entity);
            }
        }
        removeEntities(entitiesToRemove);

        this.renderEntities(entities);

        addNewEntities();

        GlStateManager.enableTexture();
        GlStateManager.disableBlend();
        GlStateManager.popMatrix();
    }

    abstract void renderEntities(List<ShootyEntity> entities);

    static void removeEntities(List<ShootyEntity> entitiesToRemove) {
        entities.removeAll(entitiesToRemove);
    }

    static void addNewEntities() {
        entities.addAll(entitiesToAdd);
        entitiesToAdd = Lists.newArrayList();
    }

    void renderBlockWithColorAndNumber(BlockPos block, Color color, String text, double x, double y, double z) {
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
                color.alpha
        );

        DebugRenderer.func_217732_a(
                "." + text + ".",
                (double) block.getX() + 0.5D,
                (double) topY + 0.35D,
                (double) block.getZ() + 0.5D,
                -1
        );
    }

    void renderBlockWithColor(BlockPos block, Color color, double offsetX, double offsetY, double offsetZ) {
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

    ActiveRenderInfo getActiveRenderInfo() {
        return this.minecraft.gameRenderer.getActiveRenderInfo();
    }
}