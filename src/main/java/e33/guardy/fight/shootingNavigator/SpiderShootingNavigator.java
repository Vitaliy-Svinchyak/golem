package e33.guardy.fight.shootingNavigator;

import net.minecraft.entity.MobEntity;
import net.minecraft.util.math.Vec3d;

import javax.annotation.Nonnull;

public class SpiderShootingNavigator extends AbstractShootingNavigator {

    @Nonnull
    public static Vec3d getShootPoint(@Nonnull MobEntity target, @Nonnull MobEntity creature) {
        // TODO 2 special case for climbing on walls (another speed etc)
        Vec3d targetPosition = guessWhereTargetWillBeWhileBulletIsInAir(target, creature);
        double targetHeight = target.getBoundingBox().maxY - target.getBoundingBox().minY;

        double attackAccelX = targetPosition.x - creature.posX;
        double attackAccelY = (targetPosition.y + (targetHeight / 2)) - (creature.posY + (creature.getHeight() / 1.2));
        double attackAccelZ = targetPosition.z - creature.posZ;

        addPathToDebug(target);

        return new Vec3d(attackAccelX, attackAccelY, attackAccelZ);
    }
}
