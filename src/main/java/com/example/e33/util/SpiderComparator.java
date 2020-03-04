package com.example.e33.util;

import net.minecraft.entity.MobEntity;
import net.minecraft.entity.monster.SpiderEntity;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Comparator;

public class SpiderComparator implements Comparator<SpiderEntity> {
    private MobEntity creature;
    private final static Logger LOGGER = LogManager.getLogger();

    public SpiderComparator(MobEntity creature) {
        this.creature = creature;
    }

    public int compare(SpiderEntity mob1, SpiderEntity mob2) {
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