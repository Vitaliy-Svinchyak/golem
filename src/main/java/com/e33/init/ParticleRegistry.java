package com.e33.init;

import com.e33.E33;
import com.e33.particle.SleeveParticleFactory;
import net.minecraft.client.Minecraft;
import net.minecraft.particles.BasicParticleType;
import net.minecraft.particles.ParticleType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ParticleFactoryRegisterEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.IForgeRegistry;

@Mod.EventBusSubscriber(modid = E33.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ParticleRegistry {
    public static final BasicParticleType SLEEVE = new BasicParticleType(false);

    @SubscribeEvent
    public static void registerParticle(RegistryEvent.Register<ParticleType<?>> event) {
        SLEEVE.setRegistryName("sleeve");
        IForgeRegistry registry = event.getRegistry();
        registry.register(SLEEVE);
    }

    @Mod.EventBusSubscriber(value = Dist.CLIENT, modid = E33.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class FactoryHandler {
        @SubscribeEvent
        public static void registerFactories(ParticleFactoryRegisterEvent evt) {
            Minecraft.getInstance().particles.registerFactory(SLEEVE, new SleeveParticleFactory());
        }
    }
}
