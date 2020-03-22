package com.e33.client.animation.animation.entity;

import com.e33.client.animation.animation.Animation;
import com.e33.client.animation.progression.AnimationProgression;
import com.e33.client.model.DynamicAnimationInterface;
import net.minecraft.entity.LivingEntity;

import java.util.List;

public class ShootingAnimation extends Animation {

    protected ShootingAnimation(DynamicAnimationInterface model, LivingEntity entity) {
        super(model, entity);
    }

    protected List<AnimationProgression> createNormalAnimation() {
        return null;
    }

    protected List<AnimationProgression> createReversedAnimation() {
        return null;
    }
}
