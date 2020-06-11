package e33.guardy;

import e33.guardy.client.renderer.entity.BulletRenderer;
import e33.guardy.client.renderer.entity.ShootyRenderer;
import e33.guardy.client.listener.AnimationStateListener;
import e33.guardy.debug.AvoidBulletDebugRenderer;
import e33.guardy.debug.PathFindingDebugRenderer;
import e33.guardy.debug.PatrolRouteDebugRenderer;
import e33.guardy.entity.BulletEntity;
import e33.guardy.entity.ShootyEntity;
import e33.guardy.init.EntityRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.debug.DebugRenderer;

import net.minecraft.entity.merchant.villager.AbstractVillagerEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.chunk.IChunk;
import net.minecraft.world.gen.feature.structure.MineshaftStructure;
import net.minecraft.world.gen.feature.structure.StructureStart;
import net.minecraft.world.gen.feature.structure.VillageStructure;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
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
    public static final boolean DEBUG = true;
    public static final IEventBus internalEventBus = BusBuilder.builder().setTrackPhases(false).build();
    public static final PathFindingDebugRenderer PATH_FINDING_DEBUG_RENDERER = new PathFindingDebugRenderer(Minecraft.getInstance());
    public static final PatrolRouteDebugRenderer PATROL_ROUTE_DEBUG_RENDERER = new PatrolRouteDebugRenderer(Minecraft.getInstance());
    public static final AvoidBulletDebugRenderer AVOID_BULLET_DEBUG_RENDERER = new AvoidBulletDebugRenderer(Minecraft.getInstance());
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
            E33.PATH_FINDING_DEBUG_RENDERER.render(50);
            E33.PATROL_ROUTE_DEBUG_RENDERER.render(50);
            E33.AVOID_BULLET_DEBUG_RENDERER.render(50);
        }
    }

    @SubscribeEvent
    public void spawnShooty(LivingDamageEvent event) {
        BlockPos pos = event.getEntity().getPosition();
        IChunk chunk = event.getEntity().world.getChunk(pos);
        if (event.getEntity() instanceof AbstractVillagerEntity) {
            this.spawnShootyInChunk(chunk, pos);
        }
    }

    public void spawnShootyInChunk(IChunk chunk, BlockPos entityPos) {
        ChunkPos chunkPos = chunk.getPos();

        boolean shootyExists = chunk.getWorldForge().getEntitiesWithinAABB(
                ShootyEntity.class,
                new AxisAlignedBB(chunkPos.getXStart() - 50, 0, chunkPos.getZStart() - 50, chunkPos.getXEnd() + 50, 255, chunkPos.getZEnd() + 50)
        ).size() > 0;

        if (shootyExists) {
            return;
        }

        for (StructureStart str : chunk.getStructureStarts().values()) {
            if (str.getStructure() instanceof VillageStructure || str.getStructure() instanceof MineshaftStructure) {
                ShootyEntity shooty = new ShootyEntity(EntityRegistry.SHOOTY, Minecraft.getInstance().world);
                shooty.setPosition(entityPos.getX(), entityPos.getY(), entityPos.getZ());
                chunk.getWorldForge().addEntity(shooty);
                return;
            }
        }
    }

}
