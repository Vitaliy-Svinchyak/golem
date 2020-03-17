package com.e33.client.util;

import net.minecraft.client.renderer.entity.model.RendererModel;

public class ModelBoxParameters {

    public final RendererModel model;
    public final int textureOffsetX;
    public final int textureOffsetY;
    public final float posX;
    public final float posY;
    public final float posZ;
    public final int width;
    public final int height;
    public final int depth;
    public final float scaleFactor;
    public final boolean mirrored;

    public ModelBoxParameters(RendererModel model, int textureOffsetX, int textureOffsetY, float posX, float posY, float posZ, int width, int height, int depth, float scaleFactor, boolean mirrored) {
        this.model = model;
        this.textureOffsetX = textureOffsetX;
        this.textureOffsetY = textureOffsetY;
        this.posX = posX;
        this.posY = posY;
        this.posZ = posZ;
        this.width = width;
        this.height = height;
        this.depth = depth;
        this.scaleFactor = scaleFactor;
        this.mirrored = mirrored;
    }
}
