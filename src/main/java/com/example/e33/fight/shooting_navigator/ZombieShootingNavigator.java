package com.example.e33.fight.shooting_navigator;

import net.minecraft.entity.MobEntity;
import net.minecraft.entity.monster.ZombieEntity;
import net.minecraft.util.math.Vec3d;

public class ZombieShootingNavigator extends AbstractShootingNavigator {

    public static Vec3d getShootPoint(MobEntity target, MobEntity creature) {
        target = (ZombieEntity) target;
        Vec3d targetPosition = ZombieShootingNavigator.guessWhereTargetWillBeWhileBulletIsInAir(target, creature);
        double targetHeight = target.getBoundingBox().maxY - target.getBoundingBox().minY;
        double targetX = target.posX;
        double targetZ = target.posZ;
        double targetY = SlimeShootingNavigator.getLowestBlockY(target);

        if (target.isChild()) {
            targetHeight /= 2;
        }

        Vec3d targetMotion = ZombieShootingNavigator.getTargetMotion(target);
        float ticksForBullet = SlimeShootingNavigator.getTicksForBullet(target, creature);
        float blocksToGo = ticksForBullet * (target.getAIMoveSpeed() * 0.4F);
        if (targetMotion.getX() != 0.0D) {
            double xBlocksToGo = blocksToGo * (targetMotion.getX() * 2);
            targetX += xBlocksToGo;
        }
        if (targetMotion.getZ() != 0.0D) {
            double zBlocksToGo = blocksToGo * (targetMotion.getZ() * 2);
            targetZ += zBlocksToGo;
        }

        double attackAccelX = targetPosition.x - creature.posX;
        double attackAccelY = (targetPosition.y + (targetHeight / 2)) - (creature.posY + (double) (creature.getHeight() / 2));
        double attackAccelZ = targetPosition.z - creature.posZ;


        return new Vec3d(attackAccelX, attackAccelY, attackAccelZ);
    }
}
