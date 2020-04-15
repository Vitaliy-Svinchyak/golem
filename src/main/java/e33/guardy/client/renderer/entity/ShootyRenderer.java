package e33.guardy.client.renderer.entity;

import e33.guardy.client.model.ShootyModel;
import e33.guardy.client.renderer.item.ShootyHeldItemLayer;
import e33.guardy.entity.ShootyEntity;
import e33.guardy.E33;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.util.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nonnull;

public class ShootyRenderer extends MobRenderer<ShootyEntity, ShootyModel<ShootyEntity>> {
    private final static Logger LOGGER = LogManager.getLogger();

    public ShootyRenderer(EntityRendererManager renderManager) {
        // 3 param - shadow size
        super(renderManager, new ShootyModel<>(), 1F);
//        this.addLayer(new HeadLayer(this));
        this.addLayer(new ShootyHeldItemLayer<>(this));
    }

    @Override
    @Nonnull
    protected ResourceLocation getEntityTexture(@Nonnull ShootyEntity shooty) {
        return new ResourceLocation(E33.MOD_ID, "textures/entity/shooty.png");
    }
}
