package com.e33.client.animation.animation.entity;

import com.e33.client.animation.animated.model.ShootyModelAimed;
import com.e33.client.animation.animation.Animation;
import com.e33.client.animation.progression.AnimationProgression;
import com.e33.client.model.DynamicAnimationInterface;
import com.e33.client.model.ShootyModel;
import net.minecraft.client.renderer.entity.model.RendererModel;
import net.minecraft.entity.LivingEntity;

import java.util.List;

public class AimingAnimation extends Animation {

    public AimingAnimation(DynamicAnimationInterface model, LivingEntity entity) {
        super(model, entity);
    }

    protected List<AnimationProgression> createReversedAnimation() {
        DynamicAnimationInterface from = new ShootyModelAimed();
        DynamicAnimationInterface to = new ShootyModel();
        RendererModel fromModel = from.getMainRendererModel();
        RendererModel toModel = to.getMainRendererModel();
        RendererModel entityModel = this.model.getMainRendererModel();

        return this.getAnimatedChangesForEntity(fromModel, toModel, entityModel, 20);
    }

    protected List<AnimationProgression> createNormalAnimation() {
        DynamicAnimationInterface from = new ShootyModel();
        DynamicAnimationInterface to = new ShootyModelAimed();
        RendererModel fromModel = from.getMainRendererModel();
        RendererModel toModel = to.getMainRendererModel();
        RendererModel entityModel = this.model.getMainRendererModel();

        return this.getAnimatedChangesForEntity(fromModel, toModel, entityModel, 10);
    }
}
