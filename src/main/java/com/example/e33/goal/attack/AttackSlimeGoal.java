package com.example.e33.goal.attack;

import com.example.e33.fight.ShootExpectations;
import com.example.e33.fight.ShootStatistic;
import com.example.e33.util.SlimeComparator;
import net.minecraft.entity.EntityPredicate;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.goal.TargetGoal;
import net.minecraft.entity.monster.SlimeEntity;
import net.minecraft.util.EntityPredicates;
import net.minecraft.util.math.AxisAlignedBB;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.EnumSet;
import java.util.List;
import java.util.PriorityQueue;

public class AttackSlimeGoal extends TargetGoal {
    private final static Logger LOGGER = LogManager.getLogger();

    protected final Class<SlimeEntity> targetClass = SlimeEntity.class;
    protected LivingEntity targetToAttack;
    protected EntityPredicate targetEntitySelector;

    public AttackSlimeGoal(MobEntity entity) {
        super(entity, true, false);
        this.setMutexFlags(EnumSet.of(Flag.TARGET));
        this.targetEntitySelector = (new EntityPredicate()).setDistance(64);
    }

    public boolean shouldExecute() {
        this.findTargetToAttack();
        return this.targetToAttack != null;
    }

    protected AxisAlignedBB getTargetableArea(double p_188511_1_) {
        return this.goalOwner.getBoundingBox().grow(p_188511_1_, 4.0D, p_188511_1_);
    }

    protected void findTargetToAttack() {
        AxisAlignedBB targetableArea = this.getTargetableArea(this.getTargetDistance());
        SlimeEntity slimeToAttack = this.goalOwner.world.func_225318_b(this.targetClass, this.targetEntitySelector, this.goalOwner, this.goalOwner.posX, this.goalOwner.posY + (double) this.goalOwner.getEyeHeight(), this.goalOwner.posZ, targetableArea);
        if (slimeToAttack == null) {
            this.targetToAttack = null;
            return;
        }

        List<SlimeEntity> slimes = this.goalOwner.world.getEntitiesWithinAABB(this.targetClass, targetableArea, EntityPredicates.NOT_SPECTATING);
        PriorityQueue<SlimeEntity> pQueue = new PriorityQueue<SlimeEntity>(new SlimeComparator(this.goalOwner));
        for (SlimeEntity slime : slimes) {
            boolean validSlime = slime.isAlive() && this.goalOwner.getEntitySenses().canSee(slime);

            if (validSlime && !ShootExpectations.isMarkedAsDead(slime)) {
                pQueue.add(slime);
            }
        }

        this.targetToAttack = pQueue.poll();
    }

    public void startExecuting() {
        this.goalOwner.setAttackTarget(this.targetToAttack);
        super.startExecuting();
    }
}

