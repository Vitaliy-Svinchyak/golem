package com.example.e33.util;

import net.minecraft.entity.MobEntity;
import net.minecraft.entity.monster.CreeperEntity;

import java.util.Comparator;

public class CreeperComparator implements Comparator<CreeperEntity> {
    private MobEntity creature;

    public CreeperComparator(MobEntity creature) {
        this.creature = creature;
    }

    public int compare(CreeperEntity mob1, CreeperEntity mob2) {
        if (mob1.hurtResistantTime > 10.0F && mob2.hurtResistantTime <= 10.0F) {
            return 1;
        }
        if (mob1.hurtResistantTime <= 10.0F && mob2.hurtResistantTime > 10.0F) {
            return -1;
        }

        if (!mob1.getPowered() && mob2.getPowered()) {
            return 1;
        }
        if (mob1.getPowered() && !mob2.getPowered()) {
            return -1;
        }

        double s1Distance = this.creature.getDistanceSq(mob1);
        double s2Distance = this.creature.getDistanceSq(mob2);

        return (int) Math.floor(s1Distance - s2Distance);
    }
}