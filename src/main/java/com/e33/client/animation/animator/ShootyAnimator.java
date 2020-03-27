package com.e33.client.animation.animator;

import com.e33.client.animation.animation.entity.AimingAnimation;
import com.e33.client.animation.animation.Animation;
import com.e33.client.animation.animation.entity.MoveAnimation;
import com.e33.client.animation.animation.entity.ShootingAnimation;
import com.e33.client.model.ShootyModel;
import com.e33.entity.ShootyEntity;
import net.minecraft.entity.LivingEntity;

import java.util.Map;

public class ShootyAnimator<T extends ShootyEntity> extends Animator {

    private final ShootyModel model;

    public ShootyAnimator(ShootyModel model) {
        this.model = model;
//        this.log = true;
    }

    Animation createShotAnimation(LivingEntity entity) {
        Map<Class, Animation> animationCache = this.getAnimationCacheFor(entity);

        if (animationCache.get(ShootingAnimation.class) == null) {
            animationCache.put(ShootingAnimation.class, new ShootingAnimation(this.model, entity));
        }

        return animationCache.get(ShootingAnimation.class);
    }

    @Override
    Animation createMoveAnimation(LivingEntity entity) {
        Map<Class, Animation> animationCache = this.getAnimationCacheFor(entity);

        if (animationCache.get(MoveAnimation.class) == null) {
            animationCache.put(MoveAnimation.class, new MoveAnimation(this.model, entity));
        }

        return animationCache.get(MoveAnimation.class);
    }

    Animation createAimingAnimation(LivingEntity entity) {
        Map<Class, Animation> animationCache = this.getAnimationCacheFor(entity);

        if (animationCache.get(AimingAnimation.class) == null) {
            animationCache.put(AimingAnimation.class, new AimingAnimation(this.model, entity));
        }

        return animationCache.get(AimingAnimation.class);
    }
}
