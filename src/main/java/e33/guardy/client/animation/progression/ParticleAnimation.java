package e33.guardy.client.animation.progression;

import net.minecraft.client.Minecraft;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particles.IParticleData;

import java.util.List;

public class ParticleAnimation extends AnimationProgression {

    private IParticleData particleType;
    private final ClientWorld world;

    ParticleAnimation(List<Float> xProgression, List<Float> yProgression, List<Float> zProgression, IParticleData particleType, ProgressionType progressionType) {
        super(null, xProgression, yProgression, zProgression, progressionType);

        this.particleType = particleType;
        this.world = Minecraft.getInstance().world;
    }

    @Override
    protected void progress(float newX, float newY, float newZ) {
        this.world.addParticle(this.particleType, newX, newY, newZ, 0.0D, 0.0D, 0.0D);
    }
}
