package com.e33.goal.attack;

import com.e33.util.mobComparator.SkeletonComparator;
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
            if (this.canShoot(skeleton)) {
                pQueue.add(skeleton);
            }
        }

        this.targetToAttack = pQueue.poll();
    }
}
