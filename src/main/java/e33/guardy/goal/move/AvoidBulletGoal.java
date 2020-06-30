package e33.guardy.goal.move;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import e33.guardy.entity.BulletEntity;
import e33.guardy.entity.ShootyEntity;
import net.minecraft.block.BlockState;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.entity.projectile.AbstractFireballEntity;
import net.minecraft.entity.projectile.DamagingProjectileEntity;
import net.minecraft.particles.ParticleTypes;
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
                && (
                this.world.getEntitiesWithinAABB(
                        DamagingProjectileEntity.class,
                        this.shooty.getBoundingBox().grow(10),
                        EntityPredicates.NOT_SPECTATING
                ).size() > 0
                        ||
                        this.world.getEntitiesWithinAABB(
                                AbstractArrowEntity.class,
                                this.shooty.getBoundingBox().grow(10),
                                EntityPredicates.NOT_SPECTATING
                        ).size() > 0)
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
        List<AbstractArrowEntity> arrows = this.world.getEntitiesWithinAABB(
                AbstractArrowEntity.class,
                this.shooty.getBoundingBox().grow(20),
                EntityPredicates.NOT_SPECTATING
        );
        Map<BlockPos, Integer> allBlocksOnWay = Maps.newHashMap();

        for (AbstractArrowEntity arrow : arrows) {
            Map<BlockPos, Integer> blocksOnWay = this.getBlocksOnWay(arrow);

            for (BlockPos pos : blocksOnWay.keySet()) {
                int tick = blocksOnWay.get(pos);
                if (allBlocksOnWay.get(pos) == null || allBlocksOnWay.get(pos) > tick) {
                    allBlocksOnWay.put(pos, tick);
                }
            }
        }

        for (DamagingProjectileEntity bullet : bullets) {
            Map<BlockPos, Integer> blocksOnWay = Maps.newHashMap();
            if (bullet instanceof AbstractFireballEntity || bullet instanceof BulletEntity) {
                blocksOnWay = this.getBlocksOnWay(bullet);
            }

            for (BlockPos pos : blocksOnWay.keySet()) {
                int tick = blocksOnWay.get(pos);
                if (allBlocksOnWay.get(pos) == null || allBlocksOnWay.get(pos) > tick) {
                    allBlocksOnWay.put(pos, tick);
                }
            }
        }

        LOGGER.info(allBlocksOnWay.size());

        this.allBlocksOnWay = allBlocksOnWay;

        if (this.shooty.getNavigator().noPath()) {
            BlockPos currentPosition1 = new BlockPos(
                    Math.floor(this.shooty.posX),
                    MathHelper.floor(this.shooty.posY),
                    Math.floor(this.shooty.posZ)
            );
            BlockPos currentPosition2 = new BlockPos(
                    Math.round(this.shooty.posX),
                    MathHelper.floor(this.shooty.posY),
                    Math.round(this.shooty.posZ)
            );
            List<BlockPos> occupiedBlocks = Lists.newArrayList(
                    currentPosition1, currentPosition1.up(), currentPosition1.up(2),
                    currentPosition2, currentPosition2.up(), currentPosition2.up(2)
            );

            for (BlockPos occupiedBlock : occupiedBlocks) {
                if (this.allBlocksOnWay.get(occupiedBlock) != null) {
                    return true;
                }
            }
        }

        return false;
    }

    private Map<BlockPos, Integer> getBlocksOnWay(AbstractArrowEntity arrow) {
        Map<BlockPos, Integer> blocksOnWay = Maps.newHashMap();
        BlockPos arrowPosition = arrow.getPosition();
        blocksOnWay.put(arrowPosition, 0);

        boolean flag = arrow.func_203047_q();
        Vec3d motion = arrow.getMotion();
        double posX = arrow.posX;
        double posY = arrow.posY;
        double posZ = arrow.posZ;
        double distanceToShooty = arrowPosition.distanceSq(this.shooty.getPosition());

        int tick = 1;
        while (!this.world.getBlockState(arrowPosition).isSolid()) {
            float f1 = 0.99F;
            // TODO check water

            motion = motion.scale(f1);
            if (!arrow.hasNoGravity() && !flag) {
                Vec3d vec3d3 = arrow.getMotion();
                motion = new Vec3d(vec3d3.x, vec3d3.y - (double) 0.05F, vec3d3.z);
            }

            posX = posX + motion.x;
            posY = posY + motion.y;
            posZ = posZ + motion.z;
            arrowPosition = new BlockPos(posX, posY, posZ);
            blocksOnWay.putIfAbsent(arrowPosition, tick);
            tick++;

            if (arrowPosition.distanceSq(this.shooty.getPosition()) < distanceToShooty) {
                distanceToShooty = arrowPosition.distanceSq(this.shooty.getPosition());
            }

            if (arrowPosition.distanceSq(this.shooty.getPosition()) > distanceToShooty) {
                break;
            }
        }
        if (blocksOnWay.size() <= 2) {
            return Maps.newHashMap();
        }
        return blocksOnWay;
    }

    private Map<BlockPos, Integer> getBlocksOnWay(DamagingProjectileEntity bullet) {
        Map<BlockPos, Integer> blocksOnWay = Maps.newHashMap();

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
            // TODO check water
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

        return blocksOnWay;
    }
}
