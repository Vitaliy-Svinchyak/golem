package com.e33.event;

import net.minecraft.entity.LivingEntity;
import net.minecraftforge.eventbus.api.Event;

public class NewTargetEvent extends Event {

    private final LivingEntity creature;
    private final LivingEntity target;

    public NewTargetEvent(LivingEntity creature, LivingEntity target) {
        super();

        this.creature = creature;
        this.target = target;
    }

    public LivingEntity getCreature() {
        return creature;
    }

    public LivingEntity getTarget() {
        return target;
    }
}
