package com.example.e33.fight;

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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ShootingNavigator {
    private final static Logger LOGGER = LogManager.getLogger();
    private final static Map<String, Vec3d> lastEntityPositions = new HashMap<String, Vec3d>();

    public static Vec3d getShootPoint(MobEntity target, MobEntity creature) {
        if (target instanceof SlimeEntity) {
            return ShootingNavigator.getShootPointForSlime(target, creature);
        }

        double attackAccelX = target.posX - creature.posX;
        double attackAccelY = target.getBoundingBox().minY + (double) (target.getHeight() / 2.0F) - (creature.posY + (double) (creature.getHeight() / 2.0F));
        double attackAccelZ = target.posZ - creature.posZ;

        return new Vec3d(attackAccelX, attackAccelY, attackAccelZ);
    }

    private static Vec3d getShootPointForSlime(MobEntity target, MobEntity creature) {
        String uuid = target.getUniqueID().toString();
        if (!target.isAirBorne && !lastEntityPositions.containsKey(uuid)) {
            lastEntityPositions.put(uuid, target.getPositionVec());
        }

        double attackAccelX;
        double attackAccelZ;
        int jumpNumber = Math.round(MathHelper.sqrt(creature.getDistanceSq(target)) * 0.9F / 15);
        LOGGER.info("jumps " + jumpNumber);

        if (lastEntityPositions.containsKey(uuid)) {
            Vec3d lastEntityPosition = lastEntityPositions.get(uuid);
            attackAccelX = lastEntityPosition.x - creature.posX;
            attackAccelZ = lastEntityPosition.z - creature.posZ;
        } else {
            attackAccelX = target.posX - creature.posX;
            attackAccelZ = target.posZ - creature.posZ;
        }

        AxisAlignedBB boundingBox = target.getBoundingBox();
        double attackAccelY = boundingBox.minY + (double) (target.getHeight() / 2.0F) - (creature.posY + creature.getHeight() / 2);
        Vec3d motion = target.getMotion();

        if (motion.getX() != 0.0D) {
            attackAccelX -= motion.getX() > 0 ? -boundingBox.getXSize() : boundingBox.getXSize();
        }
        if (motion.getZ() != 0.0D) {
            attackAccelZ -= motion.getZ() > 0 ? -boundingBox.getZSize() : boundingBox.getZSize();
        }

        LOGGER.info(motion);
        if (target.isAirBorne && motion.getY() != 0.0D) {
            attackAccelY += motion.getY() > 0 ? boundingBox.getYSize() / 3 : -boundingBox.getYSize() / 3;
        }

        LOGGER.info(target.isAirBorne);
        if (!target.isAirBorne) {
            lastEntityPositions.put(uuid, target.getPositionVec());
        }

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

    private static BlockPos getLowestBlock(MobEntity target) {
        BlockPos position = target.getPosition();
        World world = target.getEntityWorld();
        while (!world.getBlockState(position).isAir()) {
            position = position.down();
        }

        return position.up();
    }
}
