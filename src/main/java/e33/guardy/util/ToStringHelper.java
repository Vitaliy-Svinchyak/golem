package e33.guardy.util;

import net.minecraft.util.math.BlockPos;

public class ToStringHelper {
    public static String toString(BlockPos pos) {
        return pos.getX() + "," + pos.getY() + "," + pos.getZ();
    }
}
