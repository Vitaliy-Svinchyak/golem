package e33.guardy.client.animation.animation.entity;

import e33.guardy.client.animation.animated.model.ShootyModelAimed;
import e33.guardy.client.animation.animation.Animation;
import e33.guardy.client.animation.progression.AnimationProgression;
import e33.guardy.client.model.DynamicAnimationInterface;
import e33.guardy.client.model.ShootyModel;
import net.minecraft.client.renderer.entity.model.RendererModel;
import net.minecraft.entity.LivingEntity;

import java.util.List;

public class AimingAnimation extends Animation {

    public AimingAnimation(DynamicAnimationInterface model, LivingEntity entity) {
        super(model, entity);
    }

    protected List<AnimationProgression> createNormalAnimation() {
        DynamicAnimationInterface from = new ShootyModel();
        DynamicAnimationInterface to = new ShootyModelAimed();
        RendererModel fromModel = from.getMainRendererModel();
        RendererModel toModel = to.getMainRendererModel();
        RendererModel entityModel = this.model.getMainRendererModel();

        return this.getAnimatedChangesForEntity(fromModel, toModel, entityModel, 10);
    }

    protected List<AnimationProgression> createReversedAnimation() {
        DynamicAnimationInterface from = new ShootyModelAimed();
        DynamicAnimationInterface to = new ShootyModel();
        RendererModel fromModel = from.getMainRendererModel();
        RendererModel toModel = to.getMainRendererModel();
        RendererModel entityModel = this.model.getMainRendererModel();

        return this.getAnimatedChangesForEntity(fromModel, toModel, entityModel, 10);
    }
}
