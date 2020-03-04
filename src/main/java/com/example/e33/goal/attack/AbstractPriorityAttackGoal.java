package com.example.e33.goal.attack;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.goal.TargetGoal;
import net.minecraft.util.math.AxisAlignedBB;

import java.util.EnumSet;

abstract public class AbstractPriorityAttackGoal extends TargetGoal {
    LivingEntity targetToAttack;

    AbstractPriorityAttackGoal(MobEntity goalOwner) {
        super(goalOwner, true, false);
        this.setMutexFlags(EnumSet.of(Flag.TARGET));
    }

    public boolean shouldExecute() {
        this.findTargetToAttack();
        return this.targetToAttack != null;
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
