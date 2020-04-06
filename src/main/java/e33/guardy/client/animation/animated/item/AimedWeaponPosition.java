package e33.guardy.client.animation.animated.item;

import e33.guardy.client.detail.item.Rotation;
import e33.guardy.client.detail.item.Translation;

import java.util.Arrays;
import java.util.List;

public class AimedWeaponPosition {
    private static List<Rotation> rotations = Arrays.asList(
            new Rotation(-85F, 1.0F, 0.0F, 0.0F),// tail
            new Rotation(90F, 0.0F, 1.0F, 0.0F),
            new Rotation(-95F, 0.0F, 0.0F, 1.0F),

            new Rotation(-80F, 0.0F, 1.0F, 0.0F) // around axis
    );

    private static List<Translation> translations = Arrays.asList(
            new Translation(-0.3F, 0.35F, -0.65F)
    );

    public static List<Rotation> getRotations() {
        return rotations;
    }

    public static List<Translation> getTranslations() {
        return translations;
    }
}
