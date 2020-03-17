package com.e33.client.util;

import com.e33.event.NewTargetEvent;
import com.e33.event.NoTargetEvent;
import net.minecraft.entity.LivingEntity;
import net.minecraftforge.eventbus.api.IEventBus;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class AnimationStateListener {

    private static final Map<UUID, AnimationState> map = new HashMap<>();

    public static void setup(IEventBus bus) {
        bus.addListener(AnimationStateListener::onNewTarget);
    }

    private static void onNewTarget(NewTargetEvent event) {
        AnimationStateListener.map.put(event.getCreature().getUniqueID(), AnimationState.AIM);
    }

    private static void onNoTarget(NoTargetEvent event) {
        AnimationStateListener.map.put(event.getCreature().getUniqueID(), AnimationState.DEFAULT);
    }

    public static AnimationState getAnimationState(LivingEntity creature) {
        AnimationState savedState = AnimationStateListener.map.get(creature.getUniqueID());
        if (savedState != null) {
            return savedState;
        }

        return AnimationState.DEFAULT;
    }
}
