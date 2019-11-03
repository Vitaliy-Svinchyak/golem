package com.example.e33.fight.shooting_navigator;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SlabBlock;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.controller.MovementController;
import net.minecraft.entity.monster.SlimeEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.Field;

public class SlimeShootingNavigator {
    private final static Logger LOGGER = LogManager.getLogger();

    public static Vec3d getShootPointForSlime(MobEntity target, MobEntity creature) {
        target = (SlimeEntity) target;
        double targetX = target.posX;
        double targetZ = target.posZ;
        double targetY = SlimeShootingNavigator.getLowestBlockY(target);

        Vec3d targetMotion = SlimeShootingNavigator.getSlimeMotion(target);
        AxisAlignedBB targetBoundingBox = target.getBoundingBox();

        float ticksForBullet = MathHelper.sqrt(creature.getDistanceSq(target)) * 0.9F;
        // How many jumps can he while bullet is in the air
        float jumpNumber = ticksForBullet / 20;
        if (SlimeShootingNavigator.getSlimeJumpDelay(target) <= 2) {
            jumpNumber += 0.3;
        }

        if (jumpNumber > 0) {
            double xJumpLength = targetBoundingBox.getXSize() * jumpNumber;
            double zJumpLength = targetBoundingBox.getZSize() * jumpNumber;
            LOGGER.info("jumpNumber " + jumpNumber);
            LOGGER.info("xJumpLength " + xJumpLength);
            LOGGER.info("zJumpLength " + zJumpLength);
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

    private static double getLowestBlockY(MobEntity target) {
        BlockPos position = target.getPosition();
        World world = target.getEntityWorld();
        while (!world.getBlockState(position).isSolid()) {
            position = position.down();
        }

        BlockState blockState = world.getBlockState(position);
        Block block = blockState.getBlock();
        if (block instanceof SlabBlock && block.func_220074_n(blockState)) {
            return position.up().getY() + 0.5;
        }

        return position.up().getY();
    }

    private static Vec3d getSlimeMotion(MobEntity target) {
        Vec3d motion = target.getMotion();

        if (motion.getX() != 0.0D && motion.getZ() != 0.0D) {
            return motion;
        }

        return SlimeShootingNavigator.getAbsoluteMotion(target.getPositionVec(), target.rotationYaw);
    }

    // Copied from forge source code
    private static Vec3d getAbsoluteMotion(Vec3d relative, float facing) {
        double d0 = relative.lengthSquared();

        Vec3d vec3d = (d0 > 1.0D ? relative.normalize() : relative);
        float f = MathHelper.sin(facing * ((float) Math.PI / 180F));
        float f1 = MathHelper.cos(facing * ((float) Math.PI / 180F));
        return new Vec3d(vec3d.x * (double) f1 - vec3d.z * (double) f, vec3d.y, vec3d.z * (double) f1 + vec3d.x * (double) f);
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
