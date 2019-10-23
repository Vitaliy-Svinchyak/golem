package com.example.e33.client.renderer;

import com.example.e33.E33;
import com.example.e33.client.model.SpaceMarineModel2;
import com.example.e33.entity.EntityGolemShooter;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;

public class RendererGolemShooter extends MobRenderer<EntityGolemShooter, SpaceMarineModel2<EntityGolemShooter>> {

    public RendererGolemShooter(EntityRendererManager renderManager) {
        super(renderManager, new SpaceMarineModel2<>(), 0.5F);
    }

    @Override
    @Nonnull
    protected ResourceLocation getEntityTexture(@Nonnull EntityGolemShooter golem) {
        return new ResourceLocation(E33.MOD_ID, "textures/detailed_golem.png");
    }
}
