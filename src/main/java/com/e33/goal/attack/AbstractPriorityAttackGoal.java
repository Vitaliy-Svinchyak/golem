package com.e33.goal.attack;

import com.e33.entity.EntityGolemShooter;
import com.e33.fight.ShootExpectations;
import net.minecraft.entity.EntityPredicate;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.goal.TargetGoal;
import net.minecraft.util.math.AxisAlignedBB;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.EnumSet;

abstract public class AbstractPriorityAttackGoal extends TargetGoal {
    final static Logger LOGGER = LogManager.getLogger();
    LivingEntity targetToAttack;

    private final EntityPredicate entityPredicate = EntityPredicate.DEFAULT.setLineOfSiteRequired();

    AbstractPriorityAttackGoal(MobEntity goalOwner) {
        super(goalOwner, true, false);
        this.setMutexFlags(EnumSet.of(Flag.TARGET));
    }

    public boolean shouldExecute() {
        this.findTargetToAttack();
        return this.targetToAttack != null;
    }

    boolean canShoot(MobEntity mob) {
        // TODO 2 custom canSee to check not only eyes to eyes. But eyes to legs/arms too. But only if first check returns 0 enemies in district
        EntityGolemShooter goalOwner = (EntityGolemShooter) this.goalOwner;
        // func_213344_a - canTarget
        return this.goalOwner.func_213344_a(mob, this.entityPredicate) && ShootExpectations.shouldAttack(mob, this.goalOwner) && goalOwner.avoidPeacefulCreaturesGoal.bulletPathIsClear(mob);
    }

    AxisAlignedBB getTargetableArea(double distance) {
        double heightGap = 15;
        return this.goalOwner.getBoundingBox().grow(distance, heightGap, distance);
    }

    abstract void findTargetToAttack();

    public void startExecuting() {
        this.goalOwner.setAttackTarget(this.targetToAttack);
        super.startExecuting();
    }
}
