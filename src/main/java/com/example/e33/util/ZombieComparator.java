package com.example.e33.util;

import net.minecraft.entity.MobEntity;
import net.minecraft.entity.monster.ZombieEntity;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Comparator;

public class ZombieComparator implements Comparator<ZombieEntity> {
    private MobEntity creature;

    public ZombieComparator(MobEntity creature) {
        this.creature = creature;
    }

    public int compare(ZombieEntity mob1, ZombieEntity mob2) {
        if (!mob1.isBurning() && mob2.isBurning()) {
            return 1;
        }
        if (mob1.isBurning() && !mob2.isBurning()) {
            return -1;
        }

        if (mob1.hurtResistantTime > 10.0F && mob2.hurtResistantTime <= 10.0F) {
            return 1;
        }
        if (mob1.hurtResistantTime <= 10.0F && mob2.hurtResistantTime > 10.0F) {
            return -1;
        }

        if (!mob1.isChild() && mob2.isChild()) {
            return 1;
        }
        if (mob1.isChild() && !mob2.isChild()) {
            return -1;
        }

        double s1Distance = this.creature.getDistanceSq(mob1);
        double s2Distance = this.creature.getDistanceSq(mob2);

        return (int) Math.floor(s1Distance - s2Distance);
    }
}