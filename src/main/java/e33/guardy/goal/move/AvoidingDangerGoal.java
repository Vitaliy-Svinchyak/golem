package e33.guardy.goal.move;

import e33.guardy.E33;
import e33.guardy.client.detail.AnimationState;
import e33.guardy.client.listener.AnimationStateListener;
import e33.guardy.debug.TimeMeter;
import e33.guardy.entity.ShootyEntity;
import e33.guardy.event.MoveEvent;
import e33.guardy.event.NoActionEvent;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.monster.SpiderEntity;
import net.minecraft.pathfinding.Path;
import net.minecraft.util.EntityPredicates;
import net.minecraft.world.World;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.stream.Collectors;

public class AvoidingDangerGoal extends Goal {
    private World world;
    private ShootyEntity shooty;

    private final static Logger LOGGER = LogManager.getLogger();

    public AvoidingDangerGoal(ShootyEntity creatureIn) {
        this.shooty = creatureIn;
        this.world = this.shooty.getEntityWorld();
    }

    public boolean shouldExecute() {
        if (this.shooty.isBeingRidden()) {
            return false;
        }

        return this.enemiesAreTooClose();
    }

    protected List<MobEntity> getNearestEnemies(int range) {
        return this.world.getEntitiesWithinAABB(SpiderEntity.class, this.shooty.getBoundingBox().grow(range), EntityPredicates.NOT_SPECTATING)
                .stream().filter(LivingEntity::isAlive).collect(Collectors.toList());
    }

    protected boolean enemiesAreTooClose() {
        return this.getNearestEnemies(8).size() > 0;
    }

    public boolean shouldContinueExecuting() {
        boolean continueExecuting = this.enemiesAreTooClose();

        if (continueExecuting && this.shooty.getNavigator().noPath()) {
            this.createPath();
        }

        return continueExecuting;
    }

    protected void createPath() {
        TimeMeter.moduleStart(TimeMeter.MODULE_PATH_BUILDING);
        this.world.getProfiler().startSection("pathfind_my");
        Path path = this.shooty.pathCreator.getSafePath(this.getNearestEnemies(25));
        this.shooty.getNavigator().setPath(path, this.shooty.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).getValue());
        this.world.getProfiler().endSection();
        TimeMeter.moduleEnd(TimeMeter.MODULE_PATH_BUILDING);
    }

    public void startExecuting() {
        this.move();
        this.createPath();
    }

    public boolean isPreemptible() {
        return false;
    }

    public void resetTask() {
        this.stop();
        this.shooty.getNavigator().clearPath();
    }

    private void move() {
        LOGGER.info("move");
        E33.internalEventBus.post(new MoveEvent(this.shooty));
    }

    private void stop() {
        LOGGER.info("stop");
        if (AnimationStateListener.getAnimationState(this.shooty) == AnimationState.MOVE) {
            E33.internalEventBus.post(new NoActionEvent(this.shooty));
        }
    }
}