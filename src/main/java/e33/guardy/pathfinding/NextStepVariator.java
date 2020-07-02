package e33.guardy.pathfinding;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import e33.guardy.pathfinding.targetFinding.ITargetFinder;
import e33.guardy.util.ToStringHelper;
import net.minecraft.block.*;
import net.minecraft.fluid.IFluidState;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Direction;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IWorldReader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;

class NextStepVariator {
    final static Logger LOGGER = LogManager.getLogger();

    private final IWorldReader world;
    private Map<String, BlockPos> swimmingTopPositionCache = Maps.newHashMap();
    private Map<String, BlockPos> notSwimmingTopPositionCache = Maps.newHashMap();
    private Map<String, Integer> canStandOnCache = Maps.newHashMap();

    NextStepVariator(IWorldReader world) {
        this.world = world;
    }

    List<BlockPos> makeSteps(ITargetFinder finder, AxisAlignedBB zone, List<BlockPos> blockedPoints, MovementLimitations limitations) {
        StepHistoryKeeper stepHistory = finder.getStepHistory();
        List<BlockPos> previousSteps = finder.getStepHistory().getLastStepPositions();
        List<BlockPos> newSteps = Lists.newArrayList();
        Map<String, Boolean> usedOnThisStep = Maps.newHashMap();

        for (BlockPos point : previousSteps) {
            List<BlockPos> stepVariants = getStepVariants(point, zone, stepHistory, blockedPoints, usedOnThisStep, limitations);

            newSteps.addAll(stepVariants);
            for (BlockPos position : stepVariants) {
                usedOnThisStep.put(ToStringHelper.toString(position), true);
            }
        }

        return newSteps;
    }

    @Nullable
    BlockPos getTopPosition(int x, int y, int z, MovementLimitations limitations) {
        // TODO 2 null will be a problem on too big amount of high heights (because it is not cached)
        Map<String, BlockPos> cache = limitations.canSwim ? this.swimmingTopPositionCache : this.notSwimmingTopPositionCache;
        String originalPositionKey = ToStringHelper.toString(x, y, z);
        if (cache.get(originalPositionKey) != null) {
            return cache.get(originalPositionKey);
        }

        BlockPos position = new MyMutableBlockPos(x, y, z);

        int diffInHeight = 0;
        while (this.isSolid(position, limitations) && diffInHeight < limitations.jumHeight) {
            position = position.up();
            diffInHeight++;
        }

        if (this.isSolid(position, limitations)) {
            return null;
        }
        position = position.up();

        BlockPos finishedPosition = position.toImmutable();
        cache.put(originalPositionKey, finishedPosition);
        return finishedPosition;
    }

    BlockPos getTopOrBottomPosition(BlockPos position, MovementLimitations limitations) {
        return this.getTopOrBottomPosition(position.getX(), position.getY(), position.getZ(), limitations);
    }

    BlockPos getTopOrBottomPosition(int x, int y, int z, MovementLimitations limitations) {
        Map<String, BlockPos> cache = limitations.canSwim ? this.swimmingTopPositionCache : this.notSwimmingTopPositionCache;
        String originalPositionKey = ToStringHelper.toString(x, y, z);
        if (cache.get(originalPositionKey) != null) {
            return cache.get(originalPositionKey);
        }

        BlockPos position = new MyMutableBlockPos(x, y, z);

        if (this.isSolid(position, limitations)) {
            int diffInHeight = 0;
            while (this.isSolid(position, limitations) && diffInHeight < limitations.jumHeight) {
                position = position.up();
                diffInHeight++;
            }
            if (this.isSolid(position, limitations)) {
                return null;
            }
        } else {
            int diffInHeight = 0;
            while (!this.isSolid(position, limitations) && diffInHeight < limitations.maxFallHeight) {
                position = position.down();
                diffInHeight++;
            }
            if (!this.isSolid(position, limitations)) {
                return null;
            }
            position = position.up();
        }

        BlockPos finishedPosition = position.toImmutable();
        cache.put(originalPositionKey, finishedPosition);
        return finishedPosition;
    }

    void clearCache() {
        this.swimmingTopPositionCache = Maps.newHashMap();
        this.notSwimmingTopPositionCache = Maps.newHashMap();
        this.canStandOnCache = Maps.newHashMap();
    }

    protected List<BlockPos> getStepVariants(BlockPos start, AxisAlignedBB zone, StepHistoryKeeper stepHistory, List<BlockPos> blockedPoints, Map<String, Boolean> usedOnThisStepPositions, MovementLimitations limitations) {
        int x = start.getX();
        int y = start.getY();
        int z = start.getZ();
        List<BlockPos> variants = Lists.newArrayList(
                this.getTopOrBottomPosition(x + 1, y, z, limitations),
                this.getTopOrBottomPosition(x - 1, y, z, limitations),
                this.getTopOrBottomPosition(x, y, z + 1, limitations),
                this.getTopOrBottomPosition(x, y, z - 1, limitations),

                this.getTopOrBottomPosition(x + 1, y, z + 1, limitations),
                this.getTopOrBottomPosition(x + 1, y, z - 1, limitations),
                this.getTopOrBottomPosition(x - 1, y, z - 1, limitations),
                this.getTopOrBottomPosition(x - 1, y, z + 1, limitations)
        );

        List<BlockPos> filteredVariants = Lists.newArrayList();

        for (BlockPos variant : variants) {
            if (variant != null && usedOnThisStepPositions.get(ToStringHelper.toString(variant)) == null && this.isValidPosition(start, variant, zone, stepHistory, blockedPoints, limitations)) {
                filteredVariants.add(variant);
            }
        }

        return filteredVariants;
    }

    protected boolean isValidPosition(BlockPos previousPosition, BlockPos newPosition, AxisAlignedBB zone, StepHistoryKeeper stepHistory, List<BlockPos> blockedPoints, MovementLimitations limitations) {
        boolean blockInZone = stepHistory.getPositionStep(newPosition) == null
                && newPosition.getX() >= zone.minX - 1 && newPosition.getX() <= zone.maxX
                && newPosition.getZ() >= zone.minZ - 1 && newPosition.getZ() <= zone.maxZ
                && newPosition.getY() >= zone.minY / 1.5 && newPosition.getY() <= zone.maxY * 1.5;

        if (!blockInZone) {
            return false;
        }

        if (!this.canWalkFromTo(previousPosition, newPosition, limitations)) {
            if (blockedPoints != null) {
                blockedPoints.add(newPosition);
            }
            return false;
        }

        // check walls between
        boolean isDiagonalBlock = newPosition.getX() != previousPosition.getX() && newPosition.getZ() != previousPosition.getZ();
        if (!isDiagonalBlock) {
            return true;
        }

        BlockPos toCheckWall = this.getTopPosition(newPosition.getX(), previousPosition.getY(), previousPosition.getZ(), limitations);
        BlockPos toCheckWall2 = this.getTopPosition(previousPosition.getX(), previousPosition.getY(), newPosition.getZ(), limitations);

        boolean wallOnWay = (toCheckWall == null || toCheckWall2 == null) || (toCheckWall.getY() - previousPosition.getY() > limitations.jumHeight && toCheckWall2.getY() - previousPosition.getY() > limitations.jumHeight);
        if (wallOnWay) {
            if (blockedPoints != null) { // TODO 2 maybe delete to not check in canAttack
                blockedPoints.add(newPosition);
            }

            return false;
        }

        return true;
    }

    protected boolean canWalkFromTo(BlockPos start, BlockPos end, MovementLimitations limitations) {
        if (!this.fitsIn(end, limitations)) {
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

        if (this.isFence(endState)) {
            endY += 0.5D;
        }
        if (this.isFence(startState)) {
            endY += 0.5D;
        }

        double diffInHeight = startY - endY;

        if (start.getY() > end.getY()) {
            return diffInHeight <= limitations.maxFallHeight && this.fitsIn(new BlockPos(end.getX(), start.getY(), end.getZ()), limitations);
        } else if (start.getY() < end.getY()) {
            return Math.abs(diffInHeight) <= limitations.jumHeight && this.fitsIn(new BlockPos(start.getX(), end.getY(), start.getZ()), limitations);
        }

        return true;
    }

    protected boolean fitsIn(BlockPos end, MovementLimitations limitations) {
        float x = end.getX() + 0.5F;
        int y = end.getY();
        float z = end.getZ() + 0.5F;

        boolean canStandOnPosition = this.canStandOn(end, limitations);
        if (canStandOnPosition && limitations.modelWidth > 1) {
            List<List<BlockPos>> blocksToCheck = this.mapAllBlocksBetweenTwoPoints(limitations, x, y, z);
            if (!this.samePatternOfBlocks(blocksToCheck, limitations)) {
                return false;
            }
        }

        return canStandOnPosition;
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

    protected boolean canStandOn(BlockPos position, MovementLimitations limitations) {
        String cacheKey = ToStringHelper.toString(position);
        int maxHeightToCheck = (int) Math.ceil(position.getY() + limitations.modelHeight);
        if (this.canStandOnCache.get(cacheKey) != null && this.canStandOnCache.get(cacheKey) >= maxHeightToCheck) { // todo optimize to not recache if it was not high because of block
            return true;
        }

        for (int y = position.getY(); y < maxHeightToCheck; y++) {
            if (this.isSolid(new BlockPos(position.getX(), y, position.getZ()), limitations)) {
                this.canStandOnCache.put(cacheKey, y - 1);
                return false;
            }
        }

        this.canStandOnCache.put(cacheKey, maxHeightToCheck);
        return true;
    }

    protected boolean samePatternOfBlocks(List<List<BlockPos>> blocksToCheck, MovementLimitations limitations) {
        List<List<Boolean>> checked = Lists.newArrayList();
        for (List<BlockPos> xBlockToCheck : blocksToCheck) {
            List<Boolean> xChecked = Lists.newArrayList();

            for (BlockPos blockToCheck : xBlockToCheck) {
                boolean checkResult = this.canStandOn(blockToCheck, limitations);
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

    boolean isSolid(@Nonnull BlockPos position, MovementLimitations limitations) {
        BlockState state = world.getBlockState(position);
        if (state.isSolid()) {
            return true;
        }

        if (state.isAir(world, position)) {
            return false;
        }

        if (world.getFluidState(position).isTagged(FluidTags.WATER)) {
            return limitations.canSwim;
        }

        Block block = state.getBlock();

        return block instanceof LeavesBlock
                || block == Blocks.LILY_PAD
                || block instanceof GlassBlock
                || block instanceof BedBlock
                || block instanceof ShulkerBoxBlock
                || block instanceof HopperBlock
                || block instanceof TrapDoorBlock
                || block instanceof FlowerPotBlock
                || block instanceof LanternBlock;
    }

    boolean isFence(BlockState state) {
        Block block = state.getBlock();
        return block instanceof FenceBlock || block instanceof FenceGateBlock || block instanceof WallBlock;
    }
}
