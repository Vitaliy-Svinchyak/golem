package com.e33.goal.attack;

import com.e33.util.mobComparator.SpiderComparator;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.monster.SpiderEntity;
import net.minecraft.util.EntityPredicates;
import net.minecraft.util.math.AxisAlignedBB;

import java.util.List;
import java.util.PriorityQueue;

public class AttackSpiderGoal extends AbstractPriorityAttackGoal {

    public AttackSpiderGoal(MobEntity goalOwner) { super(goalOwner); }

    protected void findTargetToAttack() {
        AxisAlignedBB targetableArea = this.getTargetableArea(this.getTargetDistance());
        List<SpiderEntity> spiders = this.goalOwner.world.getEntitiesWithinAABB(SpiderEntity.class, targetableArea, EntityPredicates.NOT_SPECTATING);
        PriorityQueue<SpiderEntity> pQueue = new PriorityQueue<>(new SpiderComparator(this.goalOwner));

        for (SpiderEntity spider : spiders) {
            if (this.canShoot(spider)) {
                pQueue.add(spider);
            }
        }

        this.targetToAttack = pQueue.poll();
    }

}
