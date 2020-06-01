package e33.guardy.goal.move;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import e33.guardy.debug.TimeMeter;
import e33.guardy.entity.ShootyEntity;
import e33.guardy.pathfinding.MyMutableBlockPos;
import e33.guardy.util.ToStringHelper;
import net.minecraft.block.*;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.memory.MemoryModuleType;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.merchant.villager.VillagerEntity;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.GlobalPos;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Map;

public class PatrolVillageGoal extends Goal {
    final static Logger LOGGER = LogManager.getLogger();

    private World world;
    private ShootyEntity shooty;
    private Map<String, Integer> topPositionCache = Maps.newHashMap();
    public List<BlockPos> patrolPoints = null;

    public PatrolVillageGoal(ShootyEntity creatureIn) {
        this.shooty = creatureIn;
        this.world = this.shooty.getEntityWorld();
    }

    @Override
    public boolean shouldExecute() {
        return this.world.isAreaLoaded(this.shooty.getPosition(), 150); // TODO
    }

    public boolean shouldContinueExecuting() {
        return !this.shooty.getNavigator().noPath(); // TODO
    }

    public void startExecuting() {
        if (this.patrolPoints == null) {
            TimeMeter.moduleStart("PatrolVillageGoal");
            this.patrolPoints = this.getPatrolPoints();
            TimeMeter.moduleEnd("PatrolVillageGoal");
        }
    }

    protected List<BlockPos> getPatrolPoints() {
        List<VillagerEntity> villagers = this.world.getEntitiesWithinAABB(VillagerEntity.class, this.shooty.getBoundingBox().grow(150));
        Map<String, Boolean> usedPoses = Maps.newHashMap();

        if (villagers.size() == 0) {
            return Lists.newArrayList();
        }

        List<ChunkPos> chunks = Lists.newArrayList();

        for (VillagerEntity villager : villagers) {
            List<BlockPos> memoryPoints = this.getVillagerKnownPositionsOfVillage(villager);

            for (BlockPos memoryPosition : memoryPoints) {
                ChunkPos chunkPosition = this.world.getChunkAt(memoryPosition).getPos();

                if (usedPoses.get(ToStringHelper.toString(chunkPosition)) == null) {
                    usedPoses.put(ToStringHelper.toString(chunkPosition), true);
                    chunks.add(chunkPosition);
                }
            }
        }

        List<ChunkPos> roundedChunks = this.roundChunkPositions(chunks, usedPoses);
        List<ChunkPos> roundedChunks2 = this.roundChunkPositions(roundedChunks, usedPoses);

        return this.createPoints(roundedChunks2);
    }

    private List<BlockPos> createPoints(List<ChunkPos> villageChunks) {
        Map<String, Boolean> allBlocks = Maps.newHashMap();
        List<BlockPos> patrolPoints = this.createPatrolPoints(villageChunks);

        return patrolPoints;
    }

    private List<BlockPos> createPatrolPoints(List<ChunkPos> villageChunks) {
        Map<String, Boolean> allBlocks = Maps.newHashMap();
        List<BlockPos> patrolPoints = Lists.newArrayList();

        for (ChunkPos chunkPos : villageChunks) {
            for (int x = chunkPos.getXStart(); x <= chunkPos.getXEnd(); x++) {
                for (int z = chunkPos.getZStart(); z <= chunkPos.getZEnd(); z++) {
                    allBlocks.put(ToStringHelper.toString(x, 0, z), true);
                }
            }
        }

        for (ChunkPos chunkPos : villageChunks) {
            for (int x = chunkPos.getXStart(); x <= chunkPos.getXEnd(); x++) {
                for (int z = chunkPos.getZStart(); z <= chunkPos.getZEnd(); z++) {
                    boolean validX = allBlocks.get(ToStringHelper.toString(x + 1, 0, z)) == null || allBlocks.get(ToStringHelper.toString(x - 1, 0, z)) == null;
                    boolean validZ = allBlocks.get(ToStringHelper.toString(x, 0, z + 1)) == null || allBlocks.get(ToStringHelper.toString(x, 0, z - 1)) == null;

                    if (validX || validZ) {
                        int y = this.getTopPosition(this.world, x, this.shooty.getPosition().getY(), z);

                        if (allBlocks.get(ToStringHelper.toString(x, y, z)) == null) {
                            patrolPoints.add(new BlockPos(x, y, z));
                            allBlocks.put(ToStringHelper.toString(x, y, z), true);
                        }
                    }
                }
            }
        }

        return patrolPoints;
    }

    private List<ChunkPos> roundChunkPositions(List<ChunkPos> chunksIn, Map<String, Boolean> usedPoses) {
        List<ChunkPos> chunksOut = Lists.newArrayList(chunksIn);

        for (ChunkPos pos : chunksIn) {
            if (this.hasNeighbors(pos.x + 1, pos.z, usedPoses)) {
                ChunkPos newChunkPos = new ChunkPos(pos.x + 1, pos.z);
                chunksOut.add(newChunkPos);
                usedPoses.put(ToStringHelper.toString(newChunkPos), true);
            }
        }

        return chunksOut;
    }

    private boolean hasNeighbors(int x, int z, Map<String, Boolean> usedPoses) {
        int neighbors = 0;
        if (usedPoses.get(ToStringHelper.toString(x + 1, z)) != null) {
            neighbors++;
        }
        if (usedPoses.get(ToStringHelper.toString(x - 1, z)) != null) {
            neighbors++;
        }
        if (usedPoses.get(ToStringHelper.toString(x, z + 1)) != null) {
            neighbors++;
        }
        if (usedPoses.get(ToStringHelper.toString(x, z - 1)) != null) {
            neighbors++;
        }

        return neighbors > 1;
    }

    private List<BlockPos> getVillagerKnownPositionsOfVillage(VillagerEntity villager) {
        Brain<?> brain = villager.getBrain();
        List<BlockPos> knownPositions = Lists.newArrayList();

        if (brain.getMemory(MemoryModuleType.MEETING_POINT).isPresent()) {
            knownPositions.add(brain.getMemory(MemoryModuleType.MEETING_POINT).get().getPos());
        }
        if (brain.getMemory(MemoryModuleType.HOME).isPresent()) {
            knownPositions.add(brain.getMemory(MemoryModuleType.HOME).get().getPos());
        }
        if (brain.getMemory(MemoryModuleType.JOB_SITE).isPresent()) {
            knownPositions.add(brain.getMemory(MemoryModuleType.JOB_SITE).get().getPos());
        }
        if (brain.getMemory(MemoryModuleType.HIDING_PLACE).isPresent()) {
            knownPositions.add(brain.getMemory(MemoryModuleType.HIDING_PLACE).get().getPos());
        }
        if (brain.getMemory(MemoryModuleType.NEAREST_BED).isPresent()) {
            knownPositions.add(brain.getMemory(MemoryModuleType.NEAREST_BED).get());
        }

        if (brain.getMemory(MemoryModuleType.SECONDARY_JOB_SITE).isPresent()) {
            for (GlobalPos globalPos : brain.getMemory(MemoryModuleType.SECONDARY_JOB_SITE).get()) {
                knownPositions.add(globalPos.getPos());
            }
        }
        if (brain.getMemory(MemoryModuleType.INTERACTABLE_DOORS).isPresent()) {
            for (GlobalPos globalPos : brain.getMemory(MemoryModuleType.INTERACTABLE_DOORS).get()) {
                knownPositions.add(globalPos.getPos());
            }
        }

        return knownPositions;
    }

    private int getTopPosition(IWorldReader world, int x, int y, int z) {
        String cacheKey = ToStringHelper.toString(x, y, z);
        if (this.topPositionCache.get(cacheKey) != null) {
            return this.topPositionCache.get(cacheKey);
        }
        BlockPos position = new MyMutableBlockPos(x, y, z);

        if (isSolid(world, position)) {
            while (isSolid(world, position)) {
                position = position.up();
            }
        } else {
            while (!isSolid(world, position)) {
                position = position.down();
            }
            position = position.up();
        }

        this.topPositionCache.put(cacheKey, position.getY());

        return position.getY();
    }

    private boolean isSolid(IWorldReader world, @Nonnull BlockPos position) {
        BlockState state = world.getBlockState(position);
        if (state.isAir(world, position)) {
            return false;
        }

        if (world.getFluidState(position).isTagged(FluidTags.WATER)) {
            return true;
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
}
