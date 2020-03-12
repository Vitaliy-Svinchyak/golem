package com.example.e33.fight;

import com.example.e33.fight.shootingNavigator.*;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.monster.*;
import net.minecraft.util.math.Vec3d;

public class ShootingNavigator {

    public static Vec3d getShootPoint(MobEntity target, MobEntity creature) {
        // TODO 2 shoot to visible part of mob
        // TODO 2 don't shoot in advance if mob will be in invisible (wall etc)
        if (target instanceof SlimeEntity) {
            return SlimeShootingNavigator.getShootPoint((SlimeEntity) target, creature);
        }

        if (target instanceof ZombieEntity) {
            return ZombieShootingNavigator.getShootPoint(target, creature);
        }

        if (target instanceof SkeletonEntity) {
            return SkeletonShootingNavigator.getShootPoint(target, creature);
        }

        if (target instanceof SpiderEntity) {
            return SpiderShootingNavigator.getShootPoint(target, creature);
        }

        if (target instanceof CreeperEntity) {
            return CreeperShootingNavigator.getShootPoint(target, creature);
        }

        double attackAccelX = target.posX - creature.posX;
        double attackAccelY = (target.posY + (double) (target.getHeight() / 2)) - (creature.posY + (double) (creature.getHeight() / 2));
        double attackAccelZ = target.posZ - creature.posZ;

        return new Vec3d(attackAccelX, attackAccelY, attackAccelZ);
    }
}
