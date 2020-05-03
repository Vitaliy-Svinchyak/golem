package e33.guardy.pathfinding;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import e33.guardy.debug.TimeMeter;
import e33.guardy.entity.ShootyEntity;
import net.minecraft.block.*;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.pathfinding.NodeProcessor;
import net.minecraft.pathfinding.Path;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.pathfinding.PathPoint;
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
    private final NodeProcessor nodeProcessor;
    public List<BlockPos> unwalkableBlocks = Lists.newArrayList();
    private List<List<BlockPos>> checkingRoutes = Lists.newArrayList();
    public Map<BlockPos, Map<UUID, Integer>> routes = Maps.newHashMap();
    public List<BlockPos> safePoints = Lists.newArrayList();
    public Map<Integer, List<BlockPos>> fastestPoints = Maps.newHashMap();
    public Path currentPath;

    public PathBuilder(ShootyEntity shooty) {
        this.shooty = shooty;
        this.nodeProcessor = shooty.getNavigator().getNodeProcessor();
    }

    public Path getPath(List<MobEntity> enemies) {
        if (!this.shooty.onGround) {
            return null;
        }

        MovementLimitations shootyLimitations = this.createLimitations(this.shooty);
        IWorldReader world = this.shooty.getEntityWorld();
        AxisAlignedBB zone = this.getSearchZone();
        BlockPos myPos = this.getEntityPos(shooty, shootyLimitations);
        LOGGER.info("myPos: " + myPos);
        Map<BlockPos, Map<UUID, Integer>> localRoutes = Maps.newHashMap();

        List<List<BlockPos>> localCheckingRoutes = Lists.newArrayList();
        List<BlockPos> points = Lists.newArrayList(myPos);
        Map<String, Boolean> usedCoors = Maps.newHashMap();
        usedCoors.put(myPos.toString(), true);
        List<BlockPos> cantGo = Lists.newArrayList();
        this.setRoutes(this.shooty.getUniqueID(), points, 0, localRoutes);

        Map<UUID, MovementLimitations> enemyLimitations = this.createEnemyLimitations(enemies);
        Map<UUID, List<BlockPos>> enemyPoints = this.createEnemyPoints(world, enemies, enemyLimitations);
        Map<UUID, Map<String, Boolean>> enemyUsedCoors = this.createEnemyUsedCoors(enemies);

        for (MobEntity enemy : enemies) {
            this.setRoutes(enemy.getUniqueID(), enemyPoints.get(enemy.getUniqueID()), 0, localRoutes);
            enemy.getNavigator().getNodeProcessor().init(world, enemy);
        }

        int iteration = 0;

        while (points.size() > 0) {
            List<BlockPos> tempPoints = this.getNewWave(points, world, zone, usedCoors, cantGo, shootyLimitations);
            localCheckingRoutes.add(tempPoints);
            points = tempPoints;
            this.setRoutes(this.shooty.getUniqueID(), tempPoints, iteration, localRoutes);

            for (MobEntity enemy : enemies) {
                UUID uid = enemy.getUniqueID();
                List<BlockPos> cantGoForEnemy = Lists.newArrayList();
                List<BlockPos> tempPointsForEnemy = this.getNewWave(enemyPoints.get(uid), world, zone, enemyUsedCoors.get(uid), cantGoForEnemy, enemyLimitations.get(uid));
                enemyPoints.put(uid, tempPointsForEnemy);
                this.setRoutes(uid, tempPointsForEnemy, iteration, localRoutes);

                if (cantGoForEnemy.size() > 0) {
                    List<BlockPos> attackablePointsForEnemy = this.getAttackablePoints(tempPointsForEnemy, cantGoForEnemy);

                    this.setRoutes(uid, attackablePointsForEnemy, iteration, localRoutes);
                }
            }

            iteration++;
            if (iteration >= 100) {
                LOGGER.error("Too many iterations!!!");
                break;
            }
        }

        this.checkingRoutes = localCheckingRoutes;
        this.routes = localRoutes;
        this.safePoints = this.findSafePoints(localRoutes);

        this.fastestPoints = this.createFastestPoints();

        this.currentPath = this.buildPath(shootyLimitations);

        return this.currentPath;
    }

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

    private Map<Integer, List<BlockPos>> createFastestPoints() {
        Map<Integer, List<BlockPos>> fastestPoints = Maps.newHashMap();
        UUID shootyUid = this.shooty.getUniqueID();

        for (BlockPos point : routes.keySet()) {
            Map<UUID, Integer> steps = routes.get(point);

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

    private Path buildPath(MovementLimitations limitations) {
        LOGGER.info("It will be dangerous...");

        int maxReach = 0;
        while (maxReach < 10) {
            LOGGER.info(maxReach);
            Path path = this.buildDangerousPathWithMaxReach(maxReach, limitations);

            if (path != null) {
                return path;
            }

            maxReach++;
        }

        LOGGER.error("No path :(");
        return null;
    }

    private Path buildDangerousPathWithMaxReach(int maxReach, MovementLimitations limitations) {
        if (this.safePoints.contains(shooty.getPosition())) {
            LOGGER.error("Already on safe point!");
            return null;
        }

        List<TreeLeaf> leafs = this.checkingRoutes.get(0).stream().map(this::calculateTreeLeaf).collect(Collectors.toList());
        int i = 0;
        Map<String, Boolean> usedCoors = Maps.newHashMap();
        List<TreeLeaf> finalLeafs = Lists.newArrayList();

        while (leafs.size() > 0 && i < this.checkingRoutes.size()) {
            i++;
            List<TreeLeaf> tempLeafs = Lists.newArrayList();
            for (TreeLeaf leaf : leafs) {
                List<BlockPos> stepsFromHere = this.getNextStepFromList(
                        leaf.getBlockPos(),
                        this.checkingRoutes.get(i).stream()
                                // TODO maybe allow intersections, but optimize leafs on intersections(select safer etc)
                                .filter(point -> usedCoors.get(point.toString()) == null).collect(Collectors.toList()),
                        limitations
                );

                if (stepsFromHere.size() == 0) {
                    leaf.die();
                    usedCoors.put(leaf.getBlockPos().toString(), true);
                    continue;
                }
                List<TreeLeaf> tempPoints = this.getLeafsWithReach(stepsFromHere, maxReach, leaf);

                if (tempPoints.size() != 0) {
                    for (TreeLeaf child : tempPoints) {
                        leaf.addChild(child);

                        if (safePoints.contains(child.getBlockPos())) {
                            finalLeafs.add(child);
                        } else {
                            usedCoors.put(child.getBlockPos().toString(), true);
                            tempLeafs.add(child);
                        }
                    }
                }
            }

            leafs = tempLeafs;
        }

        if (finalLeafs.size() > 0) {
            TreeLeaf safestLeaf = finalLeafs.get(0);
            for (TreeLeaf leaf : finalLeafs) {
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

        return null;
    }

    private List<TreeLeaf> getLeafsWithReach(List<BlockPos> points, int maxEnemiesOnPoint, TreeLeaf parent) {
        List<TreeLeaf> filteredPoints = Lists.newArrayList();

        for (BlockPos point : points) {
            int enemiesOnPoint = 0;
            Map<UUID, Integer> entitiesOnPoint = this.routes.get(point);
            int shootySpeed = entitiesOnPoint.get(this.shooty.getUniqueID());
            int fastestEnemySpeed = Integer.MAX_VALUE;
            for (UUID entity : entitiesOnPoint.keySet()) {
                if (!entity.equals(this.shooty.getUniqueID()) && entitiesOnPoint.get(entity) <= shootySpeed) {
                    if (entitiesOnPoint.get(entity) < fastestEnemySpeed) {
                        fastestEnemySpeed = entitiesOnPoint.get(entity);
                    }
                    enemiesOnPoint++;
                }
            }

            if (enemiesOnPoint <= maxEnemiesOnPoint) {
                filteredPoints.add(new TreeLeaf(point, parent.enemiesCount + enemiesOnPoint, parent.totalEnemySpeed + fastestEnemySpeed));
            }
        }

        return filteredPoints;
    }

    private TreeLeaf calculateTreeLeaf(BlockPos point) {
        int enemiesOnPoint = 0;
        Map<UUID, Integer> entitiesOnPoint = this.routes.get(point);
        int shootySpeed = entitiesOnPoint.get(this.shooty.getUniqueID());
        int fastestEnemySpeed = Integer.MAX_VALUE;
        for (UUID entity : entitiesOnPoint.keySet()) {
            if (!entity.equals(this.shooty.getUniqueID()) && entitiesOnPoint.get(entity) <= shootySpeed) {
                if (entitiesOnPoint.get(entity) < fastestEnemySpeed) {
                    fastestEnemySpeed = entitiesOnPoint.get(entity);
                }
                enemiesOnPoint++;
            }
        }

        return new TreeLeaf(point, enemiesOnPoint, fastestEnemySpeed);
    }

    private Path createPathFromTree(TreeLeaf leaf) {
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

    private List<BlockPos> getNextStepFromList(BlockPos point, List<BlockPos> nextStepPoints, MovementLimitations limitations) {
        List<BlockPos> nextPoints = Lists.newArrayList();
        for (BlockPos nextPoint : nextStepPoints) {
            int xDiff = Math.abs(nextPoint.getX() - point.getX());
            int zDiff = Math.abs(nextPoint.getZ() - point.getZ());
            int yDiff = nextPoint.getY() - point.getY();

            if (xDiff <= 1 && zDiff <= 1 && yDiff <= limitations.jumHeight && yDiff >= -limitations.maxFallHeight) {
                nextPoints.add(nextPoint);
            }
        }

        return nextPoints;
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

    private List<BlockPos> findSafePoints(Map<BlockPos, Map<UUID, Integer>> routes) {
        Map<Integer, List<BlockPos>> diffInSteps = Maps.newHashMap();
        UUID shooty = this.shooty.getUniqueID();

        for (BlockPos point : routes.keySet()) {
            Map<UUID, Integer> steps = routes.get(point);

            if (steps.get(shooty) != null) {
                int fastestEnemy = Integer.MAX_VALUE;
                for (UUID enemy : steps.keySet()) {
                    if (!enemy.equals(shooty) && steps.get(enemy) < fastestEnemy) {
                        fastestEnemy = steps.get(enemy);
                    }
                }
                diffInSteps.computeIfAbsent(fastestEnemy, k -> Lists.newArrayList());
                diffInSteps.get(fastestEnemy).add(point);

            }
        }

        List<BlockPos> safestPoints = Lists.newArrayList();
        List<Integer> sortedDiffs = diffInSteps.keySet().stream().sorted().collect(Collectors.toList());
        if (sortedDiffs.size() > 0) {
            int maxDiff = sortedDiffs.get(sortedDiffs.size() - 1);
            safestPoints = diffInSteps.get(maxDiff);
        }
        return safestPoints;
    }

    protected void setRoutes(UUID uid, List<BlockPos> points, int iteration, Map<BlockPos, Map<UUID, Integer>> routes) {
        TimeMeter.start(TimeMeter.MODULE_PATH_BUILDING, "setRoutes");
        for (BlockPos point : points) {
            routes.computeIfAbsent(point, k -> Maps.newHashMap());
            routes.get(point).put(uid, iteration);
        }
        TimeMeter.end(TimeMeter.MODULE_PATH_BUILDING, "setRoutes");
    }

    protected List<BlockPos> getNewWave(List<BlockPos> points, IWorldReader world, AxisAlignedBB zone, Map<String, Boolean> usedCoors, List<BlockPos> cantGo, MovementLimitations limitations) {
        TimeMeter.start(TimeMeter.MODULE_PATH_BUILDING, "getNewWave");
        List<BlockPos> tempPoints = Lists.newArrayList();

        for (BlockPos point : points) {
            TimeMeter.start(TimeMeter.MODULE_PATH_BUILDING, "getVariants");
            List<BlockPos> vars = getVariants(world, point, zone, usedCoors, cantGo, limitations);
            TimeMeter.end(TimeMeter.MODULE_PATH_BUILDING, "getVariants");

            for (BlockPos var : vars) {
                usedCoors.put(var.toString(), true);
                tempPoints.add(var);
            }
        }
        TimeMeter.end(TimeMeter.MODULE_PATH_BUILDING, "getNewWave");

        return tempPoints;
    }

    // Used for spiders. If there is a tunnel with height and width both of 1, then he can attack from nearest point but can be inside tunnel
    protected List<BlockPos> getAttackablePoints(List<BlockPos> wave, List<BlockPos> cantGo) {
        List<BlockPos> unBlocked = Lists.newArrayList();

        for (BlockPos blockedPoint : cantGo) {
            for (BlockPos waveNeighbor : wave) {
                int xDiff = Math.abs(blockedPoint.getX() - waveNeighbor.getX());
                int zDiff = Math.abs(blockedPoint.getZ() - waveNeighbor.getZ());
                int yDiff = Math.abs(blockedPoint.getY() - waveNeighbor.getY());
                if (xDiff <= 1 && zDiff <= 1 && yDiff <= 1) {
                    unBlocked.add(blockedPoint);
                    break;
                }
            }
        }

        return unBlocked;
    }

    protected Map<UUID, List<BlockPos>> createEnemyPoints(IWorldReader world, List<MobEntity> enemies, Map<UUID, MovementLimitations> enemyLimitations) {
        Map<UUID, List<BlockPos>> enemyPoints = Maps.newHashMap();
        for (MobEntity enemy : enemies) {
            enemyPoints.put(enemy.getUniqueID(), Lists.newArrayList(getEnemyStandPosition(world, enemy.getPosition(), enemyLimitations.get(enemy.getUniqueID()))));
        }

        return enemyPoints;
    }

    protected Map<UUID, Map<String, Boolean>> createEnemyUsedCoors(List<MobEntity> enemies) {
        Map<UUID, Map<String, Boolean>> enemyPoints = Maps.newHashMap();
        for (MobEntity enemy : enemies) {
            Map<String, Boolean> usedCoors = Maps.newHashMap();
            usedCoors.put(enemy.getPosition().toString(), true);
            enemyPoints.put(enemy.getUniqueID(), usedCoors);
        }

        return enemyPoints;
    }

    protected List<BlockPos> getVariants(IWorldReader world, BlockPos start, AxisAlignedBB zone, Map<String, Boolean> usedCoors, List<BlockPos> cantGo, MovementLimitations limitations) {
        TimeMeter.start(TimeMeter.MODULE_PATH_BUILDING, "creating variants");
        int x = start.getX();
        int y = start.getY();
        int z = start.getZ();
        List<BlockPos> variants = Lists.newArrayList(
                getTopPosition(world, new BlockPos(x + 1, y, z), limitations),
                getTopPosition(world, new BlockPos(x - 1, y, z), limitations),
                getTopPosition(world, new BlockPos(x, y, z + 1), limitations),
                getTopPosition(world, new BlockPos(x, y, z - 1), limitations),

                getTopPosition(world, new BlockPos(x + 1, y, z + 1), limitations),
                getTopPosition(world, new BlockPos(x + 1, y, z - 1), limitations),
                getTopPosition(world, new BlockPos(x - 1, y, z - 1), limitations),
                getTopPosition(world, new BlockPos(x - 1, y, z + 1), limitations)
        );
        TimeMeter.end(TimeMeter.MODULE_PATH_BUILDING, "creating variants");

        TimeMeter.start(TimeMeter.MODULE_PATH_BUILDING, "filtering variants");
        List<BlockPos> filteredVariants = Lists.newArrayList();
        for (BlockPos variant : variants) {
            if (this.isValidPos(world, start, zone, usedCoors, cantGo, limitations, variant)) {
                filteredVariants.add(variant);
            }
        }
        TimeMeter.end(TimeMeter.MODULE_PATH_BUILDING, "filtering variants");
        return filteredVariants;
    }

    protected boolean isValidPos(IWorldReader world, BlockPos start, AxisAlignedBB zone, Map<String, Boolean> usedCoors, List<BlockPos> cantGo, MovementLimitations limitations, BlockPos variant) {
        boolean blockInZone = usedCoors.get(variant.toString()) == null
                && variant.getX() >= zone.minX - 1 && variant.getX() <= zone.maxX
                && variant.getZ() >= zone.minZ - 1 && variant.getZ() <= zone.maxZ
                && variant.getY() >= zone.minY / 1.5 && variant.getY() <= zone.maxY * 1.5;

        if (!blockInZone) {
            return false;
        }

        if (!canWalkFromTo(world, start, variant, limitations)) {
            cantGo.add(variant);
            return false;
        }

        // check walls between
        boolean isDiagonalBlock = variant.getX() != start.getX() && variant.getZ() != start.getZ();
        if (!isDiagonalBlock) {
            return true;
        }

        BlockPos toCheckWall = getTopPosition(world, new BlockPos(variant.getX(), start.getY(), start.getZ()), limitations);
        BlockPos toCheckWall2 = getTopPosition(world, new BlockPos(start.getX(), start.getY(), variant.getZ()), limitations);

        boolean noWallOnWay = toCheckWall.getY() - start.getY() <= limitations.jumHeight && toCheckWall2.getY() - start.getY() <= limitations.jumHeight;
        if (noWallOnWay) {
            return true;
        }

        cantGo.add(variant);
        return false;
    }

    protected BlockPos getEnemyStandPosition(IWorldReader world, @Nonnull BlockPos position, MovementLimitations limitations) {
        while (!isSolid(world, position, limitations)) {
            position = position.down();
        }
        position = position.up();

        return position;
    }

    protected BlockPos getTopPosition(IWorldReader world, @Nonnull BlockPos position, MovementLimitations limitations) {
        TimeMeter.start(TimeMeter.MODULE_PATH_BUILDING, "getTopPosition");
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
        TimeMeter.end(TimeMeter.MODULE_PATH_BUILDING, "getTopPosition");

        return position;
    }

    protected boolean canWalkFromTo(IWorldReader world, BlockPos start, BlockPos end, MovementLimitations limitations) {
        if (!this.fitsIn(world, start, end, limitations)) {
            return false;
        }

        PathNodeType endType = getPathNodeType(world, end);
        if ((endType == PathNodeType.WATER && getPathNodeType(world, end.up()) == PathNodeType.WATER && !limitations.canSwim) || endType == PathNodeType.LAVA || endType == PathNodeType.DAMAGE_FIRE) {
            return false;
        }

        float startY = start.getY();
        float endY = end.getY();

        if (world.getBlockState(start.down()).getBlock() instanceof SnowBlock) {
            startY -= 1;
            startY += world.getBlockState(start.down()).get(SnowBlock.LAYERS) * (1F / 7F);
        }
        if (getPathNodeType(world, start.down()) == PathNodeType.FENCE) {
            startY += 0.5F;
        }

        if (getPathNodeType(world, end.down()) == PathNodeType.FENCE) {
            endY += 0.5F;
        }

        float diff = startY - endY;

        if (start.getY() > end.getY()) {
            return diff <= limitations.maxFallHeight && this.fitsIn(world, start, new BlockPos(end.getX(), start.getY(), end.getZ()), limitations);
        } else if (start.getY() < end.getY()) {
            return Math.abs(diff) <= limitations.jumHeight && this.fitsIn(world, start, new BlockPos(start.getX(), end.getY(), start.getZ()), limitations);
        }

        return true;
    }

    protected boolean fitsIn(IWorldReader world, BlockPos start, BlockPos end, MovementLimitations limitations) {
        TimeMeter.start(TimeMeter.MODULE_PATH_BUILDING, "fitsIn");
        float x = end.getX() + 0.5F;
        int y = end.getY();
        float z = end.getZ() + 0.5F;

        TimeMeter.start(TimeMeter.MODULE_PATH_BUILDING, "canStandOn");
        boolean isCollisionBoxesEmpty = this.canStandOn(world, end, limitations);
        TimeMeter.end(TimeMeter.MODULE_PATH_BUILDING, "canStandOn");

        if (isCollisionBoxesEmpty && limitations.modelWidth > 1) {
            List<List<BlockPos>> blocksToCheck = this.mapAllBlocksBetweenTwoPoints(limitations, x, y, z);
            if (!this.samePatternOfBlocks(world, blocksToCheck, limitations)) {
                TimeMeter.end(TimeMeter.MODULE_PATH_BUILDING, "fitsIn");
                return false;
            }
        }

        TimeMeter.end(TimeMeter.MODULE_PATH_BUILDING, "fitsIn");
        return isCollisionBoxesEmpty;
    }

    protected boolean canStandOn(IWorldReader world, BlockPos block, MovementLimitations limitations) {
        for (int y = block.getY(); y <= Math.ceil(block.getY() + limitations.modelHeight); y++) {
            if (this.isSolid(world, block, limitations)) {
                return false;
            }
        }

        return true;
    }

    private boolean samePatternOfBlocks(IWorldReader world, List<List<BlockPos>> blocksToCheck, MovementLimitations limitations) {
        if (blocksToCheck.size() == 0) {
            return false;
        }
        TimeMeter.start(TimeMeter.MODULE_PATH_BUILDING, "samePatternOfBlocks");

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
                    TimeMeter.end(TimeMeter.MODULE_PATH_BUILDING, "samePatternOfBlocks");
                    return true;
                }
            }
        }

        TimeMeter.end(TimeMeter.MODULE_PATH_BUILDING, "samePatternOfBlocks");
        return false;
    }

    private boolean squareIsAllTrue(List<List<Boolean>> checked, int startX, int startZ, int width) {
        for (int x = startX; x < startX + width; x++) {
            for (int z = startZ; z < startZ + width; z++) {
                if (checked.size() <= x || checked.get(x).size() <= z || checked.get(x).get(z) == false) {
                    return false;
                }
            }
        }

        return true;
    }

    private List<List<BlockPos>> mapAllBlocksBetweenTwoPoints(MovementLimitations limitations, float stepX, int stepY, float stepZ) {
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
        TimeMeter.start(TimeMeter.MODULE_PATH_BUILDING, "isSolid");
        boolean r = this.isSolidM(world, position, limitations);
        TimeMeter.end(TimeMeter.MODULE_PATH_BUILDING, "isSolid");
        return r;
    }

    protected boolean isSolidM(IWorldReader world, @Nonnull BlockPos position, MovementLimitations limitations) {
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

    protected PathNodeType getPathNodeType(IWorldReader world, BlockPos blockPos) {
        return nodeProcessor.getPathNodeType(world, blockPos.getX(), blockPos.getY(), blockPos.getZ());
    }

    protected MovementLimitations createLimitations(MobEntity entity) {
        LOGGER.info(entity.getPathPriority(PathNodeType.WATER));
        return new MovementLimitations(1F, entity.getMaxFallHeight(), entity.getHeight(), entity.getWidth(), entity.getPathPriority(PathNodeType.WATER) >= 0F, entity);
    }
}
