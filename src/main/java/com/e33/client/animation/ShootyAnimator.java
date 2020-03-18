package com.e33.client.animation;

import com.e33.client.animation.animationProgression.AnimationProgression;
import com.e33.client.model.ShootyModel;
import com.e33.client.animation.animatedModels.ShootyModelAimed;
import com.e33.client.util.AnimationState;
import com.e33.client.util.AnimationStateListener;
import com.e33.client.util.ModelBoxParameters;
import com.e33.entity.ShootyEntity;
import com.google.common.collect.Lists;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ShootyAnimator<T extends ShootyEntity> {
    private final static Logger LOGGER = LogManager.getLogger();
    private final ShootyModel model;
    private List<AnimationProgression> animations = Lists.newArrayList();
    private AnimationState lastAnimationState = AnimationState.DEFAULT;

    public ShootyAnimator(ShootyModel model) {
        this.model = model;
    }

    public void setRotationAngles(ShootyEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale) {

    }

    public void setLivingAnimations(T entity, float limbSwing, float limbSwingAmount, float partialTick) {
        AnimationState animationState = AnimationStateListener.getAnimationState(entity);
        if (this.lastAnimationState == animationState) {
            for (AnimationProgression animation : this.animations) {
                animation.makeProgress();
            }

            return;
        }

        this.animations = Lists.newArrayList();
        switch (animationState) {
            case DEFAULT:
                this.animateDefaultPose();
                break;
            case AIM:
                this.animateAiming();
                break;
        }

        this.lastAnimationState = AnimationStateListener.getAnimationState(entity);
    }

    private void animateDefaultPose() {
        List<AnimationProgression> animations = this.createAimingAnimation(20);
        animations.stream()
                .map(animation -> animation.reverse())
                .collect(Collectors.toList());

        this.animations = this.concatLists(this.animations, animations);
    }

    private void animateAiming() {
        List<AnimationProgression> animations = this.createAimingAnimation(20);

        this.animations = this.concatLists(this.animations, animations);
    }

    private List<AnimationProgression> createAimingAnimation(int ticksForAnimation) {
        List<AnimationProgression> animations = Lists.newArrayList();
        ShootyModel from = new ShootyModel();
        ShootyModelAimed to = new ShootyModelAimed();

        animations.add(AnimationProgression.angle(from.shooty, to.shooty, ticksForAnimation, this.model.shooty));

        animations.add(AnimationProgression.point(from.right, to.right, ticksForAnimation, this.model.right));
        animations.add(AnimationProgression.angle(from.right, to.right, ticksForAnimation, this.model.right));

        animations.add(AnimationProgression.point(from.left, to.left, ticksForAnimation, this.model.left));
        animations.add(AnimationProgression.angle(from.left, to.left, ticksForAnimation, this.model.left));

        animations.add(AnimationProgression.angle(from.body, to.body, ticksForAnimation, this.model.body));

        animations.add(AnimationProgression.point(from.arms, to.arms, ticksForAnimation, this.model.arms));

        animations.add(AnimationProgression.point(from.right2, to.right2, ticksForAnimation, this.model.right2));

        animations.add(AnimationProgression.angle(from.shoulder, to.shoulder, ticksForAnimation, this.model.shoulder));

        animations.add(AnimationProgression.modelBox(ticksForAnimation, this.model.shoulder, 0,
                new ModelBoxParameters(from.shoulder, 24, 10, -6.2F, -19.4F, -6.5F, 3, 2, 4, 0.0F, false),
                new ModelBoxParameters(from.shoulder, 24, 10, -13.8F, -15.0F, -8.8F, 3, 2, 4, 0.0F, false)
        ));
        animations.add(AnimationProgression.modelBox(ticksForAnimation, this.model.shoulder, 1,
                new ModelBoxParameters(from.shoulder, 51, 1, -6.5F, -18.9F, -6.6F, 1, 1, 4, 0.0F, false),
                new ModelBoxParameters(from.shoulder, 51, 1, -14.1F, -14.5F, -8.9F, 1, 1, 4, 0.0F, false)
        ));
        animations.add(AnimationProgression.modelBox(ticksForAnimation, this.model.shoulder, 2,
                new ModelBoxParameters(from.shoulder, 8, 34, -5.5F, -19.7F, -6.6F, 2, 1, 4, 0.0F, false),
                new ModelBoxParameters(from.shoulder, 8, 34, -13.1F, -15.3F, -8.9F, 2, 1, 4, 0.0F, false)
        ));
        animations.add(AnimationProgression.modelBox(ticksForAnimation, this.model.shoulder, 3,
                new ModelBoxParameters(from.shoulder, 37, 21, -5.9F, -17.9F, -6.0F, 2, 3, 3, 0.0F, false),
                new ModelBoxParameters(from.shoulder, 37, 21, -13.5F, -13.5F, -8.3F, 2, 3, 3, 0.0F, false)
        ));
        animations.add(AnimationProgression.modelBox(ticksForAnimation, this.model.shoulder, 4,
                new ModelBoxParameters(from.shoulder, 44, 13, -5.91F, -15.9F, -5.5F, 2, 3, 2, 0.0F, false),
                new ModelBoxParameters(from.shoulder, 44, 13, -13.51F, -11.5F, -7.8F, 2, 3, 2, 0.0F, false)
        ));
        animations.add(AnimationProgression.modelBox(ticksForAnimation, this.model.shoulder, 5,
                new ModelBoxParameters(from.shoulder, 0, 47, -6.1F, -17.9F, -5.0F, 2, 5, 1, 0.0F, false),
                new ModelBoxParameters(from.shoulder, 0, 47, -13.7F, -13.5F, -7.3F, 2, 5, 1, 0.0F, false)
        ));
        animations.add(AnimationProgression.modelBox(ticksForAnimation, this.model.shoulder, 6,
                new ModelBoxParameters(from.shoulder, 34, 52, -6.2F, -16.9F, -6.1F, 1, 1, 3, 0.0F, false),
                new ModelBoxParameters(from.shoulder, 34, 52, -13.8F, -12.5F, -8.4F, 1, 1, 3, 0.0F, false)
        ));

        animations.add(AnimationProgression.angle(from.preshoulder, to.preshoulder, ticksForAnimation, this.model.preshoulder));
        animations.add(AnimationProgression.modelBox(ticksForAnimation, this.model.preshoulder, 0,
                new ModelBoxParameters(from.preshoulder, 17, 2, -6.5F, -13.9F, -2.4F, 2, 2, 6, 0.0F, false),
                new ModelBoxParameters(from.preshoulder, 17, 2, -7.5F, -14.9F, -1.7F, 2, 2, 6, 0.0F, false)
        ));
        animations.add(AnimationProgression.modelBox(ticksForAnimation, this.model.preshoulder, 1,
                new ModelBoxParameters(from.preshoulder, 9, 22, -6.8F, -13.4F, -1.4F, 2, 1, 5, 0.0F, false),
                new ModelBoxParameters(from.preshoulder, 9, 22, -7.8F, -14.4F, -1.6F, 2, 1, 5, 0.0F, false)
        ));

        animations.add(AnimationProgression.modelBox(ticksForAnimation, this.model.shoulder, 0,
                new ModelBoxParameters(from.cup3, 0, 2, -5.8F, -13.7F, -1.1F, 1, 1, 1, 0.0F, false),
                new ModelBoxParameters(from.cup3, 0, 2, -6.6F, -14.9F, -4.5F, 1, 1, 1, 0.0F, false)
        ));

        animations.add(AnimationProgression.angle(from.preshoulder2, to.preshoulder2, ticksForAnimation, this.model.preshoulder2));
        animations.add(AnimationProgression.modelBox(ticksForAnimation, this.model.preshoulder2, 0,
                new ModelBoxParameters(from.preshoulder2, 14, 14, -4.3F, -14.5F, -3.1F, 2, 2, 6, 0.0F, false),
                new ModelBoxParameters(from.preshoulder2, 14, 14, -6.4F, -13.6F, -10.3F, 2, 2, 6, 0.0F, false)
        ));
        animations.add(AnimationProgression.modelBox(ticksForAnimation, this.model.preshoulder2, 1,
                new ModelBoxParameters(from.preshoulder2, 0, 20, -4.0F, -14.0F, -2.1F, 2, 1, 5, 0.0F, false),
                new ModelBoxParameters(from.preshoulder2, 0, 20, -6.1F, -13.1F, -9.3F, 2, 1, 5, 0.0F, false)
        ));

        animations.add(AnimationProgression.modelBox(ticksForAnimation, this.model.cup4, 0,
                new ModelBoxParameters(from.cup4, 0, 0, -5.4F, -13.7F, -1.1F, 1, 1, 1, 0.0F, false),
                new ModelBoxParameters(from.cup4, 0, 0, -5.5F, -13.3F, -4.2F, 1, 1, 1, 0.0F, false)
        ));

        return animations;
    }

    private List<AnimationProgression> concatLists(List<AnimationProgression> list1, List<AnimationProgression> list2) {
        return Stream.concat(list1.stream(), list2.stream())
                .collect(Collectors.toList());
    }
}
