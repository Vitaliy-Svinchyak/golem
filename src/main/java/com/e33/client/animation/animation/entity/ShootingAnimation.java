package com.e33.client.animation.animation.entity;

import com.e33.client.animation.animation.Animation;
import com.e33.client.animation.progression.AnimationProgression;
import com.e33.client.animation.progression.AnimationProgressionBuilder;
import com.e33.client.model.DynamicAnimationInterface;
import com.e33.init.ParticleRegistry;
import com.google.common.collect.Lists;
import net.minecraft.entity.LivingEntity;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.math.Vec3d;

import java.util.List;

public class ShootingAnimation extends Animation {

    public ShootingAnimation(DynamicAnimationInterface model, LivingEntity entity) {
        super(model, entity);
        this.log = true;
    }

    protected void createAnimation() {
        super.createAnimation();
        this.cachedNormalAnimations = null;
    }

    protected List<AnimationProgression> createNormalAnimation() {
        List<AnimationProgression> animations = Lists.newArrayList();
        Vec3d smokePosition = this.getPositionForParticle(0.95F);
        Vec3d flamePosition = this.getPositionForParticle(0.87F);
        Vec3d sleevePosition = this.getPositionForParticle(0.5F);

        animations.add(AnimationProgressionBuilder.particle((float) smokePosition.x, (float) smokePosition.y, (float) smokePosition.z, (float) smokePosition.x, (float) smokePosition.y, (float) smokePosition.z, ParticleTypes.SMOKE, 2));
        animations.add(AnimationProgressionBuilder.particle((float) flamePosition.x, (float) flamePosition.y, (float) flamePosition.z, (float) flamePosition.x, (float) flamePosition.y, (float) flamePosition.z, ParticleTypes.FLAME, 1));

        animations.add(AnimationProgressionBuilder.particle((float) sleevePosition.x, (float) sleevePosition.y, (float) sleevePosition.z, (float) sleevePosition.x, (float) sleevePosition.y, (float) sleevePosition.z, ParticleRegistry.SLEEVE, 1));

        return animations;
    }

    protected List<AnimationProgression> createReversedAnimation() {
        return null;
    }

    private Vec3d getPositionForParticle(float radius) {
        float angle = Math.abs(this.entity.rotationYaw);
        angle -= 0.2;
        Vec3d position = this.entity.getPositionVector();
        float x = (float) (Math.sin(angle) * radius + position.getX());
        float y = (float) position.getY() + 1.25F;
        float z = (float) (Math.cos(angle) * radius + position.getZ());

        return new Vec3d(x, y, z);
    }
}
