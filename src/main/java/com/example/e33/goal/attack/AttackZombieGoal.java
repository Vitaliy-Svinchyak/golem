package com.example.e33.goal.attack;

import com.example.e33.util.mobComparator.ZombieComparator;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.monster.ZombieEntity;
import net.minecraft.util.EntityPredicates;
import net.minecraft.util.math.AxisAlignedBB;

import java.util.List;
import java.util.PriorityQueue;

public class AttackZombieGoal extends AbstractPriorityAttackGoal {

    public AttackZombieGoal(MobEntity goalOwner) {
        super(goalOwner);
    }

    protected void findTargetToAttack() {
        AxisAlignedBB targetableArea = this.getTargetableArea(this.getTargetDistance());
        List<ZombieEntity> zombies = this.goalOwner.world.getEntitiesWithinAABB(ZombieEntity.class, targetableArea, EntityPredicates.NOT_SPECTATING);
        PriorityQueue<ZombieEntity> pQueue = new PriorityQueue<>(new ZombieComparator(this.goalOwner));

        for (ZombieEntity zombie : zombies) {
            if (this.canShoot(zombie)) {
                pQueue.add(zombie);
            }
        }

        this.targetToAttack = pQueue.poll();
    }

}
