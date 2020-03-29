package com.e33.client.animation.animation.entity;

import com.e33.client.animation.animation.Animation;
import com.e33.client.animation.animation.entity.move.*;
import com.e33.client.animation.progression.AnimationProgression;
import com.e33.client.model.DynamicAnimationInterface;
import com.e33.client.model.ShootyModel;
import com.google.common.collect.Lists;
import net.minecraft.client.renderer.entity.model.RendererModel;
import net.minecraft.entity.LivingEntity;

import java.util.List;

public class MoveAnimation extends Animation {

    public MoveAnimation(DynamicAnimationInterface model, LivingEntity entity) {
        super(model, entity);
        Animation a0_1 = new MoveAnimation1(model, entity);
        Animation a1_2 = new MoveAnimation2(model, entity);
        Animation a2_3 = new MoveAnimation3(model, entity);
        Animation a3_4 = new MoveAnimation4(model, entity);
        Animation a4_5 = new MoveAnimation5(model, entity);
        Animation a5_6 = new MoveAnimation6(model, entity);
        Animation a6_7 = new MoveAnimation7(model, entity);
        Animation a7_8 = new MoveAnimation8(model, entity);
        Animation a8_1 = new MoveAnimation9(model, entity);

        a0_1.then(a1_2);
        a1_2.then(a2_3);
        a2_3.then(a3_4);
        a3_4.then(a4_5);
        a4_5.then(a5_6);
        a5_6.then(a6_7);
        a6_7.then(a7_8);
        a7_8.then(a8_1);

        a0_1.setOneTime(true);
        this.then(a0_1);
        this.log = true;
        LOGGER.info("Animation created");
    }

    public Animation create() {
        if (!this.isReversed()) {
            super.create();
        } else {
            this.createAnimation();
            this.childAnimation.finish();

            this.cachedReversedAnimations = null;
        }

        return this;
    }

    @Override
    protected List<AnimationProgression> createNormalAnimation() {
        return Lists.newArrayList();
    }

    @Override
    protected List<AnimationProgression> createReversedAnimation() {
        DynamicAnimationInterface from = this.model;
        DynamicAnimationInterface to = new ShootyModel();
        RendererModel fromModel = from.getMainRendererModel();
        RendererModel toModel = to.getMainRendererModel();
        RendererModel entityModel = this.model.getMainRendererModel();

        return this.getAnimatedChangesForEntity(fromModel, toModel, entityModel, 10);
    }
}
