package com.e33.particle;

import net.minecraft.client.particle.IParticleFactory;
import net.minecraft.client.particle.Particle;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.particles.BasicParticleType;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class SleeveParticleFactory implements IParticleFactory<BasicParticleType> {
    public SleeveParticleFactory() {
    }

    public Particle makeParticle(BasicParticleType particleType, World world, double p_199234_3_, double p_199234_5_, double p_199234_7_, double p_199234_9_, double p_199234_11_, double p_199234_13_) {
        return new FallingParticle(world, p_199234_3_, p_199234_5_, p_199234_7_, new ItemStack(Items.IRON_BLOCK));
    }
}