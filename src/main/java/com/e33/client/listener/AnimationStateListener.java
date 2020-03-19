package com.e33.client.listener;

import com.e33.client.detail.AnimationState;
import com.e33.event.NewTargetEvent;
import com.e33.event.NoTargetEvent;
import net.minecraft.entity.LivingEntity;
import net.minecraftforge.eventbus.api.IEventBus;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class AnimationStateListener {

    private static final Map<UUID, AnimationState> animationMap = new HashMap<>();

    public static void setup(IEventBus bus) {
        bus.addListener(AnimationStateListener::onNewTarget);
        bus.addListener(AnimationStateListener::onNoTarget);
    }

    private static void onNewTarget(NewTargetEvent event) {
        animationMap.remove(event.getCreature().getUniqueID());
        animationMap.put(event.getCreature().getUniqueID(), AnimationState.AIM);
    }

    private static void onNoTarget(NoTargetEvent event) {
        animationMap.remove(event.getCreature().getUniqueID());
        animationMap.put(event.getCreature().getUniqueID(), AnimationState.DEFAULT);
    }

    public static AnimationState getAnimationState(LivingEntity creature) {
        AnimationState savedState = AnimationStateListener.animationMap.get(creature.getUniqueID());
        if (savedState != null) {
            return savedState;
        }

        return AnimationState.DEFAULT;
    }
}
