package com.example.e33.fight.shooting_navigator;

import com.google.common.collect.Lists;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SlabBlock;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.pathfinding.Path;
import net.minecraft.pathfinding.PathPoint;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.Field;
import java.util.List;

abstract class AbstractShootingNavigator {
    final static Logger LOGGER = LogManager.getLogger();

    static Vec3d getTargetMotion(MobEntity target) {
        Vec3d motion = target.getMotion();

        if (motion.getX() != 0.0D && motion.getZ() != 0.0D) {
            return motion;
        }

        return AbstractShootingNavigator.getAbsoluteMotion(target.getPositionVec(), target.rotationYaw);
    }

    // Copied from forge source code
    private static Vec3d getAbsoluteMotion(Vec3d relative, float facing) {
        double d0 = relative.lengthSquared();

        Vec3d vec3d = (d0 > 1.0D ? relative.normalize() : relative);
        float f = MathHelper.sin(facing * ((float) Math.PI / 180F));
        float f1 = MathHelper.cos(facing * ((float) Math.PI / 180F));
        return new Vec3d(vec3d.x * (double) f1 - vec3d.z * (double) f, vec3d.y, vec3d.z * (double) f1 + vec3d.x * (double) f);
    }

    static float getTicksForBullet(MobEntity target, MobEntity creature) {
        float distance = MathHelper.sqrt(creature.getDistanceSq(target));
        return distance;
//        return distance > 12 ? distance : 12.0F;
    }

    static double getLowestBlockY(MobEntity target) {
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

    static Vec3d guessWhereTargetWillBeWhileBulletIsInAir(MobEntity target, MobEntity creature) {
        List<PathPoint> path = AbstractShootingNavigator.getEntityPath(target);
        int currentPathIndex = 0;
        Vec3d currentPosition = target.getPositionVec();
        float ticksForBullet = AbstractShootingNavigator.getTicksForBullet(target, creature);
        float targetBlocksPerTick = (float) target.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).getValue() * 2F;
        if (path.isEmpty()) {
            return currentPosition;
        }
        PathPoint nextPoint;
        while (ticksForBullet > 0) {
            nextPoint = path.get(currentPathIndex);
            float distanceToPoint = MathHelper.sqrt(target.getDistanceSq(new Vec3d(nextPoint.x, nextPoint.y, nextPoint.z)));
            float usedTicks = distanceToPoint / targetBlocksPerTick;
            currentPathIndex++;
            if (currentPathIndex >= path.size()) {
                return new Vec3d(nextPoint.x, nextPoint.y, nextPoint.z);
            }
            ticksForBullet -= usedTicks;

            if (ticksForBullet < 0) {
                return new Vec3d(nextPoint.x, nextPoint.y, nextPoint.z);
            }
        }

        return currentPosition;
    }

    static List<PathPoint> getEntityPath(MobEntity target) {
        Path path = target.getNavigator().getPath();
        if (path == null || path.isFinished()) {
            return Lists.newArrayList();
        }

        int currentPathIndex = path.getCurrentPathIndex();
        int currentPathLength = path.getCurrentPathLength();
        List<PathPoint> remainPath = Lists.newArrayList();
        for (int i = currentPathIndex - 1; i < currentPathLength; i++) {
            remainPath.add(path.getPathPointFromIndex(i));
        }

        return remainPath;

    }
}
