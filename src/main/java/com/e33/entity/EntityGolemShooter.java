package com.e33.entity;

import com.e33.E33;
import com.e33.goal.attack.*;
import com.e33.core.ModSounds;
import com.e33.goal.ShootBadGuysGoal;
import com.e33.goal.move.DangerousZone;
import com.e33.init.BlockRegistry;
import net.minecraft.entity.*;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

// TODO 2 implement IRangedAttackMob?
public class EntityGolemShooter extends AnimalEntity {

    public AvoidPeacefulCreaturesHelper avoidPeacefulCreaturesGoal = new AvoidPeacefulCreaturesHelper(this);

    public EntityGolemShooter(EntityType<? extends EntityGolemShooter> golem, World world) {
        super(golem, world);
        this.setBoundingBox(new AxisAlignedBB(3, 3, 3, 3, 3, 3));
        this.stepHeight = 1.0F;
    }

    @Override
    public void tick() {
        this.avoidPeacefulCreaturesGoal.findPeacefulCreatures();
        super.tick();
    }

    @Override
    public EntityGolemShooter createChild(@Nonnull AgeableEntity ageable) {
        return null;
    }

    @Nonnull
    public SoundCategory getSoundCategory() {
        return SoundCategory.NEUTRAL;
    }

    @Override
    protected void registerGoals() {
        // TODO 2 custom priority queue
//        this.goalSelector.addGoal(1, new ShootBadGuysGoal(this));
        this.targetSelector.addGoal(4, new AttackSkeletonGoal(this));
        this.targetSelector.addGoal(5, new AttackZombieGoal(this));
        this.targetSelector.addGoal(5, new AttackSpiderGoal(this));
        this.targetSelector.addGoal(6, new AttackCreeperGoal(this));
        this.targetSelector.addGoal(7, new AttackSlimeGoal(this));
    }

    // TODO 2 teams implementation (isOnSameTeam method)
    protected void registerAttributes() {
        super.registerAttributes();
        this.getAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(10.0D);
        this.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.5D);
        this.getAttribute(SharedMonsterAttributes.KNOCKBACK_RESISTANCE).setBaseValue(1.0D);
        this.getAttributes().registerAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(5.0D);
        this.getAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(32D);
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

    public int getMaxFallHeight() {
        return 3;
    }

    @Override
    public EntitySize getSize(Pose poseIn) {
        return EntitySize.fixed(0.6F, 1.85F);
    }

    @Nullable
    public ILivingEntityData onInitialSpawn(IWorld worldIn, DifficultyInstance difficultyIn, SpawnReason reason, @Nullable ILivingEntityData spawnDataIn, @Nullable CompoundNBT dataTag) {
        spawnDataIn = super.onInitialSpawn(worldIn, difficultyIn, reason, spawnDataIn, dataTag);

        this.setItemStackToSlot(EquipmentSlotType.MAINHAND, new ItemStack(BlockRegistry.stickItem));
        return spawnDataIn;
    }
}
