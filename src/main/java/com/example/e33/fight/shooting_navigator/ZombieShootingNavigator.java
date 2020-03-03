package com.example.e33.fight.shooting_navigator;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.debug.DebugRenderer;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.monster.ZombieEntity;
import net.minecraft.util.math.Vec3d;

import javax.annotation.Nonnull;
import java.util.HashMap;

public class ZombieShootingNavigator extends AbstractShootingNavigator {
    public static HashMap<Integer, Boolean> showedPaths = new HashMap<Integer, Boolean>();

    @Nonnull
    public static Vec3d getShootPoint(@Nonnull MobEntity target, @Nonnull MobEntity creature) {
        target = (ZombieEntity) target;
        Vec3d targetPosition = ZombieShootingNavigator.guessWhereTargetWillBeWhileBulletIsInAir(target, creature);
        double targetHeight = target.getBoundingBox().maxY - target.getBoundingBox().minY;

        if (target.isChild()) {
            targetHeight /= 2;
        }

        double attackAccelX = targetPosition.x - creature.posX;
        double attackAccelY = (targetPosition.y + (targetHeight / 2)) - (creature.posY + (creature.getHeight() / 1.5));
        double attackAccelZ = targetPosition.z - creature.posZ;

        if (target.getNavigator().getPath() != null && showedPaths.get(target.getNavigator().getPath().hashCode()) == null) {
            DebugRenderer renderer = Minecraft.getInstance().debugRenderer;
            renderer.pathfinding.addPath(target.getEntityId(), target.getNavigator().getPath(), 0);
            // TODO clear cache after enemy die (memory leack)
            showedPaths.put(target.getNavigator().getPath().hashCode(), true);
        }

        return new Vec3d(attackAccelX, attackAccelY, attackAccelZ);
    }
}
