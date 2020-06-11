package e33.guardy.debug;

import e33.guardy.entity.ShootyEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.util.math.BlockPos;

import java.util.List;

public class AvoidBulletDebugRenderer extends AbstractDebugRenderer {
    public AvoidBulletDebugRenderer(Minecraft minecraft) {
        super(minecraft);
    }

    @Override
    void renderEntities(List<ShootyEntity> entities) {
        for (ShootyEntity entity : entities) {
            if (entity.avoidBulletGoal != null && entity.avoidBulletGoal.allBlocksOnWay != null) {
                this.renderTrajectories(entity.avoidBulletGoal.allBlocksOnWay);
            }
        }
    }

    private void renderTrajectories(List<BlockPos> allBlocksOnWay) {
        for (BlockPos pos : allBlocksOnWay) {
            this.renderAirBlockWithColor(pos, Color.TRAJECTORY_VIOLET);
        }
    }
}
