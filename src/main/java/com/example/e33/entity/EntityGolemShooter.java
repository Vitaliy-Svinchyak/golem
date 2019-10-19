package com.example.e33.entity;

import net.minecraft.entity.AgeableEntity;
import net.minecraft.entity.EntitySize;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.world.World;

import javax.annotation.Nonnull;

public class EntityGolemShooter extends AnimalEntity {

    public EntityGolemShooter(EntityType<? extends EntityGolemShooter> golem, World world) {
        super(golem, world);
        this.stepHeight = 1.0F;
    }

    @Override
    public EntityGolemShooter createChild(@Nonnull AgeableEntity ageable) {
        return null;
    }

    public EntitySize getSize() {
        return new EntitySize(3, 4, true);
    }
}
