package e33.guardy.client.animation.animation.item;

import e33.guardy.client.animation.animated.item.AimedWeaponPosition;
import e33.guardy.client.animation.animated.item.DefaultWeaponPosition;
import e33.guardy.client.animation.animation.Animation;
import e33.guardy.client.animation.progression.AnimationProgression;
import e33.guardy.client.model.DynamicAnimationInterface;
import net.minecraft.entity.LivingEntity;

import java.util.List;

public class AimingAnimation extends Animation {

    public AimingAnimation(DynamicAnimationInterface model, LivingEntity entity) {
        super(model, entity);
    }

    protected List<AnimationProgression> createReversedAnimation() {
        return this.getAnimatedChangesForItem(AimedWeaponPosition.getTranslations(), AimedWeaponPosition.getRotations(), DefaultWeaponPosition.getTranslations(), DefaultWeaponPosition.getRotations(), 20);
    }

    protected List<AnimationProgression> createNormalAnimation() {
        return this.getAnimatedChangesForItem(DefaultWeaponPosition.getTranslations(), DefaultWeaponPosition.getRotations(), AimedWeaponPosition.getTranslations(), AimedWeaponPosition.getRotations(), 10);
    }
}
