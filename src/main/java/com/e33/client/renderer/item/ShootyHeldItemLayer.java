package com.e33.client.renderer.item;

import com.e33.client.animation.animationProgression.AnimationProgression;
import com.e33.client.model.ShootyModel;
import com.e33.client.util.AnimationState;
import com.e33.client.util.AnimationStateListener;
import com.e33.entity.ShootyEntity;
import com.e33.item.ItemDangerousStick;
import com.google.common.collect.Lists;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.IEntityRenderer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

@OnlyIn(Dist.CLIENT)
public class ShootyHeldItemLayer<T extends LivingEntity> extends LayerRenderer<ShootyEntity, ShootyModel<ShootyEntity>> {
    private final static Logger LOGGER = LogManager.getLogger();

    private List<AnimationProgression> animations = Lists.newArrayList();
    private AnimationState lastAnimationState = AnimationState.DEFAULT;

    public ShootyHeldItemLayer(IEntityRenderer<ShootyEntity, ShootyModel<ShootyEntity>> renderer) {
        super(renderer);
    }

    public void render(ShootyEntity entityIn, float p_212842_2_, float p_212842_3_, float p_212842_4_, float p_212842_5_, float p_212842_6_, float p_212842_7_, float p_212842_8_) {
        ItemStack itemstack = entityIn.getHeldItemMainhand();
        if (itemstack.isEmpty()) {
            return;
        }

        AnimationState animationState = AnimationStateListener.getAnimationState(entityIn);
        if (this.lastAnimationState == animationState) {
            for (AnimationProgression animation : this.animations) {
//                animation.makeProgress();
            }

            return;
        }

        this.animations = Lists.newArrayList();

        GlStateManager.color3f(1.0F, 1.0F, 1.0F);
        GlStateManager.pushMatrix();

        switch (animationState) {
            case DEFAULT:
                this.animateDefaultPose(entityIn, itemstack);
                break;
            case AIM:
                this.animateAiming(entityIn, itemstack);
                break;
        }

        Minecraft.getInstance().getFirstPersonRenderer().renderItem(entityIn, itemstack, ItemCameraTransforms.TransformType.THIRD_PERSON_RIGHT_HAND);
        GlStateManager.popMatrix();

        this.lastAnimationState = AnimationStateListener.getAnimationState(entityIn);
    }

    private void animateAiming(ShootyEntity entityIn, ItemStack itemstack) {
        LOGGER.info("aim");
    }

    private void animateDefaultPose(ShootyEntity entityIn, ItemStack itemstack) {
        LOGGER.info("default");
        Item item = itemstack.getItem();

        if (item.toString().equals(ItemDangerousStick.registryName)) {
//            GlStateManager.translatef(2.5F, 0.0F, 0.0F);// to right
            GlStateManager.translatef(0.3F, 0.65F, -0.45F);

            GlStateManager.rotatef(-80.0F, 1.0F, 0.0F, 0.0F);
            GlStateManager.rotatef(70.0F, 0.0F, 1.0F, 0.0F);
            GlStateManager.rotatef(-90.0F, 0.0F, 0.0F, 1.0F);
            GlStateManager.rotatef(-20.0F, 0.0F, 1.0F, 0.0F);
        }
    }

    public boolean shouldCombineTextures() {
        return false;
    }
}