package e33.guardy.util;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;

public class ToStringHelper {
    public static String toString(BlockPos pos) {
        return pos.getX() + "," + pos.getY() + "," + pos.getZ();
    }

    public static String toString(int x, int y, int z) {
        return x + "," + y + "," + z;
    }

    public static String toString(ChunkPos pos) {
        return pos.x + "," + pos.z;
    }

    public static String toString(int x, int z) {
        return x + "," + z;
    }
}
