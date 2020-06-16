package e33.guardy.goal.move;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import e33.guardy.entity.ShootyEntity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.projectile.AbstractFireballEntity;
import net.minecraft.entity.projectile.DamagingProjectileEntity;
import net.minecraft.pathfinding.Path;
import net.minecraft.util.EntityPredicates;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.Map;

// TODO jump or crouch to avoid
public class AvoidBulletGoal extends Goal {

    private World world;
    private ShootyEntity shooty;

    private final static Logger LOGGER = LogManager.getLogger();
    public Map<BlockPos, Integer> allBlocksOnWay = Maps.newHashMap();

    public AvoidBulletGoal(ShootyEntity creatureIn) {
        this.shooty = creatureIn;
        this.world = this.shooty.getEntityWorld();
    }

    @Override
    public boolean shouldExecute() {
        // TODO cache which arrows were tracked and which are new (check only new)
        return this.shooty.getNavigator().noPath()
                && this.world.getEntitiesWithinAABB(DamagingProjectileEntity.class, this.shooty.getBoundingBox().grow(10), EntityPredicates.NOT_SPECTATING).size() > 0
                && this.isOnDangerousPosition();
    }

    @Override
    public boolean shouldContinueExecuting() {
        return super.shouldContinueExecuting();
    }

    @Override
    public void startExecuting() {
        Path path = this.shooty.pathCreator.getSafestPositionNearby(this.allBlocksOnWay);
        this.shooty.getNavigator().setPath(path, this.shooty.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).getValue());
//        this.allBlocksOnWay = Maps.newHashMap();
    }

    private boolean isOnDangerousPosition() {
        List<DamagingProjectileEntity> bullets = this.world.getEntitiesWithinAABB(
                DamagingProjectileEntity.class,
                this.shooty.getBoundingBox().grow(20),
                EntityPredicates.NOT_SPECTATING
        );
        Map<BlockPos, Integer> allBlocksOnWay = Maps.newHashMap();

        for (DamagingProjectileEntity bullet : bullets) {
            Map<BlockPos, Integer> blocksOnWay = Maps.newHashMap();
            // TODO check water
            if (bullet instanceof AbstractFireballEntity) {
                double x = bullet.posX;
                double y = bullet.posY;
                double z = bullet.posZ;
                BlockPos bulletPosition = bullet.getPosition();
                blocksOnWay.put(bulletPosition, 0);
                Vec3d motion = bullet.getMotion();
                float motionFactor = 0.95F;
                double distanceToShooty = bulletPosition.distanceSq(this.shooty.getPosition());

                int tick = 1;
                while (!this.world.getBlockState(bulletPosition).isSolid()) {
                    x += motion.x;
                    y += motion.y;
                    z += motion.z;
                    motion = motion.add(bullet.accelerationX, bullet.accelerationY, bullet.accelerationZ).scale(motionFactor);
                    bulletPosition = new BlockPos(x, y, z);

                    blocksOnWay.putIfAbsent(bulletPosition, tick);
                    if (bulletPosition.distanceSq(this.shooty.getPosition()) < distanceToShooty) {
                        distanceToShooty = bulletPosition.distanceSq(this.shooty.getPosition());
                    }

                    if (bulletPosition.distanceSq(this.shooty.getPosition()) > distanceToShooty) {
                        break;
                    }
                    tick++;
                }
            }

            for (BlockPos pos : blocksOnWay.keySet()) {
                int tick = blocksOnWay.get(pos);
                if (allBlocksOnWay.get(pos) == null || allBlocksOnWay.get(pos) > tick) {
                    allBlocksOnWay.put(pos, tick);
                }
            }
        }

        this.allBlocksOnWay = allBlocksOnWay;

        if (this.shooty.getNavigator().noPath()) {
            BlockPos currentPosition1 = new BlockPos(Math.floor(this.shooty.posX), MathHelper.floor(this.shooty.posY), Math.floor(this.shooty.posZ));
            BlockPos currentPosition2 = new BlockPos(Math.round(this.shooty.posX), MathHelper.floor(this.shooty.posY), Math.round(this.shooty.posZ));
            List<BlockPos> occupiedBlocks = Lists.newArrayList(currentPosition1, currentPosition1.up(), currentPosition1.up(2), currentPosition2, currentPosition2.up(), currentPosition2.up(2));

            for (BlockPos occupiedBlock : occupiedBlocks) {
                if (this.allBlocksOnWay.get(occupiedBlock) != null) {
                    return true;
                }
            }
        }

        return false;
    }
}
