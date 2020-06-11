package e33.guardy.goal.move;

import com.google.common.collect.Lists;
import e33.guardy.entity.ShootyEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.projectile.AbstractFireballEntity;
import net.minecraft.entity.projectile.DamagingProjectileEntity;
import net.minecraft.util.EntityPredicates;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

// TODO jump or crouch to avoid
public class AvoidBulletGoal extends Goal {

    private World world;
    private ShootyEntity shooty;

    private final static Logger LOGGER = LogManager.getLogger();
    public List<BlockPos> allBlocksOnWay = Lists.newArrayList();

    public AvoidBulletGoal(ShootyEntity creatureIn) {
        this.shooty = creatureIn;
        this.world = this.shooty.getEntityWorld();
    }

    @Override
    public boolean shouldExecute() {
        return this.world.getEntitiesWithinAABB(DamagingProjectileEntity.class, this.shooty.getBoundingBox().grow(10), EntityPredicates.NOT_SPECTATING).size() > 0;
    }

    @Override
    public boolean shouldContinueExecuting() {
        return super.shouldContinueExecuting();
    }

    @Override
    public void startExecuting() {
        List<DamagingProjectileEntity> bullets = this.world.getEntitiesWithinAABB(DamagingProjectileEntity.class, this.shooty.getBoundingBox().grow(20), EntityPredicates.NOT_SPECTATING);
        List<BlockPos> allBlocksOnWay = Lists.newArrayList();

        for (DamagingProjectileEntity bullet : bullets) {
            List<BlockPos> blocksOnWay = Lists.newArrayList();
            // TODO check water
            if (bullet instanceof AbstractFireballEntity) {
                double x = bullet.posX;
                double y = bullet.posY;
                double z = bullet.posZ;
                BlockPos bulletPosition = bullet.getPosition();
                blocksOnWay.add(bulletPosition);
                Vec3d motion = bullet.getMotion();
                float motionFactor = 0.95F;
                double distanceToShooty = bulletPosition.distanceSq(this.shooty.getPosition());

                while (!this.world.getBlockState(bulletPosition).isSolid()) {
                    x += motion.x;
                    y += motion.y;
                    z += motion.z;
                    motion = motion.add(bullet.accelerationX, bullet.accelerationY, bullet.accelerationZ).scale(motionFactor);
                    bulletPosition = new BlockPos(x, y, z);

                    if (!blocksOnWay.contains(bulletPosition)) {
                        blocksOnWay.add(bulletPosition);
                    }
                    if (bulletPosition.distanceSq(this.shooty.getPosition()) < distanceToShooty) {
                        distanceToShooty = bulletPosition.distanceSq(this.shooty.getPosition());
                    }

                    if (bulletPosition.distanceSq(this.shooty.getPosition()) > distanceToShooty) {
                        break;
                    }
                }
            }
            allBlocksOnWay.addAll(blocksOnWay);
        }

        this.allBlocksOnWay = allBlocksOnWay;
    }
}
