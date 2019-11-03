package com.example.e33.fight;

import com.example.e33.fight.shooting_navigator.SlimeShootingNavigator;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.monster.SlimeEntity;
import net.minecraft.pathfinding.Path;
import net.minecraft.pathfinding.PathPoint;
import net.minecraft.util.math.Vec3d;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.Field;
import java.util.List;

public class ShootingNavigator {
    private final static Logger LOGGER = LogManager.getLogger();

    public static Vec3d getShootPoint(MobEntity target, MobEntity creature) {
        if (target instanceof SlimeEntity) {
            return SlimeShootingNavigator.getShootPointForSlime(target, creature);
        }

        double attackAccelX = target.posX - creature.posX;
        double attackAccelY = (target.posY + (double) (target.getHeight() / 2)) - (creature.posY + (double) (creature.getHeight()));
        double attackAccelZ = target.posZ - creature.posZ;

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
}
