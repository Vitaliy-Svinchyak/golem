package com.example.e33.fight.shootingNavigator;

import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.controller.MovementController;
import net.minecraft.entity.monster.SlimeEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.Vec3d;

import java.lang.reflect.Field;

public class SlimeShootingNavigator extends AbstractShootingNavigator {

    public static Vec3d getShootPoint(SlimeEntity target, MobEntity creature) {
        double targetX = target.posX;
        double targetZ = target.posZ;
        double targetY = SlimeShootingNavigator.getLowestBlockY(target);

        Vec3d targetMotion = SlimeShootingNavigator.getTargetMotion(target);
        AxisAlignedBB targetBoundingBox = target.getBoundingBox();

        float ticksForBullet = SlimeShootingNavigator.getTicksForBullet(target, creature);
        // How many jumps can he make while bullet is in the air
        float jumpNumber = ticksForBullet / 20;
        if (SlimeShootingNavigator.getSlimeJumpDelay(target) <= 2) {
            jumpNumber += 0.3;
        }

        if (jumpNumber > 0) {
            double xJumpLength = targetBoundingBox.getXSize() * jumpNumber;
            double zJumpLength = targetBoundingBox.getZSize() * jumpNumber;
            if (targetMotion.getX() != 0.0D) {
                targetX += targetMotion.getX() > 0 ? xJumpLength : -xJumpLength;
            }

            if (targetMotion.getZ() != 0.0D) {
                targetZ += targetMotion.getZ() > 0 ? zJumpLength : -zJumpLength;
            }
        }

        double attackAccelX = targetX - creature.posX;
        double attackAccelY = (targetY + (target.getEyeHeight() / 1.5)) - (creature.posY + (double) (creature.getHeight() / 2));
        double attackAccelZ = targetZ - creature.posZ;

        return new Vec3d(attackAccelX, attackAccelY, attackAccelZ);
    }

    /**
     * @return ticks to next jump
     */
    private static int getSlimeJumpDelay(MobEntity slime) {
        MovementController moveCtrl = slime.getMoveHelper();

        try {
            Field jumpDelayField = moveCtrl.getClass().getDeclaredField("jumpDelay");
            jumpDelayField.setAccessible(true);
            return (int) jumpDelayField.get(moveCtrl);
        } catch (ReflectiveOperationException e) {
            LOGGER.error(e.getMessage());
        }

        return 7;
    }
}
