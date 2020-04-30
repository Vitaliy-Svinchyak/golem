package e33.guardy.init;

import e33.guardy.E33;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.ObjectHolder;

@ObjectHolder(E33.MOD_ID)
public class SoundsRegistry {
    public static SoundEvent SHOOTY_HURT = null;
    public static SoundEvent SHOOTY_HURT_ARROW = null;
    public static SoundEvent SHOOTY_SHOT = null;
    public static SoundEvent SHOOTY_DEATH = null;
    public static SoundEvent SHOOTY_STEP = null;

    static void registerSounds(IForgeRegistry<SoundEvent> registry) {
        SHOOTY_HURT = create("entity_shooty_hurt");
        registry.register(SHOOTY_HURT);

//        SHOOTY_HURT_ARROW = create("entity_shooty_hurt_arrow");
//        registry.register(SHOOTY_HURT_ARROW);

        SHOOTY_SHOT = create("entity_shooty_shot");
        registry.register(SHOOTY_SHOT);

        SHOOTY_DEATH = create("entity_shooty_death");
        registry.register(SHOOTY_DEATH);

        SHOOTY_STEP = create("entity_shooty_step");
        registry.register(SHOOTY_STEP);
    }

    private static SoundEvent create(String soundNameIn) {
        ResourceLocation resourcelocation = new ResourceLocation(E33.MOD_ID, soundNameIn);
        SoundEvent event = new SoundEvent(resourcelocation);
        event.setRegistryName(E33.MOD_ID, soundNameIn);

        return event;
    }
}