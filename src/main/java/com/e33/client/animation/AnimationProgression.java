package com.e33.client.animation;

import com.e33.client.util.ModelBoxParameters;
import com.google.common.collect.Lists;
import net.minecraft.client.renderer.entity.model.RendererModel;
import net.minecraft.client.renderer.model.ModelBox;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.Field;
import java.util.List;

class AnimationProgression {
    private final static Logger LOGGER = LogManager.getLogger();
    private List<Float> xProgression;
    private List<Float> yProgression;
    private List<Float> zProgression;

    private final ProgressionType progressionType;
    private RendererModel rendererModel;

    private int cubeNumber;
    private ModelBoxParameters originalModelBoxParameters;

    private int currentTick = 0;

    private AnimationProgression(RendererModel rendererModel, List<Float> xProgression, List<Float> yProgression, List<Float> zProgression, ProgressionType progressionType) {
        this.rendererModel = rendererModel;

        this.xProgression = xProgression;
        this.yProgression = yProgression;
        this.zProgression = zProgression;

        this.progressionType = progressionType;
    }

    private AnimationProgression(RendererModel rendererModel, List<Float> xProgression, List<Float> yProgression, List<Float> zProgression, ProgressionType progressionType, int cubeNumber, ModelBoxParameters originalModelBoxParameters) {
        this(rendererModel, xProgression, yProgression, zProgression, progressionType);

        this.cubeNumber = cubeNumber;
        this.originalModelBoxParameters = originalModelBoxParameters;
    }

    AnimationProgression reverse() {
        this.reverseList(this.xProgression);
        this.reverseList(this.yProgression);
        this.reverseList(this.zProgression);

        return this;
    }

    private void reverseList(List<Float> list) {
        for (int i = 0, j = list.size() - 1; i < j; i++) {
            list.add(i, list.remove(j));
        }
    }

    void log() {
        LOGGER.info(this.xProgression);
        LOGGER.info(this.yProgression);
        LOGGER.info(this.zProgression);
    }

    static AnimationProgression angle(RendererModel from, RendererModel to, int ticks, RendererModel model) {
        List<Float> xProgression = createProgress(from.rotateAngleX, to.rotateAngleX, ticks);
        List<Float> yProgression = createProgress(from.rotateAngleY, to.rotateAngleY, ticks);
        List<Float> zProgression = createProgress(from.rotateAngleZ, to.rotateAngleZ, ticks);

        return new AnimationProgression(model, xProgression, yProgression, zProgression, ProgressionType.RendererModelRotationAngle);
    }

    static AnimationProgression point(RendererModel from, RendererModel to, int ticks, RendererModel model) {
        List<Float> xProgression = createProgress(from.rotationPointX, to.rotationPointX, ticks);
        List<Float> yProgression = createProgress(from.rotationPointY, to.rotationPointY, ticks);
        List<Float> zProgression = createProgress(from.rotationPointZ, to.rotationPointZ, ticks);

        return new AnimationProgression(model, xProgression, yProgression, zProgression, ProgressionType.RendererModelRotationPoint);
    }

    static AnimationProgression modelBox(RendererModel from, int ticks, RendererModel model, int cubeNumber, ModelBoxParameters fromCube, ModelBoxParameters toCube) {
        List<Float> xProgression = createProgress(fromCube.posX, toCube.posX, ticks);
        List<Float> yProgression = createProgress(fromCube.posY, toCube.posY, ticks);
        List<Float> zProgression = createProgress(fromCube.posZ, toCube.posZ, ticks);

        return new AnimationProgression(model, xProgression, yProgression, zProgression, ProgressionType.ModelBoxPosition, cubeNumber, fromCube);
    }

    void makeProgress() {
        if (this.currentTick > this.xProgression.size() - 1) {
            return;
        }

        float newX = this.xProgression.get(this.currentTick);
        float newY = this.yProgression.get(this.currentTick);
        float newZ = this.zProgression.get(this.currentTick);

        switch (this.progressionType) {
            case RendererModelRotationAngle:
                this.rendererModel.rotateAngleX = newX;
                this.rendererModel.rotateAngleY = newY;
                this.rendererModel.rotateAngleZ = newZ;
                break;
            case RendererModelRotationPoint:
                this.rendererModel.setRotationPoint(newX, newY, newZ);
                break;
            case ModelBoxPosition:
                ModelBoxParameters p = this.originalModelBoxParameters;
                ModelBox newCube = new ModelBox(p.model, p.textureOffsetX, p.textureOffsetY, newX, newY, newZ, p.width, p.height, p.depth, p.scaleFactor, p.mirrored);
                this.rendererModel.cubeList.set(this.cubeNumber, newCube);
                this.setCubesNotCompiled();
                break;
        }

        this.currentTick++;
    }

    private static List<Float> createProgress(float from, float to, int ticks) {
        List<Float> progression = Lists.newArrayList();
        float step = (to - from) / ticks;

        progression.add(from);
        for (int i = 0; i < ticks; i++) {
            progression.add(progression.get(progression.size() - 1) + step);
        }

        return progression;
    }

    private void setCubesNotCompiled() {
        try {
            Field f = this.rendererModel.getClass().getDeclaredField("compiled"); //NoSuchFieldException
            f.setAccessible(true);
            f.set(this.rendererModel, false); //IllegalAccessException

        } catch (ReflectiveOperationException e) {
            LOGGER.error(e.getMessage());
        }
    }

    private enum ProgressionType {
        RendererModelRotationAngle,
        RendererModelRotationPoint,
        ModelBoxPosition;

        ProgressionType() {

        }
    }
}
