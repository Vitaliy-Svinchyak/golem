package com.e33.client.animation.animation.entity;

import com.e33.client.animation.animation.Animation;
import com.e33.client.animation.animation.entity.move.*;
import com.e33.client.animation.progression.AnimationProgression;
import com.e33.client.model.DynamicAnimationInterface;
import com.google.common.collect.Lists;
import net.minecraft.entity.LivingEntity;

import java.util.List;

public class MoveAnimation extends Animation {

    public MoveAnimation(DynamicAnimationInterface model, LivingEntity entity) {
        super(model, entity);
        Animation a0_1 = new MoveAnimation1(model, entity);
        Animation a1_2 = new MoveAnimation2(model, entity);
        Animation a2_3 = new MoveAnimation3(model, entity);
        Animation a3_4 = new MoveAnimation4(model, entity);
        Animation a4_1 = new MoveAnimation5(model, entity);

        a0_1.then(a1_2);
        a1_2.then(a2_3);
        a2_3.then(a3_4);
        a3_4.then(a4_1);

        a0_1.setOneTime(true);
        this.then(a0_1);
        this.log = true;
        LOGGER.info("Animation created");
    }

    @Override
    protected List<AnimationProgression> createNormalAnimation() {
        return Lists.newArrayList();
    }

    @Override
    protected List<AnimationProgression> createReversedAnimation() {
        return Lists.newArrayList();
    }
}
