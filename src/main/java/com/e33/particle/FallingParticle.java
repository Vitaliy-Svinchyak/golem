package com.e33.particle;

import net.minecraft.client.particle.BreakingParticle;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class FallingParticle extends BreakingParticle {

    protected FallingParticle(World world, double motionX, double motionY, double motionZ, ItemStack item) {
        super(world, motionX, motionY, motionZ, item);

        this.motionX = -0.10490172207097681;
        this.motionY = 0.1018430291360944;
        this.motionZ = -0.058242880098767616;

        this.particleScale = 0.05F;
        this.maxAge = 30;
    }
}
