package com.example.e33.util.mobComparator;

import net.minecraft.entity.MobEntity;
import net.minecraft.entity.monster.CreeperEntity;
import net.minecraft.entity.passive.GolemEntity;

import javax.annotation.Nonnull;
import java.util.Comparator;

public class CreeperComparator implements Comparator<CreeperEntity> {
    private MobEntity creature;

    public CreeperComparator(MobEntity creature) {
        this.creature = creature;
    }

    public int compare(@Nonnull CreeperEntity mob1, @Nonnull CreeperEntity mob2) {
        int mob1HazardPoints = this.getHazardPoints(mob1);
        int mob2HazardPoints = this.getHazardPoints(mob2);

        if (mob1HazardPoints > mob2HazardPoints) {
            return -1;
        } else if (mob1HazardPoints < mob2HazardPoints) {
            return 1;
        }

        return 0;
    }

    private int getHazardPoints(@Nonnull CreeperEntity mob) {
        int hazardPoints = 0;

        if (mob.isBurning()) {
            return -100;
        }

        if (mob.getAttackTarget() instanceof GolemEntity) {
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

        if (mob.getPowered()) {
            hazardPoints *= 2;
        }

        return hazardPoints;
    }
}