package com.e33;

import com.e33.client.renderer.RendererBullet;
import com.e33.client.renderer.RendererGolemShooter;
import com.e33.debug.DangerousZoneDebugRenderer;
import com.e33.entity.BulletEntity;
import com.e33.entity.EntityGolemShooter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.debug.DebugRenderer;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(E33.MOD_ID)
public class E33 {
    public static final String MOD_ID = "e33";
    public static final DangerousZoneDebugRenderer dangerousZoneDebugRenderer = new DangerousZoneDebugRenderer(Minecraft.getInstance());
    private static DebugRenderer renderer = Minecraft.getInstance().debugRenderer;

    public E33() {
        final IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        final IEventBus forgeEventBus = MinecraftForge.EVENT_BUS;
        modEventBus.addListener(this::setup);
        //needed for @ForgeSubscribe: public void renderWorldLastEvent(RenderWorldLastEvent)
        forgeEventBus.register(this);
    }

    private void setup(final FMLCommonSetupEvent event) {
        RenderingRegistry.registerEntityRenderingHandler(EntityGolemShooter.class, RendererGolemShooter::new);
        RenderingRegistry.registerEntityRenderingHandler(BulletEntity.class, RendererBullet::new);
    }

    @SubscribeEvent
    public void renderWorldLastEvent(RenderWorldLastEvent event) {
        // TODO prevent HashMap null exception
        //E33.renderer.pathfinding.render(50);
        E33.dangerousZoneDebugRenderer.render(50);
    }
}
