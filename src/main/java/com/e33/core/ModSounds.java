package com.e33.core;

import com.e33.E33;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.ObjectHolder;

@ObjectHolder(E33.MOD_ID)
public class ModSounds {
    public static SoundEvent ENTITY_GOLEM_HURT = null;

    public static void registerSounds(IForgeRegistry<SoundEvent> registry) {
        ENTITY_GOLEM_HURT = create("entity_golem_hurt");
        registry.register(ENTITY_GOLEM_HURT);
    }

    private static SoundEvent create(String soundNameIn) {
        ResourceLocation resourcelocation = new ResourceLocation(E33.MOD_ID, soundNameIn);
        SoundEvent event = new SoundEvent(resourcelocation);
        event.setRegistryName(E33.MOD_ID, soundNameIn);

        return event;
    }
}