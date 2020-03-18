package com.e33.client.animation.animationProgression;

import com.e33.client.util.ModelBoxParameters;
import com.google.common.collect.Lists;
import net.minecraft.client.renderer.entity.model.RendererModel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

public class AnimationProgression {
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

    public AnimationProgression reverse() {
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

    public void log() {
        LOGGER.info(this.xProgression);
        LOGGER.info(this.yProgression);
        LOGGER.info(this.zProgression);
    }

    public static AnimationProgression angle(RendererModel from, RendererModel to, int ticks, RendererModel model) {
        List<Float> xProgression = createProgress(from.rotateAngleX, to.rotateAngleX, ticks);
        List<Float> yProgression = createProgress(from.rotateAngleY, to.rotateAngleY, ticks);
        List<Float> zProgression = createProgress(from.rotateAngleZ, to.rotateAngleZ, ticks);

        return new AnimationProgression(model, xProgression, yProgression, zProgression, ProgressionType.RendererModelRotationAngle);
    }

    public static AnimationProgression point(RendererModel from, RendererModel to, int ticks, RendererModel model) {
        List<Float> xProgression = createProgress(from.rotationPointX, to.rotationPointX, ticks);
        List<Float> yProgression = createProgress(from.rotationPointY, to.rotationPointY, ticks);
        List<Float> zProgression = createProgress(from.rotationPointZ, to.rotationPointZ, ticks);

        return new AnimationProgression(model, xProgression, yProgression, zProgression, ProgressionType.RendererModelRotationPoint);
    }

    public static AnimationProgression modelBox(int ticks, RendererModel model, int cubeNumber, ModelBoxParameters fromCube, ModelBoxParameters toCube) {
        List<Float> xProgression = createProgress(fromCube.posX, toCube.posX, ticks);
        List<Float> yProgression = createProgress(fromCube.posY, toCube.posY, ticks);
        List<Float> zProgression = createProgress(fromCube.posZ, toCube.posZ, ticks);

        return new CubeAnimationProgression(model, xProgression, yProgression, zProgression, ProgressionType.ModelBoxPosition, cubeNumber, fromCube);
    }

    public static AnimationProgression rotateItem(float rotationFrom, float rotationTo, int ticks, float rotateX, float rotateY, float rotateZ) {
        List<Float> rotationProgression = createProgress(rotationFrom, rotationTo, ticks);

        return new ItemAnimationProgression(rotationProgression, rotateX, rotateY, rotateZ, ProgressionType.ItemRotation);
    }

    public void makeProgress() {
        if (this.currentTick > this.xProgression.size() - 1) {
            return;
        }

        float newX = this.xProgression.get(this.currentTick);
        float newY = this.yProgression.get(this.currentTick);
        float newZ = this.zProgression.get(this.currentTick);

        this.progress(newX, newY, newZ);

        this.currentTick++;
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

    private static List<Float> createProgress(float from, float to, int ticks) {
        List<Float> progression = Lists.newArrayList();
        float step = (to - from) / ticks;

        progression.add(from);
        for (int i = 0; i < ticks; i++) {
            progression.add(progression.get(progression.size() - 1) + step);
        }

        return progression;
    }

    enum ProgressionType {
        RendererModelRotationAngle,
        RendererModelRotationPoint,
        ModelBoxPosition,

        ItemRotation;

        ProgressionType() {

        }
    }
}
