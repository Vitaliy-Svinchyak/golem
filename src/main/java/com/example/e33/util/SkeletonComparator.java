package com.example.e33.util;

import net.minecraft.entity.MobEntity;
import net.minecraft.entity.monster.SkeletonEntity;
import net.minecraft.inventory.EquipmentSlotType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Comparator;

public class SkeletonComparator implements Comparator<SkeletonEntity> {
    private MobEntity creature;

    public SkeletonComparator(MobEntity creature) {
        this.creature = creature;
    }

    public int compare(SkeletonEntity mob1, SkeletonEntity mob2) {
        if (!mob1.isBurning() && mob2.isBurning()) {
            return 1;
        }
        if (mob1.isBurning() && !mob2.isBurning()) {
            return -1;
        }

        // TODO check armor and bow enchantment
        if (!mob2.hasItemInSlot(EquipmentSlotType.MAINHAND)) {
            return -1;
        }

        if (mob1.hurtResistantTime > 10.0F && mob2.hurtResistantTime <= 10.0F) {
            return 1;
        }
        if (mob1.hurtResistantTime <= 10.0F && mob2.hurtResistantTime > 10.0F) {
            return -1;
        }

        double s1Distance = this.creature.getDistanceSq(mob1);
        double s2Distance = this.creature.getDistanceSq(mob2);

        return (int) Math.floor(s1Distance - s2Distance);
    }
}