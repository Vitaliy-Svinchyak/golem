package e33.guardy.pathfinding;

public class MovementLimitations {
    public float modelWidth;
    public float modelHeight;
    public float speed;
    public float jumHeight;
    public int maxFallHeight;
    public boolean canSwim;
    public boolean canClimb;
    public boolean canOpenDoors;
    public boolean canEnterDoors;

    public MovementLimitations(float jumHeight, int maxFallHeight, float modelHeight) {
        this.jumHeight = jumHeight;
        this.maxFallHeight = maxFallHeight;
        this.modelHeight = modelHeight;
    }
}
