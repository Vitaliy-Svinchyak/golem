package e33.guardy.pathfinding;

import net.minecraft.entity.MobEntity;
import net.minecraft.pathfinding.GroundPathNavigator;
import net.minecraft.pathfinding.PathFinder;
import net.minecraft.world.World;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DangerousZoneAvoidanceNavigator extends GroundPathNavigator {
    final static Logger LOGGER = LogManager.getLogger();

    public DangerousZoneAvoidanceNavigator(MobEntity entitylivingIn, World worldIn) {
        super(entitylivingIn, worldIn);
    }

    protected PathFinder getPathFinder(int p_179679_1_) {
        this.nodeProcessor = new CarefulWalkNodeProcessor();
        this.nodeProcessor.setCanOpenDoors(true);
        this.nodeProcessor.setCanEnterDoors(true);
        return new PathFinder(this.nodeProcessor, p_179679_1_);
    }
}