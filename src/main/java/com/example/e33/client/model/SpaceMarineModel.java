package com.example.e33.client.model;

import com.example.e33.entity.EntityGolemShooter;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.client.renderer.entity.model.RendererModel;
import net.minecraft.client.renderer.model.ModelBox;
import org.lwjgl.opengl.GL11;


public class SpaceMarineModel<T extends EntityGolemShooter> extends EntityModel<T> {
    private RendererModel golem;
    private RendererModel body;
    private RendererModel press;
    private RendererModel legs;
    private RendererModel right_leg;
    private RendererModel left_leg;
    private RendererModel hands;
    private RendererModel left_hand;
    private RendererModel right_hand;
    private RendererModel head;
    private RendererModel left_neck_plate;
    private RendererModel right_neck_plate;

    public SpaceMarineModel() {
        this.textureWidth = 64;
        this.textureHeight = 64;

        this.golem = new RendererModel(this);
        this.golem.setRotationPoint(0.0F, 13.3F, 0.0F);

        this.body = new RendererModel(this);
        this.body.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.golem.addChild(body);
        this.body.cubeList.add(new ModelBox(body, 0, 0, -3.5F, -6.3448F, -1.7007F, 7, 4, 4, 0.0F, false));
        this.body.cubeList.add(new ModelBox(body, 14, 25, -1.5F, -6.1448F, -2.7007F, 3, 7, 1, 0.0F, false));
        this.body.cubeList.add(new ModelBox(body, 18, 0, -3.5F, -6.2448F, -2.3007F, 7, 3, 1, 0.0F, false));

        this.press = new RendererModel(this);
        this.press.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.body.addChild(press);
        this.press.cubeList.add(new ModelBox(press, 0, 8, -2.5F, -2.3448F, -0.7007F, 5, 6, 3, 0.0F, false));

        this.legs = new RendererModel(this);
        this.legs.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.golem.addChild(legs);

        this.right_leg = new RendererModel(this);
        this.right_leg.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.legs.addChild(right_leg);
        this.right_leg.cubeList.add(new ModelBox(right_leg, 34, 9, -2.5F, 3.6552F, -0.7007F, 2, 3, 2, 0.0F, false));
        this.right_leg.cubeList.add(new ModelBox(right_leg, 22, 29, -2.5F, 6.6552F, -0.7007F, 2, 3, 3, 0.0F, false));
        this.right_leg.cubeList.add(new ModelBox(right_leg, 0, 24, -2.5F, 9.6552F, -2.7007F, 2, 1, 5, 0.0F, false));
        this.right_leg.cubeList.add(new ModelBox(right_leg, 28, 21, -2.5F, 8.6552F, -1.7007F, 2, 1, 1, 0.0F, false));

        this.left_leg = new RendererModel(this);
        this.left_leg.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.legs.addChild(left_leg);
        this.left_leg.cubeList.add(new ModelBox(left_leg, 16, 33, 0.5F, 3.6552F, -0.7007F, 2, 3, 2, 0.0F, false));
        this.left_leg.cubeList.add(new ModelBox(left_leg, 27, 15, 0.5F, 6.6552F, -0.7007F, 2, 3, 3, 0.0F, false));
        this.left_leg.cubeList.add(new ModelBox(left_leg, 23, 23, 0.5F, 9.6552F, -2.7007F, 2, 1, 5, 0.0F, false));
        this.left_leg.cubeList.add(new ModelBox(left_leg, 9, 24, 0.5F, 8.6552F, -1.7007F, 2, 1, 1, 0.0F, false));

        this.hands = new RendererModel(this);
        this.hands.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.golem.addChild(hands);

        this.left_hand = new RendererModel(this);
        this.left_hand.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.hands.addChild(left_hand);
        this.left_hand.cubeList.add(new ModelBox(left_hand, 14, 18, 3.5F, -6.3448F, -1.7007F, 3, 3, 4, 0.0F, false));
        this.left_hand.cubeList.add(new ModelBox(left_hand, 32, 21, 3.5F, -3.3448F, -0.7007F, 2, 3, 2, 0.0F, false));
        this.left_hand.cubeList.add(new ModelBox(left_hand, 32, 32, 3.5F, -0.3448F, -0.7007F, 2, 3, 2, 0.0F, false));
        this.left_hand.cubeList.add(new ModelBox(left_hand, 22, 25, 3.5F, 2.6552F, -0.7007F, 1, 1, 2, 0.0F, false));
        this.left_hand.cubeList.add(new ModelBox(left_hand, 34, 14, 5.1F, -1.3448F, -0.7007F, 1, 2, 2, 0.0F, false));
        this.left_hand.cubeList.add(new ModelBox(left_hand, 12, 13, 1.4F, -7.2448F, -1.7007F, 5, 1, 4, 0.0F, false));

        this.right_hand = new RendererModel(this);
        this.right_hand.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.hands.addChild(right_hand);
        this.right_hand.cubeList.add(new ModelBox(right_hand, 0, 17, -6.5F, -6.3448F, -1.7007F, 3, 3, 4, 0.0F, false));
        this.right_hand.cubeList.add(new ModelBox(right_hand, 8, 31, -5.5F, -3.3448F, -0.7007F, 2, 3, 2, 0.0F, false));
        this.right_hand.cubeList.add(new ModelBox(right_hand, 30, 4, -5.5F, -0.3448F, -0.7007F, 2, 3, 2, 0.0F, false));
        this.right_hand.cubeList.add(new ModelBox(right_hand, 13, 8, -4.5F, 2.6552F, -0.7007F, 1, 1, 2, 0.0F, false));
        this.right_hand.cubeList.add(new ModelBox(right_hand, 12, 13, -6.5F, -7.2448F, -1.7007F, 5, 1, 4, 0.0F, false));
        this.right_hand.cubeList.add(new ModelBox(right_hand, 34, 0, -6.1F, -1.3448F, -0.7007F, 1, 2, 2, 0.0F, false));

        this.head = new RendererModel(this);
        this.head.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.golem.addChild(head);
        this.head.cubeList.add(new ModelBox(head, 22, 4, -1.0F, -8.3448F, -0.7007F, 2, 2, 2, 0.0F, false));
        this.head.cubeList.add(new ModelBox(head, 0, 30, -0.5F, -8.7448F, -0.6007F, 1, 2, 3, 0.0F, false));
        this.head.cubeList.add(new ModelBox(head, 16, 38, -1.5F, -7.3448F, 1.2993F, 3, 1, 1, 0.0F, true));

        this.left_neck_plate = new RendererModel(this);
        this.left_neck_plate.setRotationPoint(0.0F, 0.0F, 0.0F);
        setRotationAngle(left_neck_plate, 0.0F, -0.5236F, 0.0F);
        this.head.addChild(left_neck_plate);
        this.left_neck_plate.cubeList.add(new ModelBox(left_neck_plate, 26, 13, -1.3F, -7.1448F, -2.2807F, 3, 1, 1, 0.0F, false));

        this.right_neck_plate = new RendererModel(this);
        this.right_neck_plate.setRotationPoint(0.0F, 0.0F, 0.0F);
        setRotationAngle(right_neck_plate, 0.0F, 0.5236F, 0.0F);
        this.head.addChild(right_neck_plate);
        this.right_neck_plate.cubeList.add(new ModelBox(right_neck_plate, 10, 18, -1.6F, -7.1448F, -2.2007F, 3, 1, 1, 0.0F, false));
    }

    @Override
    public void render(EntityGolemShooter entity, float f, float f1, float f2, float f3, float f4, float f5) {
        float scaleFactor = 2F;
        GL11.glPushMatrix();
        GL11.glTranslatef(0F, 1.5F - 1.5F * scaleFactor, 0F);
        GL11.glScalef(scaleFactor, scaleFactor, scaleFactor);
        this.golem.render(f5);
        GL11.glPopMatrix();
    }

    public void setRotationAngle(RendererModel modelRenderer, float x, float y, float z) {
        modelRenderer.rotateAngleX = x;
        modelRenderer.rotateAngleY = y;
        modelRenderer.rotateAngleZ = z;
    }
}