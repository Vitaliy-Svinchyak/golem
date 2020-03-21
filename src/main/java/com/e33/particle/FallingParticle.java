package com.e33.particle;

import net.minecraft.client.particle.BreakingParticle;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public class FallingParticle extends BreakingParticle {

    protected FallingParticle(World p_i47645_1_, double motionX, double motionY, double motionZ, ItemStack p_i47645_8_) {
        super(p_i47645_1_, motionX, motionY, motionZ, p_i47645_8_);

        double random = 0.5D;
        // Copied from sources
        this.motionX = motionX + (random * 2.0D - 1.0D) * 0.4000000059604645D;
        this.motionY = motionY + (random * 2.0D - 1.0D) * 0.4000000059604645D;
        this.motionZ = motionZ + (random * 2.0D - 1.0D) * 0.4000000059604645D;
        float lvt_14_1_ = (float) (random + random + 1.0D) * 0.15F;
        float lvt_15_1_ = MathHelper.sqrt(this.motionX * this.motionX + this.motionY * this.motionY + this.motionZ * this.motionZ);
        this.motionX = this.motionX / (double) lvt_15_1_ * (double) lvt_14_1_ * 0.4000000059604645D;
        this.motionY = this.motionY / (double) lvt_15_1_ * (double) lvt_14_1_ * 0.4000000059604645D + 0.10000000149011612D;
        this.motionZ = this.motionZ / (double) lvt_15_1_ * (double) lvt_14_1_ * 0.4000000059604645D;
    }
}
