package com.e33.client.model;

import com.e33.client.util.ModelBoxParameters;
import net.minecraft.client.renderer.entity.model.RendererModel;
import net.minecraft.client.renderer.model.ModelBox;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ModelBoxWithParameters extends ModelBox {
    public final ModelBoxParameters parameters;

    public ModelBoxWithParameters(RendererModel model, int textureOffsetX, int textureOffsetY, float posX, float posY, float posZ, int width, int height, int depth, float scaleFactor) {
        super(model, textureOffsetX, textureOffsetY, posX, posY, posZ, width, height, depth, scaleFactor, model.mirror);

        this.parameters = new ModelBoxParameters(model, textureOffsetX, textureOffsetY, posX, posY, posZ, width, height, depth, scaleFactor, model.mirror);
    }

    public ModelBoxWithParameters(RendererModel model, int textureOffsetX, int textureOffsetY, float posX, float posY, float posZ, int width, int height, int depth, float scaleFactor, boolean mirrored) {
        super(model, textureOffsetX, textureOffsetY, posX, posY, posZ, width, height, depth, scaleFactor, mirrored);

        this.parameters = new ModelBoxParameters(model, textureOffsetX, textureOffsetY, posX, posY, posZ, width, height, depth, scaleFactor, mirrored);
    }
}
