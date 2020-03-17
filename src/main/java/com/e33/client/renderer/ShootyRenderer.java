package com.e33.client.renderer;

import com.e33.client.model.ShootyModel;
import com.e33.entity.ShootyEntity;
import com.e33.E33;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;

public class ShootyRenderer extends MobRenderer<ShootyEntity, ShootyModel<ShootyEntity>> {

    public ShootyRenderer(EntityRendererManager renderManager) {
        // 3 param - shadow size
        super(renderManager, new ShootyModel<>(), 1F);
//        this.addLayer(new HeadLayer(this));
        this.addLayer(new ShootyHeldItemLayer<>(this));
    }

    @Override
    @Nonnull
    protected ResourceLocation getEntityTexture(@Nonnull ShootyEntity shooty) {
        return new ResourceLocation(E33.MOD_ID, "textures/entity/shooty.png");
    }
}
