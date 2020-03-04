package com.example.e33.util;

import net.minecraft.entity.MobEntity;
import net.minecraft.entity.monster.SlimeEntity;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Comparator;

public class SlimeComparator implements Comparator<SlimeEntity> {
    private MobEntity creature;

    public SlimeComparator(MobEntity creature) {
        this.creature = creature;
    }

    public int compare(SlimeEntity mob1, SlimeEntity mob2) {
        if (mob1.isSmallSlime() && !mob2.isSmallSlime()) {
            return 1;
        }

        if (mob2.isSmallSlime() && !mob1.isSmallSlime()) {
            return -1;
        }

        double s1Distance = this.creature.getDistanceSq(mob1);
        double s2Distance = this.creature.getDistanceSq(mob2);

        return (int) Math.floor(s1Distance - s2Distance);
    }
}