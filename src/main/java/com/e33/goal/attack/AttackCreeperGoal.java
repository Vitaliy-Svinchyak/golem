package com.e33.goal.attack;

import com.e33.util.mobComparator.CreeperComparator;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.monster.CreeperEntity;
import net.minecraft.util.EntityPredicates;
import net.minecraft.util.math.AxisAlignedBB;

import java.util.List;
import java.util.PriorityQueue;

public class AttackCreeperGoal extends AbstractPriorityAttackGoal {

    public AttackCreeperGoal(MobEntity goalOwner) { super(goalOwner); }

    protected void findTargetToAttack() {
        AxisAlignedBB targetableArea = this.getTargetableArea(this.getTargetDistance());
        List<CreeperEntity> creepers = this.goalOwner.world.getEntitiesWithinAABB(CreeperEntity.class, targetableArea, EntityPredicates.NOT_SPECTATING);
        PriorityQueue<CreeperEntity> pQueue = new PriorityQueue<>(new CreeperComparator(this.goalOwner));

        for (CreeperEntity creeper : creepers) {
            if (this.canShoot(creeper)) {
                pQueue.add(creeper);
            }
        }

        this.targetToAttack = pQueue.poll();
    }

}
