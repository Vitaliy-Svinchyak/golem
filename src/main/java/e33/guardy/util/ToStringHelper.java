package e33.guardy.util;

import net.minecraft.util.math.BlockPos;

public class ToStringHelper {
    public static String toString(BlockPos pos) {
        return pos.getX() + "," + pos.getY() + "," + pos.getZ();
    }

    public static String toString(int x, int y, int z) {
        return x + "," + y + "," + z;
    }

    public static int toInt(int x, int y, int z) {
        return toString(x, y, z).hashCode();
    }

    public static int toInt(BlockPos pos) {
        return toString(pos).hashCode();
    }

    static final int hash(Object key) {
        int h;
        return (key == null) ? 0 : (h = key.hashCode()) ^ (h >>> 16);
    }
}
