package com.e33.debug;

public enum Color {
    DANGEROUS_ZONE_RED(0.81F, 0.09F, 0.21F, 0.33F),
    DANGEROUS_ZONE_ORANGE(0.80F, 0.50F, 0.15F, 0.33F),
    DANGEROUS_ZONE_YELLOW(0.89F, 0.89F, 0.07F, 0.33F);

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
}
