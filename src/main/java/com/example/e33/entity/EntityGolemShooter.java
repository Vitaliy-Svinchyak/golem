package com.example.e33.entity;

import com.example.e33.core.ModSounds;
import com.example.e33.goal.PatrollingGoal;
import net.minecraft.block.AnvilBlock;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.World;

import javax.annotation.Nonnull;

public class EntityGolemShooter extends AnimalEntity {

    public EntityGolemShooter(EntityType<? extends EntityGolemShooter> golem, World world) {
        super(golem, world);
        this.setBoundingBox(new AxisAlignedBB(3, 3, 3, 3, 3, 3));
        this.stepHeight = 1.0F;
    }

    @Override
    public EntityGolemShooter createChild(@Nonnull AgeableEntity ageable) {
        return null;
    }

    public SoundCategory getSoundCategory() {
        return SoundCategory.NEUTRAL;
    }

    @Override
    protected void registerGoals() {

        this.goalSelector.addGoal(7, new PatrollingGoal(this, 0.5D, AnvilBlock.class));
    }

    public void fall(float distance, float damageMultiplier) {
    }

    protected void registerAttributes() {
        super.registerAttributes();
        this.getAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(100.0D);
//        this.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.25D);
        this.getAttribute(SharedMonsterAttributes.KNOCKBACK_RESISTANCE).setBaseValue(1.0D);
    }

    public boolean canDespawn(double distanceToClosestPlayer) {
        return false;
    }

    /**
     * Decrements the entity's air supply when underwater
     */
    protected int decreaseAirSupply(int air) {
        return air;
    }

    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        return ModSounds.ENTITY_GOLEM_HURT;
    }
}
