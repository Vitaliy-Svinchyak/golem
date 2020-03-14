package com.e33.init;

import com.e33.entity.BulletEntity;
import com.e33.entity.EntityGolemShooter;
import com.e33.E33;
import com.e33.core.ModSounds;
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
public class EntityRegistry {
    private final static Logger LOGGER = LogManager.getLogger();
    private static List<EntityType> entities = Lists.newArrayList();
    public static final EntityType<EntityGolemShooter> GOLEM = createGolem();
    public static final EntityType<BulletEntity> BULLET = createBulletEntity();

    private static <T extends AnimalEntity> EntityType<EntityGolemShooter> createGolem() {
        ResourceLocation location = new ResourceLocation(E33.MOD_ID, classToString(EntityGolemShooter.class));
        EntityType<EntityGolemShooter> entity = EntityType.Builder
                .create(EntityGolemShooter::new, EntityClassification.CREATURE)
                .size(1.4F, 2F)
                .setTrackingRange(128)
                .build(location.toString());
        entity.setRegistryName(location);

        entities.add(entity);

        return entity;
    }

    private static EntityType<BulletEntity> createBulletEntity() {
        ResourceLocation location = new ResourceLocation(E33.MOD_ID, classToString(BulletEntity.class));
        EntityType<BulletEntity> entity = EntityType.Builder
                .create(BulletEntity::build, EntityClassification.MISC)
                .size(1F, 1F)
                .setTrackingRange(128)
                .build(location.toString());
        entity.setRegistryName(location);

        entities.add(entity);

        return entity;
    }

    private static String classToString(Class<?> entityClass) {
        return CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, entityClass.getSimpleName()).replace("entity_", "");
    }

    @SubscribeEvent
    public static void onRegisterSounds(RegistryEvent.Register<SoundEvent> event) {
        ModSounds.registerSounds(event.getRegistry());
    }

    @SubscribeEvent
    public static void onRegisterEntities(RegistryEvent.Register<EntityType<?>> event) {
        for (EntityType entity : entities) {
            Preconditions.checkNotNull(entity.getRegistryName(), "registryName");
            event.getRegistry().register(entity);
            EntitySpawnPlacementRegistry.register(entity, EntitySpawnPlacementRegistry.PlacementType.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, EntityGolemShooter::func_223316_b);
        }
    }
}
