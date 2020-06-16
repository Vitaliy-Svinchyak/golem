package e33.guardy.debug;

import e33.guardy.entity.ShootyEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.util.math.BlockPos;

import java.util.List;
import java.util.Map;

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

    private void renderTrajectories(Map<BlockPos, Integer> allBlocksOnWay) {
        for (BlockPos pos : allBlocksOnWay.keySet()) {
            String tick = allBlocksOnWay.get(pos) + "";
            this.renderAirBlockWithColorAndText(pos, Color.TRAJECTORY_VIOLET, tick);
        }
    }
}
