package e33.guardy.debug;

import com.mojang.blaze3d.platform.GlStateManager;
import e33.guardy.pathfinding.UnwalkableMarker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.debug.DebugRenderer;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

public class UnwalkableBlocksDebugRenderer implements DebugRenderer.IDebugRenderer {
    final static Logger LOGGER = LogManager.getLogger();
    private final Minecraft minecraft;

    public UnwalkableBlocksDebugRenderer(Minecraft minecraft) {
        this.minecraft = minecraft;
    }

    @Override
    public void render(long l) {
        GlStateManager.pushMatrix();
        GlStateManager.enableBlend();
        GlStateManager.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        GlStateManager.color4f(0.0F, 1.0F, 0.0F, 0.75F);
        GlStateManager.disableTexture();
        GlStateManager.lineWidth(6.0F);

        this.renderBlocks();
        this.renderRoutes();

        GlStateManager.enableTexture();
        GlStateManager.disableBlend();
        GlStateManager.popMatrix();
    }

    private void renderRoutes() {
        List<List<BlockPos>> routes = UnwalkableMarker.getCheckingRoutes();
        ActiveRenderInfo activeRenderInfo = this.getActiveRenderInfo();
        double x = activeRenderInfo.getProjectedView().x;
        double y = activeRenderInfo.getProjectedView().y;
        double z = activeRenderInfo.getProjectedView().z;

        int iteration = 0;
        float alphaStep = 0.75F / routes.size();
        for (List<BlockPos> check : routes) {
            this.renderBlocksWithColorAndNumber(check, Color.ROUTE_VIOLET, iteration, 0.25F + alphaStep * iteration, x, y, z);
            iteration++;
        }
    }

    private void renderBlocks() {
        List<BlockPos> blocks = UnwalkableMarker.getUnwalkableBlocks();
        ActiveRenderInfo activeRenderInfo = this.getActiveRenderInfo();
        double x = activeRenderInfo.getProjectedView().x;
        double y = activeRenderInfo.getProjectedView().y;
        double z = activeRenderInfo.getProjectedView().z;

        this.renderBlocksWithColor(blocks, Color.UNWALKABLE_BLACK, x, y, z);
    }

    private void renderBlocksWithColorAndNumber(List<BlockPos> blocks, Color color, int number, float alpha, double x, double y, double z) {
        for (BlockPos block : blocks) {
            DebugRenderer.func_217732_a(
                    Integer.toString(number) + ".", // to fix the bug with number 6
                    (double) block.getX() + 0.5D,
                    (double) block.getY() + 0.25D,
                    (double) block.getZ() + 0.5D,
                    -1
            );
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

        }
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
