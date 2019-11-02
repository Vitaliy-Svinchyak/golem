package com.example.e33.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.DamagingProjectileEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

public class BulletEntity extends DamagingProjectileEntity {

    private LivingEntity owner = null;

    public BulletEntity(World worldIn, LivingEntity shooter, double accelX, double accelY, double accelZ, LivingEntity owner) {
        super(EntityType.SMALL_FIREBALL, shooter, accelX, accelY, accelZ, worldIn);
        this.owner = owner;
    }

    public BulletEntity(EntityType<? extends BulletEntity> entityType, World world) {
        super(entityType, world);
    }

    public static BulletEntity build(EntityType<? extends BulletEntity> entityType, World world) {
        return new BulletEntity(entityType, world);
    }

    /**
     * Called when this BulletEntity hits a block or entity.
     */
    protected void onImpact(RayTraceResult result) {
        if (this.world.isRemote) {
            return;
        }

        if (result.getType() == RayTraceResult.Type.ENTITY) {
            Entity entity = ((EntityRayTraceResult) result).getEntity();
            DamageSource damagesource = DamageSource.causeMobDamage(this.owner);
            entity.attackEntityFrom(damagesource, (float) 5);
//            entity.attackEntityFrom(damagesource, (float) this.owner.getAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).getValue());
        }

        this.remove();
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
}
