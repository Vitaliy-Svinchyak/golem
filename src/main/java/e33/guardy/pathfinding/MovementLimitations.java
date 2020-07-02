package e33.guardy.pathfinding;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;

public class MovementLimitations {
    public final LivingEntity entity;
    public final float modelWidth;
    public final float modelHeight;
    public float speed;
    public final float jumHeight;
    public final int maxFallHeight;
    public boolean canSwim;
    public boolean canClimb;
    public boolean canOpenDoors;
    public boolean canEnterDoors;

    public MovementLimitations(float jumHeight, int maxFallHeight, float modelHeight, float modelWidth, boolean canSwim, MobEntity entity) {
        this.jumHeight = jumHeight;
        this.maxFallHeight = maxFallHeight;
        this.modelHeight = modelHeight;
        this.modelWidth = modelWidth;
        this.canSwim = canSwim;
        this.entity = entity;
    }

    @Override
    public String toString() {
        return "MovementLimitations { " + "jumHeight: " + jumHeight + " maxFallHeight: " + maxFallHeight + " modelHeight: " + modelHeight + " modelWidth: " + modelWidth + " canSwim: " + canSwim + " }";
    }
}
