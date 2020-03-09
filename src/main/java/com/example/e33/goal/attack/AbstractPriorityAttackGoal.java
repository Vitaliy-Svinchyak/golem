package com.example.e33.goal.attack;

import com.example.e33.entity.EntityGolemShooter;
import com.example.e33.fight.ShootExpectations;
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

    AbstractPriorityAttackGoal(MobEntity goalOwner) {
        super(goalOwner, true, false);
        this.setMutexFlags(EnumSet.of(Flag.TARGET));
    }

    public boolean shouldExecute() {
        this.findTargetToAttack();
        return this.targetToAttack != null;
    }

    boolean canShoot(MobEntity mob) {
        // TODO use canTarget method
        EntityGolemShooter goalOwner = (EntityGolemShooter) this.goalOwner;
        return mob.isAlive() && ShootExpectations.shouldAttack(mob, this.goalOwner) && this.goalOwner.getEntitySenses().canSee(mob) && goalOwner.avoidPeacefulCreaturesGoal.bulletPathIsClear(mob);
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
