package com.example.e33.goal.attack;

import com.example.e33.fight.ShootExpectations;
import com.example.e33.util.CreeperComparator;
import com.example.e33.util.ZombieComparator;
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
        List<CreeperEntity> zombies = this.goalOwner.world.getEntitiesWithinAABB(CreeperEntity.class, targetableArea, EntityPredicates.NOT_SPECTATING);
        PriorityQueue<CreeperEntity> pQueue = new PriorityQueue<>(new CreeperComparator(this.goalOwner));

        for (CreeperEntity zombie : zombies) {
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
