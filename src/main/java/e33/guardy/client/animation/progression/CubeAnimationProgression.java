package e33.guardy.client.animation.progression;

import e33.guardy.client.detail.modelBox.ModelBoxParameters;
import net.minecraft.client.renderer.entity.model.RendererModel;
import net.minecraft.client.renderer.model.ModelBox;

import java.lang.reflect.Field;
import java.util.List;

public class CubeAnimationProgression extends AnimationProgression {

    private int cubeNumber;
    private ModelBoxParameters originalModelBoxParameters;

    CubeAnimationProgression(RendererModel rendererModel, List<Float> xProgression, List<Float> yProgression, List<Float> zProgression, ProgressionType progressionType, int cubeNumber, ModelBoxParameters originalModelBoxParameters) {
        super(rendererModel, xProgression, yProgression, zProgression, progressionType);
        LOGGER.error("CUBES CHANGED!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");

        this.cubeNumber = cubeNumber;
        this.originalModelBoxParameters = originalModelBoxParameters;
    }

    protected void progress(float newX, float newY, float newZ) {
        switch (this.progressionType) {
            case ModelBoxPosition:
                ModelBoxParameters p = this.originalModelBoxParameters;
                ModelBox newCube = new ModelBox(p.model, p.textureOffsetX, p.textureOffsetY, newX, newY, newZ, p.width, p.height, p.depth, p.scaleFactor, p.mirrored);
                this.rendererModel.cubeList.set(this.cubeNumber, newCube);
                this.setCubesNotCompiled();
                break;
        }
    }

    private void setCubesNotCompiled() {
        try {
            Field f = this.rendererModel.getClass().getDeclaredField("compiled"); //NoSuchFieldException
            f.setAccessible(true);
            f.set(this.rendererModel, false); //IllegalAccessException

        } catch (ReflectiveOperationException e) {
            LOGGER.error(e.getMessage());
        }
    }
}
