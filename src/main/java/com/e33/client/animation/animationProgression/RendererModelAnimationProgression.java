package com.e33.client.animation.animationProgression;

import net.minecraft.client.renderer.entity.model.RendererModel;

import java.util.List;

public class RendererModelAnimationProgression extends AnimationProgression {
    RendererModelAnimationProgression(RendererModel rendererModel, List<Float> xProgression, List<Float> yProgression, List<Float> zProgression, ProgressionType progressionType) {
        super(rendererModel, xProgression, yProgression, zProgression, progressionType);
    }

    protected void progress(float newX, float newY, float newZ) {
        switch (this.progressionType) {
            case RendererModelRotationAngle:
                this.rendererModel.rotateAngleX = newX;
                this.rendererModel.rotateAngleY = newY;
                this.rendererModel.rotateAngleZ = newZ;
                break;
            case RendererModelRotationPoint:
                this.rendererModel.setRotationPoint(newX, newY, newZ);
                break;
        }
    }
}
