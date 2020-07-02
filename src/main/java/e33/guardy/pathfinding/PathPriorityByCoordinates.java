package e33.guardy.pathfinding;

import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.util.math.BlockPos;

public interface PathPriorityByCoordinates {
    public float getPathPriority(PathNodeType nodeType, BlockPos position);
}
