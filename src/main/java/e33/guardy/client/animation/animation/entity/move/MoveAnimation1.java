package e33.guardy.client.animation.animation.entity.move;

import e33.guardy.client.animation.animated.model.move.ShootyMove1;
import e33.guardy.client.animation.animation.Animation;
import e33.guardy.client.animation.progression.AnimationProgression;
import e33.guardy.client.model.DynamicAnimationInterface;
import e33.guardy.client.model.ShootyModel;
import net.minecraft.client.renderer.entity.model.RendererModel;
import net.minecraft.entity.LivingEntity;

import java.util.List;

public class MoveAnimation1 extends Animation {
    public MoveAnimation1(DynamicAnimationInterface model, LivingEntity entity) {
        super(model, entity);
    }

    @Override
    protected List<AnimationProgression> createNormalAnimation() {
        DynamicAnimationInterface from = new ShootyModel();
        DynamicAnimationInterface to1 = new ShootyMove1<>();
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
