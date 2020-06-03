package e33.guardy.pathfinding.pathBuilding;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import e33.guardy.pathfinding.MovementLimitations;
import e33.guardy.pathfinding.StepHistoryKeeper;
import e33.guardy.pathfinding.leafs.DangerousTreeLeaf;
import e33.guardy.pathfinding.targetFinding.ITargetFinder;
import e33.guardy.util.ToStringHelper;
import net.minecraft.pathfinding.Path;
import net.minecraft.util.math.BlockPos;

import java.util.*;
import java.util.stream.Collectors;

public class SafePathBuilder extends AbstractPathBuilder implements IPathBuilder {

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
        int enemiesCount = this.enemyScouts.size();

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
        List<DangerousTreeLeaf> leafs = stepHistory.getStepPositions(0).stream().map(b -> this.calculateTreeLeaf(b, null)).collect(Collectors.toList());
        Map<String, Boolean> visitedPoints = Maps.newHashMap();
        List<DangerousTreeLeaf> safeLeafs = Lists.newArrayList();
        Iterator<Integer> stepIterator = stepHistory.getStepNumbers().iterator();
        stepIterator.next(); // skipping 0 step

        while (stepIterator.hasNext()) {
            int stepNumber = stepIterator.next();
            List<DangerousTreeLeaf> currentLeafs = Lists.newArrayList();

            for (DangerousTreeLeaf leaf : leafs) {
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

                List<DangerousTreeLeaf> newLeafs = this.getLeafsWithMaxEnemies(newSteps, maxEnemies, leaf);
                if (newLeafs.size() != 0) {
                    for (DangerousTreeLeaf child : newLeafs) {
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

        DangerousTreeLeaf safestLeaf = safeLeafs.get(0);
        for (DangerousTreeLeaf leaf : safeLeafs) {
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

    private List<DangerousTreeLeaf> getLeafsWithMaxEnemies(List<BlockPos> points, int maxEnemiesOnPoint, DangerousTreeLeaf parent) {
        List<DangerousTreeLeaf> filteredPoints = Lists.newArrayList();

        for (BlockPos point : points) {
            DangerousTreeLeaf leaf = this.calculateTreeLeaf(point, parent);
            // TODO can cache to not recalculate each time the same points
            if (parent.enemiesCount - leaf.enemiesCount <= maxEnemiesOnPoint) {
                filteredPoints.add(leaf);
            }
        }

        return filteredPoints;
    }

    private DangerousTreeLeaf calculateTreeLeaf(BlockPos position, DangerousTreeLeaf parent) {
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
            return new DangerousTreeLeaf(position, fasterEnemiesCount, fastestEnemySpeed);
        }

        return new DangerousTreeLeaf(position, parent.enemiesCount + fasterEnemiesCount, parent.totalEnemySpeed + fastestEnemySpeed);
    }
}
