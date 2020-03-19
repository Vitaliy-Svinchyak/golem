package com.e33.client.animation.animator;

import com.e33.client.animation.animated.item.AimedWeaponPosition;
import com.e33.client.animation.animated.item.DefaultWeaponPosition;
import com.e33.client.detail.item.Rotation;
import com.e33.client.detail.item.Translation;
import com.e33.client.animation.animationProgression.AnimationProgression;
import com.e33.client.animation.animationProgression.AnimationProgressionBuilder;
import com.e33.client.detail.AnimationState;
import com.e33.client.listener.AnimationStateListener;
import com.e33.entity.ShootyEntity;
import com.e33.item.ItemDangerousStick;
import com.e33.util.Helper;
import com.google.common.collect.Lists;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.stream.Collectors;

public class ShootyWeaponAnimator {
    private final static Logger LOGGER = LogManager.getLogger();

    private List<AnimationProgression> animations = Lists.newArrayList();
    private AnimationState lastAnimationState = AnimationState.DEFAULT;
    private boolean animationComplete = true;

    public boolean isAnimationComplete() {
        return this.animationComplete;
    }

    public void animate(ShootyEntity entity) {
        ItemStack itemstack = entity.getHeldItemMainhand();
        if (itemstack.isEmpty()) {
            return;
        }

        Item item = itemstack.getItem();

        if (!item.toString().equals(ItemDangerousStick.registryName)) {
            return;
        }

        AnimationState animationState = AnimationStateListener.getAnimationState(entity);
        if (this.lastAnimationState == animationState) {
            this.animateAllProgressions(entity, itemstack);

            return;
        }

        this.animationComplete = false;
        this.animations = Lists.newArrayList();

        switch (animationState) {
            case DEFAULT:
                this.animateDefaultPose();
                break;
            case AIM:
                this.animateAiming();
                break;
        }

        this.lastAnimationState = animationState;
        this.animateAllProgressions(entity, itemstack);
    }

    private void animateAllProgressions(ShootyEntity entity, ItemStack itemstack) {
        boolean animationComplete = true;
        GlStateManager.color3f(1.0F, 1.0F, 1.0F);
        GlStateManager.pushMatrix();

        for (AnimationProgression animation : this.animations) {
            boolean animationResult = animation.makeProgress();

            if (animationResult) {
                animationComplete = false;
            }
        }

        Minecraft.getInstance().getFirstPersonRenderer().renderItem(entity, itemstack, ItemCameraTransforms.TransformType.THIRD_PERSON_RIGHT_HAND);
        GlStateManager.popMatrix();
        this.animationComplete = animationComplete;
    }

    private void animateDefaultPose() {
        List<AnimationProgression> animations = this.createAimingAnimation();
        animations.stream()
                .map(animation -> animation.reverse())
                .collect(Collectors.toList());

        this.animations = Helper.concatLists(this.animations, animations);
    }

    private void animateAiming() {
        List<AnimationProgression> animations = this.createAimingAnimation();

        this.animations = Helper.concatLists(this.animations, animations);
    }

    private List<AnimationProgression> createAimingAnimation() {
        return this.createAnimation(DefaultWeaponPosition.getTranslations(), DefaultWeaponPosition.getRotations(), AimedWeaponPosition.getTranslations(), AimedWeaponPosition.getRotations(), 20);
    }

    private List<AnimationProgression> createAnimation(List<Translation> fromTranslations, List<Rotation> fromRotations, List<Translation> toTranslations, List<Rotation> toRotations, int ticks) {
        List<AnimationProgression> animations = Lists.newArrayList();

        for (int i = 0; i < fromTranslations.size(); i++) {
            Translation fromTranslation = fromTranslations.get(i);
            Translation toTranslation = toTranslations.get(i);
            animations.add(AnimationProgressionBuilder.translateItem(fromTranslation.x, fromTranslation.y, fromTranslation.z, toTranslation.x, toTranslation.y, toTranslation.z, ticks));
        }

        for (int i = 0; i < fromRotations.size(); i++) {
            Rotation fromRotation = fromRotations.get(i);
            Rotation toRotation = toRotations.get(i);
            animations.add(AnimationProgressionBuilder.rotateItem(fromRotation.angle, toRotation.angle, ticks, fromRotation.x, fromRotation.y, fromRotation.z));
        }

        return animations;
    }

}
