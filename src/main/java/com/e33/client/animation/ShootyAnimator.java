package com.e33.client.animation;

import com.e33.client.animation.animationProgression.AnimationProgression;
import com.e33.client.animation.animationProgression.AnimationProgressionBuilder;
import com.e33.client.model.ModelBoxWithParameters;
import com.e33.client.model.ShootyModel;
import com.e33.client.animation.animated.models.ShootyModelAimed;
import com.e33.client.util.AnimationState;
import com.e33.client.util.AnimationStateListener;
import com.e33.entity.ShootyEntity;
import com.google.common.collect.Lists;
import net.minecraft.client.renderer.entity.model.RendererModel;
import net.minecraft.client.renderer.model.ModelBox;
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
    private boolean animationComplete = true;

    public ShootyAnimator(ShootyModel model) {
        this.model = model;
    }

    public boolean isAnimationComplete() {
        return this.animationComplete;
    }

    public void setRotationAngles(ShootyEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale) {

    }

    public void setLivingAnimations(T entity, float limbSwing, float limbSwingAmount, float partialTick) {
        AnimationState animationState = AnimationStateListener.getAnimationState(entity);
        if (this.lastAnimationState == animationState) {
            boolean animationComplete = true;

            for (AnimationProgression animation : this.animations) {
                boolean animationResult = animation.makeProgress();
                if (animationResult) {
                    animationComplete = false;
                }
            }
            this.animationComplete = animationComplete;

            return;
        }

        this.animations = Lists.newArrayList();
        this.animationComplete = false;

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
        return this.createAnimation(new ShootyModel(), new ShootyModelAimed(), ticksForAnimation);
    }

    private List<AnimationProgression> createAnimation(ShootyModel from, ShootyModel to, int ticks) {
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
                animations = this.concatLists(
                        animations,
                        this.getAnimatedChanges(fromChildModels.get(i), toChildModels.get(i), entityChildModels.get(i), ticks)
                );
            }
        }

        return animations;
    }

    private List<AnimationProgression> concatLists(List<AnimationProgression> list1, List<AnimationProgression> list2) {
        return Stream.concat(list1.stream(), list2.stream())
                .collect(Collectors.toList());
    }
}
