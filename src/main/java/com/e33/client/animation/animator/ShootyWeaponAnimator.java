package com.e33.client.animation.animator;

import com.e33.client.animation.animation.Animation;
import com.e33.client.animation.animation.item.AimingAnimation;
import com.e33.client.animation.animation.item.ShootingAnimation;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;

import java.util.Map;

public class ShootyWeaponAnimator extends Animator {

    void animateAllProgressions(LivingEntity entity) {
        ItemStack itemstack = entity.getHeldItemMainhand();
        GlStateManager.color3f(1.0F, 1.0F, 1.0F);
        GlStateManager.pushMatrix();

        super.animateAllProgressions(entity);

        Minecraft.getInstance().getFirstPersonRenderer().renderItem(entity, itemstack, ItemCameraTransforms.TransformType.THIRD_PERSON_RIGHT_HAND);
        GlStateManager.popMatrix();
    }

    Animation createShotAnimation(LivingEntity entity) {
        Map<Class, Animation> animationCache = this.getAnimationCacheFor(entity);

        if (animationCache.get(ShootingAnimation.class) == null) {
            animationCache.put(ShootingAnimation.class, new ShootingAnimation(null, entity));
        }

        return animationCache.get(ShootingAnimation.class);
    }

    Animation createAimingAnimation(LivingEntity entity) {
        Map<Class, Animation> animationCache = this.getAnimationCacheFor(entity);

        if (animationCache.get(AimingAnimation.class) == null) {
            animationCache.put(AimingAnimation.class, new AimingAnimation(null, entity));
        }

        return animationCache.get(AimingAnimation.class);
    }

}
