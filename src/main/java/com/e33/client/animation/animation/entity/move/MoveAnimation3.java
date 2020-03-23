package com.e33.client.animation.animation.entity.move;

import com.e33.client.animation.animated.model.moving.ShootyModelMove2;
import com.e33.client.animation.animated.model.moving.ShootyModelMove3;
import com.e33.client.animation.animation.Animation;
import com.e33.client.animation.progression.AnimationProgression;
import com.e33.client.model.DynamicAnimationInterface;
import net.minecraft.client.renderer.entity.model.RendererModel;
import net.minecraft.entity.LivingEntity;

import java.util.List;

public class MoveAnimation3 extends Animation {
    public MoveAnimation3(DynamicAnimationInterface model, LivingEntity entity) {
        super(model, entity);
    }

    @Override
    protected List<AnimationProgression> createNormalAnimation() {
        DynamicAnimationInterface from = new ShootyModelMove2();
        DynamicAnimationInterface to1 = new ShootyModelMove3<>();
        RendererModel fromModel = from.getMainRendererModel();
        RendererModel toModel = to1.getMainRendererModel();
        RendererModel entityModel = this.model.getMainRendererModel();

        return this.getAnimatedChangesForEntity(fromModel, toModel, entityModel, 20);
    }

    @Override
    protected List<AnimationProgression> createReversedAnimation() {
        return null;
    }
}
