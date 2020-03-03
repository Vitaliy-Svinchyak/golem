package com.example.e33.goal;

import com.example.e33.entity.BulletEntity;
import com.example.e33.fight.ShootExpectations;
import com.example.e33.fight.ShootingNavigator;
import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.Vec3d;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.EnumSet;
import java.util.Random;

public class ShootBadGuysGoal extends Goal {
    private final static Logger LOGGER = LogManager.getLogger();

    private final CreatureEntity entity;
    protected static final Random random = new Random();
    private int attackStep;
    private int attackTime;
    private Vec3d attackPoint;
    private int bulletsToShoot = -1;

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
        this.attackTime--;
        if (this.attackTime > 0) {
            return;
        }

        MobEntity attackTarget = (MobEntity) this.entity.getAttackTarget();
        if (attackTarget == null || !this.entity.getEntitySenses().canSee(attackTarget) || !attackTarget.isAlive()) {
            return;
        }

        boolean mustBeDead = true;
        if (this.attackStep == 0 && this.attackTime <= 0) {
            this.setBulletsToShoot(attackTarget);

            if (this.bulletsToShoot > 2) {
                this.bulletsToShoot = 2;
                mustBeDead = false;
            }
        }

        if (this.attackTime <= 0) {
            this.attackStep++;
            this.attackTime = 10;

            if (this.attackStep > this.bulletsToShoot) {
                this.attackTime = 20;
                this.attackStep = 0;
            }

            if (this.attackStep >= 1) {
                this.makeShot(attackTarget);
            }

            // Last shot
            if (this.attackStep == this.bulletsToShoot) {
                if (mustBeDead) {
                    ShootExpectations.markAsDead(attackTarget);
                }
                this.entity.setAttackTarget(null);
            }
        }

        this.entity.getLookController().setLookPositionWithEntity(attackTarget, 5.0F, 5.0F);
    }

    private void setBulletsToShoot(MobEntity attackTarget) {
        float targetHealth = attackTarget.getHealth();

        if (targetHealth > attackTarget.getAttribute(SharedMonsterAttributes.MAX_HEALTH).getValue()) {
            LOGGER.info("ooooops");
            targetHealth = (float) attackTarget.getAttribute(SharedMonsterAttributes.MAX_HEALTH).getValue();
        }

        this.bulletsToShoot = (int) Math.ceil(targetHealth / 5);
    }

    private void makeShot(MobEntity attackTarget) {
        Vec3d attackPoint = ShootingNavigator.getShootPoint(attackTarget, this.entity);
        BulletEntity bullet = new BulletEntity(this.entity.world, this.entity, attackPoint.x, attackPoint.y, attackPoint.z, attackTarget);
        this.entity.world.addEntity(bullet);
        this.entity.world.playSound(null, this.entity.posX, this.entity.posY, this.entity.posZ, SoundEvents.ENTITY_ARROW_SHOOT, SoundCategory.PLAYERS, 1.0F, 1.0F / (random.nextFloat() * 0.4F + 1.2F) + 20.0F * 0.5F);
    }
}