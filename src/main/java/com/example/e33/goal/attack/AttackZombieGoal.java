package com.example.e33.goal.attack;

import com.example.e33.fight.ShootExpectations;
import com.example.e33.util.ZombieComparator;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.monster.ZombieEntity;
import net.minecraft.util.EntityPredicates;
import net.minecraft.util.math.AxisAlignedBB;

import java.util.List;
import java.util.PriorityQueue;

public class AttackZombieGoal extends AbstractPriorityAttackGoal {

    public AttackZombieGoal(MobEntity goalOwner) { super(goalOwner); }

    protected void findTargetToAttack() {
        AxisAlignedBB targetableArea = this.getTargetableArea(this.getTargetDistance());
        List<ZombieEntity> zombies = this.goalOwner.world.getEntitiesWithinAABB(ZombieEntity.class, targetableArea, EntityPredicates.NOT_SPECTATING);
        PriorityQueue<ZombieEntity> pQueue = new PriorityQueue<>(new ZombieComparator(this.goalOwner));

        for (ZombieEntity zombie : zombies) {
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
