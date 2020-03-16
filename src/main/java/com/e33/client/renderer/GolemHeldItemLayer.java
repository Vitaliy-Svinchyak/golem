package com.e33.client.renderer;

import com.e33.client.model.SpaceMarineModel;
import com.e33.entity.EntityGolemShooter;
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

@OnlyIn(Dist.CLIENT)
public class GolemHeldItemLayer<T extends LivingEntity> extends LayerRenderer<EntityGolemShooter, SpaceMarineModel<EntityGolemShooter>> {
    private final static Logger LOGGER = LogManager.getLogger();

    public GolemHeldItemLayer(IEntityRenderer<EntityGolemShooter, SpaceMarineModel<EntityGolemShooter>> renderer) {
        super(renderer);
    }

    public void render(EntityGolemShooter entityIn, float p_212842_2_, float p_212842_3_, float p_212842_4_, float p_212842_5_, float p_212842_6_, float p_212842_7_, float p_212842_8_) {
        ItemStack itemstack = entityIn.getHeldItemMainhand();
        if (itemstack.isEmpty()) {
            return;
        }

        GlStateManager.color3f(1.0F, 1.0F, 1.0F);
        GlStateManager.pushMatrix();

        Item item = itemstack.getItem();

        if (item.toString().equals(ItemDangerousStick.registryName)) {
//            GlStateManager.translatef(2.5F, 0.0F, 0.0F);// to right
            GlStateManager.translatef(0.3F, 0.65F, -0.45F);

            GlStateManager.rotatef(-80.0F, 1.0F, 0.0F, 0.0F);
            GlStateManager.rotatef(70.0F, 0.0F, 1.0F, 0.0F);
            GlStateManager.rotatef(-90.0F, 0.0F, 0.0F, 1.0F);
            GlStateManager.rotatef(-20.0F, 0.0F, 1.0F, 0.0F);
        }

        Minecraft.getInstance().getFirstPersonRenderer().renderItem(entityIn, itemstack, ItemCameraTransforms.TransformType.THIRD_PERSON_RIGHT_HAND);
        GlStateManager.popMatrix();
    }

    public boolean shouldCombineTextures() {
        return false;
    }
}