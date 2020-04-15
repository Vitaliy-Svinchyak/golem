package e33.guardy.client.animation.animated.item;

import e33.guardy.client.detail.item.Rotation;
import e33.guardy.client.detail.item.Translation;

import java.util.Arrays;
import java.util.List;

public class DefaultWeaponPosition {
    private static List<Rotation> rotations = Arrays.asList(
            new Rotation(-80F, 1.0F, 0.0F, 0.0F),
            new Rotation(-70F, 0.0F, 1.0F, 0.0F),
            new Rotation(-90F, 0.0F, 0.0F, 1.0F),

            new Rotation(-20F, 0.0F, 1.0F, 0.0F)
    );

    private static List<Translation> translations = Arrays.asList(
            // x - righ and left; y - up(-) and bottom(+); z - back(+) and forth(-)
            new Translation(3.3F, 0.65F, -0.45F)
    );

    public static List<Rotation> getRotations() {
        return rotations;
    }

    public static List<Translation> getTranslations() {
        return translations;
    }
}
