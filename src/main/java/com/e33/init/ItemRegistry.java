package com.e33.init;

import com.e33.E33;
import com.e33.item.ItemDangerousStick;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.IForgeRegistry;

@Mod.EventBusSubscriber(modid = E33.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ItemRegistry {
    public final static Item stickItem = new BlockItem(BlockRegistry.stickBlock, (new Item.Properties()).group(ItemGroup.DECORATIONS)).setRegistryName(ItemDangerousStick.registryName);


    @SubscribeEvent
    public static void registerItem(RegistryEvent.Register<Item> event) {
        IForgeRegistry registry = event.getRegistry();

        registry.register(stickItem);
    }
}
