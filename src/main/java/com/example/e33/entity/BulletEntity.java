package com.example.e33.entity;

import com.example.e33.fight.ShootExpectations;
import com.example.e33.fight.ShootStatistic;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.DamagingProjectileEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class BulletEntity extends DamagingProjectileEntity {

    private LivingEntity target = null;

    public BulletEntity(World world, LivingEntity shooter, double accelX, double accelY, double accelZ, LivingEntity target) {
        super(EntityType.SMALL_FIREBALL, world);
        this.target = target;
        this.shootingEntity = shooter;
        this.setLocationAndAngles(shooter.posX, shooter.posY, shooter.posZ, shooter.rotationYaw, shooter.rotationPitch);
        this.setBasicPosition(accelX, accelY, accelZ);
        ShootStatistic.bulletShot();
    }

    public BulletEntity(EntityType<? extends BulletEntity> entityType, World world) {
        super(entityType, world);
    }

    public void setBasicPosition(double x, double y, double z) {
        this.setPosition(this.posX, this.shootingEntity.posY + (double) (this.shootingEntity.getHeight() / 2.0F) + 0.5D, this.posZ);
        this.setMotion(Vec3d.ZERO);
        int d0 = 150;
        this.accelerationX = x / d0;
        this.accelerationY = y / d0;
        this.accelerationZ = z / d0;
    }

    public static BulletEntity build(EntityType<? extends BulletEntity> entityType, World world) {
        return new BulletEntity(entityType, world);
    }

    public void baseTick() {
        // TODO detect miss to notify
        if (World.isYOutOfBounds((int) this.posY) || MathHelper.sqrt(this.getDistanceSq(this.shootingEntity)) > 64) {
            ShootExpectations.removeFromDeadList(this.target);
        }

        super.baseTick();
    }

    /**
     * Called when this BulletEntity hits a block or entity.
     */
    protected void onImpact(RayTraceResult result) {
        if (this.world.isRemote) {
            return;
        }

        if (result.getType() == RayTraceResult.Type.ENTITY) {
            LOGGER.info("hit");
            LivingEntity target = (LivingEntity) ((EntityRayTraceResult) result).getEntity();
            LOGGER.info(target.getHealth());

            if (target.isAlive()) {
                ShootStatistic.bulletHitTheTarget();
            }

            DamageSource damagesource = DamageSource.causeMobDamage(this.shootingEntity);
            target.attackEntityFrom(damagesource, (float) 5);

            if (!target.isAlive()) {
                // Dead, remove from memory
                ShootExpectations.removeFromDeadList(this.target);
            }
            LOGGER.info(target.getHealth());
        } else {
            // To shoot it again
            LOGGER.info("miss");
            ShootExpectations.removeFromDeadList(this.target);
        }

        this.remove();
    }

    public void remove() {
        ShootExpectations.removeFromDeadList(this.target);
        super.remove();
    }

    public void remove(boolean keepData) {
        ShootExpectations.removeFromDeadList(this.target);
        super.remove(keepData);
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
    public boolean attackEntityFrom(DamageSource source, float amount) {
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
}
