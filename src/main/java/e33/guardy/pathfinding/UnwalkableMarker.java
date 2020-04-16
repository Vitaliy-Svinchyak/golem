package e33.guardy.pathfinding;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import net.minecraft.entity.MobEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorldReader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

public class UnwalkableMarker {
    final static Logger LOGGER = LogManager.getLogger();

    protected static Map<UUID, BlockPos> lastPos = Maps.newHashMap();
    protected static Map<UUID, List<BlockPos>> unwalkableBlocks = Maps.newHashMap();

    public static void mark(IWorldReader world, MobEntity me, AxisAlignedBB zone) {
        if (lastPos.get(me.getUniqueID()) != null && lastPos.get(me.getUniqueID()).equals(me.getPosition())) {
            return;
        }

        BlockPos myPos = me.getPosition();
        lastPos.put(me.getUniqueID(), myPos);
        Map<String, Boolean> usedCoors = Maps.newHashMap();
        usedCoors.put(myPos.toString(), true);
        List<BlockPos> points = Lists.newArrayList(myPos);
        List<BlockPos> cantGo = Lists.newArrayList();

        int iteration = 0;
        while (points.size() > 0) {
            List<BlockPos> tempPoints = Lists.newArrayList();
            for (BlockPos point : points) {
                List<BlockPos> vars = getVariants(world, point, zone, usedCoors, cantGo);

                for (BlockPos var : vars) {
                    usedCoors.put(var.toString(), true);
                    tempPoints.add(var);
                }
            }

            iteration++;
            if (iteration >= 16 * 16) {
                LOGGER.error("FUCK");
                break;
            }
            points = tempPoints;
        }

        double y = myPos.getY();
        List<BlockPos> notOkPositions = Lists.newArrayList();
        for (double x = zone.minX; x <= zone.maxX; x++) {
            for (double z = zone.minZ; z <= zone.maxZ; z++) {
                BlockPos pos = getTopPosition(world, new BlockPos(x, y, z));
                if (usedCoors.get(pos.toString()) == null) {
                    notOkPositions.add(pos);
                }
            }
        }

        for (BlockPos unwalkableBlock : cantGo) {
            if (usedCoors.get(unwalkableBlock.toString()) == null && !notOkPositions.contains(unwalkableBlock)) {
                notOkPositions.add(unwalkableBlock);
            }
        }

        unwalkableBlocks.put(me.getUniqueID(), notOkPositions);
    }

    static List<BlockPos> getVariants(IWorldReader world, BlockPos start, AxisAlignedBB zone, Map<String, Boolean> usedCoors, List<BlockPos> cantGo) {
        List<BlockPos> variants = Lists.newArrayList(
                getTopPosition(world, start.east()),
                getTopPosition(world, start.north()),
                getTopPosition(world, start.south()),
                getTopPosition(world, start.west()),
                getTopPosition(world, start.north().east()),
                getTopPosition(world, start.north().west()),
                getTopPosition(world, start.south().east()),
                getTopPosition(world, start.south().west())
        );

        return variants.stream()
                .filter(variant -> {
                    if (usedCoors.get(variant.toString()) == null && variant.getX() >= zone.minX - 1 && variant.getX() <= zone.maxX && variant.getZ() >= zone.minZ - 1 && variant.getZ() <= zone.maxZ) {
                        if (Math.abs(variant.getY() - start.getY()) <= 1) {
                            // check walls between
                            if (variant.getX() != start.getX() && variant.getZ() != start.getZ()) {
                                BlockPos toCheckWall = getTopPosition(world, new BlockPos(variant.getX(), start.getY(), start.getZ()));
                                BlockPos toCheckWall2 = getTopPosition(world, new BlockPos(start.getX(), start.getY(), variant.getZ()));

                                if (toCheckWall.getY() - start.getY() <= 1 && toCheckWall2.getY() - start.getY() <= 1) {
                                    return true;
                                } else {
                                    cantGo.add(variant);
                                    return false;
                                }
                            }
                            return true;
                        } else {
                            cantGo.add(variant);
                            return false;
                        }
                    }
                    return false;
                })
                .collect(Collectors.toList());
    }

    public static Map<UUID, List<BlockPos>> getUnwalkableBlocks() {
        return unwalkableBlocks;
    }

    static BlockPos getTopPosition(IWorldReader world, @Nonnull BlockPos position) {
        while (world.getBlockState(position).isSolid() || world.getBlockState(position.up()).isSolid()) {
            position = position.up();
        }

        if (!world.getBlockState(position).isSolid() && world.getBlockState(position.down()).isSolid()) {
            return position;
        }

        while (!world.getBlockState(position).isSolid()) {
            position = position.down();
        }
        return position.up();
    }

}
