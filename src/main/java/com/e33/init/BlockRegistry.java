package com.e33.init;

import com.e33.E33;
import com.e33.item.ItemDangerousStick;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.IForgeRegistry;

@Mod.EventBusSubscriber(modid = E33.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class BlockRegistry {
    private final static Block stickBlock = new ItemDangerousStick(Block.Properties.create(Material.MISCELLANEOUS).doesNotBlockMovement().lightValue(14).sound(SoundType.WOOD)).setRegistryName(ItemDangerousStick.registryName);
    public final static Item stickItem = new BlockItem(stickBlock, (new Item.Properties()).group(ItemGroup.DECORATIONS)).setRegistryName(ItemDangerousStick.registryName);

    @SubscribeEvent
    public static void registerBlock(RegistryEvent.Register<Block> event) {
        IForgeRegistry registry = event.getRegistry();

        registry.register(stickBlock);

    }

    @SubscribeEvent
    public static void registerItem(RegistryEvent.Register<Item> event) {
        IForgeRegistry registry = event.getRegistry();

        registry.register(stickItem);
    }
}
