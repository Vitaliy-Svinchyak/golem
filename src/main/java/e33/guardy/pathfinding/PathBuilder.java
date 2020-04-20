package e33.guardy.pathfinding;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import e33.guardy.entity.ShootyEntity;
import net.minecraft.block.SnowBlock;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.pathfinding.NodeProcessor;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorldReader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

public class PathBuilder {
    final static Logger LOGGER = LogManager.getLogger();

    private final ShootyEntity shooty;
    private final NodeProcessor nodeProcessor;
    public List<BlockPos> unwalkableBlocks = Lists.newArrayList();
    public List<List<BlockPos>> checkingRoutes = Lists.newArrayList();
    public Map<BlockPos, Map<UUID, Integer>> routes = Maps.newHashMap();
    private BlockPos lastPos;

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
        AxisAlignedBB zone = this.shooty.getBoundingBox().grow(Math.round(this.shooty.getAttribute(SharedMonsterAttributes.FOLLOW_RANGE).getValue() / 2));
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
        for (MobEntity enemy : enemies) {
            this.setRoutes(enemy.getUniqueID(), enemyPoints.get(enemy.getUniqueID()), 0, localRoutes);
        }

        int iteration = 0;
        while (points.size() > 0) {
            List<BlockPos> tempPoints = this.getNewWave(points, world, zone, usedCoors, cantGo);
            localCheckingRoutes.add(tempPoints);
            points = tempPoints;
            this.setRoutes(this.shooty.getUniqueID(), tempPoints, iteration, localRoutes);

            for (MobEntity enemy : enemies) {
                UUID uid = enemy.getUniqueID();
                List<BlockPos> tempPointsForEnemy = this.getNewWave(enemyPoints.get(uid), world, zone, enemyUsedCoors.get(uid), null);
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
    }

    protected void setRoutes(UUID uid, List<BlockPos> points, int iteration, Map<BlockPos, Map<UUID, Integer>> routes) {
        for (BlockPos point : points) {
            routes.computeIfAbsent(point, k -> Maps.newHashMap());
            routes.get(point).put(uid, iteration);
        }
    }

    protected List<BlockPos> getNewWave(List<BlockPos> points, IWorldReader world, AxisAlignedBB zone, Map<String, Boolean> usedCoors, List<BlockPos> cantGo) {
        List<BlockPos> tempPoints = Lists.newArrayList();
        for (BlockPos point : points) {
            List<BlockPos> vars = getVariants(world, point, zone, usedCoors, cantGo);

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
            enemyPoints.put(enemy.getUniqueID(), Lists.newArrayList(getTopPosition(world, enemy.getPosition(), true)));
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

    protected List<BlockPos> getVariants(IWorldReader world, BlockPos start, AxisAlignedBB zone, Map<String, Boolean> usedCoors, @Nullable List<BlockPos> cantGo) {
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
                    if (usedCoors.get(variant.toString()) == null
                            && variant.getX() >= zone.minX - 1 && variant.getX() <= zone.maxX
                            && variant.getZ() >= zone.minZ - 1 && variant.getZ() <= zone.maxZ
                            && variant.getY() >= zone.minY / 1.5 && variant.getY() <= zone.maxY * 1.5
                    ) {
                        if (canWalkFromTo(world, start, variant)) {
                            // check walls between
                            if (variant.getX() != start.getX() && variant.getZ() != start.getZ()) {
                                BlockPos toCheckWall = getTopPosition(world, new BlockPos(variant.getX(), start.getY(), start.getZ()));
                                BlockPos toCheckWall2 = getTopPosition(world, new BlockPos(start.getX(), start.getY(), variant.getZ()));

                                if (toCheckWall.getY() - start.getY() <= this.shooty.stepHeight && toCheckWall2.getY() - start.getY() <= this.shooty.stepHeight) {
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

    protected BlockPos getTopPosition(IWorldReader world, @Nonnull BlockPos position, boolean onlyDown) {
        while (!isSolid(world, position)) {
            position = position.down();
        }
        position = position.up();

        return position;
    }

    protected BlockPos getTopPosition(IWorldReader world, @Nonnull BlockPos position) {
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

    protected boolean canWalkFromTo(IWorldReader world, BlockPos start, BlockPos end) {
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
            return diff <= this.shooty.getMaxFallHeight();
        } else if (start.getY() < end.getY()) {
            if (getPathNodeType(world, start.up(2)) != PathNodeType.OPEN) {
                return false;
            }

            return Math.abs(diff) <= this.shooty.stepHeight;
        }

        return true;
    }

    protected boolean isSolid(IWorldReader world, @Nonnull BlockPos position) {
        if (getPathNodeType(world, position) == PathNodeType.LEAVES) {
            return true;
        }

        return world.getBlockState(position).isSolid();
    }

    protected PathNodeType getPathNodeType(IWorldReader world, BlockPos blockPos) {
        return nodeProcessor.getPathNodeType(world, blockPos.getX(), blockPos.getY(), blockPos.getZ());
    }
}
