package e33.guardy.pathfinding.targetFinding;

import net.minecraft.util.math.BlockPos;

import java.util.List;

public class FullScouting extends AbstractTargetFinder {

    @Override
    public boolean targetFound() {
        return true;
    }

    @Override
    public List<BlockPos> getTargets() {
        return null;
    }
}
