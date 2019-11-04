package com.example.e33.fight.shooting_navigator;

import net.minecraft.entity.MobEntity;
import net.minecraft.entity.monster.ZombieEntity;
import net.minecraft.util.math.Vec3d;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ZombieShootingNavigator extends AbstractShootingNavigator {
    private final static Logger LOGGER = LogManager.getLogger();

    public static Vec3d getShootPoint(MobEntity target, MobEntity creature) {
        target = (ZombieEntity) target;
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
        LOGGER.info(targetMotion);
        LOGGER.info(blocksToGo);
        if (targetMotion.getX() != 0.0D) {
            double xBlocksToGo = blocksToGo * (targetMotion.getX() * 2);
            LOGGER.info("xBlocksToGo " + xBlocksToGo);
            targetX += xBlocksToGo;
        }
        if (targetMotion.getZ() != 0.0D) {
            double zBlocksToGo = blocksToGo * (targetMotion.getZ() * 2);
            LOGGER.info("zBlocksToGo " + zBlocksToGo);
            targetZ += zBlocksToGo;
        }

        double attackAccelX = targetX - creature.posX;
        double attackAccelY = (targetY + (targetHeight / 2)) - (creature.posY + (double) (creature.getHeight() / 2));
        double attackAccelZ = targetZ - creature.posZ;

        return new Vec3d(attackAccelX, attackAccelY, attackAccelZ);
    }
}
