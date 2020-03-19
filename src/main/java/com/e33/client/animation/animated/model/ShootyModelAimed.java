package com.e33.client.animation.animated.model;

import com.e33.client.detail.modelBox.ModelBoxWithParameters;
import com.e33.client.model.ShootyModel;
import com.e33.entity.ShootyEntity;
import net.minecraft.client.renderer.entity.model.RendererModel;

public class ShootyModelAimed<T extends ShootyEntity> extends ShootyModel {

    public ShootyModelAimed() {
        textureWidth = 64;
        textureHeight = 64;

        shooty = new RendererModel(this);
        shooty.setRotationPoint(0.0F, 19.0F, 0.0F);
        setRotationAngle(shooty, 0.0F, 0.0873F, 0.0F);

        legs = new RendererModel(this);
        legs.setRotationPoint(0.0F, 0.0F, 0.0F);
        shooty.addChild(legs);

        right = new RendererModel(this);
        right.setRotationPoint(-0.3F, 0.0F, 0.3F);
        setRotationAngle(right, 0.0F, 1.1345F, 0.0F);
        legs.addChild(right);

        step = new RendererModel(this);
        step.setRotationPoint(0.0F, 0.0F, 0.0F);
        setRotationAngle(step, 0.0F, 0.4363F, 0.0F);
        right.addChild(step);
        step.cubeList.add(new ModelBoxWithParameters(step, 39, 27, -3.2F, -4.0F, -3.0F, 3, 3, 2, 0.0F, false));
        step.cubeList.add(new ModelBoxWithParameters(step, 50, 44, -2.7F, -3.9F, -3.5F, 2, 3, 1, 0.0F, false));
        step.cubeList.add(new ModelBoxWithParameters(step, 39, 49, -2.7F, -4.0F, -1.5F, 2, 4, 1, 0.0F, false));
        step.cubeList.add(new ModelBoxWithParameters(step, 0, 28, -3.2F, -1.0F, -5.0F, 3, 1, 4, 0.0F, false));
        step.cubeList.add(new ModelBoxWithParameters(step, 12, 39, -3.2F, -1.3F, -4.5F, 3, 1, 3, 0.0F, false));
        step.cubeList.add(new ModelBoxWithParameters(step, 43, 9, -2.7F, -1.6F, -4.0F, 2, 1, 3, 0.0F, false));

        knee = new RendererModel(this);
        knee.setRotationPoint(0.0F, 0.0F, 0.0F);
        setRotationAngle(knee, -0.1745F, 0.4363F, 0.0F);
        right.addChild(knee);
        knee.cubeList.add(new ModelBoxWithParameters(knee, 10, 43, -3.2F, -7.7F, -4.2F, 3, 2, 2, 0.0F, false));
        knee.cubeList.add(new ModelBoxWithParameters(knee, 48, 41, -3.19F, -6.7F, -4.5F, 3, 2, 1, 0.0F, false));
        knee.cubeList.add(new ModelBoxWithParameters(knee, 50, 35, -2.7F, -7.2F, -4.49F, 2, 3, 1, 0.0F, false));

        hip = new RendererModel(this);
        hip.setRotationPoint(0.0F, 0.0F, 0.0F);
        setRotationAngle(hip, -0.1745F, 0.4363F, 0.0F);
        right.addChild(hip);
        hip.cubeList.add(new ModelBoxWithParameters(hip, 34, 7, -3.2F, -9.7F, -4.7F, 3, 2, 3, 0.0F, false));
        hip.cubeList.add(new ModelBoxWithParameters(hip, 30, 44, -3.19F, -10.2F, -4.01F, 3, 1, 2, 0.0F, false));
        hip.cubeList.add(new ModelBoxWithParameters(hip, 47, 26, -3.19F, -8.4F, -2.9F, 3, 2, 1, 0.0F, false));
        hip.cubeList.add(new ModelBoxWithParameters(hip, 50, 13, -3.19F, -8.5F, -5.1F, 3, 1, 1, 0.0F, false));
        hip.cubeList.add(new ModelBoxWithParameters(hip, 34, 12, -3.19F, -9.7F, -5.1F, 3, 1, 1, 0.0F, false));

        cup = new RendererModel(this);
        cup.setRotationPoint(0.0F, 0.0F, 0.0F);
        setRotationAngle(cup, 0.0F, 0.4363F, 0.0F);
        right.addChild(cup);
        cup.cubeList.add(new ModelBoxWithParameters(cup, 45, 19, -2.7F, -5.3F, -3.01F, 2, 2, 2, 0.0F, false));
        cup.cubeList.add(new ModelBoxWithParameters(cup, 43, 4, -3.2F, -6.4F, -3.1F, 3, 2, 2, 0.0F, false));

        left = new RendererModel(this);
        left.setRotationPoint(0.0F, 0.0F, -1.1F);
        setRotationAngle(left, 0.0F, 0.4363F, 0.0F);
        legs.addChild(left);

        step2 = new RendererModel(this);
        step2.setRotationPoint(0.0F, 0.0F, 0.0F);
        setRotationAngle(step2, 0.0F, -0.4363F, 0.0F);
        left.addChild(step2);
        step2.cubeList.add(new ModelBoxWithParameters(step2, 26, 18, 0.5F, -1.0F, -4.3F, 3, 1, 4, 0.0F, false));
        step2.cubeList.add(new ModelBoxWithParameters(step2, 34, 39, 0.5F, -4.0F, -2.3F, 3, 3, 2, 0.0F, false));
        step2.cubeList.add(new ModelBoxWithParameters(step2, 33, 47, 1.0F, -4.0F, -0.8F, 2, 4, 1, 0.0F, false));
        step2.cubeList.add(new ModelBoxWithParameters(step2, 0, 39, 0.5F, -1.3F, -3.8F, 3, 1, 3, 0.0F, false));
        step2.cubeList.add(new ModelBoxWithParameters(step2, 0, 43, 1.0F, -1.6F, -3.3F, 2, 1, 3, 0.0F, false));
        step2.cubeList.add(new ModelBoxWithParameters(step2, 50, 8, 1.0F, -3.9F, -2.8F, 2, 3, 1, 0.0F, false));

        knee2 = new RendererModel(this);
        knee2.setRotationPoint(0.0F, 0.0F, 0.0F);
        setRotationAngle(knee2, -0.1745F, -0.4363F, 0.0F);
        left.addChild(knee2);
        knee2.cubeList.add(new ModelBoxWithParameters(knee2, 41, 32, 0.6F, -7.9F, -3.5F, 3, 2, 2, 0.0F, false));
        knee2.cubeList.add(new ModelBoxWithParameters(knee2, 47, 23, 0.59F, -6.9F, -3.8F, 3, 2, 1, 0.0F, false));
        knee2.cubeList.add(new ModelBoxWithParameters(knee2, 45, 50, 1.2F, -7.4F, -3.79F, 2, 3, 1, 0.0F, false));

        hip2 = new RendererModel(this);
        hip2.setRotationPoint(0.0F, 0.0F, 0.0F);
        setRotationAngle(hip2, -0.1745F, -0.4363F, 0.0F);
        left.addChild(hip2);
        hip2.cubeList.add(new ModelBoxWithParameters(hip2, 32, 34, 0.6F, -9.8F, -4.1F, 3, 2, 3, 0.0F, false));
        hip2.cubeList.add(new ModelBoxWithParameters(hip2, 20, 44, 0.59F, -10.3F, -3.41F, 3, 1, 2, 0.0F, false));
        hip2.cubeList.add(new ModelBoxWithParameters(hip2, 25, 47, 0.59F, -8.5F, -2.3F, 3, 2, 1, 0.0F, false));
        hip2.cubeList.add(new ModelBoxWithParameters(hip2, 0, 26, 0.59F, -8.6F, -4.5F, 3, 1, 1, 0.0F, false));
        hip2.cubeList.add(new ModelBoxWithParameters(hip2, 18, 0, 0.59F, -9.8F, -4.5F, 3, 1, 1, 0.0F, false));

        cup2 = new RendererModel(this);
        cup2.setRotationPoint(0.0F, 0.0F, 0.0F);
        setRotationAngle(cup2, 0.0F, -0.4363F, 0.0F);
        left.addChild(cup2);
        cup2.cubeList.add(new ModelBoxWithParameters(cup2, 12, 47, 1.1F, -5.5F, -2.31F, 2, 2, 2, 0.0F, false));
        cup2.cubeList.add(new ModelBoxWithParameters(cup2, 42, 37, 0.6F, -6.4F, -2.4F, 3, 2, 2, 0.0F, false));

        body = new RendererModel(this);
        body.setRotationPoint(0.0F, 0.0F, 0.0F);
        setRotationAngle(body, 0.0524F, 0.9599F, 0.0F);
        shooty.addChild(body);

        hips = new RendererModel(this);
        hips.setRotationPoint(0.0F, 0.0F, 0.0F);
        body.addChild(hips);
        hips.cubeList.add(new ModelBoxWithParameters(hips, 0, 15, -3.5F, -12.3F, -0.3F, 7, 2, 3, 0.0F, false));
        hips.cubeList.add(new ModelBoxWithParameters(hips, 23, 29, -3.5F, -11.7F, 2.0F, 7, 1, 1, 0.0F, false));
        hips.cubeList.add(new ModelBoxWithParameters(hips, 27, 5, -3.5F, -11.7F, -0.6F, 7, 1, 1, 0.0F, false));
        hips.cubeList.add(new ModelBoxWithParameters(hips, 20, 47, 2.8F, -11.7F, -0.3F, 1, 1, 3, 0.0F, false));
        hips.cubeList.add(new ModelBoxWithParameters(hips, 45, 46, -3.8F, -11.7F, -0.3F, 1, 1, 3, 0.0F, false));

        press = new RendererModel(this);
        press.setRotationPoint(0.0F, 0.0F, 0.0F);
        body.addChild(press);
        press.cubeList.add(new ModelBoxWithParameters(press, 0, 8, -3.5F, -16.3F, -0.3F, 7, 4, 3, 0.0F, false));
        press.cubeList.add(new ModelBoxWithParameters(press, 41, 0, -2.0F, -15.1F, 2.2F, 4, 3, 1, 0.0F, false));
        press.cubeList.add(new ModelBoxWithParameters(press, 20, 34, -2.5F, -15.4F, -0.8F, 5, 4, 1, 0.0F, false));

        chest = new RendererModel(this);
        chest.setRotationPoint(0.0F, 0.0F, 0.0F);
        body.addChild(chest);
        chest.cubeList.add(new ModelBoxWithParameters(chest, 0, 0, -3.5F, -20.3F, -1.3F, 7, 4, 4, 0.0F, false));
        chest.cubeList.add(new ModelBoxWithParameters(chest, 40, 45, -3.501F, -19.8F, -1.6F, 3, 3, 1, 0.0F, false));
        chest.cubeList.add(new ModelBoxWithParameters(chest, 20, 10, 0.501F, -19.8F, -1.6F, 3, 3, 1, 0.0F, false));
        chest.cubeList.add(new ModelBoxWithParameters(chest, 0, 20, -0.499F, -20.2F, -1.8F, 1, 4, 1, 0.0F, false));
        chest.cubeList.add(new ModelBoxWithParameters(chest, 10, 28, -0.5F, -16.4F, -1.3F, 1, 1, 2, 0.0F, false));
        chest.cubeList.add(new ModelBoxWithParameters(chest, 24, 16, -3.499F, -16.6F, -0.8F, 7, 1, 1, 0.0F, false));
        chest.cubeList.add(new ModelBoxWithParameters(chest, 27, 0, -3.0F, -20.301F, 2.3F, 6, 4, 1, 0.0F, false));
        chest.cubeList.add(new ModelBoxWithParameters(chest, 26, 31, -2.99F, -20.5F, 2.501F, 6, 2, 1, 0.0F, false));
        chest.cubeList.add(new ModelBoxWithParameters(chest, 24, 39, -2.0F, -19.1F, 2.5F, 4, 4, 1, 0.0F, false));

        arms = new RendererModel(this);
        arms.setRotationPoint(0.0F, -0.3F, 3.0F);
        setRotationAngle(arms, 0.1745F, 0.0F, 0.0F);
        body.addChild(arms);

        right2 = new RendererModel(this);
        right2.setRotationPoint(0.0F, 0.0F, 2.1F);
        arms.addChild(right2);

        shoulder = new RendererModel(this);
        shoulder.setRotationPoint(0.0F, 0.0F, 0.0F);
        setRotationAngle(shoulder, -0.6109F, -0.2618F, 0.4363F);
        right2.addChild(shoulder);
        shoulder.cubeList.add(new ModelBoxWithParameters(shoulder, 24, 10, -13.8F, -15.0F, -8.8F, 3, 2, 4, 0.0F, false));
        shoulder.cubeList.add(new ModelBoxWithParameters(shoulder, 51, 1, -14.1F, -14.5F, -8.9F, 1, 1, 4, 0.0F, false));
        shoulder.cubeList.add(new ModelBoxWithParameters(shoulder, 8, 34, -13.1F, -15.3F, -8.9F, 2, 1, 4, 0.0F, false));
        shoulder.cubeList.add(new ModelBoxWithParameters(shoulder, 37, 21, -13.5F, -13.5F, -8.3F, 2, 3, 3, 0.0F, false));
        shoulder.cubeList.add(new ModelBoxWithParameters(shoulder, 44, 13, -13.51F, -11.5F, -7.8F, 2, 3, 2, 0.0F, false));
        shoulder.cubeList.add(new ModelBoxWithParameters(shoulder, 0, 47, -13.7F, -13.5F, -7.3F, 2, 5, 1, 0.0F, false));
        shoulder.cubeList.add(new ModelBoxWithParameters(shoulder, 34, 52, -13.8F, -12.5F, -8.4F, 1, 1, 3, 0.0F, false));

        preshoulder = new RendererModel(this);
        preshoulder.setRotationPoint(0.0F, 0.0F, 0.0F);
        setRotationAngle(preshoulder, 0.0873F, -1.0472F, 0.0F);
        right2.addChild(preshoulder);
        preshoulder.cubeList.add(new ModelBoxWithParameters(preshoulder, 17, 2, -7.5F, -14.9F, -1.7F, 2, 2, 6, 0.0F, false));
        preshoulder.cubeList.add(new ModelBoxWithParameters(preshoulder, 9, 22, -7.8F, -14.4F, -1.6F, 2, 1, 5, 0.0F, false));

        cup3 = new RendererModel(this);
        cup3.setRotationPoint(0.0F, 0.0F, 0.0F);
        right2.addChild(cup3);
        cup3.cubeList.add(new ModelBoxWithParameters(cup3, 0, 2, -6.6F, -14.9F, -4.5F, 1, 1, 1, 0.0F, false));

        left2 = new RendererModel(this);
        left2.setRotationPoint(10.0F, 0.0F, 0.0F);
        arms.addChild(left2);

        shoulder2 = new RendererModel(this);
        shoulder2.setRotationPoint(0.0F, 0.0F, 0.0F);
        setRotationAngle(shoulder2, -0.2618F, 0.0F, 0.0F);
        left2.addChild(shoulder2);
        shoulder2.cubeList.add(new ModelBoxWithParameters(shoulder2, 23, 23, -6.8F, -19.4F, -6.5F, 3, 2, 4, 0.0F, false));
        shoulder2.cubeList.add(new ModelBoxWithParameters(shoulder2, 47, 50, -4.5F, -18.8F, -6.6F, 1, 1, 4, 0.0F, false));
        shoulder2.cubeList.add(new ModelBoxWithParameters(shoulder2, 0, 33, -6.5F, -19.7F, -6.6F, 2, 1, 4, 0.0F, false));
        shoulder2.cubeList.add(new ModelBoxWithParameters(shoulder2, 37, 15, -6.2F, -17.9F, -6.0F, 2, 3, 3, 0.0F, false));
        shoulder2.cubeList.add(new ModelBoxWithParameters(shoulder2, 18, 22, -6.19F, -15.9F, -5.5F, 2, 3, 2, 0.0F, false));
        shoulder2.cubeList.add(new ModelBoxWithParameters(shoulder2, 6, 47, -5.9F, -17.9F, -5.0F, 2, 5, 1, 0.0F, false));
        shoulder2.cubeList.add(new ModelBoxWithParameters(shoulder2, 9, 51, -4.8F, -16.9F, -6.1F, 1, 1, 3, 0.0F, false));

        preshoulder2 = new RendererModel(this);
        preshoulder2.setRotationPoint(0.0F, 0.0F, 0.0F);
        setRotationAngle(preshoulder2, -0.2618F, -0.1745F, 0.0F);
        left2.addChild(preshoulder2);
        preshoulder2.cubeList.add(new ModelBoxWithParameters(preshoulder2, 14, 14, -6.4F, -13.6F, -10.3F, 2, 2, 6, 0.0F, false));
        preshoulder2.cubeList.add(new ModelBoxWithParameters(preshoulder2, 0, 20, -6.1F, -13.1F, -9.3F, 2, 1, 5, 0.0F, false));

        cup4 = new RendererModel(this);
        cup4.setRotationPoint(0.0F, 0.0F, 0.0F);
        setRotationAngle(cup4, -0.1745F, 0.0F, 0.0F);
        left2.addChild(cup4);
        cup4.cubeList.add(new ModelBoxWithParameters(cup4, 0, 0, -5.5F, -13.3F, -4.2F, 1, 1, 1, 0.0F, false));

        head = new RendererModel(this);
        head.setRotationPoint(0.0F, 0.0F, 0.0F);
        setRotationAngle(head, 0.0349F, 0.0F, 0.0F);
        shooty.addChild(head);
        head.cubeList.add(new ModelBoxWithParameters(head, 14, 28, -1.4F, -23.0F, -1.1F, 3, 3, 3, 0.0F, false));
        head.cubeList.add(new ModelBoxWithParameters(head, 41, 41, -0.9F, -23.3F, -1.2F, 2, 1, 3, 0.0F, false));
        head.cubeList.add(new ModelBoxWithParameters(head, 26, 50, 0.8F, -22.6F, -0.5F, 1, 2, 2, 0.0F, false));
        head.cubeList.add(new ModelBoxWithParameters(head, 9, 39, -0.9F, -22.6F, 1.1F, 2, 2, 1, 0.0F, false));
        head.cubeList.add(new ModelBoxWithParameters(head, 49, 29, -1.6F, -22.6F, -0.6F, 1, 2, 2, 0.0F, false));
    }

    public void render(ShootyEntity entity, float f, float f1, float f2, float f3, float f4, float f5) {
        shooty.render(f5);
    }

    public void setRotationAngle(RendererModel modelRenderer, float x, float y, float z) {
        modelRenderer.rotateAngleX = x;
        modelRenderer.rotateAngleY = y;
        modelRenderer.rotateAngleZ = z;
    }
}