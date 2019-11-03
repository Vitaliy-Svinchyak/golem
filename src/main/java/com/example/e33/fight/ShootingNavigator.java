package com.example.e33.fight;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SlabBlock;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.monster.SlimeEntity;
import net.minecraft.pathfinding.Path;
import net.minecraft.pathfinding.PathPoint;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.Field;
import java.util.List;

public class ShootingNavigator {
    private final static Logger LOGGER = LogManager.getLogger();

    public static Vec3d getShootPoint(MobEntity target, MobEntity creature) {
        if (target instanceof SlimeEntity) {
            return ShootingNavigator.getShootPointForSlime(target, creature);
        }

        double attackAccelX = target.posX - creature.posX;
//        double attackAccelY = target.getBoundingBox().minY + (double) (target.getHeight() / 2.0F) - (creature.posY + (double) (creature.getHeight() / 2.0F));
        double attackAccelY = (target.posY + (double) (target.getHeight() / 2)) - (creature.posY + (double) (creature.getHeight()));
        double attackAccelZ = target.posZ - creature.posZ;
        LOGGER.info(new Vec3d(attackAccelX, attackAccelY, attackAccelZ));
        return new Vec3d(attackAccelX, attackAccelY, attackAccelZ);
    }

    private static Vec3d getShootPointForSlime(MobEntity target, MobEntity creature) {
        target = (SlimeEntity) target;
        // Where he is currently
        double targetX = target.posX;
        double targetZ = target.posZ;
        double targetY = ShootingNavigator.getLowestBlockY(target);

        Vec3d targetMotion = target.getMotion();
        AxisAlignedBB targetBoundingBox = target.getBoundingBox();

        float ticksForBullet = MathHelper.sqrt(creature.getDistanceSq(target)) * 0.9F;
        if (!target.onGround || target.isAirBorne) {
            ticksForBullet -= 7;
        }
        // How many jumps can he while bullet is in the air
        float jumpNumber = ticksForBullet / 15;

        LOGGER.info(jumpNumber);
        if (jumpNumber > 0) {
            double xJumpLength = targetBoundingBox.getXSize() * jumpNumber;
            double zJumpLength = targetBoundingBox.getZSize() * jumpNumber;
            if (targetMotion.getX() != 0.0D) {
                targetX += targetMotion.getX() > 0 ? xJumpLength : -xJumpLength;
            } else {
                LOGGER.info("NO MOTION X");
            }

            if (targetMotion.getZ() != 0.0D) {
                targetZ += targetMotion.getZ() > 0 ? zJumpLength : -zJumpLength;
            } else {
                LOGGER.info("NO MOTION Z");
            }
        }

        double attackAccelX = targetX - creature.posX;
        double attackAccelY = (targetY + (double) (target.getHeight() / 2)) - (creature.posY + (double) (creature.getHeight() / 2));
        double attackAccelZ = targetZ - creature.posZ;

        LOGGER.info(new Vec3d(targetX, targetY, targetZ));
        LOGGER.info(new Vec3d(attackAccelX, attackAccelY, attackAccelZ));

        return new Vec3d(attackAccelX, attackAccelY, attackAccelZ);
    }

    private static PathPoint getEntityNexPathPoint(MobEntity target) {
        Path path = target.getNavigator().getPath();
        try {
            Field currentPathField = path.getClass().getDeclaredField("field_75884_a");
            currentPathField.setAccessible(true);
            List<PathPoint> currentPath = (List<PathPoint>) currentPathField.get(path);

            Field currentPathIndexField = path.getClass().getDeclaredField("currentPathIndex");
            currentPathIndexField.setAccessible(true);
            int currentPathIndex = (int) currentPathIndexField.get(path);

            return currentPath.get(currentPathIndex);
        } catch (ReflectiveOperationException e) {
            LOGGER.error(e.getMessage());
        }

        return null;
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
}
