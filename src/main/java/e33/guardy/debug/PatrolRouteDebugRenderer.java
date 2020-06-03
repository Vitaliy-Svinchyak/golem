package e33.guardy.debug;

import e33.guardy.entity.ShootyEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.pathfinding.Path;
import net.minecraft.util.math.BlockPos;

import java.util.List;

public class PatrolRouteDebugRenderer extends AbstractDebugRenderer {

    public PatrolRouteDebugRenderer(Minecraft minecraft) {
        super(minecraft);
    }

    @Override
    void renderEntities(List<ShootyEntity> entities) {
        for (ShootyEntity entity : entities) {
            if (entity.patrolVillageGoal != null && entity.patrolVillageGoal.patrolPoints != null && entity.patrolVillageGoal.pathParts != null) {
                this.renderRoutes(entity.patrolVillageGoal.patrolPoints, entity.patrolVillageGoal.angularPoints, entity.patrolVillageGoal.pathParts);
            }
        }
    }

    private void renderRoutes(List<BlockPos> patrolPoints, List<BlockPos> angularPoints, List<Path> pathParts) {
        ActiveRenderInfo activeRenderInfo = this.getActiveRenderInfo();
        double offsetX = activeRenderInfo.getProjectedView().x;
        double offsetY = activeRenderInfo.getProjectedView().y;
        double offsetZ = activeRenderInfo.getProjectedView().z;

        for (BlockPos pos : patrolPoints) {
            this.renderBlockWithColor(pos, Color.VILLAGE_BLACK, offsetX, offsetY, offsetZ);
        }

        for (Path path : pathParts) {
            List<BlockPos> pathPoints = this.turnPathToBlocksList(path);
            for (BlockPos pos : pathPoints) {
                this.renderBlockWithColor(pos, Color.PATH_GREEN, offsetX, offsetY, offsetZ);
            }

        }

        for (int i = 0; i < angularPoints.size(); i++) {
            BlockPos pos = angularPoints.get(i);
            this.renderBlockWithColorAndNumber(pos, Color.VILLAGE_RED, i + "", offsetX, offsetY, offsetZ);
        }
    }
}
