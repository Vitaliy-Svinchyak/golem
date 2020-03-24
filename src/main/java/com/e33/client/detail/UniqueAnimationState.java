package com.e33.client.detail;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.UUID;

public class UniqueAnimationState {
    public final static Logger LOGGER = LogManager.getLogger();

    public final AnimationState state;
    private final UUID id;

    public UniqueAnimationState(AnimationState state) {
        this.state = state;
        this.id = UUID.randomUUID();
    }

    public boolean equals(UniqueAnimationState uniqueAnimationState) {
        return this.state == uniqueAnimationState.state && this.id.equals(uniqueAnimationState.id);
    }
}
