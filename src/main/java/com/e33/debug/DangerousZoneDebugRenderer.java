package com.e33.debug;

import com.e33.goal.move.DangerousZone;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.debug.DebugRenderer;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;

import java.util.List;
import java.util.Map;

public class DangerousZoneDebugRenderer implements DebugRenderer.IDebugRenderer {
    private final Minecraft minecraft;
    private final Map<String, DangerousZone> zoneMap = Maps.newHashMap();

    public DangerousZoneDebugRenderer(Minecraft minecraft) {
        this.minecraft = minecraft;
    }

    public void addZone(DangerousZone zone) {
        this.zoneMap.put(zone.getEntityUniqueId().toString(), zone);
    }

    @Override
    public void render(long l) {
        if (this.zoneMap.isEmpty()) {
            return;
        }

        List<String> zonesToRemove = Lists.newArrayList();
        for (String zoneKey : this.zoneMap.keySet()) {
            DangerousZone zone = this.zoneMap.get(zoneKey);
            if (!zone.entityIsAlive()) {
                zonesToRemove.add(zoneKey);
                continue;
            }
            zone.clearCache();
            this.startRendering(this.getActiveRenderInfo(), zone);
        }

        for (String zoneKey : zonesToRemove) {
            this.zoneMap.remove(zoneKey);
        }
    }

    private void startRendering(ActiveRenderInfo activeRenderInfo, DangerousZone zone) {
        GlStateManager.pushMatrix();
        GlStateManager.enableBlend();
        GlStateManager.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        GlStateManager.color4f(0.0F, 1.0F, 0.0F, 0.75F);
        GlStateManager.disableTexture();
        GlStateManager.lineWidth(6.0F);

        this.renderZone(activeRenderInfo, zone);

        GlStateManager.enableTexture();
        GlStateManager.disableBlend();
        GlStateManager.popMatrix();
    }

    private void renderZone(ActiveRenderInfo activeRenderInfo, DangerousZone zone) {
        double x = activeRenderInfo.getProjectedView().x;
        double y = activeRenderInfo.getProjectedView().y;
        double z = activeRenderInfo.getProjectedView().z;

        List<BlockPos> centerBlocks = zone.getCenterBlocksPos();
        this.renderBlocksWithColor(centerBlocks, Color.DANGEROUS_ZONE_RED, x, y, z);

        List<BlockPos> violetBlocksPos = zone.getRedBlocksPos();
        this.renderBlocksWithColor(violetBlocksPos, Color.DANGEROUS_ZONE_RED, x, y, z);

        List<BlockPos> redBlocksPos = zone.getOrangeBlocksPos();
        this.renderBlocksWithColor(redBlocksPos, Color.DANGEROUS_ZONE_ORANGE, x, y, z);

        List<BlockPos> yellowBlocksPos = zone.getYellowBlocksPos();
        this.renderBlocksWithColor(yellowBlocksPos, Color.DANGEROUS_ZONE_YELLOW, x, y, z);
    }

    private void renderBlocksWithColor(List<BlockPos> blocks, Color color, double x, double y, double z) {
        for (BlockPos block : blocks) {
            //            if (this.getDiffInCoordinates(activeRenderInfo, block) <= 64.0F) {
            DebugRenderer.func_217730_a(
                    (
                            new AxisAlignedBB(
                                    block.getX(),
                                    block.getY() - 1,
                                    block.getZ(),
                                    block.getX() + 1,
                                    block.getY() + 0.1,
                                    block.getZ() + 1
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

    private float getDiffInCoordinates(ActiveRenderInfo activeRenderInfo, BlockPos blockPos) {
        return (float) (Math.abs((double) blockPos.getX() - activeRenderInfo.getProjectedView().x) + Math.abs((double) blockPos.getY() - activeRenderInfo.getProjectedView().y) + Math.abs((double) blockPos.getZ() - activeRenderInfo.getProjectedView().z));
    }
}
