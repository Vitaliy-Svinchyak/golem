package e33.guardy.pathfinding;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import e33.guardy.E33;
import e33.guardy.entity.ShootyEntity;
import net.minecraft.block.SnowBlock;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorldReader;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Mod.EventBusSubscriber(modid = E33.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class UnwalkableMarker {
    final static Logger LOGGER = LogManager.getLogger();

    private static Map<UUID, BlockPos> lastPos = Maps.newHashMap();
    private static Map<UUID, List<BlockPos>> unwalkableBlocks = Maps.newHashMap();
    private static List<List<BlockPos>> checkingRoutes = Lists.newArrayList();
    private static ShootyEntity entity;
    private static boolean worldChanged;

    @SubscribeEvent
    public static void onBlockChangeEvent(BlockEvent.BreakEvent event) {
        // TODO filter by chunks
        UnwalkableMarker.worldChanged = true;
    }

    @SubscribeEvent
    public static void onBlockChangeEvent(BlockEvent.EntityPlaceEvent event) {
        UnwalkableMarker.worldChanged = true;
    }

    public static Map<UUID, List<BlockPos>> getUnwalkableBlocks() {
        return unwalkableBlocks;
    }

    public static List<List<BlockPos>> getCheckingRoutes() {
        return checkingRoutes;
    }

    //BlockEvent.EntityPlaceEvent, BlockEvent.BreakEvent
    public static void mark(IWorldReader world, ShootyEntity me, AxisAlignedBB zone) {
        if (lastPos.get(me.getUniqueID()) != null && lastPos.get(me.getUniqueID()).equals(me.getPosition()) && !UnwalkableMarker.worldChanged) {
            return;
        }

        if (!me.onGround) {
            return;
        }

        UnwalkableMarker.worldChanged = false;
        UnwalkableMarker.entity = me;
        UnwalkableMarker.entity = me;

        List<List<BlockPos>> localCheckingRoutes = Lists.newArrayList();
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

            if (E33.DEBUG) {
                localCheckingRoutes.add(tempPoints);
            }

            iteration++;
            if (iteration >= 16 * 16) {
                LOGGER.error("Too many iterations!!!");
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

        checkingRoutes = localCheckingRoutes;
        unwalkableBlocks.put(me.getUniqueID(), notOkPositions);
        UnwalkableMarker.entity = null;
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
                        if (canWalkFromTo(world, start, variant)) {
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
                        }

                        cantGo.add(variant);
                        return false;
                    }
                    return false;
                })
                .collect(Collectors.toList());
    }

    static BlockPos getTopPosition(IWorldReader world, @Nonnull BlockPos position) {
        if (isSolid(world, position) || isSolid(world, position.up())) {
            while (isSolid(world, position) || isSolid(world, position.up())) {
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

    static boolean canWalkFromTo(IWorldReader world, BlockPos start, BlockPos end) {
        PathNodeType endType = getPathNodeType(world, end);
        if ((endType == PathNodeType.WATER && getPathNodeType(world, end.up()) == PathNodeType.WATER) || endType == PathNodeType.LAVA || endType == PathNodeType.DAMAGE_FIRE) {
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
            return diff <= entity.getMaxFallHeight();
        }

        return Math.abs(diff) <= 1;
    }

    static boolean isSolid(IWorldReader world, @Nonnull BlockPos position) {
        if (getPathNodeType(world, position) == PathNodeType.LEAVES) {
            return true;
        }

        return world.getBlockState(position).isSolid();
    }

    static PathNodeType getPathNodeType(IWorldReader world, BlockPos blockPos) {
        return UnwalkableMarker.entity.getNavigator().getNodeProcessor().getPathNodeType(world, blockPos.getX(), blockPos.getY(), blockPos.getZ());
    }

}
