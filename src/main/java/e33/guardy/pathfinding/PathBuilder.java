package e33.guardy.pathfinding;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import e33.guardy.entity.ShootyEntity;
import net.minecraft.block.Blocks;
import net.minecraft.block.SnowBlock;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.pathfinding.NodeProcessor;
import net.minecraft.pathfinding.Path;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.pathfinding.PathPoint;
import net.minecraft.util.Direction8;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorldReader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.stream.Collectors;

public class PathBuilder {
    final static Logger LOGGER = LogManager.getLogger();

    private final ShootyEntity shooty;
    private BlockPos lastPos;
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

    public void getPath(List<MobEntity> enemies) {
        if (this.lastPos != null && this.lastPos.equals(this.shooty.getPosition()) && !UnwalkableMarker.worldChanged) {
            return;
        }
        UnwalkableMarker.reset();

        if (!this.shooty.onGround) {
            return;
        }

        IWorldReader world = this.shooty.getEntityWorld();
        AxisAlignedBB zone = this.getSearchZone();
        BlockPos myPos = shooty.getPosition();
        Map<BlockPos, Map<UUID, Integer>> localRoutes = Maps.newHashMap();
        this.lastPos = myPos;

        List<List<BlockPos>> localCheckingRoutes = Lists.newArrayList();
        List<BlockPos> points = Lists.newArrayList(myPos);
        Map<String, Boolean> usedCoors = Maps.newHashMap();
        usedCoors.put(myPos.toString(), true);
        List<BlockPos> cantGo = Lists.newArrayList();
        this.setRoutes(this.shooty.getUniqueID(), points, 0, localRoutes);

        Map<UUID, List<BlockPos>> enemyPoints = this.createEnemyPoints(world, enemies);
        Map<UUID, Map<String, Boolean>> enemyUsedCoors = this.createEnemyUsedCoors(enemies);
        Map<UUID, MovementLimitations> enemyLimitations = this.createEnemyLimitations(enemies);

        for (MobEntity enemy : enemies) {
            this.setRoutes(enemy.getUniqueID(), enemyPoints.get(enemy.getUniqueID()), 0, localRoutes);
            enemy.getNavigator().getNodeProcessor().init(world, enemy);
        }

        int iteration = 0;
        MovementLimitations shootyLimitations = this.createLimitations(this.shooty);
        while (points.size() > 0) {
            List<BlockPos> tempPoints = this.getNewWave(points, world, zone, usedCoors, cantGo, shootyLimitations);
            localCheckingRoutes.add(tempPoints);
            points = tempPoints;
            this.setRoutes(this.shooty.getUniqueID(), tempPoints, iteration, localRoutes);

            for (MobEntity enemy : enemies) {
                UUID uid = enemy.getUniqueID();
                List<BlockPos> tempPointsForEnemy = this.getNewWave(enemyPoints.get(uid), world, zone, enemyUsedCoors.get(uid), null, enemyLimitations.get(uid));
                enemyPoints.put(uid, tempPointsForEnemy);
                this.setRoutes(uid, tempPointsForEnemy, iteration, localRoutes);
            }

            iteration++;
            if (iteration >= 100) {
                LOGGER.error("Too many iterations!!!");
                break;
            }

        }

        List<BlockPos> notOkPositions = Lists.newArrayList();
        for (BlockPos unwalkableBlock : cantGo) {
            if (usedCoors.get(unwalkableBlock.toString()) == null && !notOkPositions.contains(unwalkableBlock)) {
                notOkPositions.add(unwalkableBlock);
            }
        }

        this.checkingRoutes = localCheckingRoutes;
        this.unwalkableBlocks = notOkPositions;
        this.routes = localRoutes;
        this.safePoints = this.findSafePoints(localRoutes);
        this.fastestPoints = this.createFastestPoints();
        this.currentPath = this.buildPath(shootyLimitations);
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
        List<BlockPos> usedPoints = Lists.newArrayList();
        List<TreeLeaf> finalLeafs = Lists.newArrayList();

        while (leafs.size() > 0 && i < this.checkingRoutes.size()) {
            i++;
            LOGGER.info("Step " + i);
            List<TreeLeaf> tempLeafs = Lists.newArrayList();
            for (TreeLeaf leaf : leafs) {
                List<BlockPos> stepsFromHere = this.getNextStepFromList(
                        leaf.getBlockPos(),
                        this.checkingRoutes.get(i).stream()
                                // TODO maybe allow intersections, but optimize leafs on intersections(select safer etc)
                                .filter(point -> !usedPoints.contains(point)).collect(Collectors.toList()),
                        limitations
                );
                if (stepsFromHere.size() == 0) {
                    leaf.die();
                    usedPoints.add(leaf.getBlockPos());
                    continue;
                }
                List<TreeLeaf> tempPoints = this.getLeafsWithReach(stepsFromHere, maxReach, leaf);

                if (tempPoints.size() != 0) {
                    for (TreeLeaf child : tempPoints) {
                        leaf.addChild(child);

                        if (safePoints.contains(child.getBlockPos())) {
                            finalLeafs.add(child);
                        } else {
                            usedPoints.add(child.getBlockPos());
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
        for (BlockPos point : points) {
            routes.computeIfAbsent(point, k -> Maps.newHashMap());
            routes.get(point).put(uid, iteration);
        }
    }

    protected List<BlockPos> getNewWaveForEnemy(List<BlockPos> points, IWorldReader world, AxisAlignedBB zone, Map<String, Boolean> usedCoors, MobEntity entity) {
        List<BlockPos> wave = Lists.newArrayList();

        for (BlockPos point : points) {
            PathPoint[] vars = new PathPoint[32];
            entity.getNavigator().getNodeProcessor().func_222859_a(vars, new PathPoint(point.getX(), point.getY(), point.getZ()));
            List<PathPoint> varsBlocks = Arrays.stream(vars).filter(Objects::nonNull).collect(Collectors.toList());

            for (PathPoint var : varsBlocks) {
                BlockPos varBlock = new BlockPos(var.x, var.y, var.z);
                if (usedCoors.get(varBlock.toString()) == null
                        && var.x >= zone.minX - 1 && var.x <= zone.maxX
                        && var.z >= zone.minZ - 1 && var.z <= zone.maxZ
                        && var.y >= zone.minY / 1.5 && var.y <= zone.maxY * 1.5
                ) {
                    usedCoors.put(var.toString(), true);
                    wave.add(varBlock);
                }
            }
        }

        return wave;
    }

    protected List<BlockPos> getNewWave(List<BlockPos> points, IWorldReader world, AxisAlignedBB zone, Map<String, Boolean> usedCoors, List<BlockPos> cantGo, MovementLimitations limitations) {
        List<BlockPos> tempPoints = Lists.newArrayList();
        for (BlockPos point : points) {
            List<BlockPos> vars = getVariants(world, point, zone, usedCoors, cantGo, limitations);

            for (BlockPos var : vars) {
                usedCoors.put(var.toString(), true);
                tempPoints.add(var);
            }
        }

        return tempPoints;
    }

    protected Map<UUID, List<BlockPos>> createEnemyPoints(IWorldReader world, List<MobEntity> enemies) {
        Map<UUID, List<BlockPos>> enemyPoints = Maps.newHashMap();
        for (MobEntity enemy : enemies) {
            enemyPoints.put(enemy.getUniqueID(), Lists.newArrayList(getTopPosition(world, enemy.getPosition())));
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

    protected List<BlockPos> getVariants(IWorldReader world, BlockPos start, AxisAlignedBB zone, Map<String, Boolean> usedCoors, @Nullable List<BlockPos> cantGo, MovementLimitations limitations) {
        List<BlockPos> variants = Lists.newArrayList(
                getTopPosition(world, start.east(), limitations),
                getTopPosition(world, start.north(), limitations),
                getTopPosition(world, start.south(), limitations),
                getTopPosition(world, start.west(), limitations),
                getTopPosition(world, start.north().east(), limitations),
                getTopPosition(world, start.north().west(), limitations),
                getTopPosition(world, start.south().east(), limitations),
                getTopPosition(world, start.south().west(), limitations)
        );

        return variants.stream()
                .filter(variant -> {
                    if (usedCoors.get(variant.toString()) == null
                            && variant.getX() >= zone.minX - 1 && variant.getX() <= zone.maxX
                            && variant.getZ() >= zone.minZ - 1 && variant.getZ() <= zone.maxZ
                            && variant.getY() >= zone.minY / 1.5 && variant.getY() <= zone.maxY * 1.5
                    ) {
                        if (canWalkFromTo(world, start, variant, limitations)) {
                            // check walls between
                            if (variant.getX() != start.getX() && variant.getZ() != start.getZ()) {
                                BlockPos toCheckWall = getTopPosition(world, new BlockPos(variant.getX(), start.getY(), start.getZ()), limitations);
                                BlockPos toCheckWall2 = getTopPosition(world, new BlockPos(start.getX(), start.getY(), variant.getZ()), limitations);

                                if (toCheckWall.getY() - start.getY() <= limitations.jumHeight && toCheckWall2.getY() - start.getY() <= limitations.jumHeight) {
                                    return true;
                                } else {
                                    if (cantGo != null) {
                                        cantGo.add(variant);
                                    }
                                    return false;
                                }
                            }

                            return true;
                        }

                        if (cantGo != null) {
                            cantGo.add(variant);
                        }
                        return false;
                    }
                    return false;
                })
                .collect(Collectors.toList());
    }

    protected BlockPos getTopPosition(IWorldReader world, @Nonnull BlockPos position) {
        while (!isSolid(world, position)) {
            position = position.down();
        }
        position = position.up();

        return position;
    }

    protected BlockPos getTopPosition(IWorldReader world, @Nonnull BlockPos position, MovementLimitations limitations) {
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

        return position;
    }

    protected boolean canWalkFromTo(IWorldReader world, BlockPos start, BlockPos end, MovementLimitations limitations) {
        if (!this.fitsIn(world, start, end, limitations)) {
            return false;
        }

        PathNodeType endType = getPathNodeType(world, end);
        if ((endType == PathNodeType.WATER && getPathNodeType(world, end.up()) == PathNodeType.WATER) || endType == PathNodeType.LAVA || endType == PathNodeType.DAMAGE_FIRE) {
            if (endType == PathNodeType.DAMAGE_FIRE) {
//                burningTicks = 60;
            }
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
        float x = end.getX() + 0.5F;
        int y = end.getY();
        float z = end.getZ() + 0.5F;
        float width = limitations.modelWidth > 1 ? 1 : limitations.modelWidth;

        AxisAlignedBB axisalignedbb1 = new AxisAlignedBB(
                (double) x - (width / 2),
                (double) y + 0.001D,
                (double) z - (width / 2),
                (double) x + (width / 2),
                ((float) y + limitations.modelHeight),
                (double) z + (width / 2)
        );
        boolean isCollisionBoxesEmpty = world.isCollisionBoxesEmpty(limitations.entity, axisalignedbb1);

        if (isCollisionBoxesEmpty && limitations.modelWidth > 1) {
            List<List<BlockPos>> blocksToCheck = this.mapAllBlocksBetweenTwoPoints(limitations, x, y, z);
            if (!this.samePatternOfBlocks(world, blocksToCheck, limitations)) {
                return false;
            }
        }

        return isCollisionBoxesEmpty;
    }

    private boolean samePatternOfBlocks(IWorldReader world, List<List<BlockPos>> blocksToCheck, MovementLimitations limitations) {
        if (blocksToCheck.size() == 0) {
            return false;
        }

        List<List<Boolean>> checked = Lists.newArrayList();
        int startY = blocksToCheck.get(0).get(0).getY();

        for (List<BlockPos> xBlockToCheck : blocksToCheck) {
            List<Boolean> xChecked = Lists.newArrayList();

            for (BlockPos blockToCheck : xBlockToCheck) {
                boolean checkResult = true;
                for (int y = startY; y <= Math.ceil(startY + limitations.modelHeight); y++) {
                    if (this.isSolid(world, blockToCheck)) {
                        checkResult = false;
                        break;
                    }
                }

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

    @Nullable
    private Direction8 getMoveDirection(BlockPos start, BlockPos end) {
        int xDiff = start.getX() - end.getX();
        int zDiff = start.getZ() - end.getZ();

        if (zDiff < 0 && xDiff == 0) {
            return Direction8.NORTH;
        }
        if (zDiff > 0 && xDiff == 0) {
            return Direction8.SOUTH;
        }
        if (xDiff > 0 && zDiff == 0) {
            return Direction8.EAST;
        }
        if (xDiff < 0 && zDiff == 0) {
            return Direction8.WEST;
        }

        if (zDiff < 0 && xDiff < 0) {
            return Direction8.NORTH_WEST;
        }
        if (zDiff > 0 && xDiff > 0) {
            return Direction8.SOUTH_EAST;
        }
        if (zDiff > 0 && xDiff < 0) {
            return Direction8.SOUTH_WEST;
        }
        if (zDiff < 0 && xDiff > 0) {
            return Direction8.NORTH_EAST;
        }

        return null;
    }

    protected boolean isSolid(IWorldReader world, @Nonnull BlockPos position) {
        if (getPathNodeType(world, position) == PathNodeType.LEAVES || world.getBlockState(position).getBlock() == Blocks.LILY_PAD) {
            return true;
        }

        return world.getBlockState(position).isSolid();
    }

    protected PathNodeType getPathNodeType(IWorldReader world, BlockPos blockPos) {
        return nodeProcessor.getPathNodeType(world, blockPos.getX(), blockPos.getY(), blockPos.getZ());
    }

    protected MovementLimitations createLimitations(LivingEntity entity) {
        return new MovementLimitations(1F, entity.getMaxFallHeight(), entity.getHeight(), entity.getWidth(), entity);
    }
}
