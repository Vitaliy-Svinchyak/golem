package com.e33.client.renderer.item;

import com.e33.client.animation.animator.ShootyWeaponAnimator;
import com.e33.client.model.ShootyModel;
import com.e33.entity.ShootyEntity;
import com.e33.item.ItemDangerousStick;
import net.minecraft.client.renderer.entity.IEntityRenderer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nonnull;

@OnlyIn(Dist.CLIENT)
public class ShootyHeldItemLayer<T extends LivingEntity> extends LayerRenderer<ShootyEntity, ShootyModel<ShootyEntity>> {
    private final ShootyWeaponAnimator weaponAnimator;
   private final static Logger LOGGER = LogManager.getLogger();

    public ShootyHeldItemLayer(IEntityRenderer<ShootyEntity, ShootyModel<ShootyEntity>> renderer) {
        super(renderer);
        this.weaponAnimator = new ShootyWeaponAnimator();
    }

    public void render(@Nonnull ShootyEntity entityIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scaleIn) {
        ItemStack itemstack = entityIn.getHeldItemMainhand();
        if (itemstack.isEmpty()) {
            return;
        }

        Item item = itemstack.getItem();

        if (!item.toString().equals(ItemDangerousStick.registryName)) {
            return;
        }

        this.weaponAnimator.animate(entityIn);
    }

    public boolean shouldCombineTextures() {
        return false;
    }
}