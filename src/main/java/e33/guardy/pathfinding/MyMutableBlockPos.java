package e33.guardy.pathfinding;

import net.minecraft.util.math.BlockPos;

public class MyMutableBlockPos extends BlockPos.MutableBlockPos {

    MyMutableBlockPos(BlockPos pos) {
        super(pos);
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
