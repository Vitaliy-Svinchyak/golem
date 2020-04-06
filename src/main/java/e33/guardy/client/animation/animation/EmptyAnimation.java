package e33.guardy.client.animation.animation;

import e33.guardy.client.animation.progression.AnimationProgression;
import e33.guardy.client.model.DynamicAnimationInterface;
import com.google.common.collect.Lists;
import net.minecraft.entity.LivingEntity;

import java.util.List;

public class EmptyAnimation extends Animation {
    public EmptyAnimation(DynamicAnimationInterface model, LivingEntity entity) {
        super(model, entity);
    }

    @Override
    protected List<AnimationProgression> createNormalAnimation() {
        return Lists.newArrayList();
    }

    @Override
    protected List<AnimationProgression> createReversedAnimation() {
        return Lists.newArrayList();
    }
}
