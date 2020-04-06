package e33.guardy.goal;

import e33.guardy.E33;
import e33.guardy.client.detail.AnimationState;
import e33.guardy.client.listener.AnimationStateListener;
import e33.guardy.event.NewTargetEvent;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.goal.Goal;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.EnumSet;
import java.util.UUID;

public class LookAtTargetGoal extends Goal {
    protected final static Logger LOGGER = LogManager.getLogger();
    protected final MobEntity entity;
    protected Entity target;
    protected final double maxDistance;
    private int ticksForAnimation;
    private boolean animationStarted = false;
    private boolean newLookTarget;
    private UUID lastTargetUUID;

    public LookAtTargetGoal(MobEntity goalOwner) {
        this.entity = goalOwner;
        this.maxDistance = goalOwner.getAttribute(SharedMonsterAttributes.FOLLOW_RANGE).getBaseValue();
        this.setMutexFlags(EnumSet.of(Flag.LOOK));
    }

    public boolean shouldExecute() {
        if (this.entity.getAttackTarget() != null) {
            this.target = this.entity.getAttackTarget();
        }

        if (this.target == null) {
            return false;
        }

        if (this.lastTargetUUID == null || !this.lastTargetUUID.equals(this.target.getUniqueID())) {
            this.newLookTarget = true;
            this.lastTargetUUID = this.target.getUniqueID();
            this.newTarget();
        }

        return !this.isAlreadyLookingOnTarget();
    }

    public boolean shouldContinueExecuting() {
        if (this.isAlreadyLookingOnTarget()) {
            return false;
        }

        if (!this.target.isAlive()) {
            return false;
        } else if (this.entity.getDistanceSq(this.target) > (this.maxDistance * this.maxDistance)) {
            return false;
        } else {
            return this.ticksForAnimation > 0;
        }
    }

    public void startExecuting() {
        if (AnimationStateListener.getAnimationState(this.entity) == AnimationState.DEFAULT) {
            this.ticksForAnimation = 10;
        } else {
            this.ticksForAnimation = 5;
        }

        this.animationStarted = false;
    }

    public void resetTask() {
        this.target = null;
    }

    public void tick() {
        this.animationStarted = true;
        this.entity.getLookController().func_220679_a(this.target.posX, this.target.posY + (double) this.target.getEyeHeight(), this.target.posZ);
        --this.ticksForAnimation;

        if (this.newLookTarget) {
            this.newLookTarget = false;
        }
    }

    public boolean isAlreadyLookingOnTarget() {
        if (this.newLookTarget) {
            return false;
        }

        if (!this.animationStarted) {
            return false;
        }

        return this.ticksForAnimation == 0 && this.entity.rotationYawHead == this.entity.renderYawOffset;
    }

    private void newTarget() {
        E33.internalEventBus.post(new NewTargetEvent(this.entity, this.entity.getAttackTarget()));
    }
}
