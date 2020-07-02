package e33.guardy.goal.move;

import e33.guardy.E33;
import e33.guardy.client.detail.AnimationState;
import e33.guardy.client.listener.AnimationStateListener;
import e33.guardy.entity.ShootyEntity;
import e33.guardy.event.MoveEvent;
import e33.guardy.event.NoActionEvent;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.world.World;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

abstract public class MovementGoal extends Goal {
    final static Logger LOGGER = LogManager.getLogger();

    protected World world;
    protected ShootyEntity shooty;

    public MovementGoal(ShootyEntity creatureIn) {
        this.shooty = creatureIn;
        this.world = this.shooty.getEntityWorld();
    }


    public void startExecuting() {
        this.move();
    }


    public void resetTask() {
        this.stop();
    }

    protected void move() {
        if (AnimationStateListener.getAnimationState(this.shooty) != AnimationState.MOVE) {
            E33.internalEventBus.post(new MoveEvent(this.shooty));
        }
    }

    protected void stop() {
        if (AnimationStateListener.getAnimationState(this.shooty) == AnimationState.MOVE) {
            E33.internalEventBus.post(new NoActionEvent(this.shooty));
        }
    }
}
