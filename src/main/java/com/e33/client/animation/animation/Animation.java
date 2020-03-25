package com.e33.client.animation.animation;

import com.e33.client.animation.progression.AnimationProgression;
import com.e33.client.animation.progression.AnimationProgressionBuilder;
import com.e33.client.detail.item.Rotation;
import com.e33.client.detail.item.Translation;
import com.e33.client.detail.modelBox.ModelBoxWithParameters;
import com.e33.client.model.DynamicAnimationInterface;
import com.e33.util.Helper;
import com.google.common.collect.Lists;
import net.minecraft.client.renderer.entity.model.RendererModel;
import net.minecraft.client.renderer.model.ModelBox;
import net.minecraft.entity.LivingEntity;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

abstract public class Animation {
    protected final static Logger LOGGER = LogManager.getLogger();

    protected boolean log = false;
    private boolean reverse = false;
    private boolean oneFrame = false;
    private boolean endless = false;
    private boolean oneTime = false;
    private boolean wasAnimated = false;

    protected final DynamicAnimationInterface model;
    protected final LivingEntity entity;
    protected Animation childAnimation;

    protected List<AnimationProgression> animations;
    private List<AnimationProgression> cachedNormalAnimations;
    protected List<AnimationProgression> cachedReversedAnimations;

    public Animation(DynamicAnimationInterface model, LivingEntity entity) {
        this.model = model;
        this.entity = entity;
    }

    public Animation finish() {
        this.setEndless(false);
        this.animations = Lists.newArrayList();

        if (this.childAnimation != null) {
            this.childAnimation.finish();
        }

        return this;
    }

    public Animation then(Animation childAnimation) {
        this.childAnimation = childAnimation;

        return this;
    }

    public Animation setEndless(boolean endless) {
        this.endless = endless;

        return this;
    }

    public Animation setOneTime(boolean oneTime) {
        this.oneTime = oneTime;

        return this;
    }

    public Animation create() {
        this.createAnimation();

        for (AnimationProgression progression : this.animations) {
            progression.reset();
        }

        if (this.childAnimation != null) {
            this.childAnimation.create();
        }

        return this;
    }

    protected abstract List<AnimationProgression> createNormalAnimation();

    protected abstract List<AnimationProgression> createReversedAnimation();

    public Animation reset() {
        this.oneFrame = false;
        this.reverse = false;
        this.endless = false;

        if (this.childAnimation != null) {
            this.childAnimation.reset();
        }

        return this;
    }

    public Animation setReverse(boolean reverse) {
        this.reverse = reverse;

        if (this.childAnimation != null) {
            this.childAnimation.setReverse(reverse);
        }

        return this;
    }

    public Animation firstFrame() {
        this.reset();
        this.oneFrame = true;

        if (this.childAnimation != null) {
            this.childAnimation.firstFrame();
        }

        return this;
    }

    public Animation lastFrame() {
        this.reset().setReverse(true);
        this.oneFrame = true;

        if (this.childAnimation != null) {
            this.childAnimation.lastFrame();
        }

        return this;
    }

    public boolean animate() {
        this.wasAnimated = true;
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

        if (this.oneFrame) {
            return true;
        }

        if (this.isAnimationComplete() && this.childAnimation != null) {
            boolean childAnimationComplete = this.childAnimation.animate();

            if (this.endless && childAnimationComplete) {
                this.create();

                return false;
            }

            return childAnimationComplete;
        }

        return this.isAnimationComplete();
    }

    protected List<AnimationProgression> getAnimatedChangesForEntity(RendererModel fromModel, RendererModel toModel, RendererModel entityModel, int ticks) {
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
                        this.getAnimatedChangesForEntity(fromChildModels.get(i), toChildModels.get(i), entityChildModels.get(i), ticks)
                );
            }
        }

        return animations;
    }

    protected List<AnimationProgression> getAnimatedChangesForItem(List<Translation> fromTranslations, List<Rotation> fromRotations, List<Translation> toTranslations, List<Rotation> toRotations, int ticks) {
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

    protected boolean isReversed() {
        return this.reverse;
    }

    protected void createAnimation() {
        if (this.cachedNormalAnimations == null && !this.isReversed()) {
            this.cachedNormalAnimations = this.createNormalAnimation();
        }

        if (this.cachedReversedAnimations == null && this.isReversed()) {
            this.cachedReversedAnimations = this.createReversedAnimation();
        }

        if (this.oneTime && this.wasAnimated) {
            return;
        }

        if (this.isReversed()) {
            this.animations = new ArrayList<>(this.cachedReversedAnimations);
        } else {
            this.animations = new ArrayList<>(this.cachedNormalAnimations);
        }
    }

    private boolean isAnimationComplete() {
        return this.animations.size() == 0;
    }

    void log(String message) {
        if (this.log) {
            LOGGER.info(message);
        }
    }
}
