package e33.guardy.debug;

public enum Color {
    DANGEROUS_ZONE_VIOLET(0.53F, 0.01F, 0.94F, 0.33F),
    DANGEROUS_ZONE_RED(0.81F, 0.09F, 0.21F, 0.33F),
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
