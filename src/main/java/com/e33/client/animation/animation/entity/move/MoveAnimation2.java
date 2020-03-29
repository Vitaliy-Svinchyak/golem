package com.e33.client.animation.animation.entity.move;

import com.e33.client.animation.animated.model.move.ShootyMove1;
import com.e33.client.animation.animated.model.move.ShootyMove2;
import com.e33.client.animation.animation.Animation;
import com.e33.client.animation.progression.AnimationProgression;
import com.e33.client.model.DynamicAnimationInterface;
import com.e33.client.model.ShootyModel;
import net.minecraft.client.renderer.entity.model.RendererModel;
import net.minecraft.entity.LivingEntity;

import java.util.List;

public class MoveAnimation2 extends Animation {
    public MoveAnimation2(DynamicAnimationInterface model, LivingEntity entity) {
        super(model, entity);
    }

    @Override
    protected List<AnimationProgression> createNormalAnimation() {
        DynamicAnimationInterface from = new ShootyMove1();
        DynamicAnimationInterface to1 = new ShootyMove2<>();
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
