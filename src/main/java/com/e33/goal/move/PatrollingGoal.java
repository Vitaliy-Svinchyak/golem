package com.e33.goal.move;

import com.e33.E33;
import com.e33.client.animation.animation.Animation;
import com.e33.client.detail.AnimationState;
import com.e33.client.listener.AnimationStateListener;
import com.e33.event.MoveEvent;
import com.e33.event.NoActionEvent;
import com.google.common.collect.Lists;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.ai.goal.RandomWalkingGoal;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nullable;
import java.util.List;

public class PatrollingGoal extends RandomWalkingGoal {
    private World world;
    private List<Vec3d> patrolRoute = Lists.newArrayList();
    private int patrolPoint = 0;
    private Class<? extends Block> blockToPatrol;
    private boolean firstLoad = true;

    private final static Logger LOGGER = LogManager.getLogger();

    public PatrollingGoal(CreatureEntity creatureIn, double speedIn, Class<? extends Block> block) {
        super(creatureIn, speedIn, 1);

        this.world = this.creature.getEntityWorld();
        this.blockToPatrol = block;
    }

    public boolean shouldExecute() {
        if (this.patrolPointExists()) {
            this.createPatrolRoute();
        } else {
            this.patrolRoute = Lists.newArrayList();
            this.stop();
            return false;
        }

        return super.shouldExecute();
    }

    private BlockPos findNearestBlockToPatrol() {
        BlockPos creaturePosition = this.creature.getPosition();
        AxisAlignedBB creatureView = new AxisAlignedBB(creaturePosition.getX() - 30, creaturePosition.getY() - 1, creaturePosition.getZ() - 30, creaturePosition.getX() + 30, creaturePosition.getY() + 1, creaturePosition.getZ() + 30);

        return this.findBlockPosInArea(creatureView);
    }

    @Nullable
    @OnlyIn(Dist.CLIENT)
    private BlockPos findBlockPosInArea(AxisAlignedBB area) {
        int i = MathHelper.floor(area.minX);
        int j = MathHelper.ceil(area.maxX);
        int k = MathHelper.floor(area.minY);
        int l = MathHelper.ceil(area.maxY);
        int i1 = MathHelper.floor(area.minZ);
        int j1 = MathHelper.ceil(area.maxZ);

        // TODO fix deprecation
        if (!this.world.isAreaLoaded(i, k, i1, j, l, j1)) {
            return null;
        }

        try (BlockPos.PooledMutableBlockPos blockpos$pooledmutableblockpos = BlockPos.PooledMutableBlockPos.retain()) {
            for (int k1 = i; k1 < j; ++k1) {
                for (int l1 = k; l1 < l; ++l1) {
                    for (int i2 = i1; i2 < j1; ++i2) {
                        BlockState blockstate = this.world.getBlockState(blockpos$pooledmutableblockpos.setPos(k1, l1, i2));
                        if (blockstate.getBlock().getClass().toString().equals(this.blockToPatrol.toString())) {
                            return new BlockPos(k1, l1, i2);
                        }
                    }
                }
            }
        }

        return null;
    }

    private boolean patrolPointExists() {
        return this.findNearestBlockToPatrol() != null;
    }

    private void createPatrolRoute() {
        if (!this.patrolRoute.isEmpty()) {
            return;
        }

        BlockPos blockToPatrol = this.findNearestBlockToPatrol();
        if (blockToPatrol == null) {
            return;
        }

        int x = blockToPatrol.getX();
        int y = blockToPatrol.getY();
        int z = blockToPatrol.getZ();

        this.patrolRoute.add(new Vec3d(x - 10, y - 1, z - 10));
        this.patrolRoute.add(new Vec3d(x - 10, y - 1, z + 10));
        this.patrolRoute.add(new Vec3d(x + 10, y - 1, z + 10));
        this.patrolRoute.add(new Vec3d(x + 10, y - 1, z - 10));
        this.move();
    }

    @Nullable
    protected Vec3d getPosition() {
        if (this.patrolRoute.isEmpty()) {
            return null;
        }

        if (!this.patrolPointExists()) {
            this.stop();
            return null;
        }

        if (!this.isGoalAchieved() && !this.firstLoad) {
            LOGGER.debug("Go again");
            return this.getCurrentPatrolTask();
        }

        if (this.firstLoad) {
            this.firstLoad = false;
            return this.getNearestPatrolPoint();
        }

        this.patrolPoint++;
        if (this.patrolPoint > this.patrolRoute.size() - 1) {
            this.patrolPoint = 0;
        }

        return this.patrolRoute.get(this.patrolPoint);
    }

    private Vec3d getCurrentPatrolTask() {
        return this.patrolRoute.get(this.patrolPoint);
    }

    public boolean isPreemptible() {
        return false;
    }

    /**
     * Returns whether an in-progress EntityAIBase should continue executing
     */
    public boolean shouldContinueExecuting() {
        return super.shouldContinueExecuting() && !this.isGoalAchieved();
    }

    public void startExecuting() {
        super.startExecuting();
    }

    private boolean isGoalAchieved() {
        Vec3d pointToAchieve = this.getCurrentPatrolTask();
        double remainingDistance = this.creature.getPositionVector().distanceTo(pointToAchieve);

        return remainingDistance <= 3.0D;
    }

    private Vec3d getNearestPatrolPoint() {
        Vec3d nearestPoint = this.patrolRoute.get(0);

        for (Vec3d patrolPoint : this.patrolRoute) {
            double distanceToPoint = this.creature.getPositionVector().distanceTo(patrolPoint);

            if (this.creature.getPositionVector().distanceTo(nearestPoint) > distanceToPoint) {
                nearestPoint = patrolPoint;
            }
        }

        return nearestPoint;
    }

    private void move() {
        E33.internalEventBus.post(new MoveEvent(this.creature));
    }

    private void stop() {
        if (AnimationStateListener.getAnimationState(this.creature) == AnimationState.MOVE) {
            E33.internalEventBus.post(new NoActionEvent(this.creature));
        }
    }
}
