package com.example.e33.goal.attack;

import com.example.e33.fight.ShootExpectations;
import com.example.e33.util.SkeletonComparator;
import net.minecraft.entity.EntityPredicate;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.goal.TargetGoal;
import net.minecraft.entity.monster.SkeletonEntity;
import net.minecraft.util.EntityPredicates;
import net.minecraft.util.math.AxisAlignedBB;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.EnumSet;
import java.util.List;
import java.util.PriorityQueue;

public class AttackSkeletonGoal extends TargetGoal {
    private final static Logger LOGGER = LogManager.getLogger();

    protected final Class<SkeletonEntity> targetClass = SkeletonEntity.class;
    protected LivingEntity targetToAttack;
    protected EntityPredicate targetEntitySelector;

    public AttackSkeletonGoal(MobEntity entity) {
        super(entity, true, false);
        this.setMutexFlags(EnumSet.of(Flag.TARGET));
        this.targetEntitySelector = (new EntityPredicate()).setDistance(64);
    }

    public boolean shouldExecute() {
        this.findTargetToAttack();
        return this.targetToAttack != null;
    }

    protected AxisAlignedBB getTargetableArea(double p_188511_1_) {
        return this.goalOwner.getBoundingBox().grow(p_188511_1_, 4.0D, p_188511_1_);
    }

    protected void findTargetToAttack() {
        AxisAlignedBB targetableArea = this.getTargetableArea(this.getTargetDistance());
        SkeletonEntity skeletonToAttack = this.goalOwner.world.func_225318_b(this.targetClass, this.targetEntitySelector, this.goalOwner, this.goalOwner.posX, this.goalOwner.posY + (double) this.goalOwner.getEyeHeight(), this.goalOwner.posZ, targetableArea);
        if (skeletonToAttack == null) {
            this.targetToAttack = null;
            return;
        }

        List<SkeletonEntity> skeletons = this.goalOwner.world.getEntitiesWithinAABB(this.targetClass, targetableArea, EntityPredicates.NOT_SPECTATING);
        PriorityQueue<SkeletonEntity> pQueue = new PriorityQueue<>(new SkeletonComparator(this.goalOwner));
        for (SkeletonEntity skeleton : skeletons) {
            boolean validSkeleton = skeleton.isAlive() && this.goalOwner.getEntitySenses().canSee(skeleton);

            if (validSkeleton && !ShootExpectations.isMarkedAsDead(skeleton)) {
                pQueue.add(skeleton);
            }
        }

        this.targetToAttack = pQueue.poll();
    }

    public void startExecuting() {
        this.goalOwner.setAttackTarget(this.targetToAttack);
        super.startExecuting();
    }
}
