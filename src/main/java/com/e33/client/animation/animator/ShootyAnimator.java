package com.e33.client.animation.animator;

import com.e33.client.animation.animation.entity.AimingAnimation;
import com.e33.client.animation.animation.Animation;
import com.e33.client.detail.UniqueAnimationState;
import com.e33.client.model.ShootyModel;
import com.e33.client.detail.AnimationState;
import com.e33.client.listener.AnimationStateListener;
import com.e33.entity.ShootyEntity;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class ShootyAnimator<T extends ShootyEntity> {
    private final static Logger LOGGER = LogManager.getLogger();

    private final ShootyModel model;
    private List<Animation> animations = Lists.newArrayList();
    private UniqueAnimationState lastAnimationState = AnimationStateListener.getDefaultUniqueAnimationState();
    private Map<Class, Animation> animationCache = Maps.newHashMap();

    public ShootyAnimator(ShootyModel model) {
        this.model = model;
    }

    public boolean isAnimationComplete() {
        return this.animations.size() == 0;
    }

    public void setRotationAngles(ShootyEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale) {

    }

    public void animate(T entity, float limbSwing, float limbSwingAmount, float partialTick) {
        this.log("have " + this.animations.size() + " animations");
        UniqueAnimationState animationState = AnimationStateListener.getUniqueAnimationState(entity);

        if (this.isAnimationComplete() && this.lastAnimationState.equals(animationState)) {
            this.renderCurrentPose(entity);
        } else if (animationState.state == AnimationState.SHOT) {
            this.renderAimed(entity);
        }

        // New animation requested
        if (!this.lastAnimationState.equals(animationState)) {
            switch (animationState.state) {
                case DEFAULT:
                    this.animateDefaultPose(entity);
                    break;
                case AIM:
                    this.animateAiming(entity);
                    break;
                case SHOT:
                    this.animateShot();
                    break;
                default:
                    LOGGER.error("no animation");
                    break;
            }
        }

        this.lastAnimationState = animationState;
        this.animateAllProgressions();
    }

    private void animateAllProgressions() {
        this.log("animateAllProgressions " + this.animations.size());
        List<Integer> animationsToRemove = Lists.newArrayList();

        for (int i = 0; i < this.animations.size(); i++) {
            Animation animation = this.animations.get(i);
            boolean animationResult = animation.animate();

            if (!animationResult) {
                animationsToRemove.add(i);
            }
        }

        Collections.reverse(animationsToRemove);
        for (Integer key : animationsToRemove) {
            this.animations.remove(key.intValue());
        }
    }

    private void renderCurrentPose(T entity) {
        this.log("renderCurrentPose");
        AnimationState animationState = AnimationStateListener.getAnimationState(entity);

        switch (animationState) {
            case DEFAULT:
                this.renderDefaultPose(entity);
                break;
            case SHOT:
            case AIM:
                this.renderAimed(entity);
                break;
        }
    }

    private void renderAimed(T entity) {
        this.log("renderAimed");
        Animation aimingAnimation = this.createAimingAnimation(entity).lastFrame().create();

        this.animations.add(aimingAnimation);
    }

    private void renderDefaultPose(T entity) {
        this.log("renderDefaultPose");
        Animation aimingAnimation = this.createAimingAnimation(entity).firstFrame().create();

        this.animations.add(aimingAnimation);
    }

    private void animateDefaultPose(T entity) {
        this.log("animateDefaultPose");
        Animation aimingAnimation = this.createAimingAnimation(entity).reset().setReverse(true).create();

        this.animations.add(aimingAnimation);
    }

    private void animateAiming(T entity) {
        this.log("animateAiming");
        Animation aimingAnimation = this.createAimingAnimation(entity).reset().setReverse(false).create();

        this.animations.add(aimingAnimation);
    }

    private void animateShot() {
        this.log("animateShot");
    }

    private Animation createAimingAnimation(T entity) {
        if (this.animationCache.get(AimingAnimation.class) == null) {
            this.animationCache.put(AimingAnimation.class, new AimingAnimation(this.model, entity));
        }

        return this.animationCache.get(AimingAnimation.class);
    }

    private void log(String message) {
//        LOGGER.info(message);
    }
}
