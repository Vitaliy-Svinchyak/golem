package e33.guardy.pathfinding;

import net.minecraft.util.math.BlockPos;

public class MyMutableBlockPos extends BlockPos.MutableBlockPos {

    MyMutableBlockPos(BlockPos pos) {
        super(pos);
    }

    MyMutableBlockPos(int x, int y, int z) {
        super(x, y, z);
    }

    public MutableBlockPos up() {
        this.y++;
        return this;
    }

    public MutableBlockPos down() {
        this.y--;
        return this;
    }
}
