package com.e33.client.model;

import com.e33.entity.EntityGolemShooter;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.client.renderer.entity.model.RendererModel;
import net.minecraft.client.renderer.model.ModelBox;


public class SpaceMarineModel2<T extends EntityGolemShooter> extends EntityModel<T> {
    private RendererModel golem;
    private RendererModel body;
    private RendererModel bone4;
    private RendererModel bone5;
    private RendererModel bone7;
    private RendererModel bone6;
    private RendererModel head;
    private RendererModel nose;
    private RendererModel right_cheekbone;
    private RendererModel left_cheekbone;
    private RendererModel bone;
    private RendererModel bone2;
    private RendererModel bone3;
    private RendererModel hands;
    private RendererModel left;
    private RendererModel bone10;
    private RendererModel bone9;
    private RendererModel bone8;
    private RendererModel right;
    private RendererModel bone11;
    private RendererModel bone12;
    private RendererModel bone13;
    private RendererModel legs;
    private RendererModel left2;
    private RendererModel bone14;
    private RendererModel bone15;
    private RendererModel bone16;
    private RendererModel right2;
    private RendererModel bone17;
    private RendererModel bone22;
    private RendererModel bone23;
    private RendererModel bone18;
    private RendererModel bone19;
    private RendererModel bone20;
    private RendererModel bone21;

    public SpaceMarineModel2() {
        this.textureWidth = 128;
        this.textureHeight = 128;

        golem = new RendererModel(this);
        golem.setRotationPoint(0.0F, 16.0F, 0.0F);

        body = new RendererModel(this);
        body.setRotationPoint(0.0F, 0.0F, 0.0F);
        golem.addChild(body);
        body.cubeList.add(new ModelBox(body, 0, 0, -5.0F, -21.0F, -3.0F, 9, 6, 7, 0.0F, false));
        body.cubeList.add(new ModelBox(body, 0, 13, -4.5F, -15.0F, -1.0F, 8, 7, 5, 0.0F, false));
        body.cubeList.add(new ModelBox(body, 24, 24, -4.999F, -23.4F, 3.1F, 9, 12, 2, 0.0F, false));
        body.cubeList.add(new ModelBox(body, 22, 47, -3.999F, -22.2F, 3.3F, 7, 10, 2, 0.0F, false));
        body.cubeList.add(new ModelBox(body, 21, 14, -3.0F, -20.0F, -3.3F, 5, 3, 1, 0.0F, false));
        body.cubeList.add(new ModelBox(body, 24, 68, -3.0F, -15.0F, -1.5F, 5, 9, 1, 0.0F, false));
        body.cubeList.add(new ModelBox(body, 72, 12, -3.0F, -12.0F, 3.2F, 5, 6, 1, 0.0F, false));
        body.cubeList.add(new ModelBox(body, 58, 52, -2.5F, -19.1F, -3.3F, 4, 3, 1, 0.0F, false));
        body.cubeList.add(new ModelBox(body, 16, 25, -2.0F, -18.4F, -3.3F, 3, 3, 1, 0.0F, false));
        body.cubeList.add(new ModelBox(body, 50, 7, -1.0F, -13.0F, 4.4F, 1, 1, 1, 0.0F, false));
        body.cubeList.add(new ModelBox(body, 48, 39, -1.0F, -15.0F, 4.4F, 1, 1, 1, 0.0F, false));
        body.cubeList.add(new ModelBox(body, 48, 23, -1.0F, -17.0F, 4.4F, 1, 1, 1, 0.0F, false));
        body.cubeList.add(new ModelBox(body, 46, 10, -1.0F, -19.0F, 4.4F, 1, 1, 1, 0.0F, false));
        body.cubeList.add(new ModelBox(body, 46, 7, -1.0F, -21.0F, 4.4F, 1, 1, 1, 0.0F, false));
        body.cubeList.add(new ModelBox(body, 25, 5, -1.0F, -23.0F, 4.4F, 1, 1, 1, 0.0F, false));

        bone4 = new RendererModel(this);
        bone4.setRotationPoint(0.0F, 0.0F, 0.0F);
        setRotationAngle(bone4, 0.2618F, 0.0F, 0.0F);
        body.addChild(bone4);
        bone4.cubeList.add(new ModelBox(bone4, 46, 71, -4.499F, -15.3F, 1.0F, 2, 7, 3, 0.0F, false));
        bone4.cubeList.add(new ModelBox(bone4, 36, 71, 1.499F, -15.3F, 1.0F, 2, 7, 3, 0.0F, false));

        bone5 = new RendererModel(this);
        bone5.setRotationPoint(0.0F, 0.0F, 0.0F);
        setRotationAngle(bone5, 0.4363F, 0.0F, 0.0F);
        body.addChild(bone5);
        bone5.cubeList.add(new ModelBox(bone5, 0, 74, -3.0F, -15.0F, 3.7F, 5, 5, 1, 0.0F, false));

        bone7 = new RendererModel(this);
        bone7.setRotationPoint(0.0F, 0.0F, 0.0F);
        setRotationAngle(bone7, 0.1745F, 0.0F, 0.0F);
        body.addChild(bone7);
        bone7.cubeList.add(new ModelBox(bone7, 52, 63, -4.99F, -22.5F, 1.0F, 2, 2, 6, 0.0F, false));
        bone7.cubeList.add(new ModelBox(bone7, 62, 15, 1.99F, -22.5F, 1.0F, 2, 2, 6, 0.0F, false));

        bone6 = new RendererModel(this);
        bone6.setRotationPoint(0.0F, 0.0F, 0.0F);
        setRotationAngle(bone6, -0.2618F, 0.0F, 0.0F);
        body.addChild(bone6);
        bone6.cubeList.add(new ModelBox(bone6, 25, 0, -4.998F, -12.4F, 0.9F, 9, 1, 1, 0.0F, false));

        head = new RendererModel(this);
        head.setRotationPoint(0.0F, -18.0F, 0.0F);
        golem.addChild(head);
        head.cubeList.add(new ModelBox(head, 12, 74, -2.0F, -6.0F, -1.0F, 3, 3, 3, 0.0F, false));
        head.cubeList.add(new ModelBox(head, 40, 39, -1.999F, -4.001F, -2.0F, 3, 1, 1, 0.0F, false));
        head.cubeList.add(new ModelBox(head, 60, 0, -1.0F, -4.0F, -2.2F, 1, 1, 1, 0.0F, false));
        head.cubeList.add(new ModelBox(head, 0, 51, -1.0F, -6.4F, -0.1F, 1, 1, 2, 0.0F, false));
        head.cubeList.add(new ModelBox(head, 19, 60, 0.3F, -5.0F, 0.0F, 1, 1, 1, 0.0F, false));
        head.cubeList.add(new ModelBox(head, 15, 60, -2.3F, -5.0F, 0.0F, 1, 1, 1, 0.0F, false));
        head.cubeList.add(new ModelBox(head, 42, 7, -2.7F, -4.002F, -0.13F, 1, 1, 2, 0.0F, false));
        head.cubeList.add(new ModelBox(head, 40, 41, 0.7F, -4.001F, -0.13F, 1, 1, 2, 0.0F, false));
        head.cubeList.add(new ModelBox(head, 16, 29, -2.19F, -4.001F, 1.75F, 3, 1, 1, 0.0F, false));
        head.cubeList.add(new ModelBox(head, 26, 18, -1.5F, -6.3F, -0.5F, 2, 1, 2, 0.0F, false));
        head.cubeList.add(new ModelBox(head, 58, 28, 0.2F, -3.998F, 1.75F, 1, 1, 1, 0.0F, false));

        nose = new RendererModel(this);
        nose.setRotationPoint(0.0F, 0.0F, 0.0F);
        setRotationAngle(nose, -0.4363F, 0.0F, 0.0F);
        head.addChild(nose);
        nose.cubeList.add(new ModelBox(nose, 0, 58, -1.0F, -4.8F, -3.5F, 1, 2, 1, 0.0F, false));

        right_cheekbone = new RendererModel(this);
        right_cheekbone.setRotationPoint(0.0F, 0.0F, 0.0F);
        setRotationAngle(right_cheekbone, 0.0F, -0.3491F, 0.0F);
        head.addChild(right_cheekbone);
        right_cheekbone.cubeList.add(new ModelBox(right_cheekbone, 44, 23, -2.565F, -4.001F, -1.2F, 1, 1, 2, 0.0F, false));

        left_cheekbone = new RendererModel(this);
        left_cheekbone.setRotationPoint(0.0F, 0.0F, 0.0F);
        setRotationAngle(left_cheekbone, 0.0F, 0.3491F, 0.0F);
        head.addChild(left_cheekbone);
        left_cheekbone.cubeList.add(new ModelBox(left_cheekbone, 42, 10, 0.635F, -4.002F, -1.536F, 1, 1, 2, 0.0F, false));

        bone = new RendererModel(this);
        bone.setRotationPoint(0.0F, 0.0F, 0.0F);
        setRotationAngle(bone, 0.0F, 0.5236F, 0.0F);
        head.addChild(bone);
        bone.cubeList.add(new ModelBox(bone, 36, 59, -3.27F, -3.999F, 0.27F, 1, 1, 1, 0.0F, false));

        bone2 = new RendererModel(this);
        bone2.setRotationPoint(0.0F, 0.0F, 0.0F);
        setRotationAngle(bone2, 0.0F, -0.5236F, 0.0F);
        head.addChild(bone2);
        bone2.cubeList.add(new ModelBox(bone2, 32, 59, 1.41F, -3.999F, 0.77F, 1, 1, 1, 0.0F, false));

        bone3 = new RendererModel(this);
        bone3.setRotationPoint(0.0F, 0.0F, 0.0F);
        setRotationAngle(bone3, 0.3491F, 0.0F, 0.0F);
        head.addChild(bone3);
        bone3.cubeList.add(new ModelBox(bone3, 39, 2, -1.999F, -4.7F, 2.9F, 3, 2, 1, 0.0F, false));

        hands = new RendererModel(this);
        hands.setRotationPoint(0.0F, 0.0F, 0.0F);
        golem.addChild(hands);

        left = new RendererModel(this);
        left.setRotationPoint(0.0F, 0.0F, 0.0F);
        hands.addChild(left);
        left.cubeList.add(new ModelBox(left, 42, 0, 4.0F, -23.0F, -2.0F, 6, 1, 6, 0.0F, false));
        left.cubeList.add(new ModelBox(left, 56, 39, 5.0F, -24.0F, -2.0F, 4, 1, 6, 0.0F, false));
        left.cubeList.add(new ModelBox(left, 40, 41, 4.0F, -22.0F, -2.0F, 5, 3, 6, 0.0F, false));
        left.cubeList.add(new ModelBox(left, 36, 62, 4.8F, -15.0F, -1.001F, 4, 5, 4, 0.0F, false));
        left.cubeList.add(new ModelBox(left, 70, 60, 8.3F, -16.0F, -1.001F, 1, 6, 4, 0.0F, false));
        left.cubeList.add(new ModelBox(left, 68, 77, 5.3F, -16.0F, -1.501F, 4, 6, 1, 0.0F, false));
        left.cubeList.add(new ModelBox(left, 76, 51, 5.3F, -16.0F, 2.499F, 4, 6, 1, 0.0F, false));
        left.cubeList.add(new ModelBox(left, 56, 73, 6.8F, -10.0F, -1.0F, 2, 2, 4, 0.0F, false));
        left.cubeList.add(new ModelBox(left, 58, 11, 7.8F, -8.0F, -1.0F, 1, 1, 1, 0.0F, false));
        left.cubeList.add(new ModelBox(left, 56, 42, 7.8F, -8.0F, 0.0F, 1, 2, 1, 0.0F, false));
        left.cubeList.add(new ModelBox(left, 56, 39, 7.8F, -8.0F, 1.0F, 1, 2, 1, 0.0F, false));
        left.cubeList.add(new ModelBox(left, 58, 9, 7.8F, -8.0F, 2.0F, 1, 1, 1, 0.0F, false));
        left.cubeList.add(new ModelBox(left, 40, 50, 7.0F, -10.0F, -1.7F, 1, 3, 1, 0.0F, false));

        bone10 = new RendererModel(this);
        bone10.setRotationPoint(0.0F, 0.0F, 0.0F);
        setRotationAngle(bone10, 0.0F, 0.0F, 0.6109F);
        left.addChild(bone10);
        bone10.cubeList.add(new ModelBox(bone10, 70, 38, -5.0F, -24.6F, -2.001F, 1, 1, 6, 0.0F, false));
        bone10.cubeList.add(new ModelBox(bone10, 70, 70, -6.4F, -24.8F, -2.001F, 1, 1, 6, 0.0F, false));
        bone10.cubeList.add(new ModelBox(bone10, 66, 52, -9.7F, -22.5F, -2.001F, 1, 2, 6, 0.0F, false));

        bone9 = new RendererModel(this);
        bone9.setRotationPoint(0.0F, 0.0F, 0.0F);
        setRotationAngle(bone9, 0.0F, 0.0F, 0.1745F);
        left.addChild(bone9);
        bone9.cubeList.add(new ModelBox(bone9, 58, 7, 6.4F, -8.5F, -1.0F, 1, 1, 1, 0.0F, false));
        bone9.cubeList.add(new ModelBox(bone9, 19, 58, 6.4F, -8.5F, 2.0F, 1, 1, 1, 0.0F, false));
        bone9.cubeList.add(new ModelBox(bone9, 0, 41, 6.6F, -7.5F, 0.0F, 1, 1, 2, 0.0F, false));

        bone8 = new RendererModel(this);
        bone8.setRotationPoint(0.0F, 0.0F, 0.0F);
        setRotationAngle(bone8, 0.0F, 0.0F, -0.0873F);
        left.addChild(bone8);
        bone8.cubeList.add(new ModelBox(bone8, 62, 3, 6.1F, -19.0F, -1.002F, 4, 5, 4, 0.0F, false));

        right = new RendererModel(this);
        right.setRotationPoint(-1.0F, 0.0F, 2.0F);
        setRotationAngle(right, 0.0F, 3.1416F, 0.0F);
        hands.addChild(right);
        right.cubeList.add(new ModelBox(right, 40, 32, 4.0F, -23.0F, -2.0F, 6, 1, 6, 0.0F, false));
        right.cubeList.add(new ModelBox(right, 52, 56, 5.0F, -24.0F, -2.0F, 4, 1, 6, 0.0F, false));
        right.cubeList.add(new ModelBox(right, 22, 38, 4.0F, -22.0F, -2.0F, 6, 3, 6, 0.0F, false));
        right.cubeList.add(new ModelBox(right, 20, 59, 4.8F, -15.0F, -1.001F, 4, 5, 4, 0.0F, false));
        right.cubeList.add(new ModelBox(right, 18, 34, 8.3F, -16.0F, -1.001F, 1, 6, 4, 0.0F, false));
        right.cubeList.add(new ModelBox(right, 76, 30, 5.3F, -16.0F, -1.501F, 4, 6, 1, 0.0F, false));
        right.cubeList.add(new ModelBox(right, 74, 0, 5.3F, -16.0F, 2.499F, 4, 6, 1, 0.0F, false));
        right.cubeList.add(new ModelBox(right, 72, 45, 6.8F, -10.0F, -1.0F, 2, 2, 4, 0.0F, false));
        right.cubeList.add(new ModelBox(right, 15, 58, 7.8F, -8.0F, -1.0F, 1, 1, 1, 0.0F, false));
        right.cubeList.add(new ModelBox(right, 16, 54, 7.8F, -8.0F, 0.0F, 1, 2, 1, 0.0F, false));
        right.cubeList.add(new ModelBox(right, 0, 54, 7.8F, -8.0F, 1.0F, 1, 2, 1, 0.0F, false));
        right.cubeList.add(new ModelBox(right, 52, 39, 7.8F, -8.0F, 2.0F, 1, 1, 1, 0.0F, false));
        right.cubeList.add(new ModelBox(right, 0, 13, 7.0F, -10.0F, 2.7F, 1, 3, 1, 0.0F, false));

        bone11 = new RendererModel(this);
        bone11.setRotationPoint(0.0F, 0.0F, 0.0F);
        setRotationAngle(bone11, 0.0F, 0.0F, 0.6109F);
        right.addChild(bone11);
        bone11.cubeList.add(new ModelBox(bone11, 70, 23, -5.0F, -24.6F, -1.999F, 1, 1, 6, 0.0F, false));
        bone11.cubeList.add(new ModelBox(bone11, 68, 31, -6.4F, -24.8F, -1.999F, 1, 1, 6, 0.0F, false));
        bone11.cubeList.add(new ModelBox(bone11, 62, 65, -9.7F, -22.5F, -1.999F, 1, 2, 6, 0.0F, false));

        bone12 = new RendererModel(this);
        bone12.setRotationPoint(0.0F, 0.0F, 0.0F);
        setRotationAngle(bone12, 0.0F, 0.0F, 0.1745F);
        right.addChild(bone12);
        bone12.cubeList.add(new ModelBox(bone12, 54, 7, 6.4F, -8.5F, -1.0F, 1, 1, 1, 0.0F, false));
        bone12.cubeList.add(new ModelBox(bone12, 53, 53, 6.4F, -8.5F, 2.0F, 1, 1, 1, 0.0F, false));
        bone12.cubeList.add(new ModelBox(bone12, 0, 38, 6.6F, -7.5F, 0.0F, 1, 1, 2, 0.0F, false));

        bone13 = new RendererModel(this);
        bone13.setRotationPoint(0.0F, 0.0F, 0.0F);
        setRotationAngle(bone13, 0.0F, 0.0F, -0.0873F);
        right.addChild(bone13);
        bone13.cubeList.add(new ModelBox(bone13, 58, 28, 6.1F, -19.0F, -1.002F, 4, 5, 4, 0.0F, false));

        legs = new RendererModel(this);
        legs.setRotationPoint(0.0F, 6.0F, 0.0F);
        golem.addChild(legs);

        left2 = new RendererModel(this);
        left2.setRotationPoint(0.0F, 0.0F, 0.0F);
        legs.addChild(left2);
        left2.cubeList.add(new ModelBox(left2, 0, 38, 0.5F, -9.0F, -3.1F, 5, 7, 6, 0.0F, false));
        left2.cubeList.add(new ModelBox(left2, 0, 58, 0.5F, -2.0F, -2.1F, 5, 1, 5, 0.0F, false));
        left2.cubeList.add(new ModelBox(left2, 0, 51, 0.5F, -1.0F, -3.0F, 5, 1, 6, 0.0F, false));
        left2.cubeList.add(new ModelBox(left2, 26, 14, 0.5F, 0.0F, -5.0F, 5, 1, 8, 0.0F, false));
        left2.cubeList.add(new ModelBox(left2, 53, 50, 2.3F, -9.7F, -3.2F, 1, 2, 1, 0.0F, false));

        bone14 = new RendererModel(this);
        bone14.setRotationPoint(0.0F, 0.0F, 0.0F);
        setRotationAngle(bone14, -0.1745F, 0.0F, -0.1745F);
        left2.addChild(bone14);
        bone14.cubeList.add(new ModelBox(bone14, 40, 50, 2.4F, -14.3F, -3.4F, 4, 7, 5, 0.0F, false));

        bone15 = new RendererModel(this);
        bone15.setRotationPoint(0.0F, 0.0F, 0.0F);
        setRotationAngle(bone15, 0.0F, 0.0F, 0.2618F);
        left2.addChild(bone15);
        bone15.cubeList.add(new ModelBox(bone15, 0, 28, 0.65F, -10.2F, -3.2F, 2, 2, 1, 0.0F, false));

        bone16 = new RendererModel(this);
        bone16.setRotationPoint(0.0F, 0.0F, 0.0F);
        setRotationAngle(bone16, 0.0F, 0.0F, -0.2618F);
        left2.addChild(bone16);
        bone16.cubeList.add(new ModelBox(bone16, 0, 25, 2.8F, -8.7F, -3.2F, 2, 2, 1, 0.0F, false));

        right2 = new RendererModel(this);
        right2.setRotationPoint(-5.0F, 0.0F, 0.0F);
        legs.addChild(right2);
        right2.cubeList.add(new ModelBox(right2, 0, 25, -1.6F, -9.0F, -3.1F, 5, 7, 6, 0.0F, false));
        right2.cubeList.add(new ModelBox(right2, 57, 46, -1.6F, -2.0F, -2.1F, 5, 1, 5, 0.0F, false));
        right2.cubeList.add(new ModelBox(right2, 46, 21, -1.6F, -1.0F, -3.0F, 5, 1, 6, 0.0F, false));
        right2.cubeList.add(new ModelBox(right2, 24, 5, -1.6F, 0.0F, -5.0F, 5, 1, 8, 0.0F, false));
        right2.cubeList.add(new ModelBox(right2, 16, 51, 0.4F, -9.7F, -3.2F, 1, 2, 1, 0.0F, false));

        bone17 = new RendererModel(this);
        bone17.setRotationPoint(0.0F, 0.0F, 0.0F);
        setRotationAngle(bone17, -0.1745F, 0.0F, 0.1745F);
        right2.addChild(bone17);
        bone17.cubeList.add(new ModelBox(bone17, 45, 9, -2.6F, -14.9F, -3.4F, 4, 7, 5, 0.0F, false));

        bone22 = new RendererModel(this);
        bone22.setRotationPoint(0.0F, 0.0F, 0.0F);
        setRotationAngle(bone22, 0.0F, 0.0F, 0.4363F);
        right2.addChild(bone22);
        bone22.cubeList.add(new ModelBox(bone22, 12, 64, -6.2F, -13.4F, -1.2F, 1, 5, 5, 0.0F, false));

        bone23 = new RendererModel(this);
        bone23.setRotationPoint(0.0F, 0.0F, 0.0F);
        setRotationAngle(bone23, 0.0F, 0.0F, -0.4363F);
        right2.addChild(bone23);
        bone23.cubeList.add(new ModelBox(bone23, 0, 64, 13.4F, -9.6F, -1.2F, 1, 5, 5, 0.0F, false));

        bone18 = new RendererModel(this);
        bone18.setRotationPoint(0.0F, 0.0F, 0.0F);
        setRotationAngle(bone18, 0.0F, 0.0F, 0.2618F);
        right2.addChild(bone18);
        bone18.cubeList.add(new ModelBox(bone18, 0, 3, -1.2F, -9.68F, -3.2F, 2, 2, 1, 0.0F, false));

        bone19 = new RendererModel(this);
        bone19.setRotationPoint(0.0F, 0.0F, 0.0F);
        setRotationAngle(bone19, 0.0F, 0.0F, -0.2618F);
        right2.addChild(bone19);
        bone19.cubeList.add(new ModelBox(bone19, 0, 0, 0.9F, -9.2F, -3.2F, 2, 2, 1, 0.0F, false));

        bone20 = new RendererModel(this);
        bone20.setRotationPoint(0.0F, 0.0F, 0.0F);
        setRotationAngle(bone20, 0.4363F, 0.0F, 0.0F);
        right2.addChild(bone20);
        bone20.cubeList.add(new ModelBox(bone20, 46, 28, -1.601F, -2.1F, -4.3F, 5, 1, 2, 0.0F, false));

        bone21 = new RendererModel(this);
        bone21.setRotationPoint(0.0F, 0.0F, 0.0F);
        setRotationAngle(bone21, 0.4363F, 0.0F, 0.0F);
        right2.addChild(bone21);
        bone21.cubeList.add(new ModelBox(bone21, 25, 2, 5.499F, -2.1F, -4.3F, 5, 1, 2, 0.0F, false));
    }

    @Override
    public void render(EntityGolemShooter entity, float f, float f1, float f2, float f3, float f4, float f5) {
        golem.render(f5);
    }

    private void setRotationAngle(RendererModel modelRenderer, float x, float y, float z) {
        modelRenderer.rotateAngleX = x;
        modelRenderer.rotateAngleY = y;
        modelRenderer.rotateAngleZ = z;
    }
}