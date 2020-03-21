package com.e33.init;

import com.e33.E33;
import com.e33.item.ItemDangerousStick;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.IForgeRegistry;

@Mod.EventBusSubscriber(modid = E33.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class BlockRegistry {
    public final static Block stickBlock = new ItemDangerousStick(Block.Properties.create(Material.MISCELLANEOUS).doesNotBlockMovement().lightValue(14).sound(SoundType.WOOD)).setRegistryName(ItemDangerousStick.registryName);

    @SubscribeEvent
    public static void registerBlock(RegistryEvent.Register<Block> event) {
        IForgeRegistry registry = event.getRegistry();

        registry.register(stickBlock);
    }

}
