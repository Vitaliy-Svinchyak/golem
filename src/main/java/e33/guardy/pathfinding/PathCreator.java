package e33.guardy.pathfinding;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import e33.guardy.debug.TimeMeter;
import e33.guardy.entity.ShootyEntity;
import e33.guardy.pathfinding.pathBuilding.PatrolPathBuilder;
import e33.guardy.pathfinding.pathBuilding.SafePathBuilder;
import e33.guardy.pathfinding.targetFinding.FullScouting;
import e33.guardy.pathfinding.targetFinding.ITargetFinder;
import e33.guardy.pathfinding.targetFinding.PositionFinder;
import e33.guardy.pathfinding.targetFinding.SafePlaceFinder;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.pathfinding.Path;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IWorldReader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;

// TODO TreeLeaf from start. Will not need to search path after
public class PathCreator {
    final static Logger LOGGER = LogManager.getLogger();

    protected final ShootyEntity shooty;
    private final NextStepVariator nextStepVariator;

    public Path currentPath;
    public SafePlaceFinder safePlaceFinder;
    public Collection<ITargetFinder> enemyScouts;
    public List<BlockPos> safestPoints = Lists.newArrayList();

    public PathCreator(ShootyEntity shooty) {
        this.shooty = shooty;
        this.nextStepVariator = new NextStepVariator(this.shooty.getEntityWorld());
    }

    public Path getSafePath(List<MobEntity> enemies) {
        if (!this.shooty.onGround) {
            return null;
        }
        this.nextStepVariator.clearCache();
        this.currentPath = null;

        MovementLimitations shootyLimitations = this.createLimitations(this.shooty);
        AxisAlignedBB searchZone = this.getSearchZone();

        Map<UUID, MovementLimitations> enemyLimitations = this.createEnemyLimitations(enemies);
        Map<UUID, ITargetFinder> enemyScouts = this.createEnemyScouts(enemies);
        this.createEnemiesCurrentSteps(enemies, enemyLimitations, enemyScouts);

        BlockPos shootyCurrentPosition = this.getShootyPos(shooty, shootyLimitations);
        this.safePlaceFinder = new SafePlaceFinder(shootyCurrentPosition, enemyScouts.values());
        this.enemyScouts = enemyScouts.values();

        int stepNumber = 1;
        while (stepNumber < 100) {
            List<BlockPos> newSteps = this.nextStepVariator.makeSteps(this.safePlaceFinder, searchZone, null, shootyLimitations);
            if (newSteps.size() == 0) {
                break;
            }
            this.safePlaceFinder.nextStep(newSteps, stepNumber);

            for (MobEntity enemy : enemies) {
                UUID uid = enemy.getUniqueID();
                ITargetFinder enemyScout = enemyScouts.get(uid);
                List<BlockPos> enemyBlockedPoints = Lists.newArrayList();
                List<BlockPos> enemyNewSteps = this.nextStepVariator.makeSteps(enemyScout, searchZone, enemyBlockedPoints, enemyLimitations.get(uid));

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

    public List<Path> getCycledPathsThroughPositions(List<BlockPos> positions) {
        this.nextStepVariator.clearCache();
        LinkedList<Path> pathParts = new LinkedList<>();
        MovementLimitations shootyLimitations = this.createLimitations(this.shooty);

        BlockPos currentPosition = this.getEntityStandPosition(this.shooty.getPosition(), shootyLimitations);
        BlockPos firstPatrolPosition = positions.get(0);
        PositionFinder firstFinder = new PositionFinder(currentPosition, firstPatrolPosition);

        TimeMeter.start("findPathBetweenPoints");
        Path routeToStart = this.findPathBetweenPoints(firstFinder, shootyLimitations);
        positions.set(0, firstFinder.getTargets().get(0));
        TimeMeter.end("findPathBetweenPoints");

        for (int i = 0; i < positions.size(); i++) {
            BlockPos startPosition = positions.get(i);
            int endIndex = i == positions.size() - 1 ? 0 : i + 1;
            BlockPos endPosition = positions.get(endIndex);

            PositionFinder finder = new PositionFinder(startPosition, endPosition);
            TimeMeter.start("findPathBetweenPoints");
            pathParts.add(this.findPathBetweenPoints(finder, shootyLimitations));
            TimeMeter.end("findPathBetweenPoints");

            positions.set(endIndex, finder.getTargets().get(0)); // Because he rewrites himself in case of targetNotFound
        }

        pathParts.addFirst(routeToStart);

        return pathParts;
    }

    private Path findPathBetweenPoints(PositionFinder finder, MovementLimitations shootyLimitations) {
        AxisAlignedBB searchZone = this.shooty.getBoundingBox().grow(150);
        StepHistoryKeeper stepHistory = finder.getStepHistory();

        int i = 0;
        while (!finder.targetFound() && stepHistory.getLastStepPositions().size() > 0) {
            List<BlockPos> steps = this.nextStepVariator.makeSteps(finder, searchZone, null, shootyLimitations);
            finder.nextStep(steps, i);
            i++;
        }

        LOGGER.info(i + " cycles " + finder.targetFound());
        PatrolPathBuilder builder = new PatrolPathBuilder();

        TimeMeter.start("build PathBetweenPoints");
        Path path = builder.build(shootyLimitations, finder);
        TimeMeter.end("build PathBetweenPoints");
        return path;
    }

    @Nullable
    protected BlockPos getTopOrBottomPosition(@Nonnull BlockPos originalPosition, MovementLimitations limitations) {
        return this.nextStepVariator.getTopOrBottomPosition(originalPosition.getX(), originalPosition.getY(), originalPosition.getZ(), limitations);
    }

    private MovementLimitations createLimitations(MobEntity entity) {
        return new MovementLimitations(1F, entity.getMaxFallHeight(), entity.getHeight(), entity.getWidth(), entity.getPathPriority(PathNodeType.WATER) >= 0F, entity);
    }

    // Needed because entity can halfly stand on higher block but Minecraft will track it's position as air near it
    private BlockPos getShootyPos(ShootyEntity entity, MovementLimitations limitations) {
        BlockPos position = entity.getPosition();

        BlockPos movedPosition = this.getTopOrBottomPosition(position, limitations);
        if (movedPosition == null || !movedPosition.equals(position)) {
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

    private void createEnemiesCurrentSteps(List<MobEntity> enemies, Map<UUID, MovementLimitations> enemyLimitations, Map<UUID, ITargetFinder> enemyScouts) {
        for (MobEntity enemy : enemies) {
            enemyScouts.get(enemy.getUniqueID()).nextStep(Lists.newArrayList(getEntityStandPosition(enemy.getPosition(), enemyLimitations.get(enemy.getUniqueID()))), 0);
        }
    }

    private BlockPos getEntityStandPosition(@Nonnull BlockPos position, MovementLimitations limitations) {
        while (!this.nextStepVariator.isSolid(position, limitations)) {
            position = position.down();
        }
        position = position.up();

        return position;
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
