package com.example.e33;

import com.example.e33.client.renderer.RendererBullet;
import com.example.e33.client.renderer.RendererGolemShooter;
import com.example.e33.entity.BulletEntity;
import com.example.e33.entity.EntityGolemShooter;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(E33.MOD_ID)
public class E33 {
    public static final String MOD_ID = "e33";

    public E33() {
        final IEventBus eventBus = FMLJavaModLoadingContext.get().getModEventBus();
        eventBus.addListener(this::setup);
    }

    private void setup(final FMLCommonSetupEvent event) {
        RenderingRegistry.registerEntityRenderingHandler(EntityGolemShooter.class, RendererGolemShooter::new);
        RenderingRegistry.registerEntityRenderingHandler(BulletEntity.class, RendererBullet::new);
    }
}
