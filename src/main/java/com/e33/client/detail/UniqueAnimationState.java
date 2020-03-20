package com.e33.client.detail;

import java.util.UUID;

public class UniqueAnimationState {
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
