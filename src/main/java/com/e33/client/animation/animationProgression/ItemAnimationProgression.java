package com.e33.client.animation.animationProgression;

import com.mojang.blaze3d.platform.GlStateManager;

import java.util.List;

public class ItemAnimationProgression extends AnimationProgression {

    private final float rotateX;
    private final float rotateY;
    private final float rotateZ;

    ItemAnimationProgression(List<Float> rotationProgression, float rotateX, float rotateY, float rotateZ, ProgressionType progressionType) {
        super(null, rotationProgression, rotationProgression, rotationProgression, progressionType);

        this.rotateX = rotateX;
        this.rotateY = rotateY;
        this.rotateZ = rotateZ;
    }

    protected void progress(float newX, float newY, float newZ) {
        switch (this.progressionType) {
            case ItemRotation:
                this.animateItemRotation(newX);
                break;

        }
    }

    private void animateItemRotation(float newX) {
        GlStateManager.rotatef(newX, this.rotateX, this.rotateY, this.rotateZ);
    }
}
