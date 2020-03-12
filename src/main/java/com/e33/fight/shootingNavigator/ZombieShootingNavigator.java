package com.e33.fight.shootingNavigator;

import net.minecraft.entity.MobEntity;
import net.minecraft.util.math.Vec3d;

import javax.annotation.Nonnull;

public class ZombieShootingNavigator extends AbstractShootingNavigator {

    @Nonnull
    public static Vec3d getShootPoint(@Nonnull MobEntity target, @Nonnull MobEntity creature) {
        Vec3d targetPosition = ZombieShootingNavigator.guessWhereTargetWillBeWhileBulletIsInAir(target, creature);
        double targetHeight = target.getBoundingBox().maxY - target.getBoundingBox().minY;

        if (target.isChild()) {
            targetHeight /= 2;
        }

        double attackAccelX = targetPosition.x - creature.posX;
        double attackAccelY = (targetPosition.y + (targetHeight / 2)) - (creature.posY + (creature.getHeight() / 1.5));
        double attackAccelZ = targetPosition.z - creature.posZ;

        AbstractShootingNavigator.addPathToDebug(target);

        return new Vec3d(attackAccelX, attackAccelY, attackAccelZ);
    }
}
