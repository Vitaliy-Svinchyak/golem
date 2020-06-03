package e33.guardy.pathfinding;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import e33.guardy.entity.ShootyEntity;
import e33.guardy.pathfinding.pathBuilding.SafePathBuilder;
import e33.guardy.pathfinding.targetFinding.FullScouting;
import e33.guardy.pathfinding.targetFinding.ITargetFinder;
import e33.guardy.pathfinding.targetFinding.SafePlaceFinder;
import e33.guardy.util.ToStringHelper;
import net.minecraft.block.*;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.fluid.IFluidState;
import net.minecraft.pathfinding.Path;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Direction;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IWorldReader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class PathCreator {
    final static Logger LOGGER = LogManager.getLogger();

    protected final ShootyEntity shooty;

    protected Map<String, BlockPos> swimmingTopPositionCache = Maps.newHashMap();
    protected Map<String, BlockPos> notSwimmingTopPositionCache = Maps.newHashMap();
    protected Map<String, Integer> canStandOnCache = Maps.newHashMap();

    public Path currentPath;
    public SafePlaceFinder safePlaceFinder;
    public Collection<ITargetFinder> enemyScouts;
    public List<BlockPos> safestPoints = Lists.newArrayList();

    public PathCreator(ShootyEntity shooty) {
        this.shooty = shooty;
    }

    public Path getPath(List<MobEntity> enemies) {
        if (!this.shooty.onGround) {
            return null;
        }
        this.currentPath = null;

        MovementLimitations shootyLimitations = this.createLimitations(this.shooty);
        IWorldReader world = this.shooty.getEntityWorld();
        AxisAlignedBB searchZone = this.getSearchZone();
        BlockPos shootyCurrentPosition = this.getEntityPos(shooty, shootyLimitations);
        List<BlockPos> blockedPoints = Lists.newArrayList();

        Map<UUID, MovementLimitations> enemyLimitations = this.createEnemyLimitations(enemies);

        Map<UUID, ITargetFinder> enemyScouts = this.createEnemyScouts(enemies);
        this.createEnemiesCurrentSteps(world, enemies, enemyLimitations, enemyScouts);

        this.safePlaceFinder = new SafePlaceFinder(shootyCurrentPosition, enemyScouts.values());
        this.enemyScouts = enemyScouts.values();

        int stepNumber = 1;
        while (stepNumber < 100) {
            List<BlockPos> newSteps = this.makeSteps(this.safePlaceFinder, world, searchZone, blockedPoints, shootyLimitations);
            if (newSteps.size() == 0) {
                break;
            }
            this.safePlaceFinder.nextStep(newSteps, stepNumber);

            for (MobEntity enemy : enemies) {
                UUID uid = enemy.getUniqueID();
                ITargetFinder enemyScout = enemyScouts.get(uid);
                List<BlockPos> enemyBlockedPoints = Lists.newArrayList();
                List<BlockPos> enemyNewSteps = this.makeSteps(enemyScout, world, searchZone, enemyBlockedPoints, enemyLimitations.get(uid));

                if (enemyBlockedPoints.size() > 0) {
                    List<BlockPos> enemyReachableForAttackPoints = this.getReachableForAttackPoints(enemyNewSteps, enemyBlockedPoints);
                    enemyNewSteps.addAll(enemyReachableForAttackPoints);
                }

                enemyScout.nextStep(enemyNewSteps, stepNumber);
            }

            stepNumber++;
        }

        this.safestPoints = this.safePlaceFinder.getTargets();
        SafePathBuilder pathBuilder = new SafePathBuilder(enemyScouts.values());
        this.currentPath = pathBuilder.build(shootyLimitations, this.safePlaceFinder);

        return this.currentPath;
    }

    protected List<BlockPos> makeSteps(ITargetFinder finder, IWorldReader world, AxisAlignedBB zone, List<BlockPos> blockedPoints, MovementLimitations limitations) {
        StepHistoryKeeper stepHistory = finder.getStepHistory();
        List<BlockPos> previousSteps = finder.getStepHistory().getLastStepPositions();
        List<BlockPos> newSteps = Lists.newArrayList();

        for (BlockPos point : previousSteps) {
            List<BlockPos> stepVariants = getStepVariants(world, point, zone, stepHistory, blockedPoints, newSteps, limitations);

            newSteps.addAll(stepVariants);
        }

        return newSteps;
    }

    protected List<BlockPos> getStepVariants(IWorldReader world, BlockPos start, AxisAlignedBB zone, StepHistoryKeeper stepHistory, List<BlockPos> blockedPoints, List<BlockPos> usedOnThisStepPositions, MovementLimitations limitations) {
        int x = start.getX();
        int y = start.getY();
        int z = start.getZ();
        List<BlockPos> variants = Lists.newArrayList(
                getTopPosition(world, x + 1, y, z, limitations),
                getTopPosition(world, x - 1, y, z, limitations),
                getTopPosition(world, x, y, z + 1, limitations),
                getTopPosition(world, x, y, z - 1, limitations),

                getTopPosition(world, x + 1, y, z + 1, limitations),
                getTopPosition(world, x + 1, y, z - 1, limitations),
                getTopPosition(world, x - 1, y, z - 1, limitations),
                getTopPosition(world, x - 1, y, z + 1, limitations)
        );

        List<BlockPos> filteredVariants = Lists.newArrayList();

        for (BlockPos variant : variants) {
            // TODO optimize usedOnThisStepPositions to map
            if (!usedOnThisStepPositions.contains(variant) && this.isValidPosition(world, start, variant, zone, stepHistory, blockedPoints, limitations)) {
                filteredVariants.add(variant);
            }
        }

        return filteredVariants;
    }

    protected boolean isValidPosition(IWorldReader world, BlockPos previousPosition, BlockPos newPosition, AxisAlignedBB zone, StepHistoryKeeper stepHistory, List<BlockPos> blockedPoints, MovementLimitations limitations) {
        boolean blockInZone = stepHistory.getPositionStep(newPosition) == null
                && newPosition.getX() >= zone.minX - 1 && newPosition.getX() <= zone.maxX
                && newPosition.getZ() >= zone.minZ - 1 && newPosition.getZ() <= zone.maxZ
                && newPosition.getY() >= zone.minY / 1.5 && newPosition.getY() <= zone.maxY * 1.5;

        if (!blockInZone) {
            return false;
        }

        if (!canWalkFromTo(world, previousPosition, newPosition, limitations)) {
            blockedPoints.add(newPosition);
            return false;
        }

        // check walls between
        boolean isDiagonalBlock = newPosition.getX() != previousPosition.getX() && newPosition.getZ() != previousPosition.getZ();
        if (!isDiagonalBlock) {
            return true;
        }

        BlockPos toCheckWall = this.getTopPosition(world, newPosition.getX(), previousPosition.getY(), previousPosition.getZ(), limitations);
        BlockPos toCheckWall2 = this.getTopPosition(world, previousPosition.getX(), previousPosition.getY(), newPosition.getZ(), limitations);

        boolean noWallOnWay = toCheckWall.getY() - previousPosition.getY() <= limitations.jumHeight && toCheckWall2.getY() - previousPosition.getY() <= limitations.jumHeight;
        if (noWallOnWay) {
            return true;
        }

        blockedPoints.add(newPosition);
        return false;
    }

    protected BlockPos getEnemyStandPosition(IWorldReader world, @Nonnull BlockPos position, MovementLimitations limitations) {
        while (!isSolid(world, position, limitations)) {
            position = position.down();
        }
        position = position.up();

        return position;
    }

    protected BlockPos getTopPosition(IWorldReader world, @Nonnull BlockPos originalPosition, MovementLimitations limitations) {
        return this.getTopPosition(world, originalPosition.getX(), originalPosition.getY(), originalPosition.getZ(), limitations);
    }

    protected BlockPos getTopPosition(IWorldReader world, int x, int y, int z, MovementLimitations limitations) {
        Map<String, BlockPos> cache = limitations.canSwim ? this.swimmingTopPositionCache : this.notSwimmingTopPositionCache;
        String originalPositionKey = ToStringHelper.toString(x, y, z);
        if (cache.get(originalPositionKey) != null) {
            return cache.get(originalPositionKey);
        }

        BlockPos position = new MyMutableBlockPos(x, y, z);

        if (isSolid(world, position, limitations)) {
            while (isSolid(world, position, limitations)) {
                position = position.up();
            }
        } else {
            while (!isSolid(world, position, limitations)) {
                position = position.down();
            }
            position = position.up();
        }

        BlockPos finishedPosition = position.toImmutable();
        cache.put(originalPositionKey, finishedPosition);
        return finishedPosition;
    }

    protected boolean canWalkFromTo(IWorldReader world, BlockPos start, BlockPos end, MovementLimitations limitations) {
        if (!this.fitsIn(world, end, limitations)) {
            return false;
        }

        Block block = world.getBlockState(end).getBlock();
        IFluidState fluidState = world.getFluidState(end);
        if (
                (!limitations.canSwim && fluidState.isTagged(FluidTags.WATER) && world.getFluidState(end.up()).isTagged(FluidTags.WATER))
                        || fluidState.isTagged(FluidTags.LAVA) || block == Blocks.FIRE) {
            return false;
        }

        double startY = start.getY();
        double endY = end.getY();

        BlockPos start2 = start.down();
        BlockState startState = world.getBlockState(start2);
        VoxelShape startShape = startState.getShape(world, start2);
        if (!startShape.isEmpty()) {
            startY = startY - 1 + startShape.getEnd(Direction.Axis.Y);
        }

        BlockPos end2 = end.down();
        BlockState endState = world.getBlockState(end2);
        VoxelShape endShape = endState.getShape(world, end2);
        if (!endShape.isEmpty()) {
            endY = endY - 1 + endShape.getEnd(Direction.Axis.Y);
        }

        double diffInHeight = startY - endY;

        if (start.getY() > end.getY()) {
            return diffInHeight <= limitations.maxFallHeight && this.fitsIn(world, new BlockPos(end.getX(), start.getY(), end.getZ()), limitations);
        } else if (start.getY() < end.getY()) {
            return Math.abs(diffInHeight) <= limitations.jumHeight && this.fitsIn(world, new BlockPos(start.getX(), end.getY(), start.getZ()), limitations);
        }

        return true;
    }

    protected boolean fitsIn(IWorldReader world, BlockPos end, MovementLimitations limitations) {
        float x = end.getX() + 0.5F;
        int y = end.getY();
        float z = end.getZ() + 0.5F;

        boolean canStandOnPosition = this.canStandOn(world, end, limitations);
        if (canStandOnPosition && limitations.modelWidth > 1) {
            List<List<BlockPos>> blocksToCheck = this.mapAllBlocksBetweenTwoPoints(limitations, x, y, z);
            if (!this.samePatternOfBlocks(world, blocksToCheck, limitations)) {
                return false;
            }
        }

        return canStandOnPosition;
    }

    protected boolean canStandOn(IWorldReader world, BlockPos position, MovementLimitations limitations) {
        String cacheKey = ToStringHelper.toString(position);
        int maxHeightToCheck = (int) Math.ceil(position.getY() + limitations.modelHeight);
        if (this.canStandOnCache.get(cacheKey) != null && this.canStandOnCache.get(cacheKey) >= maxHeightToCheck) { // todo optimize to not recache if it was not high because of block
            return true;
        }

        for (int y = position.getY(); y < maxHeightToCheck; y++) {
            if (this.isSolid(world, new BlockPos(position.getX(), y, position.getZ()), limitations)) {
                this.canStandOnCache.put(cacheKey, y - 1);
                return false;
            }
        }

        this.canStandOnCache.put(cacheKey, maxHeightToCheck);
        return true;
    }

    protected boolean samePatternOfBlocks(IWorldReader world, List<List<BlockPos>> blocksToCheck, MovementLimitations limitations) {
        List<List<Boolean>> checked = Lists.newArrayList();
        for (List<BlockPos> xBlockToCheck : blocksToCheck) {
            List<Boolean> xChecked = Lists.newArrayList();

            for (BlockPos blockToCheck : xBlockToCheck) {
                boolean checkResult = this.canStandOn(world, blockToCheck, limitations);
                xChecked.add(checkResult);
            }
            checked.add(xChecked);
        }

        int roundedWidth = (int) Math.ceil(limitations.modelWidth);
        for (int x = 0; x < checked.size(); x++) {
            for (int z = 0; z < checked.size(); z++) {
                if (this.squareIsAllTrue(checked, x, z, roundedWidth)) {
                    return true;
                }
            }
        }

        return false;
    }

    protected boolean squareIsAllTrue(List<List<Boolean>> checked, int startX, int startZ, int width) {
        for (int x = startX; x < startX + width; x++) {
            for (int z = startZ; z < startZ + width; z++) {
                if (checked.size() <= x || checked.get(x).size() <= z || checked.get(x).get(z) == false) {
                    return false;
                }
            }
        }

        return true;
    }

    protected List<List<BlockPos>> mapAllBlocksBetweenTwoPoints(MovementLimitations limitations, float stepX, int stepY, float stepZ) {
        double startX = stepX - Math.ceil(limitations.modelWidth / 2);
        double endX = stepX + Math.ceil(limitations.modelWidth / 2);
        double startZ = stepZ - Math.ceil(limitations.modelWidth / 2);
        double endZ = stepZ + Math.ceil(limitations.modelWidth / 2);

        List<List<BlockPos>> downBlocks = Lists.newArrayList();
        for (double x = startX; x <= endX; x++) {
            List<BlockPos> downBlocksForX = Lists.newArrayList();
            for (double z = startZ; z <= endZ; z++) {
                downBlocksForX.add(new BlockPos(x, stepY, z));
            }
            downBlocks.add(downBlocksForX);
        }

        return downBlocks;
    }

    protected boolean isSolid(IWorldReader world, @Nonnull BlockPos position, MovementLimitations limitations) {
        BlockState state = world.getBlockState(position);
        if (state.isAir(world, position)) {
            return false;
        }

        if (world.getFluidState(position).isTagged(FluidTags.WATER)) {
            return limitations.canSwim;
        }

        Block block = state.getBlock();
        if (block instanceof LeavesBlock
                || block == Blocks.LILY_PAD
                || block instanceof GlassBlock
                || block instanceof BedBlock
                || block instanceof ShulkerBoxBlock
                || block instanceof HopperBlock
                || block instanceof TrapDoorBlock
                || block instanceof FlowerPotBlock
                || block instanceof LanternBlock
        ) {
            return true;
        }

        return state.isSolid();
    }

    private MovementLimitations createLimitations(MobEntity entity) {
        return new MovementLimitations(1F, entity.getMaxFallHeight(), entity.getHeight(), entity.getWidth(), entity.getPathPriority(PathNodeType.WATER) >= 0F, entity);
    }

    // Needed because entity can halfly stand on higher block but Minecraft will track it's position as air near it
    private BlockPos getEntityPos(ShootyEntity entity, MovementLimitations limitations) {
        BlockPos position = entity.getPosition();

        if (!this.getTopPosition(entity.world, position, limitations).equals(position)) {
            return new BlockPos(Math.round(entity.posX), MathHelper.floor(entity.posY), Math.round(entity.posZ));
        }

        return position;
    }

    private Map<UUID, MovementLimitations> createEnemyLimitations(List<MobEntity> enemies) {
        Map<UUID, MovementLimitations> limitations = Maps.newHashMap();

        for (MobEntity enemy : enemies) {
            limitations.put(enemy.getUniqueID(), this.createLimitations(enemy));
        }

        return limitations;
    }


    private Map<UUID, ITargetFinder> createEnemyScouts(List<MobEntity> enemies) {
        Map<UUID, ITargetFinder> scouts = Maps.newHashMap();

        for (MobEntity enemy : enemies) {
            scouts.put(enemy.getUniqueID(), new FullScouting(enemy.getPosition()));
        }

        return scouts;
    }

    // Used for spiders. If there is a tunnel with height and width both of 1, then he can attack from nearest point but can be inside tunnel
    private List<BlockPos> getReachableForAttackPoints(List<BlockPos> newSteps, List<BlockPos> blockedPoints) {
        List<BlockPos> unBlockedPoints = Lists.newArrayList();

        for (BlockPos blockedPoint : blockedPoints) {
            for (BlockPos waveNeighbor : newSteps) {
                int xDiff = Math.abs(blockedPoint.getX() - waveNeighbor.getX());
                int zDiff = Math.abs(blockedPoint.getZ() - waveNeighbor.getZ());
                int yDiff = Math.abs(blockedPoint.getY() - waveNeighbor.getY());

                if (xDiff <= 1 && zDiff <= 1 && yDiff <= 1) {
                    unBlockedPoints.add(blockedPoint);
                    break;
                }
            }
        }

        return unBlockedPoints;
    }

    private void createEnemiesCurrentSteps(IWorldReader world, List<MobEntity> enemies, Map<UUID, MovementLimitations> enemyLimitations, Map<UUID, ITargetFinder> enemyScouts) {
        for (MobEntity enemy : enemies) {
            enemyScouts.get(enemy.getUniqueID()).nextStep(Lists.newArrayList(getEnemyStandPosition(world, enemy.getPosition(), enemyLimitations.get(enemy.getUniqueID()))), 0);
        }
    }

    private AxisAlignedBB getSearchZone() {
        AxisAlignedBB zone = this.shooty.getBoundingBox().grow(Math.round(this.shooty.getAttribute(SharedMonsterAttributes.FOLLOW_RANGE).getValue() / 2));
        // Rounding to chunk borders
        double minX = zone.minX - (15 - Math.abs(zone.minX % 16));
        double minZ = zone.minZ - (15 - Math.abs(zone.minZ % 16));
        double maxX = zone.maxX + Math.abs(zone.maxX % 16) - 1;
        double maxZ = zone.maxZ + Math.abs(zone.maxZ % 16) - 1;

        return new AxisAlignedBB(minX, zone.minY, minZ, maxX, zone.maxY, maxZ);
    }
}
