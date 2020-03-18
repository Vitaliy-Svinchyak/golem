package com.e33;

import com.e33.client.renderer.entity.RendererBullet;
import com.e33.client.renderer.entity.ShootyRenderer;
import com.e33.client.util.AnimationStateListener;
import com.e33.debug.DangerousZoneDebugRenderer;
import com.e33.entity.BulletEntity;
import com.e33.entity.ShootyEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.debug.DebugRenderer;

import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.BusBuilder;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(E33.MOD_ID)
public class E33 {
    public static final String MOD_ID = "e33";
    public static final IEventBus internalEventBus = BusBuilder.builder().setTrackPhases(false).build();
    public static final DangerousZoneDebugRenderer dangerousZoneDebugRenderer = new DangerousZoneDebugRenderer(Minecraft.getInstance());
    private static DebugRenderer renderer = Minecraft.getInstance().debugRenderer;
    private final static Logger LOGGER = LogManager.getLogger();

    public E33() {
        final IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        final IEventBus forgeEventBus = MinecraftForge.EVENT_BUS;
        modEventBus.addListener(this::setup);
        //needed for @ForgeSubscribe: public void renderWorldLastEvent(RenderWorldLastEvent)
        forgeEventBus.register(this);

        AnimationStateListener.setup(E33.internalEventBus);
    }

    private void setup(final FMLCommonSetupEvent event) {
        RenderingRegistry.registerEntityRenderingHandler(ShootyEntity.class, ShootyRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(BulletEntity.class, RendererBullet::new);
    }

    @SubscribeEvent
    public void renderWorldLastEvent(RenderWorldLastEvent event) {
        // TODO prevent HashMap null exception
//        E33.renderer.pathfinding.render(50);
        E33.dangerousZoneDebugRenderer.render(50);
    }
}
