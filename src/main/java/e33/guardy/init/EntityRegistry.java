package e33.guardy.init;

import e33.guardy.entity.BulletEntity;
import e33.guardy.entity.ShootyEntity;
import e33.guardy.E33;
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
import net.minecraftforge.registries.IForgeRegistry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

@Mod.EventBusSubscriber(modid = E33.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class EntityRegistry {
    private final static Logger LOGGER = LogManager.getLogger();
    public static EntityType<ShootyEntity> SHOOTY = createShooty();
    public static EntityType<BulletEntity> BULLET;

    private static <T extends AnimalEntity> EntityType<ShootyEntity> createShooty() {
        ResourceLocation location = new ResourceLocation(E33.MOD_ID, classToString(ShootyEntity.class));
        EntityType<ShootyEntity> entity = EntityType.Builder
                .create(ShootyEntity::new, EntityClassification.MISC)
                .setTrackingRange(128)
                .build(location.toString());
        entity.setRegistryName(location);

        return entity;
    }

    private static EntityType<BulletEntity> createBulletEntity() {
        ResourceLocation location = new ResourceLocation(E33.MOD_ID, classToString(BulletEntity.class));
        EntityType<BulletEntity> entity = EntityType.Builder
                .create(BulletEntity::build, EntityClassification.MISC)
                .size(1F, 1F)
                .setTrackingRange(128)
                .setUpdateInterval(10)
                .build(location.toString());
        entity.setRegistryName(location);

        return entity;
    }

    private static String classToString(Class<?> entityClass) {
        return CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, entityClass.getSimpleName()).replace("entity_", "");
    }

    @SubscribeEvent
    public static void onRegisterSounds(RegistryEvent.Register<SoundEvent> event) {
        SoundsRegistry.registerSounds(event.getRegistry());
    }

    @SubscribeEvent
    public static void onRegisterEntities(RegistryEvent.Register<EntityType<?>> event) {
        IForgeRegistry registry = event.getRegistry();

        registry.register(SHOOTY);
        EntitySpawnPlacementRegistry.register(SHOOTY, EntitySpawnPlacementRegistry.PlacementType.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, ShootyEntity::func_223316_b);

        BULLET = createBulletEntity();
        registry.register(BULLET);
    }
}
