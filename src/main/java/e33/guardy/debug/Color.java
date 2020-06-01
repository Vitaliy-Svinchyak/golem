package e33.guardy.debug;

import com.google.common.collect.Lists;

import java.util.List;

public enum Color {
    SHOOTY(0.02F, 0.49F, 0.63F, 1F),
    VILLAGE_BLACK(0F, 0F, 0F, 1F),
    VILLAGE_RED(0.81F, 0.09F, 0.21F, 1F),
    SAFE_GREEN(0F, 0.45F, 0.35F, 1F),
    PATH_GREEN(0F, 1F, 0F, 1F),
    ROUTE_VIOLET(0.53F, 0.01F, 0.94F, 1F),
    DANGEROUS_ZONE_RED(0.81F, 0.09F, 0.21F, 1F),
    DANGEROUS_ZONE_ORANGE(0.80F, 0.50F, 0.15F, 1F),
    DANGEROUS_ZONE_YELLOW(0.89F, 0.89F, 0.07F, 1F);

    public final float red;
    public final float green;
    public final float blue;
    public final float alpha;

    Color(float red, float green, float blue, float alpha) {
        this.red = red;
        this.green = green;
        this.blue = blue;
        this.alpha = alpha;
    }

    static List<Float> RANDOM() {
        return Lists.newArrayList((float) Math.random(), (float) Math.random(), (float) Math.random(), 0.5F);
    }

    public List<Float> toArray() {
        return Lists.newArrayList(this.red, this.green, this.blue, this.alpha);
    }
}
