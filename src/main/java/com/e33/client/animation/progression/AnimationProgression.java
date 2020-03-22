package com.e33.client.animation.progression;

import net.minecraft.client.renderer.entity.model.RendererModel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

abstract public class AnimationProgression {
    protected final static Logger LOGGER = LogManager.getLogger();
    private List<Float> xProgression;
    private List<Float> yProgression;
    private List<Float> zProgression;

    ProgressionType progressionType;
    RendererModel rendererModel;

    private int currentTick = 0;

    AnimationProgression(RendererModel rendererModel, List<Float> xProgression, List<Float> yProgression, List<Float> zProgression, ProgressionType progressionType) {
        this.rendererModel = rendererModel;

        this.xProgression = xProgression;
        this.yProgression = yProgression;
        this.zProgression = zProgression;

        this.progressionType = progressionType;
    }

    public AnimationProgression reset() {
        this.currentTick = 0;

        return this;
    }

    public AnimationProgression reverse() {
        this.reverseList(this.xProgression);
        this.reverseList(this.yProgression);
        this.reverseList(this.zProgression);

        return this;
    }

    public boolean makeProgress() {
        if (this.currentTick == this.xProgression.size()) {
            LOGGER.error("I said NO!");
            return false;
        }

        float newX = this.xProgression.get(this.currentTick);
        float newY = this.yProgression.get(this.currentTick);
        float newZ = this.zProgression.get(this.currentTick);

        this.progress(newX, newY, newZ);

        this.currentTick++;

        if (this.currentTick == this.xProgression.size()) {
            return false;
        }

        return true;
    }

    private void reverseList(List<Float> list) {
        for (int i = 0, j = list.size() - 1; i < j; i++) {
            list.add(i, list.remove(j));
        }
    }

    public void log() {
        LOGGER.info(this.xProgression);
        LOGGER.info(this.yProgression);
        LOGGER.info(this.zProgression);
        LOGGER.info("------------------------------------------------------------------------------------");
    }

    abstract protected void progress(float newX, float newY, float newZ);


    enum ProgressionType {
        RendererModelRotationAngle,
        RendererModelRotationPoint,
        ModelBoxPosition,

        ItemRotation,
        ItemTranslation,

        Particle;

        ProgressionType() {

        }
    }
}
