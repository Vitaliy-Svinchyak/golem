package com.example.e33.fight.shooting_navigator;

import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.controller.MovementController;
import net.minecraft.entity.monster.SlimeEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.Vec3d;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.Field;

public class SlimeShootingNavigator extends AbstractShootingNavigator {
    private final static Logger LOGGER = LogManager.getLogger();

    public static Vec3d getShootPoint(MobEntity target, MobEntity creature) {
        target = (SlimeEntity) target;
        double targetX = target.posX;
        double targetZ = target.posZ;
        double targetY = SlimeShootingNavigator.getLowestBlockY(target);

        Vec3d targetMotion = SlimeShootingNavigator.getTargetMotion(target);
        AxisAlignedBB targetBoundingBox = target.getBoundingBox();

        float ticksForBullet = SlimeShootingNavigator.getTicksForBullet(target, creature);
        // How many jumps can he while bullet is in the air
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

    private static int getSlimeJumpDelay(MobEntity target) {
        MovementController moveCtrl = target.getMoveHelper();

        try {
            Field jumpDelayField = moveCtrl.getClass().getDeclaredField("jumpDelay");
            jumpDelayField.setAccessible(true);
            int jumpDelay = (int) jumpDelayField.get(moveCtrl);
            return jumpDelay;
        } catch (ReflectiveOperationException e) {
            LOGGER.error(e.getMessage());
        }

        return 7;
    }
}
