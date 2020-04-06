package e33.guardy.particle;

import e33.guardy.entity.ShootyEntity;
import net.minecraft.client.particle.BreakingParticle;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EntityPredicates;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

public class FallingParticle extends BreakingParticle {
    public final static Logger LOGGER = LogManager.getLogger();

    protected FallingParticle(World world, double motionX, double motionY, double motionZ, ItemStack item) {
        super(world, motionX, motionY, motionZ, item);

        double random = 1D;
        // Copied from sources
        this.motionX = motionX + (random * 2.0D - 1.0D) * 0.4000000059604645D;
        this.motionY = motionY + (random * 2.0D - 1.0D) * 0.4000000059604645D;
        this.motionZ = motionZ + (random * 2.0D - 1.0D) * 0.4000000059604645D;
        float l1 = (float) (random + random + 1.0D) * 0.15F;
        float l2 = MathHelper.sqrt(this.motionX * this.motionX + this.motionY * this.motionY + this.motionZ * this.motionZ);
        this.motionX = this.motionX / (double) l2 * (double) l1 * 0.4000000059604645D;
        this.motionY = this.motionY / (double) l2 * (double) l1 * 0.4000000059604645D + 0.10000000149011612D;
        this.motionZ = this.motionZ / (double) l2 * (double) l1 * 0.4000000059604645D;

        this.particleScale = 0.05F;
        this.maxAge = 30;

        List<ShootyEntity> shooties = this.world.getEntitiesWithinAABB(ShootyEntity.class, this.getBoundingBox().grow(1), EntityPredicates.NOT_SPECTATING);
        if (shooties.size() > 0) {
            // TODO 2 may bug if multiple of them are on the same place
            ShootyEntity shooty = shooties.get(0);
            Vec3d lookPosition = shooty.getLook(0);
            LOGGER.info(lookPosition);
            LOGGER.info(this.motionX + " " + this.motionZ);

            this.motionX = lookPosition.getX();
            this.motionZ = lookPosition.getZ() / 3;
        }
    }
}
