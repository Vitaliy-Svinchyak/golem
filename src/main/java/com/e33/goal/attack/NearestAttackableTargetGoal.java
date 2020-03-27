package com.e33.goal.attack;

import net.minecraft.entity.EntityPredicate;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.goal.TargetGoal;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.scoreboard.Team;
import net.minecraft.util.math.AxisAlignedBB;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.EnumSet;

public class NearestAttackableTargetGoal<T extends LivingEntity> extends TargetGoal {
    final static Logger LOGGER = LogManager.getLogger();
    protected final Class<T> targetClass;
    protected LivingEntity target;
    protected EntityPredicate targetEntitySelector;

    public NearestAttackableTargetGoal(MobEntity p_i50315_1_, Class<T> p_i50315_2_) {
        super(p_i50315_1_, true, false);
        this.targetClass = p_i50315_2_;
        this.setMutexFlags(EnumSet.of(Flag.TARGET));
        this.targetEntitySelector = (new EntityPredicate()).setDistance(this.getTargetDistance());
        this.setUnseenMemoryTicks(Integer.MAX_VALUE);
    }

    public boolean shouldExecute() {
        this.findNearestTarget();
        return this.target != null;
    }

    protected AxisAlignedBB getTargetableArea(double p_188511_1_) {
        return this.goalOwner.getBoundingBox().grow(p_188511_1_, 4.0D, p_188511_1_);
    }

    protected void findNearestTarget() {
        LOGGER.info("findNearestTarget");
        if (this.targetClass != PlayerEntity.class && this.targetClass != ServerPlayerEntity.class) {
            this.target = this.goalOwner.world.func_225318_b(this.targetClass, this.targetEntitySelector, this.goalOwner, this.goalOwner.posX, this.goalOwner.posY + (double) this.goalOwner.getEyeHeight(), this.goalOwner.posZ, this.getTargetableArea(this.getTargetDistance()));
        } else {
            this.target = this.goalOwner.world.getClosestPlayer(this.targetEntitySelector, this.goalOwner, this.goalOwner.posX, this.goalOwner.posY + (double) this.goalOwner.getEyeHeight(), this.goalOwner.posZ);
        }

    }

    public void startExecuting() {
        this.goalOwner.setAttackTarget(this.target);
        super.startExecuting();
    }
}
