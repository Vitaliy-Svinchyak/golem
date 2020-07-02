package e33.guardy.pathfinding;

import com.google.common.collect.Sets;

import java.util.EnumSet;
import java.util.Set;
import javax.annotation.Nullable;

import e33.guardy.entity.ShootyEntity;
import net.minecraft.block.AbstractRailBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.DoorBlock;
import net.minecraft.block.FenceGateBlock;
import net.minecraft.block.LeavesBlock;
import net.minecraft.block.material.Material;
import net.minecraft.entity.MobEntity;
import net.minecraft.fluid.Fluids;
import net.minecraft.fluid.IFluidState;
import net.minecraft.pathfinding.*;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Direction;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
// TODO 2 remove it at all?
public class CarefulWalkNodeProcessor extends WalkNodeProcessor {
    final static Logger LOGGER = LogManager.getLogger();

    protected float avoidsWater;
    protected MobEntity currentEntity;
    protected ShootyEntity shooty;

    public void init(ShootyEntity mob) {
        this.shooty = mob;
    }

    public void postProcess() {
        this.shooty.setPathPriority(PathNodeType.WATER, this.avoidsWater);
        super.postProcess();
    }

    public PathPoint getStart() {
        this.init((ShootyEntity) this.entity);

        int currentY;
        if (this.getCanSwim() && this.shooty.isInWater()) {
            currentY = MathHelper.floor(this.shooty.getBoundingBox().minY);
            BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos(this.shooty.posX, (double) currentY, this.shooty.posZ);

            for (BlockState blockstate = this.blockaccess.getBlockState(blockpos$mutableblockpos); blockstate.getBlock() == Blocks.WATER || blockstate.getFluidState() == Fluids.WATER.getStillFluidState(false); blockstate = this.blockaccess.getBlockState(blockpos$mutableblockpos)) {
                ++currentY;
                blockpos$mutableblockpos.setPos(this.shooty.posX, (double) currentY, this.shooty.posZ);
            }

            --currentY;
        } else if (this.shooty.onGround) {
            currentY = MathHelper.floor(this.shooty.getBoundingBox().minY + 0.5D);
        } else {
            BlockPos blockpos;
            for (blockpos = new BlockPos(this.shooty); (this.blockaccess.getBlockState(blockpos).isAir() || this.blockaccess.getBlockState(blockpos).allowsMovement(this.blockaccess, blockpos, PathType.LAND)) && blockpos.getY() > 0; blockpos = blockpos.down()) {
                ;
            }

            currentY = blockpos.up().getY();
        }

        BlockPos blockpos2 = new BlockPos(this.shooty);
        PathNodeType pathnodetype1 = this.getPathNodeType(this.shooty, blockpos2.getX(), currentY, blockpos2.getZ());
        // if entity doesn't like his current position as start point - try all 4 around it
        if (this.shooty.getPathPriority(pathnodetype1, blockpos2) < 0.0F) {
            Set<BlockPos> set = Sets.newHashSet();
            set.add(new BlockPos(this.shooty.getBoundingBox().minX, (double) currentY, this.shooty.getBoundingBox().minZ));
            set.add(new BlockPos(this.shooty.getBoundingBox().minX, (double) currentY, this.shooty.getBoundingBox().maxZ));
            set.add(new BlockPos(this.shooty.getBoundingBox().maxX, (double) currentY, this.shooty.getBoundingBox().minZ));
            set.add(new BlockPos(this.shooty.getBoundingBox().maxX, (double) currentY, this.shooty.getBoundingBox().maxZ));

            for (BlockPos blockpos1 : set) {
                PathNodeType pathnodetype = this.getPathNodeType(this.shooty, blockpos1);
                // don't try all points, just use first one
                // TODO 2 maybe fix it
                if (this.shooty.getPathPriority(pathnodetype, blockpos1) >= 0.0F) {
                    return this.openPoint(blockpos1.getX(), blockpos1.getY(), blockpos1.getZ());
                }
            }
        }

        return this.openPoint(blockpos2.getX(), currentY, blockpos2.getZ());
    }

    // createFlaggedPathPoint
    public FlaggedPathPoint func_224768_a(double x, double y, double z) {
        return new FlaggedPathPoint(this.openPoint(MathHelper.floor(x), MathHelper.floor(y), MathHelper.floor(z)));
    }

    // getNumberOfSafePoints
    public int func_222859_a(PathPoint[] pathOptions, PathPoint startPoint) {
        int i = 0;
        int stepHeight = 0;
        BlockPos blockPos = new BlockPos(startPoint.x, startPoint.y + 1, startPoint.z);
        PathNodeType pathnodetype = this.getPathNodeType(this.shooty, startPoint.x, startPoint.y + 1, startPoint.z);
        if (this.shooty.getPathPriority(pathnodetype, blockPos) >= 0.0F) {
            stepHeight = MathHelper.floor(Math.max(1.0F, this.shooty.stepHeight));
        }

        double groundY = getGroundY(this.blockaccess, new BlockPos(startPoint.x, startPoint.y, startPoint.z));
        PathPoint pathpoint = this.getSafePoint(startPoint.x, startPoint.y, startPoint.z + 1, stepHeight, groundY, Direction.SOUTH);
        if (pathpoint != null && !pathpoint.visited && pathpoint.costMalus >= 0.0F) {
            pathOptions[i++] = pathpoint;
        }

        PathPoint pathpoint1 = this.getSafePoint(startPoint.x - 1, startPoint.y, startPoint.z, stepHeight, groundY, Direction.WEST);
        if (pathpoint1 != null && !pathpoint1.visited && pathpoint1.costMalus >= 0.0F) {
            pathOptions[i++] = pathpoint1;
        }

        PathPoint pathpoint2 = this.getSafePoint(startPoint.x + 1, startPoint.y, startPoint.z, stepHeight, groundY, Direction.EAST);
        if (pathpoint2 != null && !pathpoint2.visited && pathpoint2.costMalus >= 0.0F) {
            pathOptions[i++] = pathpoint2;
        }

        PathPoint pathpoint3 = this.getSafePoint(startPoint.x, startPoint.y, startPoint.z - 1, stepHeight, groundY, Direction.NORTH);
        if (pathpoint3 != null && !pathpoint3.visited && pathpoint3.costMalus >= 0.0F) {
            pathOptions[i++] = pathpoint3;
        }

        PathPoint pathpoint4 = this.getSafePoint(startPoint.x - 1, startPoint.y, startPoint.z - 1, stepHeight, groundY, Direction.NORTH);
        if (this.pointIsSaferThenPrevious(startPoint, pathpoint1, pathpoint3, pathpoint4)) {
            pathOptions[i++] = pathpoint4;
        }

        PathPoint pathpoint5 = this.getSafePoint(startPoint.x + 1, startPoint.y, startPoint.z - 1, stepHeight, groundY, Direction.NORTH);
        if (this.pointIsSaferThenPrevious(startPoint, pathpoint2, pathpoint3, pathpoint5)) {
            pathOptions[i++] = pathpoint5;
        }

        PathPoint pathpoint6 = this.getSafePoint(startPoint.x - 1, startPoint.y, startPoint.z + 1, stepHeight, groundY, Direction.SOUTH);
        if (this.pointIsSaferThenPrevious(startPoint, pathpoint1, pathpoint, pathpoint6)) {
            pathOptions[i++] = pathpoint6;
        }

        PathPoint pathpoint7 = this.getSafePoint(startPoint.x + 1, startPoint.y, startPoint.z + 1, stepHeight, groundY, Direction.SOUTH);
        if (this.pointIsSaferThenPrevious(startPoint, pathpoint2, pathpoint, pathpoint7)) {
            pathOptions[i++] = pathpoint7;
        }

        return i;
    }

    private boolean pointIsSaferThenPrevious(PathPoint pathPoint, @Nullable PathPoint pathpoint2, @Nullable PathPoint pathpoint3, @Nullable PathPoint pathpoint4) {
        if (pathpoint4 != null && pathpoint3 != null && pathpoint2 != null) {
            if (pathpoint4.visited) {
                return false;
            } else if (pathpoint3.y <= pathPoint.y && pathpoint2.y <= pathPoint.y) {
                return pathpoint4.costMalus >= 0.0F && (pathpoint3.y < pathPoint.y || pathpoint3.costMalus >= 0.0F) && (pathpoint2.y < pathPoint.y || pathpoint2.costMalus >= 0.0F);
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    public static double getGroundY(IBlockReader region, BlockPos pos) {
        BlockPos blockpos = pos.down();
        VoxelShape voxelshape = region.getBlockState(blockpos).getCollisionShape(region, blockpos);
        return (double) blockpos.getY() + (voxelshape.isEmpty() ? 0.0D : voxelshape.getEnd(Direction.Axis.Y));
    }

    /**
     * Returns a point that the entity can safely move to
     */
    @Nullable
    private PathPoint getSafePoint(int x, int y, int z, int stepHeight, double groundYIn, Direction facing) {
        PathPoint pathpoint = null;
        BlockPos blockpos = new BlockPos(x, y, z);
        double groundY = getGroundY(this.blockaccess, blockpos);
        if (groundY - groundYIn > 1.125D) {
            return null;
        }

        PathNodeType pathnodetype = this.getPathNodeType(this.shooty, x, y, z);
        float pathPriority = this.shooty.getPathPriority(pathnodetype, blockpos);
        double halfedEntityWidth = (double) this.shooty.getWidth() / 2.0D;
        if (pathPriority >= 0.0F) {
            pathpoint = this.openPoint(x, y, z);
            pathpoint.nodeType = pathnodetype;
            pathpoint.costMalus = Math.max(pathpoint.costMalus, pathPriority);
        }

        if (pathnodetype == PathNodeType.WALKABLE) {
            return pathpoint;
        }

        if ((pathpoint == null || pathpoint.costMalus < 0.0F) && stepHeight > 0 && pathnodetype != PathNodeType.FENCE && pathnodetype != PathNodeType.TRAPDOOR) {
            pathpoint = this.getSafePoint(x, y + 1, z, stepHeight - 1, groundYIn, facing);
            if (pathpoint != null && (pathpoint.nodeType == PathNodeType.OPEN || pathpoint.nodeType == PathNodeType.WALKABLE) && this.shooty.getWidth() < 1.0F) {
                double d2 = (double) (x - facing.getXOffset()) + 0.5D;
                double d3 = (double) (z - facing.getZOffset()) + 0.5D;
                AxisAlignedBB axisalignedbb = new AxisAlignedBB(d2 - halfedEntityWidth, getGroundY(this.blockaccess, new BlockPos(d2, (double) (y + 1), d3)) + 0.001D, d3 - halfedEntityWidth, d2 + halfedEntityWidth, (double) this.shooty.getHeight() + getGroundY(this.blockaccess, new BlockPos(pathpoint.x, pathpoint.y, pathpoint.z)) - 0.002D, d3 + halfedEntityWidth);
                if (!this.blockaccess.isCollisionBoxesEmpty(this.shooty, axisalignedbb)) {
                    pathpoint = null;
                }
            }
        }

        if (pathnodetype == PathNodeType.WATER && !this.getCanSwim()) {
            if (this.getPathNodeType(this.shooty, x, y - 1, z) != PathNodeType.WATER) {
                return pathpoint;
            }

            while (y > 0) {
                --y;
                pathnodetype = this.getPathNodeType(this.shooty, x, y, z);
                if (pathnodetype != PathNodeType.WATER) {
                    return pathpoint;
                }

                pathpoint = this.openPoint(x, y, z);
                pathpoint.nodeType = pathnodetype;
                pathpoint.costMalus = Math.max(pathpoint.costMalus, this.shooty.getPathPriority(pathnodetype, new BlockPos(x, y, z)));
            }
        }

        if (pathnodetype == PathNodeType.OPEN) {
            AxisAlignedBB axisalignedbb1 = new AxisAlignedBB((double) x - halfedEntityWidth + 0.5D, (double) y + 0.001D, (double) z - halfedEntityWidth + 0.5D, (double) x + halfedEntityWidth + 0.5D, (double) ((float) y + this.shooty.getHeight()), (double) z + halfedEntityWidth + 0.5D);
            if (!this.blockaccess.isCollisionBoxesEmpty(this.shooty, axisalignedbb1)) {
                return null;
            }

            if (this.shooty.getWidth() >= 1.0F) {
                PathNodeType pathnodetype1 = this.getPathNodeType(this.shooty, x, y - 1, z);
                if (pathnodetype1 == PathNodeType.BLOCKED) {
                    pathpoint = this.openPoint(x, y, z);
                    pathpoint.nodeType = PathNodeType.WALKABLE;
                    pathpoint.costMalus = Math.max(pathpoint.costMalus, pathPriority);
                    return pathpoint;
                }
            }

            int i = 0;
            int j = y;

            while (pathnodetype == PathNodeType.OPEN) {
                --y;
                if (y < 0) {
                    PathPoint pathpoint2 = this.openPoint(x, j, z);
                    pathpoint2.nodeType = PathNodeType.BLOCKED;
                    pathpoint2.costMalus = -1.0F;
                    return pathpoint2;
                }

                PathPoint pathpoint1 = this.openPoint(x, y, z);
                if (i++ >= this.shooty.getMaxFallHeight()) {
                    pathpoint1.nodeType = PathNodeType.BLOCKED;
                    pathpoint1.costMalus = -1.0F;
                    return pathpoint1;
                }

                pathnodetype = this.getPathNodeType(this.shooty, x, y, z);
                pathPriority = this.shooty.getPathPriority(pathnodetype, new BlockPos(x, y, z));
                if (pathnodetype != PathNodeType.OPEN && pathPriority >= 0.0F) {
                    pathpoint = pathpoint1;
                    pathpoint1.nodeType = pathnodetype;
                    pathpoint1.costMalus = Math.max(pathpoint1.costMalus, pathPriority);
                    break;
                }

                if (pathPriority < 0.0F) {
                    pathpoint1.nodeType = PathNodeType.BLOCKED;
                    pathpoint1.costMalus = -1.0F;
                    return pathpoint1;
                }
            }
        }

        return pathpoint;


    }

    // first
    public PathNodeType getPathNodeType2(IBlockReader blockaccessIn, int x, int y, int z, ShootyEntity entitylivingIn, int xSize, int ySize, int zSize, boolean canBreakDoorsIn, boolean canEnterDoorsIn) {
        EnumSet<PathNodeType> enumset = EnumSet.noneOf(PathNodeType.class);
        PathNodeType pathnodetype = PathNodeType.BLOCKED;
        double d0 = (double) entitylivingIn.getWidth() / 2.0D;
        BlockPos blockpos = new BlockPos(entitylivingIn);
        this.currentEntity = entitylivingIn;
        // get into the enumset all types which entity touches
        pathnodetype = this.getPathNodeType(blockaccessIn, x, y, z, xSize, ySize, zSize, canBreakDoorsIn, canEnterDoorsIn, enumset, pathnodetype, blockpos);
        this.currentEntity = entitylivingIn;
        if (enumset.contains(PathNodeType.FENCE)) {
            return PathNodeType.FENCE;
        } else {
            PathNodeType pathnodetype1 = PathNodeType.BLOCKED;

            for (PathNodeType pathnodetype2 : enumset) {
                // return the block which entity doesn't like
                if (entitylivingIn.getPathPriority(pathnodetype2) < 0.0F) {
                    return pathnodetype2;
                }

                // remember the most prioritized block
                if (entitylivingIn.getPathPriority(pathnodetype2) >= entitylivingIn.getPathPriority(pathnodetype1)) {
                    pathnodetype1 = pathnodetype2;
                }
            }

            if (pathnodetype == PathNodeType.OPEN && entitylivingIn.getPathPriority(pathnodetype1) == 0.0F) {
                return PathNodeType.OPEN;
            } else {
                return pathnodetype1;
            }
        }
    }

    // second. adds to nodeTypeEnum all types of block which model of entity touches on its way
    public PathNodeType getPathNodeType(IBlockReader region, int x, int y, int z, int xSize, int ySize, int zSize, boolean canOpenDoorsIn, boolean canEnterDoorsIn, EnumSet<PathNodeType> nodeTypeEnum, PathNodeType nodeType, BlockPos pos) {
        for (int i = 0; i < xSize; ++i) {
            for (int j = 0; j < ySize; ++j) {
                for (int k = 0; k < zSize; ++k) {
                    int l = i + x;
                    int i1 = j + y;
                    int j1 = k + z;
                    PathNodeType pathnodetype = this.getPathNodeType(region, l, i1, j1);
                    pathnodetype = this.getPathNodeType(region, canOpenDoorsIn, canEnterDoorsIn, pos, pathnodetype);
                    if (i == 0 && j == 0 && k == 0) {
                        nodeType = pathnodetype;
                    }

                    nodeTypeEnum.add(pathnodetype);
                }
            }
        }

        return nodeType;
    }

    // fourth. rewrites type if entity is blocked by block(f.e. door, but it can open it)
    protected PathNodeType getPathNodeType(IBlockReader region, boolean canOpenDoorsIn, boolean canEnterDoorsIn, BlockPos pos, PathNodeType pathnodetype) {
        if (pathnodetype == PathNodeType.DOOR_WOOD_CLOSED && canOpenDoorsIn && canEnterDoorsIn) {
            pathnodetype = PathNodeType.WALKABLE;
        }

        if (pathnodetype == PathNodeType.DOOR_OPEN && !canEnterDoorsIn) {
            pathnodetype = PathNodeType.BLOCKED;
        }

        if (pathnodetype == PathNodeType.RAIL && !(region.getBlockState(pos).getBlock() instanceof AbstractRailBlock) && !(region.getBlockState(pos.down()).getBlock() instanceof AbstractRailBlock)) {
            pathnodetype = PathNodeType.FENCE;
        }

        if (pathnodetype == PathNodeType.LEAVES) {
            pathnodetype = PathNodeType.BLOCKED;
        }

        return pathnodetype;
    }

    // used in code
    private PathNodeType getPathNodeType(ShootyEntity entitylivingIn, BlockPos pos) {
        return this.getPathNodeType(entitylivingIn, pos.getX(), pos.getY(), pos.getZ());
    }

    // used in code
    private PathNodeType getPathNodeType(ShootyEntity entitylivingIn, int x, int y, int z) {
        return this.getPathNodeType2(this.blockaccess, x, y, z, entitylivingIn, this.entitySizeX, this.entitySizeY, this.entitySizeZ, this.getCanOpenDoors(), this.getCanEnterDoors());
    }

    // third. checks block under the current
    public PathNodeType getPathNodeType(IBlockReader blockaccessIn, int x, int y, int z) {
        PathNodeType pathnodetype = this.getPathNodeTypeRaw(blockaccessIn, x, y, z);
        // checking block under the current(f.e. current is air, but under him is lava)
        // TODO 2 very strange that we don't check blocks till the surface
        if (pathnodetype == PathNodeType.OPEN && y >= 1) {
            Block block = blockaccessIn.getBlockState(new BlockPos(x, y - 1, z)).getBlock();
            PathNodeType pathnodetype1 = this.getPathNodeTypeRaw(blockaccessIn, x, y - 1, z);
            pathnodetype = pathnodetype1 != PathNodeType.WALKABLE && pathnodetype1 != PathNodeType.OPEN && pathnodetype1 != PathNodeType.WATER && pathnodetype1 != PathNodeType.LAVA ? PathNodeType.WALKABLE : PathNodeType.OPEN;
            if (pathnodetype1 == PathNodeType.DAMAGE_FIRE || block == Blocks.MAGMA_BLOCK || block == Blocks.CAMPFIRE) {
                pathnodetype = PathNodeType.DAMAGE_FIRE;
            }

            if (pathnodetype1 == PathNodeType.DAMAGE_CACTUS) {
                pathnodetype = PathNodeType.DAMAGE_CACTUS;
            }

            if (pathnodetype1 == PathNodeType.DAMAGE_OTHER) {
                pathnodetype = PathNodeType.DAMAGE_OTHER;
            }
            if (pathnodetype1 == PathNodeType.DAMAGE_OTHER)
                pathnodetype = PathNodeType.DAMAGE_OTHER; // Forge: consider modded damage types
        }

        pathnodetype = this.checkNeighborBlocks(blockaccessIn, x, y, z, pathnodetype);
        return pathnodetype;
    }

    public PathNodeType checkNeighborBlocks(IBlockReader blockaccessIn, int x, int y, int z, PathNodeType nodeType) {
        if (nodeType == PathNodeType.WALKABLE) {
            try (BlockPos.PooledMutableBlockPos blockpos$pooledmutableblockpos = BlockPos.PooledMutableBlockPos.retain()) {
                for (int i = -1; i <= 1; ++i) {
                    for (int j = -1; j <= 1; ++j) {
                        if (i != 0 || j != 0) {
                            BlockState state = blockaccessIn.getBlockState(blockpos$pooledmutableblockpos.setPos(i + x, y, j + z));
                            Block block = state.getBlock();
                            PathNodeType type = block.getAiPathNodeType(state, blockaccessIn, blockpos$pooledmutableblockpos, this.currentEntity);
                            if (block == Blocks.CACTUS || type == PathNodeType.DAMAGE_CACTUS) {
                                nodeType = PathNodeType.DANGER_CACTUS;
                            } else if (block == Blocks.FIRE || type == PathNodeType.DAMAGE_FIRE) {
                                nodeType = PathNodeType.DANGER_FIRE;
                            } else if (block == Blocks.SWEET_BERRY_BUSH || type == PathNodeType.DAMAGE_OTHER) {
                                nodeType = PathNodeType.DANGER_OTHER;
                            }
                        }
                    }
                }
            }
        }

        return nodeType;
    }

    protected PathNodeType getPathNodeTypeRaw(IBlockReader blockaccessIn, int x, int y, int z) {
        BlockPos blockpos = new BlockPos(x, y, z);
        BlockState blockstate = blockaccessIn.getBlockState(blockpos);
        // returns only if block is burning
        PathNodeType type = blockstate.getAiPathNodeType(blockaccessIn, blockpos, this.currentEntity);
        if (type != null) return type;

        Block block = blockstate.getBlock();
        Material material = blockstate.getMaterial();
        if (blockstate.isAir(blockaccessIn, blockpos)) {
            return PathNodeType.OPEN;
        } else if (!block.isIn(BlockTags.TRAPDOORS) && block != Blocks.LILY_PAD) {
            if (block == Blocks.FIRE) {
                return PathNodeType.DAMAGE_FIRE;
            } else if (block == Blocks.CACTUS) {
                return PathNodeType.DAMAGE_CACTUS;
            } else if (block == Blocks.SWEET_BERRY_BUSH) {
                return PathNodeType.DAMAGE_OTHER;
            } else if (block instanceof DoorBlock && material == Material.WOOD && !blockstate.get(DoorBlock.OPEN)) {
                return PathNodeType.DOOR_WOOD_CLOSED;
            } else if (block instanceof DoorBlock && material == Material.IRON && !blockstate.get(DoorBlock.OPEN)) {
                return PathNodeType.DOOR_IRON_CLOSED;
            } else if (block instanceof DoorBlock && blockstate.get(DoorBlock.OPEN)) {
                return PathNodeType.DOOR_OPEN;
            } else if (block instanceof AbstractRailBlock) {
                return PathNodeType.RAIL;
            } else if (block instanceof LeavesBlock) {
                return PathNodeType.LEAVES;
            } else if (!block.isIn(BlockTags.FENCES) && !block.isIn(BlockTags.WALLS) && (!(block instanceof FenceGateBlock) || blockstate.get(FenceGateBlock.OPEN))) {
                IFluidState ifluidstate = blockaccessIn.getFluidState(blockpos);
                if (ifluidstate.isTagged(FluidTags.WATER)) {
                    return PathNodeType.WATER;
                } else if (ifluidstate.isTagged(FluidTags.LAVA)) {
                    return PathNodeType.LAVA;
                } else {
                    return blockstate.allowsMovement(blockaccessIn, blockpos, PathType.LAND) ? PathNodeType.OPEN : PathNodeType.BLOCKED;
                }
            } else {
                return PathNodeType.FENCE;
            }
        } else {
            return PathNodeType.TRAPDOOR;
        }
    }
}
