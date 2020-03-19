package com.e33.client.animation.animationProgression;

import com.e33.client.util.ModelBoxParameters;
import com.google.common.collect.Lists;
import net.minecraft.client.renderer.entity.model.RendererModel;

import java.util.List;

public class AnimationProgressionBuilder {

    public static boolean angleDiffers(RendererModel from, RendererModel to) {
        return from.rotateAngleX != to.rotateAngleX || from.rotateAngleY != to.rotateAngleY || from.rotateAngleZ != to.rotateAngleZ;
    }

    public static boolean pointDiffers(RendererModel from, RendererModel to) {
        return from.rotationPointX != to.rotationPointX || from.rotationPointY != to.rotationPointY || from.rotationPointZ != to.rotationPointZ;
    }

    public static boolean cubeDiffers(ModelBoxParameters from, ModelBoxParameters to) {
        return from.posX != to.posX || from.posY != to.posY || from.posZ != to.posZ;
    }

    public static AnimationProgression angle(RendererModel from, RendererModel to, int ticks, RendererModel model) {
        List<Float> xProgression = createProgress(from.rotateAngleX, to.rotateAngleX, ticks);
        List<Float> yProgression = createProgress(from.rotateAngleY, to.rotateAngleY, ticks);
        List<Float> zProgression = createProgress(from.rotateAngleZ, to.rotateAngleZ, ticks);

        return new RendererModelAnimationProgression(model, xProgression, yProgression, zProgression, AnimationProgression.ProgressionType.RendererModelRotationAngle);
    }

    public static AnimationProgression point(RendererModel from, RendererModel to, int ticks, RendererModel model) {
        List<Float> xProgression = createProgress(from.rotationPointX, to.rotationPointX, ticks);
        List<Float> yProgression = createProgress(from.rotationPointY, to.rotationPointY, ticks);
        List<Float> zProgression = createProgress(from.rotationPointZ, to.rotationPointZ, ticks);

        return new RendererModelAnimationProgression(model, xProgression, yProgression, zProgression, AnimationProgression.ProgressionType.RendererModelRotationPoint);
    }

    public static AnimationProgression modelBox(int ticks, RendererModel model, int cubeNumber, ModelBoxParameters fromCube, ModelBoxParameters toCube) {
        List<Float> xProgression = createProgress(fromCube.posX, toCube.posX, ticks);
        List<Float> yProgression = createProgress(fromCube.posY, toCube.posY, ticks);
        List<Float> zProgression = createProgress(fromCube.posZ, toCube.posZ, ticks);

        return new CubeAnimationProgression(model, xProgression, yProgression, zProgression, AnimationProgression.ProgressionType.ModelBoxPosition, cubeNumber, fromCube);
    }

    public static AnimationProgression rotateItem(float rotationFrom, float rotationTo, int ticks, float rotateX, float rotateY, float rotateZ) {
        List<Float> rotationProgression = createProgress(rotationFrom, rotationTo, ticks);

        return new ItemAnimationProgression(rotationProgression, rotateX, rotateY, rotateZ, AnimationProgression.ProgressionType.ItemRotation);
    }

    public static AnimationProgression translateItem(float fromX, float fromY, float fromZ, float toX, float toY, float toZ, int ticks) {
        List<Float> xProgression = createProgress(fromX, toX, ticks);
        List<Float> yProgression = createProgress(fromY, toY, ticks);
        List<Float> zProgression = createProgress(fromZ, toZ, ticks);

        return new ItemAnimationProgression(xProgression, yProgression, zProgression, AnimationProgression.ProgressionType.ItemTranslation);
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
}
