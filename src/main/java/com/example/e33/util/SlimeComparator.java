package com.example.e33.util;

import net.minecraft.entity.MobEntity;
import net.minecraft.entity.monster.SlimeEntity;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Comparator;

public class SlimeComparator implements Comparator<SlimeEntity> {
    private MobEntity creature;
    private final static Logger LOGGER = LogManager.getLogger();

    public SlimeComparator(MobEntity creature) {
        this.creature = creature;
    }

    public int compare(SlimeEntity s1, SlimeEntity s2) {
        if (s1.isSmallSlime() && s2.isSmallSlime()) {
            return 0;
        }

        if (s1.isSmallSlime() && !s2.isSmallSlime()) {
            return 1;
        }

        if (s2.isSmallSlime() && !s1.isSmallSlime()) {
            return -1;
        }

        double s1Distance = this.creature.getDistanceSq(s1);
        double s2Distance = this.creature.getDistanceSq(s2);

        return (int) Math.floor(s1Distance - s2Distance);
    }
}