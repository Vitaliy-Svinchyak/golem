package com.e33.event;

import net.minecraft.entity.LivingEntity;
import net.minecraftforge.eventbus.api.Event;

public class MoveEvent extends Event {
    private final LivingEntity creature;

    public MoveEvent(LivingEntity creature) {
        super();

        this.creature = creature;
    }

    public LivingEntity getCreature() {
        return creature;
    }
}
