package e33.guardy.pathfinding;

import e33.guardy.debug.TimeMeter;
import e33.guardy.pathfinding.targetFinding.ITargetFinder;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorldReader;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Map;

public class NextStepVariatorMeter extends NextStepVariator {
    NextStepVariatorMeter(IWorldReader world) {
        super(world);
    }

    List<BlockPos> makeSteps(ITargetFinder finder, AxisAlignedBB zone, List<BlockPos> blockedPoints, MovementLimitations limitations) {
        TimeMeter.start("makeSteps");
        List<BlockPos> r = super.makeSteps(finder, zone, blockedPoints, limitations);
        TimeMeter.end("makeSteps");

        return r;
    }

//    BlockPos getTopPosition(int x, int y, int z, MovementLimitations limitations) {
//        TimeMeter.start("getTopPosition");
//        BlockPos r = super.getTopPosition(x, y, z, limitations);
//        TimeMeter.end("getTopPosition");
//
//        return r;
//    }

//    BlockPos getTopOrBottomPosition(int x, int y, int z, MovementLimitations limitations) {
//        TimeMeter.start("getTopOrBottomPosition");
//        BlockPos r = super.getTopOrBottomPosition(x, y, z, limitations);
//        TimeMeter.end("getTopOrBottomPosition");
//
//        return r;
//    }

    protected List<BlockPos> getStepVariants(BlockPos start, AxisAlignedBB zone, StepHistoryKeeper stepHistory, List<BlockPos> blockedPoints, Map<String, Boolean> usedOnThisStepPositions, MovementLimitations limitations) {
        TimeMeter.start("getStepVariants");
        List<BlockPos> r = super.getStepVariants(start, zone, stepHistory, blockedPoints, usedOnThisStepPositions, limitations);
        TimeMeter.end("getStepVariants");

        return r;
    }

    protected boolean isValidPosition(BlockPos previousPosition, BlockPos newPosition, AxisAlignedBB zone, StepHistoryKeeper stepHistory, List<BlockPos> blockedPoints, MovementLimitations limitations) {
        TimeMeter.start("isValidPosition");
        boolean r = super.isValidPosition(previousPosition, newPosition, zone, stepHistory, blockedPoints, limitations);
        TimeMeter.end("isValidPosition");

        return r;
    }

    protected boolean canWalkFromTo(BlockPos start, BlockPos end, MovementLimitations limitations) {
        TimeMeter.start("canWalkFromTo");
        boolean r = super.canWalkFromTo(start, end, limitations);
        TimeMeter.end("canWalkFromTo");

        return r;
    }

    protected boolean fitsIn(BlockPos end, MovementLimitations limitations) {
        TimeMeter.start("fitsIn");
        boolean r = super.fitsIn(end, limitations);
        TimeMeter.end("fitsIn");

        return r;
    }

    protected List<List<BlockPos>> mapAllBlocksBetweenTwoPoints(MovementLimitations limitations, float stepX, int stepY, float stepZ) {
        TimeMeter.start("mapAllBlocksBetweenTwoPoints");
        List<List<BlockPos>> r = super.mapAllBlocksBetweenTwoPoints(limitations, stepX, stepY, stepZ);
        TimeMeter.end("mapAllBlocksBetweenTwoPoints");

        return r;
    }

    protected boolean canStandOn(BlockPos position, MovementLimitations limitations) {
        TimeMeter.start("canStandOn");
        boolean r = super.canStandOn(position, limitations);
        TimeMeter.end("canStandOn");

        return r;
    }

    protected boolean samePatternOfBlocks(List<List<BlockPos>> blocksToCheck, MovementLimitations limitations) {
        TimeMeter.start("samePatternOfBlocks");
        boolean r = super.samePatternOfBlocks(blocksToCheck, limitations);
        TimeMeter.end("samePatternOfBlocks");

        return r;
    }

    protected boolean squareIsAllTrue(List<List<Boolean>> checked, int startX, int startZ, int width) {
        TimeMeter.start("squareIsAllTrue");
        boolean r = super.squareIsAllTrue(checked, startX, startZ, width);
        TimeMeter.end("squareIsAllTrue");

        return r;
    }

//    boolean isSolid(@Nonnull BlockPos position, MovementLimitations limitations) {
//        TimeMeter.start("isSolid");
//        boolean r = super.isSolid(position, limitations);
//        TimeMeter.end("isSolid");
//
//        return r;
//    }
}
