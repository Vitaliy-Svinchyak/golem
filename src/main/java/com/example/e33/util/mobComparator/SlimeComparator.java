package com.example.e33.util.mobComparator;

import com.example.e33.entity.EntityGolemShooter;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.monster.SlimeEntity;

import javax.annotation.Nonnull;
import java.util.Comparator;

public class SlimeComparator implements Comparator<SlimeEntity> {
    private MobEntity creature;

    public SlimeComparator(MobEntity creature) {
        this.creature = creature;
    }

    public int compare(@Nonnull SlimeEntity mob1, @Nonnull SlimeEntity mob2) {
        int mob1HazardPoints = this.getHazardPoints(mob1);
        int mob2HazardPoints = this.getHazardPoints(mob2);

        if (mob1HazardPoints > mob2HazardPoints) {
            return -1;
        } else if (mob1HazardPoints < mob2HazardPoints) {
            return 1;
        }

        return 0;
    }

    private int getHazardPoints(@Nonnull SlimeEntity mob) {
        int hazardPoints = 0;

        if (mob.isBurning()) {
            return -100;
        }

        if (mob.isSmallSlime()) {
            return -100;
        }

        if (mob.getAttackTarget() instanceof EntityGolemShooter) {
            hazardPoints += 10;
        }

        double distanceToMob = this.creature.getDistanceSq(mob);

        if (distanceToMob <= 10) {
            hazardPoints += 2;
        }
        if (distanceToMob <= 5) {
            hazardPoints += 4;
        }
        if (distanceToMob > 10) {
            hazardPoints -= 3;
        }

        if (mob.hurtResistantTime <= 10.0F) {
            hazardPoints += 1;
        }
        if (mob.hurtResistantTime > 10.0F) {
            hazardPoints -= 1;
        }

        return hazardPoints;
    }
}