package com.e33.goal;

import com.e33.E33;
import com.e33.entity.BulletEntity;
import com.e33.entity.ShootyEntity;
import com.e33.event.NewTargetEvent;
import com.e33.event.NoTargetEvent;
import com.e33.event.ShotEvent;
import com.e33.fight.ShootExpectations;
import com.e33.fight.ShootingNavigator;
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
    public final static Logger LOGGER = LogManager.getLogger();

    private final ShootyEntity entity;
    private MobEntity lastTarget = null;
    private static final Random random = new Random();
    private int attackStep;
    private int ticksToNextAttack;
    private int bulletsToShoot = -1;
    private String lastEvent = "no";

    public ShootBadGuysGoal(ShootyEntity entity) {
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
        this.ticksToNextAttack--;
        if (this.ticksToNextAttack > 0) {
            return;
        }

        MobEntity attackTarget = (MobEntity) this.entity.getAttackTarget();
        if (attackTarget != null) {
            if (this.lastEvent.equals("no")) {
                this.newTarget(attackTarget);
                this.lastEvent = "aim";
            } else {
//                this.shot(attackTarget);
                this.noTarget();
                this.lastEvent = "no";
            }

            this.ticksToNextAttack = 20;
            return;
        }

        if (this.lastTarget == null) {
            this.lastTarget = attackTarget;
            this.newTarget(attackTarget);
        }

        if (!this.entity.avoidPeacefulCreaturesGoal.bulletPathIsClear(attackTarget)) {
            this.entity.setAttackTarget(null);
            this.noTarget();
            return;
        }

        if (attackTarget == null) {
            return;
        }

        if (!this.lastTarget.equals(attackTarget)) {
            ShootExpectations.removeFromBusyList(this.lastTarget);
            this.lastTarget = attackTarget;

            this.newTarget(attackTarget);
            return;
        }

        ShootExpectations.markAsBusy(attackTarget, this.entity);
        boolean mustBeDead = true;
        if (this.attackStep == 0 && this.ticksToNextAttack <= 0) {
//            this.setBulletsToShoot(attackTarget);

            if (this.bulletsToShoot > 1) {
                this.bulletsToShoot = 1;
                mustBeDead = false;
            }
        }

        if (this.ticksToNextAttack <= 0) {
            this.attackStep++;
            this.ticksToNextAttack = 10;

            if (this.attackStep > this.bulletsToShoot) {
                this.ticksToNextAttack = 20;
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

    private void newTarget(LivingEntity attackTarget) {
        this.entity.getLookController().func_220679_a(attackTarget.posX, attackTarget.posY + (double) attackTarget.getEyeHeight(), attackTarget.posZ);

        E33.internalEventBus.post(new NewTargetEvent(this.entity, attackTarget));
        this.ticksToNextAttack = 20;
    }

    private void noTarget() {
        E33.internalEventBus.post(new NoTargetEvent(this.entity));
        this.ticksToNextAttack = 20;
    }

    private void shot(LivingEntity attackTarget) {
        E33.internalEventBus.post(new ShotEvent(this.entity, attackTarget));
    }
}