package com.example.e33.goal.attack;

import com.example.e33.fight.ShootExpectations;
import com.example.e33.util.SpiderComparator;
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
        List<SpiderEntity> zombies = this.goalOwner.world.getEntitiesWithinAABB(SpiderEntity.class, targetableArea, EntityPredicates.NOT_SPECTATING);
        PriorityQueue<SpiderEntity> pQueue = new PriorityQueue<>(new SpiderComparator(this.goalOwner));

        for (SpiderEntity zombie : zombies) {
            // TODO add check that nobody is on the way of bullet
            // TODO use canTarget method
            boolean validZombie = zombie.isAlive() && this.goalOwner.getEntitySenses().canSee(zombie);

            if (validZombie && !ShootExpectations.isMarkedAsDead(zombie)) {
                pQueue.add(zombie);
            }
        }

        this.targetToAttack = pQueue.poll();
    }

}
