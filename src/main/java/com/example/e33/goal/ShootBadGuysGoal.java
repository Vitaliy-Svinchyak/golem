package com.example.e33.goal;

import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.projectile.SmallFireballEntity;
import net.minecraft.util.math.MathHelper;

import java.util.EnumSet;

public class ShootBadGuysGoal extends Goal {
    private final CreatureEntity entity;
    private int attackStep;
    private int attackTime;
    private int shootDistance;

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
     * Reset the task's internal state. Called when this task is interrupted by another one
     */
    public void resetTask() {
        this.shootDistance = 0;
    }

    /**
     * Keep ticking a continuous task that has already been started
     */
    public void tick() {
        --this.attackTime;
        LivingEntity livingentity = this.entity.getAttackTarget();
        if (livingentity == null) {
            return;
        }

        boolean flag = this.entity.getEntitySenses().canSee(livingentity);
        if (flag) {
            this.shootDistance = 0;
        } else {
            ++this.shootDistance;
        }

        double distanceToEntity = this.entity.getDistanceSq(livingentity);

        if (flag) {
            double d1 = livingentity.posX - this.entity.posX;
            double d2 = livingentity.getBoundingBox().minY + (double) (livingentity.getHeight() / 2.0F) - (this.entity.posY + (double) (this.entity.getHeight() / 2.0F));
            double d3 = livingentity.posZ - this.entity.posZ;

            if (this.attackTime <= 0) {
                ++this.attackStep;
                this.attackTime = 5;

                if (this.attackStep > 4) {
                    this.attackTime = 20;
                    this.attackStep = 0;
                }

                if (this.attackStep > 1) {
                    float f = MathHelper.sqrt(MathHelper.sqrt(distanceToEntity)) * 0.5F;

                    for (int i = 0; i < 1; ++i) {
                        SmallFireballEntity smallfireballentity = new SmallFireballEntity(this.entity.world, this.entity, d1, d2, d3);
                        smallfireballentity.posY = this.entity.posY + (double) (this.entity.getHeight() / 2.0F) + 0.5D;
                        this.entity.world.addEntity(smallfireballentity);
                    }
                }
            }

            this.entity.getLookController().setLookPositionWithEntity(livingentity, 10.0F, 10.0F);
        } else if (this.shootDistance < 5) {
//            this.entity.getMoveHelper().setMoveTo(livingentity.posX, livingentity.posY, livingentity.posZ, 1.0D);
        }

        super.tick();
    }

    private double getFollowDistance() {
        return this.entity.getAttribute(SharedMonsterAttributes.FOLLOW_RANGE).getValue();
    }
}