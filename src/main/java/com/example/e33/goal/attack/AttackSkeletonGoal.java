package com.example.e33.goal.attack;

import com.example.e33.fight.ShootExpectations;
import com.example.e33.util.SkeletonComparator;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.monster.SkeletonEntity;
import net.minecraft.util.EntityPredicates;
import net.minecraft.util.math.AxisAlignedBB;

import java.util.List;
import java.util.PriorityQueue;

public class AttackSkeletonGoal extends AbstractPriorityAttackGoal {

    public AttackSkeletonGoal(MobEntity goalOwner) {
        super(goalOwner);
    }

    void findTargetToAttack() {
        AxisAlignedBB targetableArea = this.getTargetableArea(this.getTargetDistance());
        List<SkeletonEntity> skeletons = this.goalOwner.world.getEntitiesWithinAABB(SkeletonEntity.class, targetableArea, EntityPredicates.NOT_SPECTATING);
        PriorityQueue<SkeletonEntity> pQueue = new PriorityQueue<>(new SkeletonComparator(this.goalOwner));

        for (SkeletonEntity skeleton : skeletons) {
            // TODO add check that nobody is on the way of bullet
            // TODO use canTarget method
            boolean validSkeleton = skeleton.isAlive() && this.goalOwner.getEntitySenses().canSee(skeleton);

            if (validSkeleton && ShootExpectations.shouldAttack(skeleton, this.goalOwner)) {
                pQueue.add(skeleton);
            }
        }

        this.targetToAttack = pQueue.poll();
    }
}
