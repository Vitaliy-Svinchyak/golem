package com.example.e33.fight;

import com.example.e33.fight.shooting_navigator.SkeletonShootingNavigator;
import com.example.e33.fight.shooting_navigator.SlimeShootingNavigator;
import com.example.e33.fight.shooting_navigator.ZombieShootingNavigator;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.monster.SkeletonEntity;
import net.minecraft.entity.monster.SlimeEntity;
import net.minecraft.entity.monster.ZombieEntity;
import net.minecraft.util.math.Vec3d;

public class ShootingNavigator {

    public static Vec3d getShootPoint(MobEntity target, MobEntity creature) {
        if (target instanceof SlimeEntity) {
            return SlimeShootingNavigator.getShootPoint(target, creature);
        }

        if (target instanceof ZombieEntity) {
            return ZombieShootingNavigator.getShootPoint(target, creature);
        }

        if (target instanceof SkeletonEntity) {
            return SkeletonShootingNavigator.getShootPoint(target, creature);
        }

        double attackAccelX = target.posX - creature.posX;
        double attackAccelY = (target.posY + (double) (target.getHeight() / 2)) - (creature.posY + (double) (creature.getHeight() / 2));
        double attackAccelZ = target.posZ - creature.posZ;

        return new Vec3d(attackAccelX, attackAccelY, attackAccelZ);
    }
}
