package e33.guardy.entity;

import e33.guardy.fight.ShootExpectations;
import e33.guardy.fight.ShootStatistic;
import e33.guardy.init.EntityRegistry;
import e33.guardy.net.BulletSSpawnObjectPacket;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.IRendersAsItem;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.DamagingProjectileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.IPacket;
import net.minecraft.network.play.server.SSpawnObjectPacket;
import net.minecraft.particles.IParticleData;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;

public class BulletEntity extends DamagingProjectileEntity implements IRendersAsItem {

    private LivingEntity target = null;

    public BulletEntity(@Nonnull World world, @Nonnull LivingEntity shooter, double accelX, double accelY, double accelZ, LivingEntity target) {
        super(EntityType.SMALL_FIREBALL, world);
//        this(EntityRegistry.BULLET, world);
        this.target = target;
        this.shootingEntity = shooter;
        this.setLocationAndAngles(shooter.posX, shooter.posY, shooter.posZ, shooter.rotationYaw, shooter.rotationPitch);
        this.setBasicPosition(accelX, accelY, accelZ);
        ShootStatistic.bulletShot();
    }

    private BulletEntity(EntityType<? extends BulletEntity> entityType, World world) {
        super(entityType, world);
    }

    private void setBasicPosition(double x, double y, double z) {
        this.setPosition(this.posX, this.shootingEntity.posY + (double) (this.shootingEntity.getHeight() / 2.0F) + 0.5D, this.posZ);
        this.setMotion(Vec3d.ZERO);
        // WHY?
        int d0 = 100;
        this.accelerationX = x / d0;
        this.accelerationY = y / d0;
        this.accelerationZ = z / d0;
    }

    public static BulletEntity build(EntityType<? extends BulletEntity> entityType, World world) {
        return new BulletEntity(entityType, world);
    }

    @OnlyIn(Dist.CLIENT)
    public ItemStack getItem() {
        LOGGER.info("getItem");
        return new ItemStack(Items.FIRE_CHARGE);
    }

    public void baseTick() {
        super.baseTick();
        if (this.shootingEntity == null) {
            return;
        }

        if (World.isYOutOfBounds((int) this.posY) || this.getDistance(this.shootingEntity) > 64) {
            ShootExpectations.removeFromDeadList(this.target);
        }
    }

    /**
     * Called when this BulletEntity hits a block or entity.
     */
    protected void onImpact(@Nonnull RayTraceResult result) {
        if (this.world.isRemote || this.target == null) {
            return;
        }

        if (result.getType() == RayTraceResult.Type.ENTITY) {
            LivingEntity target = (LivingEntity) ((EntityRayTraceResult) result).getEntity();

            if (target.isAlive()) {
                ShootStatistic.bulletHitTheTarget();
            }

            DamageSource damagesource = DamageSource.causeMobDamage(this.shootingEntity);
            target.attackEntityFrom(damagesource, (float) 10);

            if (!target.isAlive()) {
                // Dead, remove from memory
                ShootExpectations.forgetTarget(this.target);
            }
        } else {
            // To shoot it again
            ShootExpectations.removeFromDeadList(this.target);
        }

        this.remove();
    }

    public void remove() {
        if (this.target != null) {
            ShootExpectations.removeFromDeadList(this.target);
        }
        super.remove();
    }

    public void remove(boolean keepData) {
        if (this.target != null) {
            ShootExpectations.removeFromDeadList(this.target);
        }
        super.remove(keepData);
    }

    protected IParticleData getParticle() {
        return ParticleTypes.SQUID_INK;
    }

    /**
     * Returns true if other Entities should be prevented from moving through this Entity.
     */
    public boolean canBeCollidedWith() {
        return false;
    }

    /**
     * Called when the entity is attacked.
     */
    public boolean attackEntityFrom(@Nonnull DamageSource source, float amount) {
        return false;
    }

    /**
     * Return the motion factor for this projectile. The factor is multiplied by the original motion.
     */
    protected float getMotionFactor() {
        return 1.0F;
    }

    protected boolean isFireballFiery() {
        return false;
    }

//    public IPacket<?> createSpawnPacket() {
//        if (this.world instanceof ServerWorld) {
//            return super.createSpawnPacket();
//        }
//
//        int i = this.shootingEntity == null ? 0 : this.shootingEntity.getEntityId();
//        return new BulletSSpawnObjectPacket(this.getEntityId(), this.getUniqueID(), this.posX, this.posY, this.posZ, this.rotationPitch, this.rotationYaw, this.getType(), i, new Vec3d(this.accelerationX, this.accelerationY, this.accelerationZ));
//    }
}
