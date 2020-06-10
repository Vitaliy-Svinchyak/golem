package e33.guardy.goal.move;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import e33.guardy.E33;
import e33.guardy.client.detail.AnimationState;
import e33.guardy.client.listener.AnimationStateListener;
import e33.guardy.entity.ShootyEntity;
import e33.guardy.event.MoveEvent;
import e33.guardy.event.NoActionEvent;
import e33.guardy.pathfinding.MyMutableBlockPos;
import e33.guardy.util.ToStringHelper;
import net.minecraft.block.*;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.memory.MemoryModuleType;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.merchant.villager.VillagerEntity;
import net.minecraft.pathfinding.Path;
import net.minecraft.pathfinding.PathPoint;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.GlobalPos;
import net.minecraft.world.World;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class PatrolVillageGoal extends Goal {
    final static Logger LOGGER = LogManager.getLogger();

    private World world;
    private ShootyEntity shooty;
    private Map<String, Integer> topPositionCache = Maps.newHashMap();
    public List<BlockPos> patrolPoints = null;
    public List<BlockPos> angularPoints = null;
    private static Map<String, Boolean> allBlocks = Maps.newHashMap();
    private int currentPathNumber;

    public PatrolVillageGoal(ShootyEntity creatureIn) {
        this.shooty = creatureIn;
        this.world = this.shooty.getEntityWorld();
    }

    @Override
    public boolean shouldExecute() {
        return this.world.isAreaLoaded(this.shooty.getPosition(), 150); // TODO dynamic range and only when shooty is near MEET_POINT
    }

    public boolean shouldContinueExecuting() {
        // TODO check monsters
        if (this.shooty.getNavigator().noPath()) {
            int nextPathNumber = this.currentPathNumber + 1;
            LOGGER.info(this.currentPathNumber);

            // TODO implement normally. Minecraft somewhy decides that he finished path earlier than it really was finished
            boolean forceToFinish = false;
            if (this.shooty.getPosition().distanceSq(this.angularPoints.get(this.currentPathNumber)) > 4D) {
                nextPathNumber = this.currentPathNumber;
                forceToFinish = true;
            }

            if (nextPathNumber > this.angularPoints.size() - 1) {
                nextPathNumber = 0;
            }
            this.currentPathNumber = nextPathNumber;
            LOGGER.info(nextPathNumber);
            LOGGER.info("--------------");

            Path pathToStartOfPatrol = this.shooty.pathCreator.getPathBetweenPoints(this.shooty.getPosition(), this.angularPoints.get(this.currentPathNumber));
            this.shooty.getNavigator().setPath(pathToStartOfPatrol, this.shooty.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).getValue());

            if (!forceToFinish) {
                PathPoint lastPathPoint = pathToStartOfPatrol.getFinalPathPoint();
                BlockPos lastPoint = new BlockPos(lastPathPoint.x, lastPathPoint.y, lastPathPoint.z);
                this.angularPoints.set(this.currentPathNumber, lastPoint);
            }

        }

        return true;
    }

    public void startExecuting() {
        if (this.patrolPoints == null) {
            this.patrolPoints = this.getPatrolPoints();
            this.currentPathNumber = this.getNearestAngularlPoint();
            Path pathToStartOfPatrol = this.shooty.pathCreator.getPathBetweenPoints(this.shooty.getPosition(), this.angularPoints.get(this.currentPathNumber));
            PathPoint lastPathPoint = pathToStartOfPatrol.getFinalPathPoint();
            BlockPos lastPoint = new BlockPos(lastPathPoint.x, lastPathPoint.y, lastPathPoint.z);
            this.angularPoints.set(this.currentPathNumber, lastPoint);
            this.shooty.getNavigator().setPath(pathToStartOfPatrol, this.shooty.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).getValue());
            this.move();
        }
    }

    public void resetTask() {
        this.stop();
        this.shooty.getNavigator().clearPath();
    }

    private int getNearestAngularlPoint() {
        int nearestPatrolPoint = 0;
        BlockPos currentPosition = this.shooty.getPosition();

        for (int i = 1; i < this.angularPoints.size(); i++) {
            BlockPos patrolPoint = this.angularPoints.get(i);
            if (currentPosition.distanceSq(patrolPoint) < currentPosition.distanceSq(this.angularPoints.get(nearestPatrolPoint))) {
                nearestPatrolPoint = i;
            }
        }

        return nearestPatrolPoint;
    }

    protected List<BlockPos> getPatrolPoints() {
        List<VillagerEntity> villagers = this.world.getEntitiesWithinAABB(VillagerEntity.class, this.shooty.getBoundingBox().grow(150));
        Map<String, Boolean> usedChunkPoses = Maps.newHashMap();

        if (villagers.size() == 0) {
            return Lists.newArrayList();
        }

        List<ChunkPos> chunks = Lists.newArrayList();

        for (VillagerEntity villager : villagers) {
            List<BlockPos> memoryPoints = this.getVillagerKnownPositionsOfVillage(villager);

            for (BlockPos memoryPosition : memoryPoints) {
                ChunkPos chunkPosition = this.world.getChunkAt(memoryPosition).getPos();

                if (usedChunkPoses.get(ToStringHelper.toString(chunkPosition)) == null) {
                    usedChunkPoses.put(ToStringHelper.toString(chunkPosition), true);
                    chunks.add(chunkPosition);
                }
            }
        }

        List<ChunkPos> roundedChunks = this.roundChunkPositions(chunks, usedChunkPoses);
        List<ChunkPos> roundedChunks2 = this.roundChunkPositions(roundedChunks, usedChunkPoses);

        return this.createPoints(roundedChunks2);
    }

    private List<BlockPos> createPoints(List<ChunkPos> villageChunks) {
        List<BlockPos> patrolPoints = this.createPatrolPoints(villageChunks);
        angularPoints = this.createAngularPoints(patrolPoints);

        for (BlockPos pos : angularPoints) {
            patrolPoints.remove(pos);
        }

        return patrolPoints;
    }

    private List<BlockPos> createAngularPoints(List<BlockPos> patrolPoints) {
        List<BlockPos> angularPoints = Lists.newArrayList();

        for (BlockPos pos : patrolPoints) {
            List<BlockPos> neighbors = this.getNeighbors(pos, allBlocks);
            if (neighbors.size() != 2) {
                continue;
            }

            boolean neighborsOnOneLine = neighbors.get(0).getX() == neighbors.get(1).getX() || neighbors.get(0).getZ() == neighbors.get(1).getZ();
            if (!neighborsOnOneLine) {
                angularPoints.add(pos);
            }
        }

        int maxX = angularPoints.get(0).getX();
        int minX = angularPoints.get(0).getX();
        int maxZ = angularPoints.get(0).getZ();
        int minZ = angularPoints.get(0).getZ();
        for (BlockPos pos : angularPoints) {
            if (pos.getX() > maxX) {
                maxX = pos.getX();
            }
            if (pos.getX() < minX) {
                minX = pos.getX();
            }

            if (pos.getZ() > maxZ) {
                maxZ = pos.getZ();
            }
            if (pos.getZ() < minZ) {
                minZ = pos.getZ();
            }
        }

        int finalMinX = minX;
        int finalMaxX = maxX;
        int finalMinZ = minZ;
        int finalMaxZ = maxZ;
        angularPoints = angularPoints.stream()
                .filter(pos -> pos.getX() == finalMinX || pos.getX() == finalMaxX || pos.getZ() == finalMinZ || pos.getZ() == finalMaxZ)
                .collect(Collectors.toList());

        return this.sortAngularPoints(angularPoints, maxX, maxZ, minX, minZ);
    }

    private List<BlockPos> sortAngularPoints(List<BlockPos> angularPoints, int maxX, int maxZ, int minX, int minZ) {
        List<BlockPos> topRow = angularPoints.stream()
                .filter(pos -> pos.getX() == maxX)
                .sorted((pos1, pos2) -> pos1.getZ() - pos2.getZ())
                .collect(Collectors.toList());
        List<BlockPos> rightRow = angularPoints.stream()
                .filter(pos -> pos.getZ() == maxZ)
                .sorted((pos1, pos2) -> pos2.getX() - pos1.getX())
                .collect(Collectors.toList());
        List<BlockPos> bottomRow = angularPoints.stream()
                .filter(pos -> pos.getX() == minX)
                .sorted((pos1, pos2) -> pos2.getZ() - pos1.getZ())
                .collect(Collectors.toList());
        List<BlockPos> leftRow = angularPoints.stream()
                .filter(pos -> pos.getZ() == minZ)
                .sorted((pos1, pos2) -> pos1.getX() - pos2.getX())
                .collect(Collectors.toList());

        List<BlockPos> sortedAngularPoints = Lists.newArrayList();
        sortedAngularPoints.addAll(topRow);
        sortedAngularPoints.addAll(rightRow);
        sortedAngularPoints.addAll(bottomRow);
        sortedAngularPoints.addAll(leftRow);

        return sortedAngularPoints.stream()
                .distinct()
                .collect(Collectors.toList());
    }

    private List<BlockPos> createPatrolPoints(List<ChunkPos> villageChunks) {
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
                        int y = this.getTopPosition(x, this.shooty.getPosition().getY(), z);
                        patrolPoints.add(new BlockPos(x, y, z));
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

    private List<BlockPos> getNeighbors(BlockPos point, Map<String, Boolean> usedPoses) {
        List<BlockPos> neighbors = Lists.newArrayList();
        if (usedPoses.get(ToStringHelper.toString(point.getX(), 0, point.getZ() + 1)) != null) {
            int y = this.getTopPosition(point.getX(), this.shooty.getPosition().getY(), point.getZ() + 1);
            neighbors.add(new BlockPos(point.getX(), y, point.getZ() + 1));
        }

        if (usedPoses.get(ToStringHelper.toString(point.getX(), 0, point.getZ() - 1)) != null) {
            int y = this.getTopPosition(point.getX(), this.shooty.getPosition().getY(), point.getZ() - 1);
            neighbors.add(new BlockPos(point.getX(), y, point.getZ() - 1));
        }

        if (usedPoses.get(ToStringHelper.toString(point.getX() + 1, 0, point.getZ())) != null) {
            int y = this.getTopPosition(point.getX() + 1, this.shooty.getPosition().getY(), point.getZ());
            neighbors.add(new BlockPos(point.getX() + 1, y, point.getZ()));
        }

        if (usedPoses.get(ToStringHelper.toString(point.getX() - 1, 0, point.getZ())) != null) {
            int y = this.getTopPosition(point.getX() - 1, this.shooty.getPosition().getY(), point.getZ());
            neighbors.add(new BlockPos(point.getX() - 1, y, point.getZ()));
        }

        return neighbors;
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

    // TODO remove it
    private int getTopPosition(int x, int y, int z) {
        String cacheKey = ToStringHelper.toString(x, y, z);
        if (this.topPositionCache.get(cacheKey) != null) {
            return this.topPositionCache.get(cacheKey);
        }
        BlockPos position = new MyMutableBlockPos(x, y, z);

        if (isSolid(position)) {
            while (isSolid(position)) {
                position = position.up();
            }
        } else {
            while (!isSolid(position)) {
                position = position.down();
            }
            position = position.up();
        }

        this.topPositionCache.put(cacheKey, position.getY());

        return position.getY();
    }

    private boolean isSolid(@Nonnull BlockPos position) {
        BlockState state = this.world.getBlockState(position);
        if (state.isAir(this.world, position)) {
            return false;
        }

        if (this.world.getFluidState(position).isTagged(FluidTags.WATER)) {
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

    private void move() {
        LOGGER.info("move");
        E33.internalEventBus.post(new MoveEvent(this.shooty));
    }

    private void stop() {
        LOGGER.info("stop");
        if (AnimationStateListener.getAnimationState(this.shooty) == AnimationState.MOVE) {
            E33.internalEventBus.post(new NoActionEvent(this.shooty));
        }
    }
}
