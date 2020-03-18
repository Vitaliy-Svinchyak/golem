package com.e33.client.animation.animationProgression;

import com.mojang.blaze3d.platform.GlStateManager;

import java.util.List;

public class ItemAnimationProgression extends AnimationProgression {

    private float rotateX;
    private float rotateY;
    private float rotateZ;

    ItemAnimationProgression(List<Float> rotationProgression, float rotateX, float rotateY, float rotateZ, ProgressionType progressionType) {
        super(null, rotationProgression, rotationProgression, rotationProgression, progressionType);

        this.rotateX = rotateX;
        this.rotateY = rotateY;
        this.rotateZ = rotateZ;
    }

    ItemAnimationProgression(List<Float> xProgression, List<Float> yProgression, List<Float> zProgression, ProgressionType progressionType) {
        super(null, xProgression, yProgression, zProgression, progressionType);
    }

    public boolean makeProgress() {
        float newX = this.xProgression.get(this.currentTick);
        float newY = this.yProgression.get(this.currentTick);
        float newZ = this.zProgression.get(this.currentTick);

        this.progress(newX, newY, newZ);

        this.currentTick++;

        if (this.currentTick > this.xProgression.size() - 1) {
            return false;
        }

        return true;
    }

    protected void progress(float newX, float newY, float newZ) {
        switch (this.progressionType) {
            case ItemRotation:
                this.animateItemRotation(newX);
                break;
            case ItemTranslation:
                this.animateItemTranslation(newX, newY, newZ);
                break;

        }
    }

    private void animateItemTranslation(float newX, float newY, float newZ) {
        GlStateManager.translatef(newX, newY, newZ);
    }

    private void animateItemRotation(float newX) {
        GlStateManager.rotatef(newX, this.rotateX, this.rotateY, this.rotateZ);
    }
}
