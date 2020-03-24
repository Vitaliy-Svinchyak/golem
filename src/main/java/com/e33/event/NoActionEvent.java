package com.e33.event;

import net.minecraft.entity.LivingEntity;
import net.minecraftforge.eventbus.api.Event;

public class NoActionEvent extends Event {

    private final LivingEntity creature;

    public NoActionEvent(LivingEntity creature) {
        super();

        this.creature = creature;
    }

    public LivingEntity getCreature() {
        return creature;
    }

}
