package com.e33.init;

import com.e33.E33;
import com.e33.item.ItemDangerousStick;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.IForgeRegistry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod.EventBusSubscriber(modid = E33.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class BlockRegistry {
    private final static Logger LOGGER = LogManager.getLogger();

    private static IForgeRegistry registry;

    @SubscribeEvent
    public static void registerBlockItem(RegistryEvent.Register<Block> event) {

        Block block = registerBlock("shooting_stick", new ItemDangerousStick(Block.Properties.create(Material.MISCELLANEOUS).doesNotBlockMovement().lightValue(14).sound(SoundType.WOOD)));
        BlockItem item = new BlockItem(block, (new Item.Properties()).group(ItemGroup.DECORATIONS));
        registerItem(new ResourceLocation(E33.MOD_ID, "shooting_stick"), item);
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
