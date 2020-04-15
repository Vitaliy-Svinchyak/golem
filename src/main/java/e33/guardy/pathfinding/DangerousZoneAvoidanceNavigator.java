package e33.guardy.pathfinding;

import net.minecraft.entity.MobEntity;
import net.minecraft.pathfinding.GroundPathNavigator;
import net.minecraft.pathfinding.PathFinder;
import net.minecraft.world.World;

public class DangerousZoneAvoidanceNavigator extends GroundPathNavigator {
    public DangerousZoneAvoidanceNavigator(MobEntity entitylivingIn, World worldIn) {
        super(entitylivingIn, worldIn);
    }

    protected PathFinder getPathFinder(int p_179679_1_) {
        this.nodeProcessor = new CarefulWalkNodeProcessor();
        this.nodeProcessor.setCanEnterDoors(true);
        return new PathFinder(this.nodeProcessor, p_179679_1_);
    }
}