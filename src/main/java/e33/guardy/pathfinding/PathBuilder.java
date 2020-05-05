package e33.guardy.pathfinding;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import e33.guardy.entity.ShootyEntity;
import e33.guardy.util.ToStringHelper;
import net.minecraft.block.*;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.fluid.IFluidState;
import net.minecraft.pathfinding.Path;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.pathfinding.PathPoint;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IWorldReader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nonnull;
import java.util.*;
import java.util.stream.Collectors;

public class PathBuilder {
    final static Logger LOGGER = LogManager.getLogger();

    private final ShootyEntity shooty;
    private List<List<BlockPos>> blocksPerStep = Lists.newArrayList();

    private Map<String, BlockPos> swimmingTopPositionCache = Maps.newHashMap();
    private Map<String, BlockPos> notSwimmingTopPositionCache = Maps.newHashMap();
    private Map<String, Integer> canStandOnCache = Maps.newHashMap();

    public Map<BlockPos, Map<UUID, Integer>> speedTracker = Maps.newHashMap();
    public List<BlockPos> safePoints = Lists.newArrayList();
    public Path currentPath;

    public PathBuilder(ShootyEntity shooty) {
        this.shooty = shooty;
    }

    public Path getPath(List<MobEntity> enemies) {
        if (!this.shooty.onGround) {
            return null;
        }

        MovementLimitations shootyLimitations = this.createLimitations(this.shooty);
        IWorldReader world = this.shooty.getEntityWorld();
        AxisAlignedBB searchZone = this.getSearchZone();
        BlockPos shootyCurrentPosition = this.getEntityPos(shooty, shootyLimitations);
        Map<BlockPos, Map<UUID, Integer>> speedTracker = Maps.newHashMap();
        List<BlockPos> currentSteps = Lists.newArrayList(shootyCurrentPosition);
        Map<String, Boolean> visitedPoints = Maps.newHashMap();
        visitedPoints.put(ToStringHelper.toString(shootyCurrentPosition), true);
        List<List<BlockPos>> blocksPerStep = Lists.newArrayList();
        List<BlockPos> blockedPoints = Lists.newArrayList();
        this.rememberSpeed(this.shooty.getUniqueID(), currentSteps, 0, speedTracker);

        Map<UUID, MovementLimitations> enemyLimitations = this.createEnemyLimitations(enemies);
        Map<UUID, List<BlockPos>> enemyCurrentSteps = this.createEnemiesCurrentSteps(world, enemies, enemyLimitations);
        Map<UUID, Map<String, Boolean>> enemyVisitedPoints = this.createEnemiesVisitedPoints(enemies);
        for (MobEntity enemy : enemies) {
            this.rememberSpeed(enemy.getUniqueID(), enemyCurrentSteps.get(enemy.getUniqueID()), 0, speedTracker);
        }

        int stepNumber = 0;
        while (currentSteps.size() > 0) {
            List<BlockPos> newSteps = this.makeSteps(currentSteps, world, searchZone, visitedPoints, blockedPoints, shootyLimitations);
            blocksPerStep.add(newSteps);
            currentSteps = newSteps;
            this.rememberSpeed(this.shooty.getUniqueID(), newSteps, stepNumber, speedTracker);

            for (MobEntity enemy : enemies) {
                UUID uid = enemy.getUniqueID();
                List<BlockPos> enemyBlockedPoints = Lists.newArrayList();
                List<BlockPos> enemyNewSteps = this.makeSteps(enemyCurrentSteps.get(uid), world, searchZone, enemyVisitedPoints.get(uid), enemyBlockedPoints, enemyLimitations.get(uid));
                enemyCurrentSteps.put(uid, enemyNewSteps);
                this.rememberSpeed(uid, enemyNewSteps, stepNumber, speedTracker);

                if (enemyBlockedPoints.size() > 0) {
                    List<BlockPos> enemyReachableForAttackPoints = this.getReachableForAttackPoints(enemyNewSteps, enemyBlockedPoints);

                    this.rememberSpeed(uid, enemyReachableForAttackPoints, stepNumber, speedTracker);
                }
            }

            stepNumber++;
            if (stepNumber >= 100) {
                LOGGER.error("Too many iterations!!!");
                break;
            }
        }

        this.blocksPerStep = blocksPerStep;
        this.speedTracker = speedTracker;
        this.safePoints = this.findSafePoints(speedTracker);
        this.currentPath = this.buildPath(shootyLimitations, enemies.size());

        return this.currentPath;
    }

    protected Map<Integer, List<BlockPos>> createFastestPoints() {
        Map<Integer, List<BlockPos>> fastestPoints = Maps.newHashMap();
        UUID shootyUid = this.shooty.getUniqueID();

        for (BlockPos point : speedTracker.keySet()) {
            Map<UUID, Integer> steps = speedTracker.get(point);

            if (steps.get(shootyUid) != null) {
                int stepNumber = steps.get(shootyUid);
                int fastestEnemy = Integer.MAX_VALUE;
                for (UUID enemy : steps.keySet()) {
                    if (!enemy.equals(shootyUid) && steps.get(enemy) < fastestEnemy) {
                        fastestEnemy = steps.get(enemy);
                    }
                }

                if (fastestEnemy > stepNumber) {
                    if (fastestPoints.get(stepNumber) == null) {
                        fastestPoints.put(stepNumber, Lists.newArrayList());
                    }
                    fastestPoints.get(stepNumber).add(point);
                }
            }
        }

        return fastestPoints;
    }

    protected Path buildPath(MovementLimitations limitations, int enemiesCount) {
        if (this.safePoints.contains(shooty.getPosition())) {
            LOGGER.debug("Already on safe point!");
            return null;
        }

        int maxEnemiesOnPoint = 0;
        while (maxEnemiesOnPoint < enemiesCount) {
            LOGGER.debug(maxEnemiesOnPoint);
            Path path = this.buildPathWithMaxEnemiesOnPoint(maxEnemiesOnPoint, limitations);

            if (path != null) {
                return path;
            }

            maxEnemiesOnPoint++;
        }

        LOGGER.debug("No path :(");
        return null;
    }

    protected Path buildPathWithMaxEnemiesOnPoint(int maxEnemies, MovementLimitations limitations) {
        List<TreeLeaf> leafs = this.blocksPerStep.get(0).stream().map(b -> this.calculateTreeLeaf(b, null)).collect(Collectors.toList());
        int stepNumber = 0;
        Map<String, Boolean> visitedPoints = Maps.newHashMap();
        List<TreeLeaf> safeLeafs = Lists.newArrayList();

        while (leafs.size() > 0 && stepNumber < this.blocksPerStep.size()) {
            stepNumber++;
            List<TreeLeaf> currentLeafs = Lists.newArrayList();

            for (TreeLeaf leaf : leafs) {
                List<BlockPos> newSteps = this.getNextStepsFromCurrentPosition(
                        leaf.getBlockPos(),
                        // TODO maybe allow intersections, but optimize leafs on intersections(select safer etc)
                        this.filterByVisitedPoints(this.blocksPerStep.get(stepNumber), visitedPoints),
                        limitations
                );

                if (newSteps.size() == 0) {
                    leaf.die();
                    visitedPoints.put(ToStringHelper.toString(leaf.getBlockPos()), true);
                    continue;
                }

                List<TreeLeaf> newLeafs = this.getLeafsWithMaxEnemies(newSteps, maxEnemies, leaf);
                if (newLeafs.size() != 0) {
                    for (TreeLeaf child : newLeafs) {
                        leaf.addChild(child);

                        if (safePoints.contains(child.getBlockPos())) {
                            safeLeafs.add(child);
                        } else {
                            visitedPoints.put(ToStringHelper.toString(child.getBlockPos()), true);
                            currentLeafs.add(child);
                        }
                    }
                }
            }

            leafs = currentLeafs;
        }

        if (safeLeafs.size() == 0) {
            return null;
        }

        TreeLeaf safestLeaf = safeLeafs.get(0);
        for (TreeLeaf leaf : safeLeafs) {
            // TODO compare length
            // TODO try to find another way between dangerous parts (with lower maxEnemies)
            if (leaf.enemiesCount < safestLeaf.enemiesCount) {
                safestLeaf = leaf;
            }
            if (leaf.enemiesCount == safestLeaf.enemiesCount && leaf.totalEnemySpeed > safestLeaf.totalEnemySpeed) {
                safestLeaf = leaf;
            }
        }

        return this.createPathFromTree(safestLeaf);
    }

    protected List<TreeLeaf> getLeafsWithMaxEnemies(List<BlockPos> points, int maxEnemiesOnPoint, TreeLeaf parent) {
        List<TreeLeaf> filteredPoints = Lists.newArrayList();

        for (BlockPos point : points) {
            TreeLeaf leaf = this.calculateTreeLeaf(point, parent);
            // TODO can cache to not recalculate each time the same points
            if (parent.enemiesCount - leaf.enemiesCount <= maxEnemiesOnPoint) {
                filteredPoints.add(leaf);
            }
        }

        return filteredPoints;
    }

    protected TreeLeaf calculateTreeLeaf(BlockPos point, TreeLeaf parent) {
        int fasterEnemiesCount = 0;
        Map<UUID, Integer> entitiesOnPoint = this.speedTracker.get(point);
        int shootySpeed = entitiesOnPoint.get(this.shooty.getUniqueID());
        int fastestEnemySpeed = Integer.MAX_VALUE;
        UUID shootyUid = this.shooty.getUniqueID();

        for (UUID entity : entitiesOnPoint.keySet()) {
            if (!entity.equals(shootyUid) && entitiesOnPoint.get(entity) <= shootySpeed) {
                if (entitiesOnPoint.get(entity) < fastestEnemySpeed) {
                    fastestEnemySpeed = entitiesOnPoint.get(entity);
                }
                fasterEnemiesCount++;
            }
        }

        if (parent == null) {
            return new TreeLeaf(point, fasterEnemiesCount, fastestEnemySpeed);
        }

        return new TreeLeaf(point, parent.enemiesCount + fasterEnemiesCount, parent.totalEnemySpeed + fastestEnemySpeed);
    }

    protected Path createPathFromTree(TreeLeaf leaf) {
        LOGGER.info("Yeah boy, that's Path!");
        List<PathPoint> pathPoints = Lists.newArrayList();
        BlockPos target = leaf.getBlockPos();

        while (leaf != null) {
            BlockPos block = leaf.getBlockPos();
            pathPoints.add(new PathPoint(block.getX(), block.getY(), block.getZ()));
            leaf = leaf.getParent();
        }

        Collections.reverse(pathPoints);
        return new Path(pathPoints, target, true);
    }

    protected List<BlockPos> getNextStepsFromCurrentPosition(BlockPos currentPosition, List<BlockPos> nextStepPoints, MovementLimitations limitations) {
        List<BlockPos> nextSteps = Lists.newArrayList();

        for (BlockPos nextPoint : nextStepPoints) {
            int xDiff = Math.abs(nextPoint.getX() - currentPosition.getX());
            int zDiff = Math.abs(nextPoint.getZ() - currentPosition.getZ());
            int yDiff = nextPoint.getY() - currentPosition.getY();

            if (xDiff <= 1 && zDiff <= 1 && yDiff <= limitations.jumHeight && yDiff >= -limitations.maxFallHeight) {
                nextSteps.add(nextPoint);
            }
        }

        return nextSteps;
    }

    protected List<BlockPos> findSafePoints(Map<BlockPos, Map<UUID, Integer>> speedTracker) {
        Map<Integer, List<BlockPos>> diffInSpeed = Maps.newHashMap();
        UUID shootyUid = this.shooty.getUniqueID();

        for (BlockPos position : speedTracker.keySet()) {
            Map<UUID, Integer> steps = speedTracker.get(position);

            if (steps.get(shootyUid) != null) {
                int fastestEnemySpeed = Integer.MAX_VALUE;

                for (UUID entityUid : steps.keySet()) {
                    if (!entityUid.equals(shootyUid) && steps.get(entityUid) < fastestEnemySpeed) {
                        fastestEnemySpeed = steps.get(entityUid);
                    }
                }

                diffInSpeed.computeIfAbsent(fastestEnemySpeed, k -> Lists.newArrayList());
                diffInSpeed.get(fastestEnemySpeed).add(position);
            }
        }

        List<BlockPos> safestPoints = Lists.newArrayList();
        List<Integer> sortedDiffs = diffInSpeed.keySet().stream().sorted().collect(Collectors.toList());

        if (sortedDiffs.size() > 0) {
            int maxDiff = sortedDiffs.get(sortedDiffs.size() - 1);
            safestPoints = diffInSpeed.get(maxDiff);
        }

        return safestPoints;
    }

    protected List<BlockPos> makeSteps(List<BlockPos> previousSteps, IWorldReader world, AxisAlignedBB zone, Map<String, Boolean> visitedPoints, List<BlockPos> blockedPoints, MovementLimitations limitations) {
        List<BlockPos> newSteps = Lists.newArrayList();

        for (BlockPos point : previousSteps) {
            List<BlockPos> stepVariants = getStepVariants(world, point, zone, visitedPoints, blockedPoints, limitations);

            for (BlockPos variant : stepVariants) {
                visitedPoints.put(ToStringHelper.toString(variant), true);
                newSteps.add(variant);
            }
        }

        return newSteps;
    }

    protected List<BlockPos> getStepVariants(IWorldReader world, BlockPos start, AxisAlignedBB zone, Map<String, Boolean> visitedPoints, List<BlockPos> blockedPoints, MovementLimitations limitations) {
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
            if (this.isValidPosition(world, start, variant, zone, visitedPoints, blockedPoints, limitations)) {
                filteredVariants.add(variant);
            }
        }

        return filteredVariants;
    }

    protected boolean isValidPosition(IWorldReader world, BlockPos previousPosition, BlockPos newPosition, AxisAlignedBB zone, Map<String, Boolean> visitedPoints, List<BlockPos> blockedPoints, MovementLimitations limitations) {
        boolean blockInZone = visitedPoints.get(ToStringHelper.toString(newPosition)) == null
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

        BlockPos toCheckWall = getTopPosition(world, newPosition.getX(), previousPosition.getY(), previousPosition.getZ(), limitations);
        BlockPos toCheckWall2 = getTopPosition(world, previousPosition.getX(), previousPosition.getY(), newPosition.getZ(), limitations);

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
        if (!this.fitsIn(world, start, end, limitations)) {
            return false;
        }

        BlockState state = world.getBlockState(end);
        Block block = state.getBlock();
        IFluidState fluidState = world.getFluidState(end);
        if (
                (!limitations.canSwim && fluidState.isTagged(FluidTags.WATER) && world.getFluidState(end.up()).isTagged(FluidTags.WATER))
                        || fluidState.isTagged(FluidTags.LAVA) || block == Blocks.FIRE) {
            return false;
        }

        float startY = start.getY();
        float endY = end.getY();

        if (world.getBlockState(start.down()).getBlock() instanceof SnowBlock) {
            startY -= 1;
            startY += world.getBlockState(start.down()).get(SnowBlock.LAYERS) * (1F / 7F);
        }

        if (this.isFence(world, start.down())) {
            startY += 0.5F;
        }

        if (this.isFence(world, end.down())) {
            endY += 0.5F;
        }

        float diffInHeight = startY - endY;

        if (start.getY() > end.getY()) {
            return diffInHeight <= limitations.maxFallHeight && this.fitsIn(world, start, new BlockPos(end.getX(), start.getY(), end.getZ()), limitations);
        } else if (start.getY() < end.getY()) {
            return Math.abs(diffInHeight) <= limitations.jumHeight && this.fitsIn(world, start, new BlockPos(start.getX(), end.getY(), start.getZ()), limitations);
        }

        return true;
    }

    protected boolean fitsIn(IWorldReader world, BlockPos start, BlockPos end, MovementLimitations limitations) {
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

    protected boolean canStandOn(IWorldReader world, BlockPos block, MovementLimitations limitations) {
        String cacheKey = ToStringHelper.toString(block);
        int maxHeightToCheck = (int) Math.ceil(block.getY() + limitations.modelHeight);
        if (this.canStandOnCache.get(cacheKey) != null && this.canStandOnCache.get(cacheKey) >= maxHeightToCheck) {
            return true;
        }

        for (int y = block.getY(); y <= maxHeightToCheck; y++) {
            if (this.isSolid(world, block, limitations)) {
                this.canStandOnCache.put(cacheKey, y);
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
        if (block instanceof LeavesBlock || block == Blocks.LILY_PAD) {
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

    private boolean isFence(IWorldReader world, BlockPos position) {
        BlockState state = world.getBlockState(position);
        Block block = state.getBlock();

        return block.isIn(BlockTags.FENCES) || block.isIn(BlockTags.WALLS) || block instanceof FenceGateBlock;
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

    private Map<UUID, List<BlockPos>> createEnemiesCurrentSteps(IWorldReader world, List<MobEntity> enemies, Map<UUID, MovementLimitations> enemyLimitations) {
        Map<UUID, List<BlockPos>> enemiesCurrentSteps = Maps.newHashMap();

        for (MobEntity enemy : enemies) {
            enemiesCurrentSteps.put(enemy.getUniqueID(), Lists.newArrayList(getEnemyStandPosition(world, enemy.getPosition(), enemyLimitations.get(enemy.getUniqueID()))));
        }

        return enemiesCurrentSteps;
    }

    private Map<UUID, Map<String, Boolean>> createEnemiesVisitedPoints(List<MobEntity> enemies) {
        Map<UUID, Map<String, Boolean>> enemiesVisitedPoints = Maps.newHashMap();

        for (MobEntity enemy : enemies) {
            Map<String, Boolean> visitedPoints = Maps.newHashMap();
            visitedPoints.put(ToStringHelper.toString(enemy.getPosition()), true);
            enemiesVisitedPoints.put(enemy.getUniqueID(), visitedPoints);
        }

        return enemiesVisitedPoints;
    }

    private void rememberSpeed(UUID uid, List<BlockPos> positions, int stepNumber, Map<BlockPos, Map<UUID, Integer>> speedTracker) {
        for (BlockPos point : positions) {
            speedTracker.computeIfAbsent(point, k -> Maps.newHashMap());
            speedTracker.get(point).put(uid, stepNumber);
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

    private List<BlockPos> filterByVisitedPoints(List<BlockPos> positions, Map<String, Boolean> visitedPoints) {
        List<BlockPos> filtered = Lists.newArrayList();

        for (BlockPos position : positions) {
            if (visitedPoints.get(ToStringHelper.toString(position)) == null) {
                filtered.add(position);
            }
        }

        return filtered;
    }
}
