package e33.guardy.goal.attack;

import e33.guardy.util.mobComparator.SlimeComparator;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.monster.SlimeEntity;
import net.minecraft.util.EntityPredicates;
import net.minecraft.util.math.AxisAlignedBB;

import java.util.List;
import java.util.PriorityQueue;

public class AttackSlimeGoal extends AbstractPriorityAttackGoal {

    public AttackSlimeGoal(MobEntity goalOwner) {
        super(goalOwner);
    }

    void findTargetToAttack() {
        AxisAlignedBB targetableArea = this.getTargetableArea(this.getTargetDistance());
        List<SlimeEntity> slimes = this.goalOwner.world.getEntitiesWithinAABB(SlimeEntity.class, targetableArea, EntityPredicates.NOT_SPECTATING);
        PriorityQueue<SlimeEntity> pQueue = new PriorityQueue<>(new SlimeComparator(this.goalOwner));

        for (SlimeEntity slime : slimes) {
            if (this.canShoot(slime)) {
                pQueue.add(slime);
            }
        }

        this.targetToAttack = pQueue.poll();
    }
}

