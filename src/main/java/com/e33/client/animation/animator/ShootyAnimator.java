package com.e33.client.animation.animator;

import com.e33.client.animation.animation.entity.AimingAnimation;
import com.e33.client.animation.animation.Animation;
import com.e33.client.model.ShootyModel;
import com.e33.entity.ShootyEntity;
import net.minecraft.entity.LivingEntity;

import java.util.Map;

public class ShootyAnimator<T extends ShootyEntity> extends Animator {

    private final ShootyModel model;

    public ShootyAnimator(ShootyModel model) {
        this.model = model;
    }

    @Override
    void animateShot(LivingEntity entity) {
        return;
    }

    Animation createShotAnimation(LivingEntity entity) {
        return null;
    }

    Animation createAimingAnimation(LivingEntity entity) {
        Map<Class, Animation> animationCache = this.getAnimationCacheFor(entity);

        if (animationCache.get(AimingAnimation.class) == null) {
            animationCache.put(AimingAnimation.class, new AimingAnimation(this.model, entity));
        }

        return animationCache.get(AimingAnimation.class);
    }
}
