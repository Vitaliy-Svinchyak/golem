package e33.guardy.pathfinding.pathBuilding;

import e33.guardy.pathfinding.MovementLimitations;
import e33.guardy.pathfinding.targetFinding.ITargetFinder;
import net.minecraft.pathfinding.Path;

public interface IPathBuilder {

    public Path build(MovementLimitations limitations, ITargetFinder finder);
}
