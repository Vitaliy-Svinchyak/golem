package com.e33.client.renderer;

import com.e33.client.model.SpaceMarineModel;
import com.e33.entity.EntityGolemShooter;
import com.e33.E33;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.HeadLayer;
import net.minecraft.client.renderer.entity.layers.HeldItemLayer;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;

public class RendererGolemShooter extends MobRenderer<EntityGolemShooter, SpaceMarineModel<EntityGolemShooter>> {

    public RendererGolemShooter(EntityRendererManager renderManager) {
        // 3 param - shadow size
        super(renderManager, new SpaceMarineModel<>(), 1F);
//        this.addLayer(new HeadLayer(this));
        this.addLayer(new GolemHeldItemLayer<>(this));
    }

    @Override
    @Nonnull
    protected ResourceLocation getEntityTexture(@Nonnull EntityGolemShooter golem) {
        return new ResourceLocation(E33.MOD_ID, "textures/small_golem.png");
    }
}
