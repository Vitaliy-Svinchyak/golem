package e33.guardy.client.animation.animator;

import e33.guardy.client.animation.animation.entity.AimingAnimation;
import e33.guardy.client.animation.animation.Animation;
import e33.guardy.client.animation.animation.entity.MoveAnimation;
import e33.guardy.client.animation.animation.entity.ShootingAnimation;
import e33.guardy.client.model.ShootyModel;
import e33.guardy.entity.ShootyEntity;
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
        Animation aimAnimation = this.getAnimationCacheFor(entity).get(AimingAnimation.class);
        Animation movingAnimation = this.getAnimationCacheFor(entity).get(MoveAnimation.class);
        this.removeAnimation(entity, aimAnimation); // TODO 2 avoid this shit
        this.removeAnimation(entity, movingAnimation); // TODO 2 avoid this shit
        Map<Class, Animation> animationCache = this.getAnimationCacheFor(entity);

        if (animationCache.get(MoveAnimation.class) == null) {
            animationCache.put(MoveAnimation.class, new MoveAnimation(this.model, entity));
        }

        return animationCache.get(MoveAnimation.class);
    }

    Animation createAimingAnimation(LivingEntity entity) {
        Animation movingAnimation = this.getAnimationCacheFor(entity).get(MoveAnimation.class);
        this.removeAnimation(entity, movingAnimation); // TODO 2 avoid this shit

        Map<Class, Animation> animationCache = this.getAnimationCacheFor(entity);

        if (animationCache.get(AimingAnimation.class) == null) {
            animationCache.put(AimingAnimation.class, new AimingAnimation(this.model, entity));
        }

        return animationCache.get(AimingAnimation.class);
    }
}
