package e33.guardy.client.listener;

import e33.guardy.client.detail.AnimationState;
import e33.guardy.client.detail.UniqueAnimationState;
import e33.guardy.event.MoveEvent;
import e33.guardy.event.NewTargetEvent;
import e33.guardy.event.NoActionEvent;
import e33.guardy.event.ShotEvent;
import net.minecraft.entity.LivingEntity;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.IEventBus;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class AnimationStateListener {
    public final static Logger LOGGER = LogManager.getLogger();

    private static final Map<UUID, UniqueAnimationState> animationMap = new HashMap<>();
    private static final Map<UUID, Event> eventMap = new HashMap<>();

    public static void setup(IEventBus bus) {
        bus.addListener(AnimationStateListener::onNewTarget);
        bus.addListener(AnimationStateListener::onNoAction);
        bus.addListener(AnimationStateListener::onShot);
        bus.addListener(AnimationStateListener::onMove);
    }

    private static void onNewTarget(NewTargetEvent event) {
        if (getAnimationState(event.getCreature()) == AnimationState.AIM) {
            return;
        }

        animationMap.remove(event.getCreature().getUniqueID());
        animationMap.put(event.getCreature().getUniqueID(), new UniqueAnimationState(AnimationState.AIM));
        eventMap.put(event.getCreature().getUniqueID(), event);
    }

    private static void onNoAction(NoActionEvent event) {
        if (getAnimationState(event.getCreature()) == AnimationState.DEFAULT) {
            return;
        }

        animationMap.remove(event.getCreature().getUniqueID());
        animationMap.put(event.getCreature().getUniqueID(), new UniqueAnimationState(AnimationState.DEFAULT));
        eventMap.put(event.getCreature().getUniqueID(), event);
    }

    private static void onShot(ShotEvent event) {
        animationMap.remove(event.getCreature().getUniqueID());
        animationMap.put(event.getCreature().getUniqueID(), new UniqueAnimationState(AnimationState.SHOT));
        eventMap.put(event.getCreature().getUniqueID(), event);
    }

    private static void onMove(MoveEvent event) {
        animationMap.remove(event.getCreature().getUniqueID());
        animationMap.put(event.getCreature().getUniqueID(), new UniqueAnimationState(AnimationState.MOVE));
        eventMap.put(event.getCreature().getUniqueID(), event);
    }

    @Nullable
    public static Event getEvent(LivingEntity creature) {
        return AnimationStateListener.eventMap.get(creature.getUniqueID());
    }

    public static AnimationState getAnimationState(LivingEntity creature) {
        return getUniqueAnimationState(creature).state;
    }

    public static UniqueAnimationState getUniqueAnimationState(LivingEntity creature) {
        UniqueAnimationState savedState = animationMap.get(creature.getUniqueID());

        if (savedState == null) {
            animationMap.put(creature.getUniqueID(), getDefaultUniqueAnimationState());
        }

        return animationMap.get(creature.getUniqueID());
    }

    static UniqueAnimationState getDefaultUniqueAnimationState() {
        LOGGER.info("Creating new");
        return new UniqueAnimationState(AnimationState.DEFAULT);
    }
}
