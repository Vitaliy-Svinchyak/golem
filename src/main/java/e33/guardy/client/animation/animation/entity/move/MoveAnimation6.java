package e33.guardy.client.animation.animation.entity.move;

import e33.guardy.client.animation.animated.model.move.ShootyMove5;
import e33.guardy.client.animation.animated.model.move.ShootyMove6;
import e33.guardy.client.animation.animation.Animation;
import e33.guardy.client.animation.progression.AnimationProgression;
import e33.guardy.client.model.DynamicAnimationInterface;
import net.minecraft.client.renderer.entity.model.RendererModel;
import net.minecraft.entity.LivingEntity;

import java.util.List;

public class MoveAnimation6 extends Animation {
    public MoveAnimation6(DynamicAnimationInterface model, LivingEntity entity) {
        super(model, entity);
    }

    @Override
    protected List<AnimationProgression> createNormalAnimation() {
        DynamicAnimationInterface from = new ShootyMove5<>();
        DynamicAnimationInterface to1 = new ShootyMove6<>();
        RendererModel fromModel = from.getMainRendererModel();
        RendererModel toModel = to1.getMainRendererModel();
        RendererModel entityModel = this.model.getMainRendererModel();

        return this.getAnimatedChangesForEntity(fromModel, toModel, entityModel, 10);
    }

    @Override
    protected List<AnimationProgression> createReversedAnimation() {
        return null;
    }
}
