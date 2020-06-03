package e33.guardy.pathfinding;

import e33.guardy.debug.TimeMeter;
import e33.guardy.entity.ShootyEntity;
import e33.guardy.pathfinding.leafs.DangerousTreeLeaf;
import e33.guardy.pathfinding.leafs.TreeLeaf;
import net.minecraft.entity.MobEntity;
import net.minecraft.pathfinding.Path;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorldReader;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class PathBuilderMetrics extends PathBuilder {

    public PathBuilderMetrics(ShootyEntity shooty) {
        super(shooty);
    }

    public Path getPath(List<MobEntity> enemies) {
        TimeMeter.start(TimeMeter.MODULE_PATH_BUILDING, "getPath");
        Path r = super.getPath(enemies);
        TimeMeter.end(TimeMeter.MODULE_PATH_BUILDING, "getPath");
        return r;
    }

    protected Path buildPath(MovementLimitations limitations, int enemiesCount) {
        TimeMeter.start(TimeMeter.MODULE_PATH_BUILDING, "buildPath");
        Path r = super.buildPath(limitations, enemiesCount);
        TimeMeter.end(TimeMeter.MODULE_PATH_BUILDING, "buildPath");

        return r;
    }

    protected Path buildPathWithMaxEnemiesOnPoint(int maxEnemies, MovementLimitations limitations) {
        TimeMeter.start(TimeMeter.MODULE_PATH_BUILDING, "buildDangerousPathWithMaxReach");
        Path r = super.buildPathWithMaxEnemiesOnPoint(maxEnemies, limitations);
        TimeMeter.end(TimeMeter.MODULE_PATH_BUILDING, "buildDangerousPathWithMaxReach");

        return r;
    }

    protected List<DangerousTreeLeaf> getLeafsWithMaxEnemies(List<BlockPos> points, int maxEnemiesOnPoint, DangerousTreeLeaf parent) {
        TimeMeter.start(TimeMeter.MODULE_PATH_BUILDING, "getLeafsWithReach");
        List<DangerousTreeLeaf> r = super.getLeafsWithMaxEnemies(points, maxEnemiesOnPoint, parent);
        TimeMeter.end(TimeMeter.MODULE_PATH_BUILDING, "getLeafsWithReach");

        return r;
    }

    protected Path createPathFromTree(DangerousTreeLeaf leaf) {
        TimeMeter.start(TimeMeter.MODULE_PATH_BUILDING, "createPathFromTree");
        Path r = super.createPathFromTree(leaf);
        TimeMeter.end(TimeMeter.MODULE_PATH_BUILDING, "createPathFromTree");

        return r;
    }

    protected List<BlockPos> findSafePoints(Map<BlockPos, Map<UUID, Integer>> speedTracker) {
        TimeMeter.start(TimeMeter.MODULE_PATH_BUILDING, "findSafePoints");
        List<BlockPos> r = super.findSafePoints(speedTracker);
        TimeMeter.end(TimeMeter.MODULE_PATH_BUILDING, "findSafePoints");

        return r;
    }

    protected List<BlockPos> makeSteps(List<BlockPos> previousSteps, IWorldReader world, AxisAlignedBB zone, Map<String, Boolean> visitedPoints, List<BlockPos> blockedPoints, MovementLimitations limitations) {
        TimeMeter.start(TimeMeter.MODULE_PATH_BUILDING, "getNewWave");
        List<BlockPos> r = super.makeSteps(previousSteps, world, zone, visitedPoints, blockedPoints, limitations);
        TimeMeter.end(TimeMeter.MODULE_PATH_BUILDING, "getNewWave");

        return r;
    }

    protected List<BlockPos> getStepVariants(IWorldReader world, BlockPos start, AxisAlignedBB zone, Map<String, Boolean> visitedPoints, List<BlockPos> blockedPoints, MovementLimitations limitations) {
        TimeMeter.start(TimeMeter.MODULE_PATH_BUILDING, "getVariants");
        List<BlockPos> r = super.getStepVariants(world, start, zone, visitedPoints, blockedPoints, limitations);
        TimeMeter.end(TimeMeter.MODULE_PATH_BUILDING, "getVariants");

        return r;
    }

    protected boolean isValidPosition(IWorldReader world, BlockPos previousPosition, BlockPos variant, AxisAlignedBB zone, Map<String, Boolean> visitedPoints, List<BlockPos> blockedPoints, MovementLimitations limitations) {
        TimeMeter.start(TimeMeter.MODULE_PATH_BUILDING, "isValidPos");
        boolean r = super.isValidPosition(world, previousPosition, variant, zone, visitedPoints, blockedPoints, limitations);
        TimeMeter.end(TimeMeter.MODULE_PATH_BUILDING, "isValidPos");

        return r;
    }

    protected BlockPos getTopPosition(IWorldReader world, int x, int y, int z, MovementLimitations limitations) {
        TimeMeter.start(TimeMeter.MODULE_PATH_BUILDING, "getTopPosition");
        BlockPos r = super.getTopPosition(world, x, y, z, limitations);
        TimeMeter.end(TimeMeter.MODULE_PATH_BUILDING, "getTopPosition");

        return r;
    }

    protected boolean canWalkFromTo(IWorldReader world, BlockPos start, BlockPos end, MovementLimitations limitations) {
        TimeMeter.start(TimeMeter.MODULE_PATH_BUILDING, "canWalkFromTo");
        boolean r = super.canWalkFromTo(world, start, end, limitations);
        TimeMeter.end(TimeMeter.MODULE_PATH_BUILDING, "canWalkFromTo");

        return r;
    }

    protected boolean fitsIn(IWorldReader world, BlockPos end, MovementLimitations limitations) {
        TimeMeter.start(TimeMeter.MODULE_PATH_BUILDING, "fitsIn");
        boolean r = super.fitsIn(world, end, limitations);
        TimeMeter.end(TimeMeter.MODULE_PATH_BUILDING, "fitsIn");

        return r;
    }

    protected boolean canStandOn(IWorldReader world, BlockPos position, MovementLimitations limitations) {
        TimeMeter.start(TimeMeter.MODULE_PATH_BUILDING, "canStandOn");
        boolean r = super.canStandOn(world, position, limitations);
        TimeMeter.end(TimeMeter.MODULE_PATH_BUILDING, "canStandOn");

        return r;
    }

    protected boolean samePatternOfBlocks(IWorldReader world, List<List<BlockPos>> blocksToCheck, MovementLimitations limitations) {
        TimeMeter.start(TimeMeter.MODULE_PATH_BUILDING, "samePatternOfBlocks");
        boolean r = super.samePatternOfBlocks(world, blocksToCheck, limitations);
        TimeMeter.end(TimeMeter.MODULE_PATH_BUILDING, "samePatternOfBlocks");

        return r;
    }

    protected List<List<BlockPos>> mapAllBlocksBetweenTwoPoints(MovementLimitations limitations, float stepX, int stepY, float stepZ) {
        TimeMeter.start(TimeMeter.MODULE_PATH_BUILDING, "squareIsAllTrue");
        List<List<BlockPos>> r = super.mapAllBlocksBetweenTwoPoints(limitations, stepX, stepY, stepZ);
        TimeMeter.end(TimeMeter.MODULE_PATH_BUILDING, "squareIsAllTrue");

        return r;
    }

    protected boolean isSolid(IWorldReader world, @Nonnull BlockPos position, MovementLimitations limitations) {
        TimeMeter.start(TimeMeter.MODULE_PATH_BUILDING, "isSolid");
        boolean r = super.isSolid(world, position, limitations);
        TimeMeter.end(TimeMeter.MODULE_PATH_BUILDING, "isSolid");

        return r;
    }
}