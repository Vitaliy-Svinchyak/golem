package com.example.e33.fight.shootingNavigator;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.debug.DebugRenderer;
import net.minecraft.entity.MobEntity;
import net.minecraft.util.math.Vec3d;

import javax.annotation.Nonnull;
import java.util.HashMap;

public class CreeperShootingNavigator extends AbstractShootingNavigator {
    private static HashMap<Integer, Boolean> showedPaths = new HashMap<>();

    @Nonnull
    public static Vec3d getShootPoint(@Nonnull MobEntity target, @Nonnull MobEntity creature) {
        Vec3d targetPosition = CreeperShootingNavigator.guessWhereTargetWillBeWhileBulletIsInAir(target, creature);
        double targetHeight = target.getBoundingBox().maxY - target.getBoundingBox().minY;

        if (target.isChild()) {
            targetHeight /= 2;
        }

        double attackAccelX = targetPosition.x - creature.posX;
        double attackAccelY = (targetPosition.y + (targetHeight / 2)) - (creature.posY + (creature.getHeight() / 1.5));
        double attackAccelZ = targetPosition.z - creature.posZ;

        if (target.getNavigator().getPath() != null && showedPaths.get(target.getNavigator().getPath().hashCode()) == null) {
            DebugRenderer renderer = Minecraft.getInstance().debugRenderer;
            renderer.pathfinding.addPath(target.getUniqueID().hashCode(), target.getNavigator().getPath(), 0);
            // TODO clear cache after enemy die (memory leak)
            showedPaths.put(target.getNavigator().getPath().hashCode(), true);
        }

        return new Vec3d(attackAccelX, attackAccelY, attackAccelZ);
    }
}
