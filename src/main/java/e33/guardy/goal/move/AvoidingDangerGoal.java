package e33.guardy.goal.move;

import e33.guardy.E33;
import e33.guardy.client.detail.AnimationState;
import e33.guardy.client.listener.AnimationStateListener;
import e33.guardy.entity.ShootyEntity;
import e33.guardy.event.MoveEvent;
import e33.guardy.event.NoActionEvent;
import e33.guardy.pathfinding.PathBuilder;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.goal.RandomWalkingGoal;
import net.minecraft.entity.monster.SpiderEntity;
import net.minecraft.util.EntityPredicates;
import net.minecraft.world.World;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

public class AvoidingDangerGoal extends RandomWalkingGoal {
    private World world;
    private PathBuilder pathBuilder;
    private ShootyEntity shooty;

    private final static Logger LOGGER = LogManager.getLogger();

    public AvoidingDangerGoal(ShootyEntity creatureIn, double speedIn) {
        super(creatureIn, speedIn, 1);

        this.shooty = creatureIn;
        this.world = this.creature.getEntityWorld();
    }

    public boolean shouldExecute() {
        if (this.creature.isBeingRidden()) {
            return false;
        }

        return this.enemiesAreTooClose();
    }

    protected List<MobEntity> getNearestEnemies(int range) {
        return this.world.getEntitiesWithinAABB(SpiderEntity.class, this.creature.getBoundingBox().grow(range), EntityPredicates.NOT_SPECTATING)
                .stream().filter(LivingEntity::isAlive).collect(Collectors.toList());
    }

    protected boolean enemiesAreTooClose() {
        List<MobEntity> enemies = this.getNearestEnemies(8);

        for (MobEntity enemy : enemies) {
            if (this.creature.getDistanceSq(enemy) <= 50F) {
                return true;
            }
        }

        return false;
    }

    public boolean shouldContinueExecuting() {
        boolean continueExecuting = this.enemiesAreTooClose();

        if (continueExecuting && this.creature.getNavigator().noPath()) {
            this.createPath();
        }

        return continueExecuting;
    }

    protected void createPath() {
        Instant start = Instant.now();
        this.creature.getNavigator().setPath(this.pathBuilder.getPath(this.getNearestEnemies(25)), this.speed);
        Instant end = Instant.now();
        LOGGER.info("createPath time: " + Duration.between(start, end));
    }

    public void startExecuting() {
        if (this.pathBuilder == null) {
            this.pathBuilder = this.shooty.pathBuilder;
        }

        this.move();
        this.createPath();
    }

    public boolean isPreemptible() {
        return false;
    }

    public void resetTask() {
        this.stop();
        this.creature.getNavigator().clearPath();
    }

    private void move() {
        LOGGER.info("move");
        E33.internalEventBus.post(new MoveEvent(this.creature));
    }

    private void stop() {
        LOGGER.info("stop");
        if (AnimationStateListener.getAnimationState(this.creature) == AnimationState.MOVE) {
            E33.internalEventBus.post(new NoActionEvent(this.creature));
        }
    }
}