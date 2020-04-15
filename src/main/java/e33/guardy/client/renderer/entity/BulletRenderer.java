package e33.guardy.client.renderer.entity;

import e33.guardy.E33;
import e33.guardy.entity.BulletEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.culling.ICamera;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.SpriteRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.IRendersAsItem;
import net.minecraft.util.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class BulletRenderer<T extends Entity & IRendersAsItem> extends SpriteRenderer<T> {
    private final static Logger LOGGER = LogManager.getLogger();

    public BulletRenderer(EntityRendererManager renderManager) {
        super(renderManager, Minecraft.getInstance().getItemRenderer(), 0.75F);
        LOGGER.info("new BulletRenderer");
    }

    public boolean shouldRender(T p_177071_1_, ICamera p_177071_2_, double p_177071_3_, double p_177071_5_, double p_177071_7_) {
        LOGGER.info("shouldRender");
        return true;
    }

    public void doRender(T p_76986_1_, double p_76986_2_, double p_76986_4_, double p_76986_6_, float p_76986_8_, float p_76986_9_) {
        LOGGER.info("doRender");
        super.doRender(p_76986_1_, p_76986_2_, p_76986_4_, p_76986_6_, p_76986_8_, p_76986_9_);
    }

    public void doRenderShadowAndFire(Entity p_76979_1_, double p_76979_2_, double p_76979_4_, double p_76979_6_, float p_76979_8_, float p_76979_9_) {
        LOGGER.info("doRender");
    }

    @Override
    protected ResourceLocation getEntityTexture(Entity bulletEntity) {
        LOGGER.info("getEntityTexture");
        return new ResourceLocation(E33.MOD_ID, "textures/entity/bullet.png");
    }
}