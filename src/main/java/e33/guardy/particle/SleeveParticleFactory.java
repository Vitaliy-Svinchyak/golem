package e33.guardy.particle;

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

    public Particle makeParticle(BasicParticleType particleType, World world, double motionX, double motionY, double motionZ, double xSpeed, double ySpeed, double zSpeed) {
        return new FallingParticle(world, motionX, motionY, motionZ, new ItemStack(Items.IRON_BLOCK));
    }
}