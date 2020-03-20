package com.e33.client.listener;

import com.e33.client.detail.AnimationState;
import com.e33.client.detail.UniqueAnimationState;
import com.e33.event.NewTargetEvent;
import com.e33.event.NoTargetEvent;
import com.e33.event.ShotEvent;
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
        bus.addListener(AnimationStateListener::onNoTarget);
        bus.addListener(AnimationStateListener::onShot);
    }

    private static void onNewTarget(NewTargetEvent event) {
        animationMap.remove(event.getCreature().getUniqueID());
        animationMap.put(event.getCreature().getUniqueID(), new UniqueAnimationState(AnimationState.AIM));
        eventMap.put(event.getCreature().getUniqueID(), event);
    }

    private static void onNoTarget(NoTargetEvent event) {
        animationMap.remove(event.getCreature().getUniqueID());
        animationMap.put(event.getCreature().getUniqueID(), new UniqueAnimationState(AnimationState.DEFAULT));
        eventMap.put(event.getCreature().getUniqueID(), event);
    }

    private static void onShot(ShotEvent event) {
        animationMap.remove(event.getCreature().getUniqueID());
        animationMap.put(event.getCreature().getUniqueID(), new UniqueAnimationState(AnimationState.SHOT));
        eventMap.put(event.getCreature().getUniqueID(), event);
    }

    @Nullable
    public static Event getEvent(LivingEntity creature) {
        return AnimationStateListener.eventMap.get(creature.getUniqueID());
    }

    public static AnimationState getAnimationState(LivingEntity creature) {
        UniqueAnimationState savedState = AnimationStateListener.animationMap.get(creature.getUniqueID());
        if (savedState != null) {
            return savedState.state;
        }

        animationMap.put(creature.getUniqueID(), getDefaultUniqueAnimationState());

        return AnimationState.DEFAULT;
    }

    public static UniqueAnimationState getUniqueAnimationState(LivingEntity creature) {
        UniqueAnimationState savedState = AnimationStateListener.animationMap.get(creature.getUniqueID());
        if (savedState != null) {
            return savedState;
        }

        animationMap.put(creature.getUniqueID(), getDefaultUniqueAnimationState());

        return new UniqueAnimationState(AnimationState.DEFAULT);
    }

    public static UniqueAnimationState getDefaultUniqueAnimationState() {
        return new UniqueAnimationState(AnimationState.DEFAULT);
    }
}
