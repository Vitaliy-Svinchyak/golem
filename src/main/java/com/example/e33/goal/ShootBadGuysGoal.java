package com.example.e33.goal;

import com.example.e33.entity.BulletEntity;
import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.MathHelper;

import java.util.EnumSet;
import java.util.Random;

public class ShootBadGuysGoal extends Goal {
    private final CreatureEntity entity;
    protected static final Random random = new Random();
    private int attackStep;
    private int attackTime;

    private double attackAccelY = -0.0;
    private double attackAccelX = -0.0;
    private double attackAccelZ = -0.0;

    public ShootBadGuysGoal(CreatureEntity entity) {
        this.entity = entity;
        this.setMutexFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
    }

    /**
     * Returns whether the EntityAIBase should begin execution.
     */
    public boolean shouldExecute() {
        LivingEntity livingentity = this.entity.getAttackTarget();
        return livingentity != null && livingentity.isAlive() && this.entity.canAttack(livingentity);
    }

    /**
     * Execute a one shot task or start executing a continuous task
     */
    public void startExecuting() {
        this.attackStep = 0;
    }

    /**
     * Keep ticking a continuous task that has already been started
     */
    public void tick() {
        --this.attackTime;

        LivingEntity attackTarget = this.entity.getAttackTarget();
        if (attackTarget == null) {
            return;
        }

        boolean flag = this.entity.getEntitySenses().canSee(attackTarget);
        if (!flag) {
            return;
        }

        if (this.attackAccelY == -0.0) {
            this.attackAccelY = MathHelper.floor(attackTarget.posY - this.entity.posY - (double) (attackTarget.getHeight() / 3.0F));
            this.attackAccelZ = attackTarget.posZ - this.entity.posZ;
            this.attackAccelX = attackTarget.posX - this.entity.posX;
        }

        if (this.attackTime <= 0) {
            ++this.attackStep;
            this.attackTime = 2;

            if (this.attackStep > 4) {
                this.attackTime = 10;
                this.attackStep = 0;
                this.attackAccelY = -0.0;
            }

            if (this.attackStep > 1) {
                BulletEntity bullet = new BulletEntity(this.entity.world, this.entity, this.attackAccelX, this.attackAccelY, this.attackAccelZ, this.entity);
                bullet.posY = this.entity.posY + (double) (this.entity.getHeight() / 2.0F) + 0.5D;
                this.entity.world.addEntity(bullet);
                this.entity.world.playSound((PlayerEntity) null, this.entity.posX, this.entity.posY, this.entity.posZ, SoundEvents.ENTITY_ARROW_SHOOT, SoundCategory.PLAYERS, 1.0F, 1.0F / (random.nextFloat() * 0.4F + 1.2F) + 20.0F * 0.5F);
            }
        }

        this.entity.getLookController().setLookPositionWithEntity(attackTarget, 10.0F, 10.0F);
    }
}