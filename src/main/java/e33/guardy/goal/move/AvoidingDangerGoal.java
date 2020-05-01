package e33.guardy.goal.move;

import e33.guardy.E33;
import e33.guardy.client.detail.AnimationState;
import e33.guardy.client.listener.AnimationStateListener;
import e33.guardy.entity.ShootyEntity;
import e33.guardy.event.MoveEvent;
import e33.guardy.event.NoActionEvent;
import e33.guardy.pathfinding.PathBuilder;
import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.EntityPredicate;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.RandomPositionGenerator;
import net.minecraft.entity.ai.goal.RandomWalkingGoal;
import net.minecraft.entity.monster.SpiderEntity;
import net.minecraft.entity.monster.ZombieEntity;
import net.minecraft.pathfinding.Path;
import net.minecraft.util.EntityPredicates;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nullable;
import java.util.List;
import java.util.stream.Collectors;

public class AvoidingDangerGoal extends RandomWalkingGoal {
    private World world;
    private Path cachedPath = null;
    private PathBuilder pathBuilder;
    private ShootyEntity shooty;

    private final static Logger LOGGER = LogManager.getLogger();

    public AvoidingDangerGoal(ShootyEntity creatureIn, double speedIn) {
        super(creatureIn, speedIn, 1);

        this.shooty = creatureIn;
        this.world = this.creature.getEntityWorld();
    }

    public boolean shouldExecute() {
        if (this.pathBuilder == null) {
            this.pathBuilder = this.shooty.pathBuilder;
        }

        if (this.creature.isBeingRidden()) {
            return false;
        } else {
            LOGGER.info(this.pathBuilder);
            this.cachedPath = this.pathBuilder.getPath(
                    this.world.getEntitiesWithinAABB(SpiderEntity.class, this.creature.getBoundingBox().grow(24), EntityPredicates.NOT_SPECTATING)
                            .stream().filter(LivingEntity::isAlive).collect(Collectors.toList())
            );

            if (this.cachedPath != null) {
                this.move();
            } else {
                this.stop();
            }

            return this.cachedPath != null;
        }
    }


    public boolean shouldContinueExecuting() {
        return !this.creature.getNavigator().noPath();
    }

    public void startExecuting() {
        this.creature.getNavigator().setPath(this.cachedPath, this.speed);
    }

    public boolean isPreemptible() {
        return true;
    }

    public void resetTask() {
        this.cachedPath = null;
        this.creature.getNavigator().setPath(null, this.speed);
    }

    private void move() {
        E33.internalEventBus.post(new MoveEvent(this.creature));
    }

    private void stop() {
        if (AnimationStateListener.getAnimationState(this.creature) == AnimationState.MOVE) {
            E33.internalEventBus.post(new NoActionEvent(this.creature));
        }
    }
}