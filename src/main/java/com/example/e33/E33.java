package com.example.e33;

import com.example.e33.client.renderer.RendererGolemShooter;
import com.example.e33.entity.EntityGolemShooter;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.IUnbakedModel;
import net.minecraft.client.renderer.model.ModelResourceLocation;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.client.model.obj.OBJLoader;
import net.minecraftforge.client.model.obj.OBJModel;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import net.minecraftforge.client.model.BasicState;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(E33.MOD_ID)
public class E33 {
    public static final String MOD_ID = "e33";
    // Directly reference a log4j logger.
    private static final Logger LOGGER = LogManager.getLogger();

    public E33() {
        final IEventBus eventBus = FMLJavaModLoadingContext.get().getModEventBus();
        eventBus.addListener(this::setup);
        eventBus.addListener(this::doClientStuff);
    }

    private void setup(final FMLCommonSetupEvent event) {
        RenderingRegistry.registerEntityRenderingHandler(EntityGolemShooter.class, RendererGolemShooter::new);
        LOGGER.info("HELLO FROM PREINIT");
    }

    private void doClientStuff(final FMLClientSetupEvent event) {
        LOGGER.info("Got game settings {}", event.getMinecraftSupplier().get().gameSettings);
        OBJLoader.INSTANCE.addDomain("examplemod");
    }

    @Mod.EventBusSubscriber(bus=Mod.EventBusSubscriber.Bus.MOD)
    public static class RegistryEvents {
        @SubscribeEvent
        public static void onModelBakeEvent(ModelBakeEvent event) {
            try {
                // Try to load an OBJ model (placed in src/main/resources/assets/examplemod/models/)
                IUnbakedModel model = ModelLoaderRegistry.getModelOrMissing(new ResourceLocation("examplemod:sample_model.obj"));

                if (model instanceof OBJModel) {
                    // If loading OBJ model succeeds, bake the model and replace stick's model with the baked model
                    IBakedModel bakedModel = model.bake(event.getModelLoader(), ModelLoader.defaultTextureGetter(), new BasicState(model.getDefaultState(), false), DefaultVertexFormats.ITEM);
                    event.getModelRegistry().put(new ModelResourceLocation("stick", "inventory"), bakedModel);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
