package com.e33.client.animation.animation.entity.move;

import com.e33.client.animation.animated.model.move.ShootyMove6;
import com.e33.client.animation.animated.model.move.ShootyMove7;
import com.e33.client.animation.animated.model.move.ShootyMove8;
import com.e33.client.animation.animation.Animation;
import com.e33.client.animation.progression.AnimationProgression;
import com.e33.client.model.DynamicAnimationInterface;
import net.minecraft.client.renderer.entity.model.RendererModel;
import net.minecraft.entity.LivingEntity;

import java.util.List;

public class MoveAnimation8 extends Animation {
    public MoveAnimation8(DynamicAnimationInterface model, LivingEntity entity) {
        super(model, entity);
    }

    @Override
    protected List<AnimationProgression> createNormalAnimation() {
        DynamicAnimationInterface from = new ShootyMove7<>();
        DynamicAnimationInterface to1 = new ShootyMove8<>();
        RendererModel fromModel = from.getMainRendererModel();
        RendererModel toModel = to1.getMainRendererModel();
        RendererModel entityModel = this.model.getMainRendererModel();

        return this.getAnimatedChangesForEntity(fromModel, toModel, entityModel, 10);
    }

    @Override
    protected List<AnimationProgression> createReversedAnimation() {
        return null;
    }
}
