package e33.guardy.client.renderer.entity;

import e33.guardy.E33;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.SpriteRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class BulletRenderer extends SpriteRenderer {
    private final static Logger LOGGER = LogManager.getLogger();

    public BulletRenderer(EntityRendererManager renderManager) {
        super(renderManager, Minecraft.getInstance().getItemRenderer(), 0.75F);
    }

    @Override
    protected ResourceLocation getEntityTexture(Entity bulletEntity) {
        return new ResourceLocation(E33.MOD_ID, "textures/bullet.png");
    }
}