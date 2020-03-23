package com.e33.client.model;

import com.e33.client.animation.animator.Animator;
import com.e33.client.animation.animator.ShootyAnimator;
import com.e33.client.detail.modelBox.ModelBoxWithParameters;
import com.e33.entity.ShootyEntity;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.client.renderer.entity.model.RendererModel;

public class ShootyModel<T extends ShootyEntity> extends EntityModel<T> implements DynamicAnimationInterface {
    protected Animator animator;

    protected RendererModel shooty;
    protected RendererModel body;
    protected RendererModel hips;
    protected RendererModel legs;
    protected RendererModel right_leg;
    protected RendererModel right_cup;
    protected RendererModel right_shine;
    protected RendererModel right_step;
    protected RendererModel left_leg;
    protected RendererModel left_cup;
    protected RendererModel left_shine;
    protected RendererModel left_step;
    protected RendererModel press;
    protected RendererModel chest;
    protected RendererModel head;
    protected RendererModel arms;
    protected RendererModel right_arm;
    protected RendererModel right_cubit;
    protected RendererModel right_preshoulder;
    protected RendererModel left_arm;
    protected RendererModel left_cubit;
    protected RendererModel left_preshoulder;

    public ShootyModel() {
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
        hips.cubeList.add(new ModelBoxWithParameters(hips, 0, 15, -3.5F, -0.05F, -1.4F, 7, 2, 3, 0.0F, false));
        hips.cubeList.add(new ModelBoxWithParameters(hips, 23, 29, -3.5F, 0.55F, 0.9F, 7, 1, 1, 0.0F, false));
        hips.cubeList.add(new ModelBoxWithParameters(hips, 27, 5, -3.5F, 0.55F, -1.7F, 7, 1, 1, 0.0F, false));
        hips.cubeList.add(new ModelBoxWithParameters(hips, 20, 47, 2.8F, 0.55F, -1.4F, 1, 1, 3, 0.0F, false));
        hips.cubeList.add(new ModelBoxWithParameters(hips, 45, 46, -3.8F, 0.55F, -1.4F, 1, 1, 3, 0.0F, false));

        legs = new RendererModel(this);
        legs.setRotationPoint(0.0F, 11.75F, 1.4F);
        hips.addChild(legs);

        right_leg = new RendererModel(this);
        right_leg.setRotationPoint(-2.0F, -10.4F, -1.2F);
        setRotationAngle(right_leg, 0.0F, 0.2618F, 0.0F);
        legs.addChild(right_leg);
        right_leg.cubeList.add(new ModelBoxWithParameters(right_leg, 34, 7, -1.5F, 0.6F, -1.9F, 3, 2, 3, 0.0F, false));
        right_leg.cubeList.add(new ModelBoxWithParameters(right_leg, 30, 44, -1.49F, 0.1F, -1.21F, 3, 1, 2, 0.0F, false));
        right_leg.cubeList.add(new ModelBoxWithParameters(right_leg, 47, 26, -1.49F, 1.9F, -0.1F, 3, 2, 1, 0.0F, false));
        right_leg.cubeList.add(new ModelBoxWithParameters(right_leg, 50, 13, -1.49F, 1.8F, -2.3F, 3, 1, 1, 0.0F, false));
        right_leg.cubeList.add(new ModelBoxWithParameters(right_leg, 34, 12, -1.49F, 0.6F, -2.3F, 3, 1, 1, 0.0F, false));
        right_leg.cubeList.add(new ModelBoxWithParameters(right_leg, 10, 43, -1.5F, 2.6F, -1.3F, 3, 2, 2, 0.0F, false));
        right_leg.cubeList.add(new ModelBoxWithParameters(right_leg, 48, 41, -1.49F, 3.7F, -1.7F, 3, 2, 1, 0.0F, false));
        right_leg.cubeList.add(new ModelBoxWithParameters(right_leg, 50, 35, -1.0F, 3.2F, -1.79F, 2, 3, 1, 0.0F, false));
        right_leg.cubeList.add(new ModelBoxWithParameters(right_leg, 43, 4, -1.5F, 4.2F, -1.3F, 3, 2, 2, 0.0F, false));

        right_cup = new RendererModel(this);
        right_cup.setRotationPoint(0.0F, 6.1F, -1.21F);
        right_leg.addChild(right_cup);
        right_cup.cubeList.add(new ModelBoxWithParameters(right_cup, 45, 19, -1.0F, -1.0F, -0.1F, 2, 2, 2, 0.0F, false));

        right_shine = new RendererModel(this);
        right_shine.setRotationPoint(0.0F, 0.3F, 0.51F);
        right_cup.addChild(right_shine);
        right_shine.cubeList.add(new ModelBoxWithParameters(right_shine, 0, 53, -1.5F, 0.1F, -0.6F, 3, 4, 2, 0.0F, false));
        right_shine.cubeList.add(new ModelBoxWithParameters(right_shine, 50, 44, -1.0F, 1.2F, -1.1F, 2, 3, 1, 0.0F, false));
        right_shine.cubeList.add(new ModelBoxWithParameters(right_shine, 39, 49, -1.0F, 1.1F, 0.9F, 2, 3, 1, 0.0F, false));

        right_step = new RendererModel(this);
        right_step.setRotationPoint(0.0F, 4.4F, 0.5F);
        right_shine.addChild(right_step);
        right_step.cubeList.add(new ModelBoxWithParameters(right_step, 0, 28, -1.5F, -0.3F, -3.1F, 3, 1, 4, 0.0F, false));
        right_step.cubeList.add(new ModelBoxWithParameters(right_step, 12, 39, -1.501F, -0.6F, -2.6F, 3, 1, 3, 0.0F, false));
        right_step.cubeList.add(new ModelBoxWithParameters(right_step, 43, 9, -1.001F, -0.9F, -2.1F, 2, 1, 3, 0.0F, false));

        left_leg = new RendererModel(this);
        left_leg.setRotationPoint(2.0F, -10.4F, -1.2F);
        setRotationAngle(left_leg, 0.0F, -0.2618F, 0.0F);
        legs.addChild(left_leg);
        left_leg.cubeList.add(new ModelBoxWithParameters(left_leg, 34, 7, -1.5F, 0.6F, -1.9F, 3, 2, 3, 0.0F, false));
        left_leg.cubeList.add(new ModelBoxWithParameters(left_leg, 30, 44, -1.51F, 0.1F, -1.21F, 3, 1, 2, 0.0F, false));
        left_leg.cubeList.add(new ModelBoxWithParameters(left_leg, 47, 26, -1.51F, 1.9F, -0.1F, 3, 2, 1, 0.0F, false));
        left_leg.cubeList.add(new ModelBoxWithParameters(left_leg, 50, 13, -1.49F, 1.8F, -2.3F, 3, 1, 1, 0.0F, false));
        left_leg.cubeList.add(new ModelBoxWithParameters(left_leg, 34, 12, -1.49F, 0.6F, -2.3F, 3, 1, 1, 0.0F, false));
        left_leg.cubeList.add(new ModelBoxWithParameters(left_leg, 10, 43, -1.5F, 2.6F, -1.3F, 3, 2, 2, 0.0F, false));
        left_leg.cubeList.add(new ModelBoxWithParameters(left_leg, 48, 41, -1.49F, 3.7F, -1.7F, 3, 2, 1, 0.0F, false));
        left_leg.cubeList.add(new ModelBoxWithParameters(left_leg, 50, 35, -1.0F, 3.2F, -1.79F, 2, 3, 1, 0.0F, false));
        left_leg.cubeList.add(new ModelBoxWithParameters(left_leg, 43, 4, -1.5F, 4.2F, -1.3F, 3, 2, 2, 0.0F, false));

        left_cup = new RendererModel(this);
        left_cup.setRotationPoint(0.0F, 6.1F, -1.21F);
        left_leg.addChild(left_cup);
        left_cup.cubeList.add(new ModelBoxWithParameters(left_cup, 45, 19, -1.0F, -1.0F, -0.1F, 2, 2, 2, 0.0F, false));

        left_shine = new RendererModel(this);
        left_shine.setRotationPoint(0.0F, 0.3F, 0.51F);
        left_cup.addChild(left_shine);
        left_shine.cubeList.add(new ModelBoxWithParameters(left_shine, 0, 53, -1.5F, 0.1F, -0.6F, 3, 4, 2, 0.0F, false));
        left_shine.cubeList.add(new ModelBoxWithParameters(left_shine, 50, 44, -1.0F, 1.2F, -1.1F, 2, 3, 1, 0.0F, false));
        left_shine.cubeList.add(new ModelBoxWithParameters(left_shine, 39, 49, -1.0F, 1.1F, 0.9F, 2, 3, 1, 0.0F, false));

        left_step = new RendererModel(this);
        left_step.setRotationPoint(0.0F, 4.4F, 0.5F);
        left_shine.addChild(left_step);
        left_step.cubeList.add(new ModelBoxWithParameters(left_step, 0, 28, -1.5F, -0.3F, -3.1F, 3, 1, 4, 0.0F, false));
        left_step.cubeList.add(new ModelBoxWithParameters(left_step, 12, 39, -1.499F, -0.6F, -2.6F, 3, 1, 3, 0.0F, false));
        left_step.cubeList.add(new ModelBoxWithParameters(left_step, 43, 9, -1.0F, -0.9F, -2.1F, 2, 1, 3, 0.0F, false));

        press = new RendererModel(this);
        press.setRotationPoint(0.0F, -13.7667F, 1.2F);
        body.addChild(press);
        press.cubeList.add(new ModelBoxWithParameters(press, 0, 8, -3.5F, -2.5333F, -1.5F, 7, 4, 3, 0.0F, false));
        press.cubeList.add(new ModelBoxWithParameters(press, 41, 0, -2.0F, -1.3333F, 1.0F, 4, 3, 1, 0.0F, false));
        press.cubeList.add(new ModelBoxWithParameters(press, 20, 34, -2.5F, -1.6333F, -2.0F, 5, 4, 1, 0.0F, false));

        chest = new RendererModel(this);
        chest.setRotationPoint(0.0F, -2.0333F, -0.6F);
        press.addChild(chest);
        chest.cubeList.add(new ModelBoxWithParameters(chest, 0, 0, -3.5F, -4.5F, -1.9F, 7, 4, 4, 0.0F, false));
        chest.cubeList.add(new ModelBoxWithParameters(chest, 40, 45, -3.501F, -4.0F, -2.2F, 3, 3, 1, 0.0F, false));
        chest.cubeList.add(new ModelBoxWithParameters(chest, 20, 10, 0.501F, -4.0F, -2.2F, 3, 3, 1, 0.0F, false));
        chest.cubeList.add(new ModelBoxWithParameters(chest, 0, 20, -0.499F, -4.4F, -2.4F, 1, 4, 1, 0.0F, false));
        chest.cubeList.add(new ModelBoxWithParameters(chest, 10, 28, -0.5F, -0.6F, -1.9F, 1, 1, 2, 0.0F, false));
        chest.cubeList.add(new ModelBoxWithParameters(chest, 24, 16, -3.499F, -0.8F, -1.4F, 7, 1, 1, 0.0F, false));
        chest.cubeList.add(new ModelBoxWithParameters(chest, 27, 0, -3.0F, -4.501F, 1.7F, 6, 4, 1, 0.0F, false));
        chest.cubeList.add(new ModelBoxWithParameters(chest, 26, 31, -2.99F, -4.7F, 1.901F, 6, 2, 1, 0.0F, false));
        chest.cubeList.add(new ModelBoxWithParameters(chest, 24, 39, -2.0F, -3.3F, 1.9F, 4, 4, 1, 0.0F, false));

        head = new RendererModel(this);
        head.setRotationPoint(0.1F, -3.6F, 0.64F);
        chest.addChild(head);
        head.cubeList.add(new ModelBoxWithParameters(head, 14, 28, -1.5F, -3.9F, -1.74F, 3, 3, 3, 0.0F, false));
        head.cubeList.add(new ModelBoxWithParameters(head, 41, 41, -1.0F, -4.2F, -1.84F, 2, 1, 3, 0.0F, false));
        head.cubeList.add(new ModelBoxWithParameters(head, 26, 50, 0.7F, -3.5F, -1.14F, 1, 2, 2, 0.0F, false));
        head.cubeList.add(new ModelBoxWithParameters(head, 9, 39, -1.0F, -3.5F, 0.46F, 2, 2, 1, 0.0F, false));
        head.cubeList.add(new ModelBoxWithParameters(head, 49, 29, -1.7F, -3.5F, -1.24F, 1, 2, 2, 0.0F, false));

        arms = new RendererModel(this);
        arms.setRotationPoint(0.0F, 15.0F, 2.8F);
        chest.addChild(arms);

        right_arm = new RendererModel(this);
        right_arm.setRotationPoint(-3.5F, -18.6F, -2.65F);
        setRotationAngle(right_arm, -0.6109F, 0.6981F, 0.0F);
        arms.addChild(right_arm);
        right_arm.cubeList.add(new ModelBoxWithParameters(right_arm, 24, 10, -2.7F, -0.8F, -1.95F, 3, 2, 4, 0.0F, false));
        right_arm.cubeList.add(new ModelBoxWithParameters(right_arm, 51, 1, -3.0F, -0.3F, -2.05F, 1, 1, 4, 0.0F, false));
        right_arm.cubeList.add(new ModelBoxWithParameters(right_arm, 8, 34, -2.0F, -1.1F, -2.05F, 2, 1, 4, 0.0F, false));
        right_arm.cubeList.add(new ModelBoxWithParameters(right_arm, 37, 21, -2.4F, 0.7F, -1.45F, 2, 3, 3, 0.0F, false));
        right_arm.cubeList.add(new ModelBoxWithParameters(right_arm, 44, 13, -2.41F, 2.7F, -0.95F, 2, 3, 2, 0.0F, false));
        right_arm.cubeList.add(new ModelBoxWithParameters(right_arm, 0, 47, -2.6F, 0.6F, -0.45F, 2, 5, 1, 0.0F, false));
        right_arm.cubeList.add(new ModelBoxWithParameters(right_arm, 34, 52, -2.7F, 1.7F, -1.46F, 1, 1, 3, 0.0F, false));

        right_cubit = new RendererModel(this);
        right_cubit.setRotationPoint(-1.5F, 5.75F, -0.05F);
        setRotationAngle(right_cubit, 0.0F, -1.3963F, -1.5708F);
        right_arm.addChild(right_cubit);
        right_cubit.cubeList.add(new ModelBoxWithParameters(right_cubit, 0, 2, -0.5F, -0.5F, -0.4F, 1, 1, 1, 0.0F, false));

        right_preshoulder = new RendererModel(this);
        right_preshoulder.setRotationPoint(0.0F, 0.45F, 0.1F);
        setRotationAngle(right_preshoulder, 0.3491F, 0.0F, -0.4363F);
        right_cubit.addChild(right_preshoulder);
        right_preshoulder.cubeList.add(new ModelBoxWithParameters(right_preshoulder, 17, 2, -0.9F, -0.3F, -1.0F, 2, 6, 2, 0.0F, false));
        right_preshoulder.cubeList.add(new ModelBoxWithParameters(right_preshoulder, 9, 22, -1.2F, 0.6F, -0.4F, 2, 5, 1, 0.0F, false));

        left_arm = new RendererModel(this);
        left_arm.setRotationPoint(3.5F, -18.6F, -2.65F);
        setRotationAngle(left_arm, 0.1745F, 0.0F, 0.0F);
        arms.addChild(left_arm);
        left_arm.cubeList.add(new ModelBoxWithParameters(left_arm, 24, 10, -0.3F, -0.8F, -1.95F, 3, 2, 4, 0.0F, true));
        left_arm.cubeList.add(new ModelBoxWithParameters(left_arm, 51, 1, 2.0F, -0.3F, -2.05F, 1, 1, 4, 0.0F, true));
        left_arm.cubeList.add(new ModelBoxWithParameters(left_arm, 8, 34, 0.0F, -1.1F, -2.05F, 2, 1, 4, 0.0F, true));
        left_arm.cubeList.add(new ModelBoxWithParameters(left_arm, 37, 21, 0.4F, 0.7F, -1.45F, 2, 3, 3, 0.0F, true));
        left_arm.cubeList.add(new ModelBoxWithParameters(left_arm, 44, 13, 0.41F, 2.7F, -0.95F, 2, 3, 2, 0.0F, true));
        left_arm.cubeList.add(new ModelBoxWithParameters(left_arm, 0, 47, 0.6F, 0.6F, -0.45F, 2, 5, 1, 0.0F, true));
        left_arm.cubeList.add(new ModelBoxWithParameters(left_arm, 34, 52, 1.7F, 1.7F, -1.46F, 1, 1, 3, 0.0F, true));

        left_cubit = new RendererModel(this);
        left_cubit.setRotationPoint(1.5F, 5.75F, -0.05F);
        setRotationAngle(left_cubit, -0.7854F, 0.0F, 0.0F);
        left_arm.addChild(left_cubit);
        left_cubit.cubeList.add(new ModelBoxWithParameters(left_cubit, 0, 2, -0.5F, -0.5F, -0.4F, 1, 1, 1, 0.0F, true));

        left_preshoulder = new RendererModel(this);
        left_preshoulder.setRotationPoint(0.0F, 0.45F, 0.1F);
        setRotationAngle(left_preshoulder, -0.5236F, 0.0F, 0.0F);
        left_cubit.addChild(left_preshoulder);
        left_preshoulder.cubeList.add(new ModelBoxWithParameters(left_preshoulder, 17, 2, -1.1F, -0.3F, -0.9F, 2, 6, 2, 0.0F, true));
        left_preshoulder.cubeList.add(new ModelBoxWithParameters(left_preshoulder, 9, 22, -0.8F, 0.6F, -0.4F, 2, 5, 1, 0.0F, true));
    }

    public RendererModel getMainRendererModel() {
        return this.shooty;
    }

    public void render(ShootyEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
        scale = 0.08F;
        shooty.render(scale);
    }

    public void setRotationAngles(ShootyEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
        if (this.animator == null) {
            this.animator = new ShootyAnimator(this);
        }

        this.animator.animate(entity);
    }

    public void setLivingAnimations(T entity, float limbSwing, float limbSwingAmount, float partialTick) {

    }

    protected void setRotationAngle(RendererModel modelRenderer, float x, float y, float z) {
        modelRenderer.rotateAngleX = x;
        modelRenderer.rotateAngleY = y;
        modelRenderer.rotateAngleZ = z;
    }
}