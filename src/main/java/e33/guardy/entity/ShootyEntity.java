package e33.guardy.entity;

import e33.guardy.debug.AvoidBulletDebugRenderer;
import e33.guardy.debug.PathFindingDebugRenderer;
import e33.guardy.debug.PatrolRouteDebugRenderer;
import e33.guardy.goal.LookAtTargetGoal;
import e33.guardy.goal.ShootBadGuysGoal;
import e33.guardy.goal.attack.*;
import e33.guardy.goal.move.AvoidBulletGoal;
import e33.guardy.goal.move.AvoidingDangerGoal;
import e33.guardy.goal.move.PatrolVillageGoal;
import e33.guardy.init.SoundsRegistry;
import e33.guardy.pathfinding.*;
import net.minecraft.block.BlockState;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.goal.HurtByTargetGoal;
import net.minecraft.entity.ai.goal.OpenDoorGoal;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.pathfinding.PathNavigator;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

// TODO 2 implement IRangedAttackMob?
public class ShootyEntity extends AnimalEntity implements PathPriorityByCoordinates {

    public final PathCreator pathCreator;
    public AvoidPeacefulCreaturesHelper avoidPeacefulCreaturesHelper = new AvoidPeacefulCreaturesHelper(this);
    public PatrolVillageGoal patrolVillageGoal;
    public AvoidBulletGoal avoidBulletGoal;

    public ShootyEntity(EntityType<? extends ShootyEntity> shooty, World world) {
        super(shooty, world);
        this.setBoundingBox(new AxisAlignedBB(3, 3, 3, 3, 3, 3));
        this.stepHeight = 1.0F;

        this.pathCreator = new PathCreator(this);
        PathFindingDebugRenderer.addEntity(this);
        PatrolRouteDebugRenderer.addEntity(this);
        AvoidBulletDebugRenderer.addEntity(this);
        this.setPathPriority(PathNodeType.WATER, -1.0F);
    }

    @Override
    public void tick() {
        this.avoidPeacefulCreaturesHelper.findPeacefulCreatures();// TODO 2 not every tick
        super.tick();
    }

    @Override
    public ShootyEntity createChild(@Nonnull AgeableEntity ageable) {
        return null;
    }

    @Nonnull
    public SoundCategory getSoundCategory() {
        return SoundCategory.NEUTRAL;
    }

    @Override
    protected void registerGoals() {
        this.patrolVillageGoal = new PatrolVillageGoal(this);
        this.avoidBulletGoal = new AvoidBulletGoal(this);
        this.goalSelector.addGoal(1, new AvoidingDangerGoal(this));
        this.goalSelector.addGoal(1, new OpenDoorGoal(this, true));
        this.goalSelector.addGoal(1, this.avoidBulletGoal);
        LookAtTargetGoal lookGoal = new LookAtTargetGoal(this);
        this.goalSelector.addGoal(2, lookGoal);
        this.goalSelector.addGoal(4, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(5, new AttackZombieGoal(this));
        this.targetSelector.addGoal(5, new AttackSpiderGoal(this));
        this.targetSelector.addGoal(5, new AttackCreeperGoal(this));
//        this.targetSelector.addGoal(5, new AttackSlimeGoal(this));
        this.goalSelector.addGoal(6, new ShootBadGuysGoal(this, lookGoal));
        this.goalSelector.addGoal(7, this.patrolVillageGoal);
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
//        if (damageSourceIn.isProjectile()) {
//            return SoundsRegistry.SHOOTY_HURT_ARROW;
//        }

        return SoundsRegistry.SHOOTY_HURT;
    }

    protected SoundEvent getDeathSound() {
        return SoundsRegistry.SHOOTY_DEATH;
    }

    protected void playStepSound(BlockPos pos, BlockState blockIn) {
        this.playSound(this.getStepSound(), 0.15F, 1.0F);
    }

    protected SoundEvent getStepSound() {
        return SoundsRegistry.SHOOTY_STEP;
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

        LOGGER.info("spawned!");
//        this.setItemStackToSlot(EquipmentSlotType.MAINHAND, new ItemStack(ItemRegistry.stickItem));
        return spawnDataIn;
    }

    @Override
    public void onDeath(DamageSource damageSource) {
        super.onDeath(damageSource);
    }

    public int getHorizontalFaceSpeed() {
        return 150;
    }

    /**
     * Returns new PathNavigateGround instance
     */
    protected PathNavigator createNavigator(World worldIn) {
        return new DangerousZoneAvoidanceNavigator(this, worldIn);
    }

    @Override
    public float getPathPriority(PathNodeType nodeType, BlockPos position) {
        return super.getPathPriority(nodeType);
    }
}
