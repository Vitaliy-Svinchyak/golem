package com.e33.goal;

import com.e33.E33;
import com.e33.client.detail.AnimationState;
import com.e33.client.listener.AnimationStateListener;
import com.e33.entity.BulletEntity;
import com.e33.entity.ShootyEntity;
import com.e33.event.NoActionEvent;
import com.e33.event.ShotEvent;
import com.e33.fight.ShootExpectations;
import com.e33.fight.ShootingNavigator;
import com.e33.init.SoundsRegistry;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.Vec3d;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.EnumSet;
import java.util.Random;

public class ShootBadGuysGoal extends Goal {
    public final static Logger LOGGER = LogManager.getLogger();

    private final ShootyEntity entity;
    private final LookAtTargetGoal lookGoal;
    private static final Random random = new Random();
    private int attackStep;
    private int ticksToNextAttack;
    private int bulletsToShoot = -1;
    private boolean noTarget = false;

    public ShootBadGuysGoal(ShootyEntity entity, LookAtTargetGoal lookGoal) {
        this.entity = entity;
        this.lookGoal = lookGoal;
        this.setMutexFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
    }

    /**
     * Returns whether the EntityAIBase should begin execution.
     */
    public boolean shouldExecute() {
        if (!this.lookGoal.isAlreadyLookingOnTarget()) {
            return false;
        }

        LivingEntity target = this.entity.getAttackTarget();
        boolean shouldExecute = target != null && target.isAlive() && this.entity.canAttack(target);
        LOGGER.info("null " + (target != null));

        AnimationState currentState = AnimationStateListener.getAnimationState(this.entity);
        if (!shouldExecute && (currentState == AnimationState.SHOT || currentState == AnimationState.AIM)) {
            if (this.noTarget) {
                this.noTarget();
            }

            this.noTarget = true;
        } else {
            this.noTarget = false;
        }

        return shouldExecute;
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

        LivingEntity attackTarget = this.entity.getAttackTarget();

        if (!this.entity.avoidPeacefulCreaturesGoal.bulletPathIsClear(attackTarget)) {
            LOGGER.error("I can't!!!!!!");
            this.entity.setAttackTarget(null);
            return;
        }

        boolean mustBeDead = true;
        if (this.attackStep == 0 && this.ticksToNextAttack <= 0) {
            this.bulletsToShoot = this.getBulletsToShoot(attackTarget);

            // TODO 2 more then 1 shot
            if (this.bulletsToShoot > 1) {
                this.bulletsToShoot = 1;
                mustBeDead = false;
            }
        }

        if (this.ticksToNextAttack <= 0) {
            this.attackStep++;
            this.ticksToNextAttack = 20;

            if (this.attackStep > this.bulletsToShoot) {
                this.ticksToNextAttack = 30;
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

    private int getBulletsToShoot(LivingEntity attackTarget) {
        float targetHealth = attackTarget.getHealth();

        if (targetHealth > attackTarget.getAttribute(SharedMonsterAttributes.MAX_HEALTH).getValue()) {
            LOGGER.info("ooooops");
            targetHealth = (float) attackTarget.getAttribute(SharedMonsterAttributes.MAX_HEALTH).getValue();
        }

        return (int) Math.ceil(targetHealth / 5);
    }

    private void makeShot(LivingEntity attackTarget) {
        Vec3d attackPoint = ShootingNavigator.getShootPoint(attackTarget, this.entity);
        BulletEntity bullet = new BulletEntity(this.entity.world, this.entity, attackPoint.x, attackPoint.y, attackPoint.z, attackTarget);
        Vec3d position = this.getPositionForParticle();
        bullet.setPosition(position.x, position.y, position.z);
        this.entity.world.addEntity(bullet);
        this.shot(attackTarget);
    }

    private void noTarget() {
        LOGGER.info("no target");
        E33.internalEventBus.post(new NoActionEvent(this.entity));
//        this.ticksToNextAttack = 10;
    }

    private void shot(LivingEntity attackTarget) {
        E33.internalEventBus.post(new ShotEvent(this.entity, attackTarget));
        this.entity.world.playSound(null, this.entity.posX, this.entity.posY, this.entity.posZ, SoundsRegistry.SHOOTY_SHOT, SoundCategory.PLAYERS, 1.0F, 1.0F / (random.nextFloat() * 0.4F + 1.2F) + 20.0F * 0.5F);
    }

    private Vec3d getPositionForParticle() {
        Vec3d lookVec = this.entity.getLook(0);
        Vec3d position = this.entity.getPositionVector();

        float y = (float) position.getY() + 1.25F;

        return new Vec3d(position.getX() + lookVec.getX(), y, position.getZ() + lookVec.getZ());
    }
}