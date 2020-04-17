package e33.guardy;

import com.google.common.collect.BiMap;
import e33.guardy.client.renderer.entity.BulletRenderer;
import e33.guardy.client.renderer.entity.ShootyRenderer;
import e33.guardy.client.listener.AnimationStateListener;
import e33.guardy.debug.DangerousZoneDebugRenderer;
import e33.guardy.debug.UnwalkableBlocksDebugRenderer;
import e33.guardy.entity.BulletEntity;
import e33.guardy.entity.ShootyEntity;
import e33.guardy.net.BulletSSpawnObjectPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.debug.DebugRenderer;

import net.minecraft.network.IPacket;
import net.minecraft.network.PacketDirection;
import net.minecraft.network.ProtocolType;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.BusBuilder;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

@Mod(E33.MOD_ID)
public class E33 {
    public static final String MOD_ID = "e33";
    public static final boolean DEBUG = true;
    public static final IEventBus internalEventBus = BusBuilder.builder().setTrackPhases(false).build();
    public static final DangerousZoneDebugRenderer dangerousZoneDebugRenderer = new DangerousZoneDebugRenderer(Minecraft.getInstance());
    public static final UnwalkableBlocksDebugRenderer unwalkableBlocksDebugRenderer = new UnwalkableBlocksDebugRenderer(Minecraft.getInstance());
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
        RenderingRegistry.registerEntityRenderingHandler(BulletEntity.class, BulletRenderer::new);
    }

    @SubscribeEvent
    public void renderWorldLastEvent(RenderWorldLastEvent event) {
        // TODO prevent HashMap null exception
        if (DEBUG) {
            //        E33.renderer.pathfinding.render(50);
            E33.dangerousZoneDebugRenderer.render(50);
            E33.unwalkableBlocksDebugRenderer.render(50);
        }
    }
}
