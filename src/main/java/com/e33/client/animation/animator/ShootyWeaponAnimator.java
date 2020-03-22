package com.e33.client.animation.animator;

import com.e33.client.animation.animation.Animation;
import com.e33.client.animation.animation.item.AimingAnimation;
import com.e33.client.animation.animation.item.ShootingAnimation;
import com.e33.client.detail.UniqueAnimationState;
import com.e33.client.detail.AnimationState;
import com.e33.client.listener.AnimationStateListener;
import com.e33.entity.ShootyEntity;
import com.e33.item.ItemDangerousStick;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class ShootyWeaponAnimator {
    private final static Logger LOGGER = LogManager.getLogger();

    private List<Animation> animations = Lists.newArrayList();
    private UniqueAnimationState lastAnimationState = AnimationStateListener.getDefaultUniqueAnimationState();
    private Map<Class, Animation> animationCache = Maps.newHashMap();

    public boolean isAnimationComplete() {
        return this.animations.size() == 0;
    }

    public void animate(ShootyEntity entity, ItemStack itemstack) {
        this.log("have " + this.animations.size() + " animations");
        UniqueAnimationState animationState = AnimationStateListener.getUniqueAnimationState(entity);

        if (this.isAnimationComplete() && this.lastAnimationState.equals(animationState)) {
            this.renderCurrentPose(entity);
        } else if (animationState.state == AnimationState.SHOT) {
            this.renderAimed(entity);
        }

        if (!this.lastAnimationState.equals(animationState)) {
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
                default:
                    LOGGER.error("no animation");
                    break;
            }
        }

        this.lastAnimationState = animationState;
        this.animateAllProgressions(entity, itemstack);
    }

    private void animateAllProgressions(ShootyEntity entity, ItemStack itemstack) {
        this.log("animateAllProgressions " + this.animations.size());
        GlStateManager.color3f(1.0F, 1.0F, 1.0F);
        GlStateManager.pushMatrix();
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

        Minecraft.getInstance().getFirstPersonRenderer().renderItem(entity, itemstack, ItemCameraTransforms.TransformType.THIRD_PERSON_RIGHT_HAND);
        GlStateManager.popMatrix();
    }

    private void renderCurrentPose(ShootyEntity entity) {
        this.log("renderCurrentPose");
        ItemStack itemstack = entity.getHeldItemMainhand();
        if (itemstack.isEmpty()) {
            return;
        }

        Item item = itemstack.getItem();
        if (!item.toString().equals(ItemDangerousStick.registryName)) {
            return;
        }

        switch (AnimationStateListener.getAnimationState(entity)) {
            case DEFAULT:
                this.renderDefaultPose(entity);
                break;
            case SHOT:
            case AIM:
                this.renderAimed(entity);
                break;
        }
    }

    private void renderAimed(ShootyEntity entity) {
        this.log("renderAimed");
        Animation aimingAnimation = this.createAimingAnimation(entity).lastFrame().create();

        this.animations.add(aimingAnimation);
    }

    private void renderDefaultPose(ShootyEntity entity) {
        this.log("renderDefaultPose");
        Animation aimingAnimation = this.createAimingAnimation(entity).firstFrame().create();

        this.animations.add(aimingAnimation);
    }

    private void animateDefaultPose(ShootyEntity entity) {
        this.log("animateDefaultPose");
        Animation aimingAnimation = this.createAimingAnimation(entity).reset().setReverse(true).create();

        this.animations.add(aimingAnimation);
    }

    private void animateAiming(ShootyEntity entity) {
        this.log("animateAiming");
        Animation aimingAnimation = this.createAimingAnimation(entity).reset().setReverse(false).create();

        this.animations.add(aimingAnimation);
    }

    private void animateShot(ShootyEntity entity) {
        this.log("animateShot");
        Animation aimingAnimation = this.createShotAnimation(entity).reset().setReverse(false).create();

        this.animations.add(aimingAnimation);
    }

    private Animation createShotAnimation(ShootyEntity entity) {
        if (this.animationCache.get(ShootingAnimation.class) == null) {
            this.animationCache.put(ShootingAnimation.class, new ShootingAnimation(null, entity));
        }

        return this.animationCache.get(ShootingAnimation.class);
    }

    private Animation createAimingAnimation(ShootyEntity entity) {
        if (this.animationCache.get(AimingAnimation.class) == null) {
            this.animationCache.put(AimingAnimation.class, new AimingAnimation(null, entity));
        }

        return this.animationCache.get(AimingAnimation.class);
    }


    private void log(String message) {
        LOGGER.info(message);
    }

}
