package com.e33.client.animation.animation.item;

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

    protected List<AnimationProgression> createNormalAnimation() {
        List<AnimationProgression> animations = Lists.newArrayList();

        Vec3d position = this.entity.getPositionVector();
        float radius = 1.35F;
        float angle = Math.abs(entity.rotationYaw);

        float y = (float) position.getY() + 1.1F;
        float x = (float) (Math.sin(angle) * radius + position.getX());
        float z = (float) (Math.cos(angle) * radius + position.getZ());

        animations.add(AnimationProgressionBuilder.particle(x, y, z, x, y, z, ParticleTypes.SMOKE, 2));
        animations.add(AnimationProgressionBuilder.particle(x, y, z, x, y, z, ParticleTypes.FLAME, 1));

        animations.add(AnimationProgressionBuilder.particle(x, y, z, x, y, z, ParticleRegistry.SLEEVE, 1));

        return animations;
    }

    protected List<AnimationProgression> createReversedAnimation() {
        return null;
    }
}
