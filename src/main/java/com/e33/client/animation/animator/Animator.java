package com.e33.client.animation.animator;

import com.e33.client.animation.animation.Animation;
import com.e33.client.detail.AnimationState;
import com.e33.client.detail.UniqueAnimationState;
import com.e33.client.listener.AnimationStateListener;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import net.minecraft.entity.LivingEntity;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;

abstract public class Animator {
    final static Logger LOGGER = LogManager.getLogger();

    protected boolean log = false;
    private Map<UUID, List<Animation>> animations = Maps.newHashMap();
    private Map<UUID, UniqueAnimationState> lastAnimationState = Maps.newHashMap();
    private Map<UUID, Map<Class, Animation>> animationCache = Maps.newHashMap();

    abstract Animation createAimingAnimation(LivingEntity entity);

    abstract Animation createShotAnimation(LivingEntity entity);

    abstract Animation createMoveAnimation(LivingEntity entity);

    public void animate(LivingEntity entity) {
        UniqueAnimationState animationState = AnimationStateListener.getUniqueAnimationState(entity);
        this.log(" have " + this.getAnimationsFor(entity).size() + " animations " + animationState.state);

        if (this.isAnimationComplete(entity) && this.getLastAnimationState(entity).equals(animationState)) {
            this.renderCurrentPose(entity);
        } else if (animationState.state == AnimationState.SHOT) {
            this.renderAimed(entity);
        }

        // New animation requested
        if (!this.getLastAnimationState(entity).equals(animationState)) {
            switch (animationState.state) {
                case DEFAULT:
                    this.animateDefaultPose(entity);
                    break;
                case AIM:
                    this.animateAiming(entity);
                    break;
                case SHOT:
                    this.animateShot(entity);
                    break;
                case MOVE:
                    this.animateMove(entity);
                    break;
                default:
                    this.error("No animation!");
                    break;
            }
        }

        this.setLastAnimationState(entity, animationState);
        this.animateAll(entity);
    }

    void animateAll(LivingEntity entity) {
        this.log(" animateAll " + this.getAnimationsFor(entity).size());
        List<Integer> animationsToRemove = Lists.newArrayList();
        List<Animation> entityAnimations = this.getAnimationsFor(entity);

        for (int i = 0; i < entityAnimations.size(); i++) {
            Animation animation = entityAnimations.get(i);
            boolean animationComplete = animation.animate();

            if (animationComplete) {
//                this.log("animation finished " + animation.getClass());
                animationsToRemove.add(i);
            }
        }

        Collections.reverse(animationsToRemove);
        for (Integer key : animationsToRemove) {
            entityAnimations.remove(key.intValue());
        }
    }

    void renderCurrentPose(LivingEntity entity) {
        this.log("renderCurrentPose");

        switch (AnimationStateListener.getAnimationState(entity)) {
            case DEFAULT:
                this.renderDefaultPose(entity);
                break;
            case SHOT:
            case AIM:
                this.renderAimed(entity);
                break;
            default:
                this.error("No position! " + AnimationStateListener.getAnimationState(entity));
                break;
        }
    }

    void renderAimed(LivingEntity entity) {
        this.log(" renderAimed");
        Animation animation = this.createAimingAnimation(entity).lastFrame().create();

        this.addAnimation(entity, animation);
    }

    void renderDefaultPose(LivingEntity entity) {
        this.log(" renderDefaultPose");
        Animation animation = this.createAimingAnimation(entity).firstFrame().create();

        this.addAnimation(entity, animation);
    }

    void animateDefaultPose(LivingEntity entity) {
        this.log(" animateDefaultPose");
        Animation animation;

        LOGGER.info("it was " + this.getLastAnimationState(entity).state);
        if (this.getLastAnimationState(entity).state == AnimationState.AIM) {
            animation = this.createAimingAnimation(entity).reset().setReverse(true).create();
        } else {
            animation = this.createMoveAnimation(entity)
                    .reset()
                    .setReverse(true)
                    .create();
        }

        this.addAnimation(entity, animation);
    }

    void animateAiming(LivingEntity entity) {
        this.log(" animateAiming");
        Animation animation = this.createAimingAnimation(entity).reset().create();

        this.addAnimation(entity, animation);
    }

    void animateShot(LivingEntity entity) {
        this.log(" animateShot");
        Animation animation = this.createShotAnimation(entity).reset().create();

        this.addAnimation(entity, animation);
    }

    void animateMove(LivingEntity entity) {
        this.log(" animateMove");
        Animation animation = this.createMoveAnimation(entity).reset().setEndless(true).create();

        this.addAnimation(entity, animation);
    }

    List<Animation> getAnimationsFor(LivingEntity entity) {
        if (!this.animations.containsKey(entity.getUniqueID())) {
            this.animations.put(entity.getUniqueID(), Lists.newArrayList());
        }

        return this.animations.get(entity.getUniqueID());
    }

    UniqueAnimationState getLastAnimationState(LivingEntity entity) {
        if (this.lastAnimationState.get(entity.getUniqueID()) == null) {
            this.lastAnimationState.put(entity.getUniqueID(), AnimationStateListener.getUniqueAnimationState(entity));
        }

        return this.lastAnimationState.get(entity.getUniqueID());
    }

    void setLastAnimationState(LivingEntity entity, UniqueAnimationState state) {
        this.lastAnimationState.put(entity.getUniqueID(), state);
    }

    boolean addAnimation(LivingEntity entity, Animation animation) {
        return this.getAnimationsFor(entity).add(animation);
    }

    boolean isAnimationComplete(LivingEntity entity) {
        return this.getAnimationsFor(entity).size() == 0;
    }

    Map<Class, Animation> getAnimationCacheFor(LivingEntity entity) {
        if (this.animationCache.get(entity.getUniqueID()) == null) {
            this.animationCache.put(entity.getUniqueID(), Maps.newHashMap());
        }

        return this.animationCache.get(entity.getUniqueID());
    }

    void log(String message) {
        if (this.log) {
            LOGGER.info(message);
        }
    }

    void error(String message) {
        if (this.log) {
            LOGGER.info(message);
        }
    }
}
