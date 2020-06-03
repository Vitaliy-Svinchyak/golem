package e33.guardy.pathfinding.leafs;

import net.minecraft.util.math.BlockPos;

public class DangerousTreeLeaf extends TreeLeaf {
    public final int enemiesCount;
    public final int totalEnemySpeed;

    public DangerousTreeLeaf(BlockPos blockPos, int enemiesCount, int totalEnemySpeed) {
        super(blockPos);
        this.enemiesCount = enemiesCount;
        this.totalEnemySpeed = totalEnemySpeed;
    }

    public DangerousTreeLeaf getParent() {
        return (DangerousTreeLeaf) super.getParent();
    }
}
