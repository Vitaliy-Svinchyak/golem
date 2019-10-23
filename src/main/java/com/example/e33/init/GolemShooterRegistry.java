package com.example.e33.init;

import com.example.e33.E33;
import com.example.e33.core.ModSounds;
import com.example.e33.entity.EntityGolemShooter;
import com.google.common.base.CaseFormat;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntitySpawnPlacementRegistry;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.gen.Heightmap;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

@Mod.EventBusSubscriber(modid = E33.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class GolemShooterRegistry {
    private final static Logger LOGGER = LogManager.getLogger();
    private static List<EntityType> entities = Lists.newArrayList();
    public static final EntityType<EntityGolemShooter> GOLEM = createEntity(EntityGolemShooter.class, EntityGolemShooter::new);

    private static <T extends AnimalEntity> EntityType<T> createEntity(Class<T> entityClass, EntityType.IFactory<T> factory) {
        ResourceLocation location = new ResourceLocation(E33.MOD_ID, classToString(entityClass));
        EntityType<T> entity = EntityType.Builder
                .create(factory, EntityClassification.CREATURE)
                .size(1.4F, 2F)
                .setTrackingRange(128)
//                .setUpdateInterval(1)
                .build(location.toString());
        entity.setRegistryName(location);
        entities.add(entity);

        return entity;
    }

    private static String classToString(Class<? extends AnimalEntity> entityClass) {
        return CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, entityClass.getSimpleName()).replace("entity_", "");
    }

    @SubscribeEvent
    public static void onRegisterSounds(RegistryEvent.Register<SoundEvent> event) {
        ModSounds.registerSounds(event.getRegistry());
    }

    @SubscribeEvent
    public static void registerGolemShooters(RegistryEvent.Register<EntityType<?>> event) {
        for (EntityType entity : entities) {
            Preconditions.checkNotNull(entity.getRegistryName(), "registryName");
            event.getRegistry().register(entity);
            EntitySpawnPlacementRegistry.register(entity, EntitySpawnPlacementRegistry.PlacementType.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, EntityGolemShooter::func_223316_b);
        }
    }
}
