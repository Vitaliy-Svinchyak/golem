package com.e33;

import com.e33.client.renderer.RendererBullet;
import com.e33.client.renderer.RendererGolemShooter;
import com.e33.debug.DangerousZoneDebugRenderer;
import com.e33.entity.BulletEntity;
import com.e33.entity.EntityGolemShooter;
import com.e33.item.ItemDangerousStick;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.SoundType;
import net.minecraft.block.TorchBlock;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.debug.DebugRenderer;
import net.minecraft.client.renderer.model.ModelResourceLocation;

import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.WallOrFloorItem;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import net.minecraft.util.registry.Registry;

@Mod(E33.MOD_ID)
public class E33 {
    public static final String MOD_ID = "e33";
    public static final DangerousZoneDebugRenderer dangerousZoneDebugRenderer = new DangerousZoneDebugRenderer(Minecraft.getInstance());
    private static DebugRenderer renderer = Minecraft.getInstance().debugRenderer;
    private final static Logger LOGGER = LogManager.getLogger();

    public E33() {
        final IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        final IEventBus forgeEventBus = MinecraftForge.EVENT_BUS;
        modEventBus.addListener(this::setup);
        //needed for @ForgeSubscribe: public void renderWorldLastEvent(RenderWorldLastEvent)
        forgeEventBus.register(this);
    }

    private void setup(final FMLCommonSetupEvent event) {
        RenderingRegistry.registerEntityRenderingHandler(EntityGolemShooter.class, RendererGolemShooter::new);
        RenderingRegistry.registerEntityRenderingHandler(BulletEntity.class, RendererBullet::new);
    }

    @SubscribeEvent
    public void renderWorldLastEvent(RenderWorldLastEvent event) {
        // TODO prevent HashMap null exception
//        E33.renderer.pathfinding.render(50);
//        E33.dangerousZoneDebugRenderer.render(50);
    }

    @SubscribeEvent
    public static void onBlocksRegistry(ModelRegistryEvent BlockRegistryEvent) {
//        BlockRegistryEvent.getRegistry().register
//                (
//                        new ItemDangerousStick(
//                                ItemDangerousStick.Properties.create(Material.PLANTS).hardnessAndResistance(0.0f).doesNotBlockMovement().tickRandomly()
//                        )
//                                .setRegistryName(new ResourceLocation(MOD_ID, "shooting_stick"))
//                );
        Block block = registerBlock("shooting_stick", new ItemDangerousStick(Block.Properties.create(Material.MISCELLANEOUS).doesNotBlockMovement().lightValue(14).sound(SoundType.WOOD)));
        BlockItem item = new BlockItem(block, (new Item.Properties()).group(ItemGroup.DECORATIONS));
        registerItem(new ResourceLocation(MOD_ID, "shooting_stick"), item);

        LOGGER.info("shooting_stick registered");
    }

    private static Item registerItem(ResourceLocation resourceLocation, Item item) {
        if (item instanceof BlockItem) {
            ((BlockItem) item).addToBlockToItemMap(Item.BLOCK_TO_ITEM, item);
        }

        return Registry.register(Registry.ITEM, resourceLocation, item);
    }

    private static Block registerBlock(String key, Block block) {
        return Registry.register(Registry.BLOCK, key, block);
    }
}
