package com.example.e33.util;

import net.minecraft.entity.MobEntity;
import net.minecraft.entity.monster.SkeletonEntity;
import net.minecraft.inventory.EquipmentSlotType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Comparator;

public class SkeletonComparator implements Comparator<SkeletonEntity> {
    private MobEntity creature;
    private final static Logger LOGGER = LogManager.getLogger();

    public SkeletonComparator(MobEntity creature) {
        this.creature = creature;
    }

    public int compare(SkeletonEntity z1, SkeletonEntity z2) {
        // TODO check armor and bow enchantment
        if (!z2.hasItemInSlot(EquipmentSlotType.MAINHAND)) {
            return -1;
        }

        if (z1.hurtResistantTime > 10.0F && z2.hurtResistantTime <= 10.0F) {
            return 1;
        }
        if (z1.hurtResistantTime <= 10.0F && z2.hurtResistantTime > 10.0F) {
            return -1;
        }
        if (z1.hurtResistantTime > 10.0F && z2.hurtResistantTime > 10.0F) {
            return 0;
        }

        double s1Distance = this.creature.getDistanceSq(z1);
        double s2Distance = this.creature.getDistanceSq(z2);

        return (int) Math.floor(s1Distance - s2Distance);
    }
}