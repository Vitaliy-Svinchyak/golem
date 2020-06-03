package e33.guardy.pathfinding.pathBuilding;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import e33.guardy.pathfinding.MovementLimitations;
import e33.guardy.pathfinding.StepHistoryKeeper;
import e33.guardy.pathfinding.TreeLeaf;
import e33.guardy.pathfinding.targetFinding.ITargetFinder;
import e33.guardy.util.ToStringHelper;
import net.minecraft.pathfinding.Path;
import net.minecraft.pathfinding.PathPoint;
import net.minecraft.util.math.BlockPos;

import java.util.*;
import java.util.stream.Collectors;

public class SafePathBuilder implements IPathBuilder {

    private ITargetFinder finder;
    private Collection<ITargetFinder> enemyScouts;

    public SafePathBuilder(Collection<ITargetFinder> enemyScouts) {
        this.enemyScouts = enemyScouts;
    }

    @Override
    public Path build(MovementLimitations limitations, ITargetFinder finder) {
        this.finder = finder;

        List<BlockPos> safePoints = finder.getTargets();

        int maxEnemiesOnPoint = 0;
        int enemiesCount = 10; // TODO
        while (maxEnemiesOnPoint < enemiesCount) {
            Path path = this.buildPathWithMaxEnemiesOnPoint(maxEnemiesOnPoint, limitations, safePoints);

            if (path != null) {
                return path;
            }

            maxEnemiesOnPoint++;
        }

        return null;
    }

    private Path buildPathWithMaxEnemiesOnPoint(int maxEnemies, MovementLimitations limitations, List<BlockPos> safePoints) {
        StepHistoryKeeper stepHistory = this.finder.getStepHistory();
        List<TreeLeaf> leafs = stepHistory.getStepPositions(0).stream().map(b -> this.calculateTreeLeaf(b, null)).collect(Collectors.toList());
        Map<String, Boolean> visitedPoints = Maps.newHashMap();
        List<TreeLeaf> safeLeafs = Lists.newArrayList();
        Iterator<Integer> stepIterator = stepHistory.getStepNumbers().iterator();
        stepIterator.next(); // skipping 0 step

        while (stepIterator.hasNext()) {
            int stepNumber = stepIterator.next();
            List<TreeLeaf> currentLeafs = Lists.newArrayList();

            for (TreeLeaf leaf : leafs) {
                List<BlockPos> newSteps = this.getNextStepsFromCurrentPosition(
                        leaf.getBlockPos(),
                        // TODO maybe allow intersections, but optimize leafs on intersections(select safer etc)
                        this.filterByVisitedPoints(stepHistory.getStepPositions(stepNumber), visitedPoints),
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

            if (leafs.size() == 0) {
                break;
            }
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


    private List<TreeLeaf> getLeafsWithMaxEnemies(List<BlockPos> points, int maxEnemiesOnPoint, TreeLeaf parent) {
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

    private TreeLeaf calculateTreeLeaf(BlockPos position, TreeLeaf parent) {
        int fasterEnemiesCount = 0;
        int shootySpeed = this.finder.getStepHistory().getPositionStep(position);
        int fastestEnemySpeed = Integer.MAX_VALUE;
        String positionKey = ToStringHelper.toString(position);

        for (ITargetFinder enemyScout : this.enemyScouts) {
            Integer enemySpeed = enemyScout.getStepHistory().getPositionStep(positionKey);

            if (enemySpeed != null && enemySpeed <= shootySpeed) {
                if (enemySpeed < fastestEnemySpeed) {
                    fastestEnemySpeed = enemySpeed;
                }
                fasterEnemiesCount++;
            }
        }

        if (parent == null) {
            return new TreeLeaf(position, fasterEnemiesCount, fastestEnemySpeed);
        }

        return new TreeLeaf(position, parent.enemiesCount + fasterEnemiesCount, parent.totalEnemySpeed + fastestEnemySpeed);
    }

    private Path createPathFromTree(TreeLeaf leaf) {
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

    private List<BlockPos> getNextStepsFromCurrentPosition(BlockPos currentPosition, List<BlockPos> nextStepPoints, MovementLimitations limitations) {
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
