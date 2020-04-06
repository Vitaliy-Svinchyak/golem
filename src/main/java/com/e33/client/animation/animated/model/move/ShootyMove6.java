package com.e33.client.animation.animated.model.move;//Made with Blockbench
//Paste this code into your mod.

import com.e33.client.detail.modelBox.ModelBoxWithParameters;
import com.e33.client.model.ShootyModel;
import com.e33.entity.ShootyEntity;
import net.minecraft.client.renderer.entity.model.RendererModel;

public class ShootyMove6<T extends ShootyEntity> extends ShootyModel<T> {

    public ShootyMove6() {
        textureWidth = 64;
        textureHeight = 64;

        shooty = new RendererModel(this);
        shooty.setRotationPoint(0.0F, 17.5F, 0.0F);

        body = new RendererModel(this);
        body.setRotationPoint(0.0F, 0.5F, -0.6F);
        shooty.addChild(body);

        hips = new RendererModel(this);
        hips.setRotationPoint(0.0F, -12.25F, 1.1F);
        body.addChild(hips);
        hips.cubeList.add(new ModelBoxWithParameters(hips, 0, 27, -3.5F, -0.05F, -1.4F, 7, 2, 3, 0.0F, false));
        hips.cubeList.add(new ModelBoxWithParameters(hips, 30, 5, -3.5F, 0.55F, 0.9F, 7, 1, 1, 0.0F, false));
        hips.cubeList.add(new ModelBoxWithParameters(hips, 0, 25, -3.5F, 0.55F, -1.7F, 7, 1, 1, 0.0F, false));
        hips.cubeList.add(new ModelBoxWithParameters(hips, 51, 51, 2.8F, 0.55F, -1.4F, 1, 1, 3, 0.0F, false));
        hips.cubeList.add(new ModelBoxWithParameters(hips, 49, 1, -3.8F, 0.55F, -1.4F, 1, 1, 3, 0.0F, false));

        legs = new RendererModel(this);
        legs.setRotationPoint(0.0F, 11.75F, 1.4F);
        hips.addChild(legs);

        right_leg = new RendererModel(this);
        right_leg.setRotationPoint(-2.0F, -10.4F, -1.2F);
        setRotationAngle(right_leg, -0.5236F, 0.0F, 0.0F);
        legs.addChild(right_leg);
        right_leg.cubeList.add(new ModelBoxWithParameters(right_leg, 0, 37, -1.5F, 0.6F, -1.9F, 3, 2, 3, 0.0F, false));
        right_leg.cubeList.add(new ModelBoxWithParameters(right_leg, 46, 34, -1.49F, 0.1F, -1.21F, 3, 1, 2, 0.0F, false));
        right_leg.cubeList.add(new ModelBoxWithParameters(right_leg, 29, 52, -1.49F, 1.9F, -0.1F, 3, 2, 1, 0.0F, false));
        right_leg.cubeList.add(new ModelBoxWithParameters(right_leg, 54, 8, -1.49F, 1.8F, -2.3F, 3, 1, 1, 0.0F, false));
        right_leg.cubeList.add(new ModelBoxWithParameters(right_leg, 53, 13, -1.49F, 0.6F, -2.3F, 3, 1, 1, 0.0F, false));
        right_leg.cubeList.add(new ModelBoxWithParameters(right_leg, 0, 47, -1.5F, 2.6F, -1.3F, 3, 2, 2, 0.0F, false));
        right_leg.cubeList.add(new ModelBoxWithParameters(right_leg, 52, 29, -1.49F, 3.7F, -1.7F, 3, 2, 1, 0.0F, false));
        right_leg.cubeList.add(new ModelBoxWithParameters(right_leg, 54, 32, -1.0F, 3.2F, -1.79F, 2, 3, 1, 0.0F, false));
        right_leg.cubeList.add(new ModelBoxWithParameters(right_leg, 16, 47, -1.5F, 4.2F, -1.3F, 3, 2, 2, 0.0F, false));

        right_cup = new RendererModel(this);
        right_cup.setRotationPoint(0.0F, 6.1F, -1.21F);
        setRotationAngle(right_cup, 0.5236F, 0.0F, 0.0F);
        right_leg.addChild(right_cup);
        right_cup.cubeList.add(new ModelBoxWithParameters(right_cup, 51, 16, -1.0F, -1.0F, -0.1F, 2, 2, 2, 0.0F, false));

        right_shine = new RendererModel(this);
        right_shine.setRotationPoint(0.0F, 0.3F, 0.51F);
        setRotationAngle(right_shine, 0.1745F, 0.0F, 0.0F);
        right_cup.addChild(right_shine);
        right_shine.cubeList.add(new ModelBoxWithParameters(right_shine, 32, 41, -1.5F, 0.1F, -0.6F, 3, 4, 2, 0.0F, false));
        right_shine.cubeList.add(new ModelBoxWithParameters(right_shine, 29, 55, -1.0F, 1.2F, -1.1F, 2, 3, 1, 0.0F, false));
        right_shine.cubeList.add(new ModelBoxWithParameters(right_shine, 0, 55, -1.0F, 1.1F, 0.9F, 2, 3, 1, 0.0F, false));

        right_step = new RendererModel(this);
        right_step.setRotationPoint(0.0F, 4.4F, 0.5F);
        setRotationAngle(right_step, -0.1745F, 0.0F, 0.0F);
        right_shine.addChild(right_step);
        right_step.cubeList.add(new ModelBoxWithParameters(right_step, 33, 16, -1.5F, -0.3F, -3.1F, 3, 1, 4, 0.0F, false));
        right_step.cubeList.add(new ModelBoxWithParameters(right_step, 40, 0, -1.501F, -0.6F, -2.6F, 3, 1, 3, 0.0F, false));
        right_step.cubeList.add(new ModelBoxWithParameters(right_step, 44, 38, -1.001F, -0.9F, -2.1F, 2, 1, 3, 0.0F, false));

        left_leg = new RendererModel(this);
        left_leg.setRotationPoint(2.0F, -10.4F, -1.2F);
        setRotationAngle(left_leg, 0.1745F, 0.0F, 0.0F);
        legs.addChild(left_leg);
        left_leg.cubeList.add(new ModelBoxWithParameters(left_leg, 34, 21, -1.5F, 0.6F, -1.9F, 3, 2, 3, 0.0F, false));
        left_leg.cubeList.add(new ModelBoxWithParameters(left_leg, 46, 26, -1.51F, 0.1F, -1.21F, 3, 1, 2, 0.0F, false));
        left_leg.cubeList.add(new ModelBoxWithParameters(left_leg, 51, 37, -1.51F, 1.9F, -0.1F, 3, 2, 1, 0.0F, false));
        left_leg.cubeList.add(new ModelBoxWithParameters(left_leg, 53, 5, -1.49F, 1.8F, -2.3F, 3, 1, 1, 0.0F, false));
        left_leg.cubeList.add(new ModelBoxWithParameters(left_leg, 51, 46, -1.49F, 0.6F, -2.3F, 3, 1, 1, 0.0F, false));
        left_leg.cubeList.add(new ModelBoxWithParameters(left_leg, 45, 5, -1.5F, 2.6F, -1.3F, 3, 2, 2, 0.0F, false));
        left_leg.cubeList.add(new ModelBoxWithParameters(left_leg, 29, 16, -1.49F, 3.7F, -1.7F, 3, 2, 1, 0.0F, false));
        left_leg.cubeList.add(new ModelBoxWithParameters(left_leg, 53, 41, -1.0F, 3.2F, -1.79F, 2, 3, 1, 0.0F, false));
        left_leg.cubeList.add(new ModelBoxWithParameters(left_leg, 8, 45, -1.5F, 4.2F, -1.3F, 3, 2, 2, 0.0F, false));

        left_cup = new RendererModel(this);
        left_cup.setRotationPoint(0.0F, 6.1F, -1.21F);
        setRotationAngle(left_cup, 0.6109F, 0.0F, 0.0F);
        left_leg.addChild(left_cup);
        left_cup.cubeList.add(new ModelBoxWithParameters(left_cup, 48, 9, -1.0F, -1.0F, -0.1F, 2, 2, 2, 0.0F, false));

        left_shine = new RendererModel(this);
        left_shine.setRotationPoint(0.0F, 0.3F, 0.51F);
        setRotationAngle(left_shine, 0.4363F, 0.0F, 0.0F);
        left_cup.addChild(left_shine);
        left_shine.cubeList.add(new ModelBoxWithParameters(left_shine, 12, 39, -1.5F, 0.1F, -0.6F, 3, 4, 2, 0.0F, false));
        left_shine.cubeList.add(new ModelBoxWithParameters(left_shine, 43, 53, -1.0F, 1.2F, -1.1F, 2, 3, 1, 0.0F, false));
        left_shine.cubeList.add(new ModelBoxWithParameters(left_shine, 37, 52, -1.0F, 1.1F, 0.9F, 2, 3, 1, 0.0F, false));

        left_step = new RendererModel(this);
        left_step.setRotationPoint(0.0F, 4.4F, 0.5F);
        setRotationAngle(left_step, -0.1745F, 0.0F, 0.0F);
        left_shine.addChild(left_step);
        left_step.cubeList.add(new ModelBoxWithParameters(left_step, 30, 0, -1.5F, -0.3F, -3.1F, 3, 1, 4, 0.0F, false));
        left_step.cubeList.add(new ModelBoxWithParameters(left_step, 37, 26, -1.499F, -0.6F, -2.6F, 3, 1, 3, 0.0F, false));
        left_step.cubeList.add(new ModelBoxWithParameters(left_step, 40, 30, -1.0F, -0.9F, -2.1F, 2, 1, 3, 0.0F, false));

        press = new RendererModel(this);
        press.setRotationPoint(0.0F, -13.7667F, 1.2F);
        setRotationAngle(press, 0.1745F, 0.0873F, 0.0F);
        body.addChild(press);
        press.cubeList.add(new ModelBoxWithParameters(press, 17, 23, -3.5F, -2.5333F, -1.5F, 7, 4, 3, 0.0F, false));
        press.cubeList.add(new ModelBoxWithParameters(press, 46, 22, -2.0F, -1.3333F, 1.0F, 4, 3, 1, 0.0F, false));
        press.cubeList.add(new ModelBoxWithParameters(press, 35, 36, -2.5F, -1.6333F, -2.0F, 5, 4, 1, 0.0F, false));

        chest = new RendererModel(this);
        chest.setRotationPoint(0.0F, -2.0333F, -0.6F);
        setRotationAngle(chest, 0.0F, 0.1745F, 0.0F);
        press.addChild(chest);
        chest.cubeList.add(new ModelBoxWithParameters(chest, 11, 15, -3.5F, -4.5F, -1.9F, 7, 4, 4, 0.0F, false));
        chest.cubeList.add(new ModelBoxWithParameters(chest, 0, 51, -3.501F, -4.0F, -2.2F, 3, 3, 1, 0.0F, false));
        chest.cubeList.add(new ModelBoxWithParameters(chest, 9, 50, 0.501F, -4.0F, -2.2F, 3, 3, 1, 0.0F, false));
        chest.cubeList.add(new ModelBoxWithParameters(chest, 12, 54, -0.499F, -4.4F, -2.4F, 1, 4, 1, 0.0F, false));
        chest.cubeList.add(new ModelBoxWithParameters(chest, 54, 24, -0.5F, -0.6F, -1.9F, 1, 1, 2, 0.0F, false));
        chest.cubeList.add(new ModelBoxWithParameters(chest, 18, 9, -3.499F, -0.8F, -1.4F, 7, 1, 1, 0.0F, false));
        chest.cubeList.add(new ModelBoxWithParameters(chest, 0, 32, -3.0F, -4.501F, 1.7F, 6, 4, 1, 0.0F, false));
        chest.cubeList.add(new ModelBoxWithParameters(chest, 13, 36, -2.99F, -4.7F, 1.901F, 6, 2, 1, 0.0F, false));
        chest.cubeList.add(new ModelBoxWithParameters(chest, 43, 13, -2.0F, -3.3F, 1.9F, 4, 4, 1, 0.0F, false));

        head = new RendererModel(this);
        head.setRotationPoint(0.1F, -3.6F, 0.64F);
        chest.addChild(head);
        head.cubeList.add(new ModelBoxWithParameters(head, 0, 0, -1.5F, -3.9F, -1.74F, 3, 3, 3, 0.0F, false));
        head.cubeList.add(new ModelBoxWithParameters(head, 44, 18, -1.0F, -4.2F, -1.84F, 2, 1, 3, 0.0F, false));
        head.cubeList.add(new ModelBoxWithParameters(head, 54, 0, 0.7F, -3.5F, -1.14F, 1, 2, 2, 0.0F, false));
        head.cubeList.add(new ModelBoxWithParameters(head, 49, 55, -1.0F, -3.5F, 0.46F, 2, 2, 1, 0.0F, false));
        head.cubeList.add(new ModelBoxWithParameters(head, 6, 54, -1.7F, -3.5F, -1.24F, 1, 2, 2, 0.0F, false));

        arms = new RendererModel(this);
        arms.setRotationPoint(0.0F, -4.6F, 0.0F);
        setRotationAngle(arms, 0.0F, 0.0873F, 0.0F);
        chest.addChild(arms);

        right_arm = new RendererModel(this);
        right_arm.setRotationPoint(-3.5F, 1.0F, 0.15F);
        setRotationAngle(right_arm, -0.6109F, 0.6981F, 0.0F);
        arms.addChild(right_arm);
        right_arm.cubeList.add(new ModelBoxWithParameters(right_arm, 30, 30, -2.7F, -0.8F, -1.95F, 3, 2, 4, 0.0F, false));
        right_arm.cubeList.add(new ModelBoxWithParameters(right_arm, 42, 42, -3.0F, -0.3F, -2.05F, 1, 1, 4, 0.0F, false));
        right_arm.cubeList.add(new ModelBoxWithParameters(right_arm, 23, 36, -2.0F, -1.1F, -2.05F, 2, 1, 4, 0.0F, false));
        right_arm.cubeList.add(new ModelBoxWithParameters(right_arm, 22, 41, -2.4F, 0.7F, -1.45F, 2, 3, 3, 0.0F, false));
        right_arm.cubeList.add(new ModelBoxWithParameters(right_arm, 34, 47, -2.41F, 2.7F, -0.95F, 2, 3, 2, 0.0F, false));
        right_arm.cubeList.add(new ModelBoxWithParameters(right_arm, 17, 51, -2.6F, 0.6F, -0.45F, 2, 5, 1, 0.0F, false));
        right_arm.cubeList.add(new ModelBoxWithParameters(right_arm, 48, 42, -2.7F, 1.7F, -1.46F, 1, 1, 3, 0.0F, false));

        right_cubit = new RendererModel(this);
        right_cubit.setRotationPoint(-1.5F, 5.75F, -0.05F);
        setRotationAngle(right_cubit, 0.0F, -1.3963F, -1.5708F);
        right_arm.addChild(right_cubit);
        right_cubit.cubeList.add(new ModelBoxWithParameters(right_cubit, 30, 0, -0.5F, -0.5F, -0.4F, 1, 1, 1, 0.0F, false));

        right_preshoulder = new RendererModel(this);
        right_preshoulder.setRotationPoint(0.0F, 0.45F, 0.1F);
        setRotationAngle(right_preshoulder, 0.3491F, 0.0F, -0.4363F);
        right_cubit.addChild(right_preshoulder);
        right_preshoulder.cubeList.add(new ModelBoxWithParameters(right_preshoulder, 18, 0, -0.9F, -0.3F, -1.0F, 2, 6, 2, 0.0F, false));
        right_preshoulder.cubeList.add(new ModelBoxWithParameters(right_preshoulder, 23, 52, -1.2F, 0.6F, -0.4F, 2, 5, 1, 0.0F, false));

        weapon = new RendererModel(this);
        weapon.setRotationPoint(0.0F, 5.7F, -0.2F);
        setRotationAngle(weapon, 0.8727F, 0.1745F, -0.3491F);
        right_preshoulder.addChild(weapon);
        weapon.cubeList.add(new ModelBoxWithParameters(weapon, 0, 0, -1.3F, -3.6F, -11.2F, 3, 3, 12, 0.0F, false));
        weapon.cubeList.add(new ModelBoxWithParameters(weapon, 0, 15, -0.3F, -4.7F, -9.2F, 1, 1, 9, 0.0F, false));
        weapon.cubeList.add(new ModelBoxWithParameters(weapon, 11, 15, -0.3F, -4.3F, -9.2F, 1, 1, 1, 0.0F, false));
        weapon.cubeList.add(new ModelBoxWithParameters(weapon, 8, 6, -0.3F, -4.3F, -1.2F, 1, 1, 1, 0.0F, false));
        weapon.cubeList.add(new ModelBoxWithParameters(weapon, 26, 30, -0.8F, -1.0F, -2.1F, 2, 2, 2, 0.0F, false));
        weapon.cubeList.add(new ModelBoxWithParameters(weapon, 29, 10, -0.8F, -1.0F, -9.4F, 2, 1, 5, 0.0F, false));
        weapon.cubeList.add(new ModelBoxWithParameters(weapon, 54, 48, -0.8F, -3.1F, -11.5F, 2, 2, 1, 0.0F, false));
        weapon.cubeList.add(new ModelBoxWithParameters(weapon, 8, 8, -0.3F, -2.7F, -11.7F, 1, 1, 1, 0.0F, false));
        weapon.cubeList.add(new ModelBoxWithParameters(weapon, 18, 0, -0.9F, -2.9F, -5.2F, 2, 1, 8, 0.0F, false));
        weapon.cubeList.add(new ModelBoxWithParameters(weapon, 6, 42, -0.9F, -3.4F, 2.8F, 2, 2, 1, 0.0F, false));
        weapon.cubeList.add(new ModelBoxWithParameters(weapon, 0, 6, -0.5F, -3.4F, 0.5F, 1, 2, 1, 0.0F, false));

        left_arm = new RendererModel(this);
        left_arm.setRotationPoint(3.5F, 1.0F, 0.15F);
        setRotationAngle(left_arm, 0.1745F, 0.0F, 0.0F);
        arms.addChild(left_arm);
        left_arm.cubeList.add(new ModelBoxWithParameters(left_arm, 16, 30, -0.3F, -0.8F, -1.95F, 3, 2, 4, 0.0F, false));
        left_arm.cubeList.add(new ModelBoxWithParameters(left_arm, 0, 42, 2.0F, -0.3F, -2.05F, 1, 1, 4, 0.0F, false));
        left_arm.cubeList.add(new ModelBoxWithParameters(left_arm, 0, 6, 0.0F, -1.1F, -2.05F, 2, 1, 4, 0.0F, false));
        left_arm.cubeList.add(new ModelBoxWithParameters(left_arm, 38, 7, 0.4F, 0.7F, -1.45F, 2, 3, 3, 0.0F, false));
        left_arm.cubeList.add(new ModelBoxWithParameters(left_arm, 26, 47, 0.41F, 2.7F, -0.95F, 2, 3, 2, 0.0F, false));
        left_arm.cubeList.add(new ModelBoxWithParameters(left_arm, 48, 48, 0.6F, 0.6F, -0.45F, 2, 5, 1, 0.0F, false));
        left_arm.cubeList.add(new ModelBoxWithParameters(left_arm, 47, 29, 1.7F, 1.7F, -1.46F, 1, 1, 3, 0.0F, false));

        left_cubit = new RendererModel(this);
        left_cubit.setRotationPoint(1.5F, 5.75F, -0.05F);
        setRotationAngle(left_cubit, -0.7854F, 0.0F, 0.0F);
        left_arm.addChild(left_cubit);
        left_cubit.cubeList.add(new ModelBoxWithParameters(left_cubit, 11, 17, -0.5F, -0.5F, -0.4F, 1, 1, 1, 0.0F, false));

        left_preshoulder = new RendererModel(this);
        left_preshoulder.setRotationPoint(0.0F, 0.45F, 0.1F);
        setRotationAngle(left_preshoulder, -0.5236F, 0.0F, 0.0F);
        left_cubit.addChild(left_preshoulder);
        left_preshoulder.cubeList.add(new ModelBoxWithParameters(left_preshoulder, 0, 15, -1.1F, -0.3F, -0.9F, 2, 6, 2, 0.0F, false));
        left_preshoulder.cubeList.add(new ModelBoxWithParameters(left_preshoulder, 42, 47, -0.8F, 0.6F, -0.4F, 2, 5, 1, 0.0F, false));
    }

    @Override
    public void render(ShootyEntity entity, float f, float f1, float f2, float f3, float f4, float f5) {
        shooty.render(f5);
    }

    public void setRotationAngle(RendererModel modelRenderer, float x, float y, float z) {
        modelRenderer.rotateAngleX = x;
        modelRenderer.rotateAngleY = y;
        modelRenderer.rotateAngleZ = z;
    }
}