package com.example.e33.goal;

import com.example.e33.entity.EntityGolemShooter;
import com.example.e33.util.SlimeComparator;
import net.minecraft.entity.EntityPredicate;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.goal.NearestAttackableTargetGoal;
import net.minecraft.entity.ai.goal.TargetGoal;
import net.minecraft.entity.monster.SlimeEntity;
import net.minecraft.util.EntityPredicates;
import net.minecraft.util.math.AxisAlignedBB;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.EnumSet;
import java.util.List;
import java.util.PriorityQueue;

public class AttackSlimeGoal<T extends LivingEntity> extends TargetGoal {
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
        SlimeEntity slimeToAttack = this.goalOwner.world.func_225318_b(this.targetClass, this.targetEntitySelector, this.goalOwner, this.goalOwner.posX, this.goalOwner.posY + (double) this.goalOwner.getEyeHeight(), this.goalOwner.posZ, this.getTargetableArea(this.getTargetDistance()));
        if (slimeToAttack == null) {
            this.targetToAttack = null;
            return;
        }

        List<SlimeEntity> slimes = this.goalOwner.world.getEntitiesWithinAABB(this.targetClass, this.getTargetableArea(this.getTargetDistance()), EntityPredicates.NOT_SPECTATING);
        PriorityQueue<SlimeEntity> pQueue = new PriorityQueue<SlimeEntity>(new SlimeComparator(this.goalOwner));
        for (SlimeEntity slime : slimes) {
            boolean validSlime = slime.isAlive() && this.goalOwner.getEntitySenses().canSee(slime);

            // To make it more interesting
            if (validSlime) {
                slime.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(slime, EntityGolemShooter.class, true));
                pQueue.add(slime);
            }

            if (validSlime && slime.getSlimeSize() > slimeToAttack.getSlimeSize()) {
                slimeToAttack = slime;
            }
        }

        this.targetToAttack = pQueue.poll();
//        this.targetToAttack = slimeToAttack;
    }

    public void startExecuting() {
        this.goalOwner.setAttackTarget(this.targetToAttack);
        super.startExecuting();
    }
}

