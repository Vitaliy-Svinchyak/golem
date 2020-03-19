package com.e33.client.renderer.item;

import com.e33.client.animation.ShootyWeaponAnimator;
import com.e33.client.animation.animated.items.AimedWeaponPosition;
import com.e33.client.animation.animated.items.DefaultWeaponPosition;
import com.e33.client.animation.animated.items.util.Rotation;
import com.e33.client.animation.animated.items.util.Translation;
import com.e33.client.model.ShootyModel;
import com.e33.client.util.AnimationState;
import com.e33.client.util.AnimationStateListener;
import com.e33.entity.ShootyEntity;
import com.e33.item.ItemDangerousStick;
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
    private final ShootyWeaponAnimator weaponAnimator;
    private AnimationState lastAnimationState = AnimationState.DEFAULT;
    private final static Logger LOGGER = LogManager.getLogger();

    public ShootyHeldItemLayer(IEntityRenderer<ShootyEntity, ShootyModel<ShootyEntity>> renderer) {
        super(renderer);
        this.weaponAnimator = new ShootyWeaponAnimator();
    }

    public void render(ShootyEntity entityIn, float p_212842_2_, float p_212842_3_, float p_212842_4_, float p_212842_5_, float p_212842_6_, float p_212842_7_, float p_212842_8_) {
        ItemStack itemstack = entityIn.getHeldItemMainhand();
        if (itemstack.isEmpty()) {
            return;
        }

        AnimationState animationState = AnimationStateListener.getAnimationState(entityIn);
        if (this.weaponAnimator.isAnimationComplete() && this.lastAnimationState == animationState) {
            this.renderCurrentPose(entityIn);
            return;
        }

        this.weaponAnimator.animate(entityIn);
        this.lastAnimationState = animationState;
    }

    private void renderCurrentPose(ShootyEntity entityIn) {
        ItemStack itemstack = entityIn.getHeldItemMainhand();
        if (itemstack.isEmpty()) {
            return;
        }

        Item item = itemstack.getItem();

        if (!item.toString().equals(ItemDangerousStick.registryName)) {
            return;
        }

        GlStateManager.color3f(1.0F, 1.0F, 1.0F);
        GlStateManager.pushMatrix();
        switch (AnimationStateListener.getAnimationState(entityIn)) {
            case DEFAULT:
                this.renderDefaultPose();
                break;
            case AIM:
                this.renderAimedPose();
                break;
        }

        Minecraft.getInstance().getFirstPersonRenderer().renderItem(entityIn, itemstack, ItemCameraTransforms.TransformType.THIRD_PERSON_RIGHT_HAND);
        GlStateManager.popMatrix();
    }

    private void renderAimedPose() {
        this.renderPose(AimedWeaponPosition.getTranslations(), AimedWeaponPosition.getRotations());
    }

    private void renderDefaultPose() {
        this.renderPose(DefaultWeaponPosition.getTranslations(), DefaultWeaponPosition.getRotations());
    }

    private void renderPose(List<Translation> translations, List<Rotation> rotations) {
        for (Translation translation : translations) {
            GlStateManager.translatef(translation.x, translation.y, translation.z);
        }

        for (Rotation rotation : rotations) {
            GlStateManager.rotatef(rotation.angle, rotation.x, rotation.y, rotation.z);
        }
    }

    public boolean shouldCombineTextures() {
        return false;
    }
}