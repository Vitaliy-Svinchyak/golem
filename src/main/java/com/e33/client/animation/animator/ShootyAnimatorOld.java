package com.e33.client.animation.animator;

import com.e33.client.animation.animated.model.ShootyModelAimed;
import com.e33.client.animation.progression.AnimationProgression;
import com.e33.client.animation.progression.AnimationProgressionBuilder;
import com.e33.client.detail.AnimationState;
import com.e33.client.detail.UniqueAnimationState;
import com.e33.client.detail.modelBox.ModelBoxWithParameters;
import com.e33.client.listener.AnimationStateListener;
import com.e33.client.model.DynamicAnimationInterface;
import com.e33.client.model.ShootyModel;
import com.e33.entity.ShootyEntity;
import com.e33.util.Helper;
import com.google.common.collect.Lists;
import net.minecraft.client.renderer.entity.model.RendererModel;
import net.minecraft.client.renderer.model.ModelBox;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class ShootyAnimatorOld<T extends ShootyEntity> {
    private final static Logger LOGGER = LogManager.getLogger();

    private final ShootyModel model;
    private List<AnimationProgression> animations = Lists.newArrayList();
    private UniqueAnimationState lastAnimationState = AnimationStateListener.getDefaultUniqueAnimationState();

    public ShootyAnimatorOld(ShootyModel model) {
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
            this.animateAllProgressions();
        } else if (animationState.state == AnimationState.SHOT) {
            this.renderAimed();
        }

        // New animation requested
        if (!this.lastAnimationState.equals(animationState)) {
            switch (animationState.state) {
                case DEFAULT:
                    this.animateDefaultPose();
                    break;
                case AIM:
                    this.animateAiming();
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
    }

    private void renderCurrentPose(T entity) {
        this.log("renderCurrentPose ");
        AnimationState animationState = AnimationStateListener.getAnimationState(entity);

        switch (animationState) {
            case DEFAULT:
                this.renderDefaultPose();
                break;
            case SHOT:
            case AIM:
                this.renderAimed();
                break;
        }
    }

    private void renderAimed() {
        this.log("renderAimed");
        List<AnimationProgression> animations = this.createAnimation(new ShootyModel(), new ShootyModelAimed(), 1);

        this.animations = Helper.concatLists(this.animations, animations);
    }

    private void renderDefaultPose() {
        this.log("renderDefaultPose");
        List<AnimationProgression> animations = this.createAnimation(new ShootyModelAimed(), new ShootyModel(), 1);

        this.animations = Helper.concatLists(this.animations, animations);
    }

    private void animateDefaultPose() {
        this.log("animateDefaultPose");
        List<AnimationProgression> animations = this.createAimingAnimation(20);
        animations.stream()
                .map(animation -> animation.reverse())
                .collect(Collectors.toList());

        this.animations = Helper.concatLists(this.animations, animations);
    }

    private void animateAiming() {
        this.log("animateAiming");
        List<AnimationProgression> animations = this.createAimingAnimation(10);

        this.animations = Helper.concatLists(this.animations, animations);
    }

    private void animateShot() {
        this.log("animateShot");
    }

    private List<AnimationProgression> createAimingAnimation(int ticksForAnimation) {
        return this.createAnimation(new ShootyModel(), new ShootyModelAimed(), ticksForAnimation);
    }

    private List<AnimationProgression> createAnimation(DynamicAnimationInterface from, DynamicAnimationInterface to, int ticks) {
        RendererModel fromModel = from.getMainRendererModel();
        RendererModel toModel = to.getMainRendererModel();
        RendererModel entityModel = this.model.getMainRendererModel();

        return this.getAnimatedChanges(fromModel, toModel, entityModel, ticks);
    }

    private List<AnimationProgression> getAnimatedChanges(RendererModel fromModel, RendererModel toModel, RendererModel entityModel, int ticks) {
        List<AnimationProgression> animations = Lists.newArrayList();

        if (AnimationProgressionBuilder.angleDiffers(fromModel, toModel)) {
            animations.add(AnimationProgressionBuilder.angle(fromModel, toModel, ticks, entityModel));
        }

        if (AnimationProgressionBuilder.pointDiffers(fromModel, toModel)) {
            animations.add(AnimationProgressionBuilder.point(fromModel, toModel, ticks, entityModel));
        }

        List<ModelBox> fromCubes = fromModel.cubeList;
        List<ModelBox> toCubes = toModel.cubeList;

        for (int i = 0; i < fromCubes.size(); i++) {
            ModelBoxWithParameters fromCube = (ModelBoxWithParameters) fromCubes.get(i);
            ModelBoxWithParameters toCube = (ModelBoxWithParameters) toCubes.get(i);

            if (AnimationProgressionBuilder.cubeDiffers(fromCube.parameters, toCube.parameters)) {
                animations.add(
                        AnimationProgressionBuilder.modelBox(ticks, entityModel, i, fromCube.parameters, toCube.parameters)
                );
            }
        }

        List<RendererModel> fromChildModels = fromModel.childModels;
        List<RendererModel> toChildModels = toModel.childModels;
        List<RendererModel> entityChildModels = entityModel.childModels;

        if (fromChildModels != null) {
            for (int i = 0; i < fromChildModels.size(); i++) {
                animations = Helper.concatLists(
                        animations,
                        this.getAnimatedChanges(fromChildModels.get(i), toChildModels.get(i), entityChildModels.get(i), ticks)
                );
            }
        }

        return animations;
    }

    private void log(String message) {
//        LOGGER.info(message);
    }
}
