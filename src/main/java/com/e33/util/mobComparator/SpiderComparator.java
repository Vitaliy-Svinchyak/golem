package com.e33.util.mobComparator;

import com.e33.entity.EntityGolemShooter;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.merchant.villager.AbstractVillagerEntity;
import net.minecraft.entity.monster.SpiderEntity;
import net.minecraft.entity.passive.IronGolemEntity;

import javax.annotation.Nonnull;
import java.util.Comparator;

public class SpiderComparator implements Comparator<SpiderEntity> {
    private MobEntity creature;
    private Minecraft minecraft;

    public SpiderComparator(MobEntity creature) {
        this.creature = creature;
        this.minecraft = Minecraft.getInstance();
    }

    public int compare(@Nonnull SpiderEntity mob1, @Nonnull SpiderEntity mob2) {
        int mob1HazardPoints = this.getHazardPoints(mob1);
        int mob2HazardPoints = this.getHazardPoints(mob2);

        if (mob1HazardPoints > mob2HazardPoints) {
            return -1;
        } else if (mob1HazardPoints < mob2HazardPoints) {
            return 1;
        }

        double mob1Distance = this.creature.getDistanceSq(mob1);
        double mob2Distance = this.creature.getDistanceSq(mob2);

        return (int) Math.floor(mob1Distance - mob2Distance);
    }

    private int getHazardPoints(@Nonnull SpiderEntity mob) {
        int hazardPoints = 0;

        if (mob.isBurning()) {
            return -100;
        }

        if (this.minecraft.world != null && this.minecraft.world.isDaytime() && mob.getAttackTarget() == null) {
            return -100;
        }

        LivingEntity target = mob.getAttackTarget();
        if (target instanceof EntityGolemShooter || target instanceof IronGolemEntity || target instanceof AbstractVillagerEntity) {
            hazardPoints += 10;
        }

        if (mob.getHealth() < mob.getMaxHealth()) {
            hazardPoints += 1;
        }

        double distanceToMob = this.creature.getDistance(mob);

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

        if (mob.getActivePotionEffects().size() > 0) {
            hazardPoints += 2;
        }

        return hazardPoints;
    }
}