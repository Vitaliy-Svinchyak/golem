package com.e33.client.animation.animator;

import com.e33.client.animation.animated.item.AimedWeaponPosition;
import com.e33.client.animation.animated.item.DefaultWeaponPosition;
import com.e33.client.detail.UniqueAnimationState;
import com.e33.client.detail.item.Rotation;
import com.e33.client.detail.item.Translation;
import com.e33.client.animation.animationProgression.AnimationProgression;
import com.e33.client.animation.animationProgression.AnimationProgressionBuilder;
import com.e33.client.detail.AnimationState;
import com.e33.client.listener.AnimationStateListener;
import com.e33.entity.ShootyEntity;
import com.e33.init.ParticleRegistry;
import com.e33.item.ItemDangerousStick;
import com.e33.util.Helper;
import com.google.common.collect.Lists;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.math.Vec3d;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class ShootyWeaponAnimator {
    private final static Logger LOGGER = LogManager.getLogger();

    private List<AnimationProgression> animations = Lists.newArrayList();
    private UniqueAnimationState lastAnimationState = AnimationStateListener.getDefaultUniqueAnimationState();

    public boolean isAnimationComplete() {
        return this.animations.size() == 0;
    }

    public void animate(ShootyEntity entity, ItemStack itemstack) {
        UniqueAnimationState animationState = AnimationStateListener.getUniqueAnimationState(entity);

        if (this.isAnimationComplete() && this.lastAnimationState.equals(animationState)) {
            this.renderCurrentPose(entity);
            return;
        } else if (animationState.state == AnimationState.SHOT) {
            this.renderAimed();
        }

        if (!this.lastAnimationState.equals(animationState)) {
            this.animations = Lists.newArrayList();

            switch (animationState.state) {
                case DEFAULT:
                    this.createDefaultPoseAnimations();
                    break;
                case AIM:
                    this.createAimAnimation();
                    break;
                case SHOT:
                    this.createShotAnimation(entity);
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
        GlStateManager.color3f(1.0F, 1.0F, 1.0F);
        GlStateManager.pushMatrix();
        List<Integer> animationsToRemove = Lists.newArrayList();

        for (int i = 0; i < this.animations.size(); i++) {
            AnimationProgression animation = this.animations.get(i);
            boolean animationResult = animation.makeProgress();

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

    private void createDefaultPoseAnimations() {
        List<AnimationProgression> animations = this.createAimingAnimation(20);
        animations.stream()
                .map(animation -> animation.reverse())
                .collect(Collectors.toList());

        this.animations = Helper.concatLists(this.animations, animations);
    }

    private void createAimAnimation() {
        List<AnimationProgression> animations = this.createAimingAnimation(10);

        this.animations = Helper.concatLists(this.animations, animations);
    }

    private void createShotAnimation(ShootyEntity entity) {
        Vec3d position = entity.getPositionVector();
        float radius = 1.35F;
        float angle = Math.abs(entity.rotationYaw);

        float y = (float) position.getY() + 1.1F;
        float x = (float) (Math.sin(angle) * radius + position.getX());
        float z = (float) (Math.cos(angle) * radius + position.getZ());

        this.animations.add(AnimationProgressionBuilder.particle(x, y, z, x, y, z, ParticleTypes.SMOKE, 2));
        this.animations.add(AnimationProgressionBuilder.particle(x, y, z, x, y, z, ParticleTypes.FLAME, 1));

        this.animations.add(AnimationProgressionBuilder.particle(x, y, z, x, y, z, ParticleRegistry.SLEEVE, 1));
    }

    private List<AnimationProgression> createAimingAnimation(int ticks) {
        return this.createAnimation(DefaultWeaponPosition.getTranslations(), DefaultWeaponPosition.getRotations(), AimedWeaponPosition.getTranslations(), AimedWeaponPosition.getRotations(), ticks);
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

    private void renderCurrentPose(ShootyEntity entityIn) {
        ItemStack itemstack = entityIn.getHeldItemMainhand();
        if (itemstack.isEmpty()) {
            return;
        }

        Item item = itemstack.getItem();

        if (!item.toString().equals(ItemDangerousStick.registryName)) {
            return;
        }

        GlStateManager.color3f(1.0F, 1.0F, 1.0F);
        GlStateManager.pushMatrix();
        switch (AnimationStateListener.getAnimationState(entityIn)) {
            case DEFAULT:
                this.renderDefaultPose();
                break;
            case SHOT:
            case AIM:
                this.renderAimed();
                break;
        }

        Minecraft.getInstance().getFirstPersonRenderer().renderItem(entityIn, itemstack, ItemCameraTransforms.TransformType.THIRD_PERSON_RIGHT_HAND);
        GlStateManager.popMatrix();
    }

    private void renderAimed() {
        this.renderPose(AimedWeaponPosition.getTranslations(), AimedWeaponPosition.getRotations());
    }

    private void renderDefaultPose() {
        this.renderPose(DefaultWeaponPosition.getTranslations(), DefaultWeaponPosition.getRotations());
    }

    private void renderPose(List<Translation> translations, List<Rotation> rotations) {
        for (Translation translation : translations) {
            GlStateManager.translatef(translation.x, translation.y, translation.z);
        }

        for (Rotation rotation : rotations) {
            GlStateManager.rotatef(rotation.angle, rotation.x, rotation.y, rotation.z);
        }
    }

    private void log(String message) {
//        LOGGER.info(message);
    }

}
