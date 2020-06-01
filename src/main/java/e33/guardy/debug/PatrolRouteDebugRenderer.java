package e33.guardy.debug;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.blaze3d.platform.GlStateManager;
import e33.guardy.entity.ShootyEntity;
import e33.guardy.pathfinding.MyMutableBlockPos;
import e33.guardy.util.ToStringHelper;
import net.minecraft.block.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.debug.DebugRenderer;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Direction;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.IWorldReader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Map;

public class PatrolRouteDebugRenderer implements DebugRenderer.IDebugRenderer {
    final static Logger LOGGER = LogManager.getLogger();
    private final static List<ShootyEntity> entities = Lists.newArrayList();
    private static List<ShootyEntity> entitiesToAdd = Lists.newArrayList();
    private Map<String, Integer> topPositionCache = Maps.newHashMap();
    private Map<String, Boolean> allBlocks = Maps.newHashMap();
    private final Minecraft minecraft;

    public PatrolRouteDebugRenderer(Minecraft minecraft) {
        this.minecraft = minecraft;
    }

    public static void addEntity(ShootyEntity entity) {
        entitiesToAdd.add(entity);
    }

    private static void removeEntities(List<ShootyEntity> entitiesToRemove) {
        entities.removeAll(entitiesToRemove);
    }

    private static void addNewEntities() {
        entities.addAll(entitiesToAdd);
        entitiesToAdd = Lists.newArrayList();
    }

    @Override
    public void render(long l) {
        if (entities.size() == 0) {
            addNewEntities();
            return;
        }

        GlStateManager.pushMatrix();
        GlStateManager.enableBlend();
        GlStateManager.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        GlStateManager.color4f(0.0F, 1.0F, 0.0F, 0.75F);
        GlStateManager.disableTexture();
        GlStateManager.lineWidth(6.0F);

        List<ShootyEntity> entitiesToRemove = Lists.newArrayList();
        allBlocks = Maps.newHashMap();

        for (ShootyEntity entity : entities) {
            if (entity.isAlive()) {
                if (entity.patrolVillageGoal != null && entity.patrolVillageGoal.patrolPoints != null) {
                    this.renderRoutes(entity.patrolVillageGoal.patrolPoints, entity.getPosition().getY());
                }
            } else {
                entitiesToRemove.add(entity);
            }
        }

        removeEntities(entitiesToRemove);
        addNewEntities();

        GlStateManager.enableTexture();
        GlStateManager.disableBlend();
        GlStateManager.popMatrix();
    }

    private void renderRoutes(List<BlockPos> patrolPoints, int startY) {
        ActiveRenderInfo activeRenderInfo = this.getActiveRenderInfo();
        double offsetX = activeRenderInfo.getProjectedView().x;
        double offsetY = activeRenderInfo.getProjectedView().y;
        double offsetZ = activeRenderInfo.getProjectedView().z;

        for (BlockPos pos : patrolPoints) {
            this.renderBlockWithColorAndNumber(pos, Color.VILLAGE_BLACK, offsetX, offsetY, offsetZ);
        }
    }

    private void renderBlockWithColorAndNumber(BlockPos block, Color color, double offsetX, double offsetY, double offsetZ) {
        BlockState state = this.minecraft.world.getBlockState(block.down());
        double topY = block.getY() - 1 + state.getShape(this.minecraft.world, block).getEnd(Direction.Axis.Y);

        DebugRenderer.func_217730_a(
                (
                        new AxisAlignedBB(
                                block.getX() + 0.01,
                                block.getY() - 1.01,
                                block.getZ() + 0.01,
                                block.getX() + 0.99,
                                topY + 0.01,
                                block.getZ() + 0.99
                        )
                ).offset(-offsetX, -offsetY, -offsetZ),
                color.red,
                color.green,
                color.blue,
                color.alpha
        );
    }

    private ActiveRenderInfo getActiveRenderInfo() {
        return this.minecraft.gameRenderer.getActiveRenderInfo();
    }

    protected int getTopPosition(IWorldReader world, int x, int y, int z) {
        String cacheKey = ToStringHelper.toString(x, y, z);
        if (this.topPositionCache.get(cacheKey) != null) {
            return this.topPositionCache.get(cacheKey);
        }
        BlockPos position = new MyMutableBlockPos(x, y, z);

        if (isSolid(world, position)) {
            while (isSolid(world, position)) {
                position = position.up();
            }
        } else {
            while (!isSolid(world, position)) {
                position = position.down();
            }
            position = position.up();
        }

        this.topPositionCache.put(cacheKey, position.getY());

        return position.getY();
    }

    protected boolean isSolid(IWorldReader world, @Nonnull BlockPos position) {
        BlockState state = world.getBlockState(position);
        if (state.isAir(world, position)) {
            return false;
        }

        if (world.getFluidState(position).isTagged(FluidTags.WATER)) {
            return true;
        }

        Block block = state.getBlock();
        if (block instanceof LeavesBlock
                || block == Blocks.LILY_PAD
                || block instanceof GlassBlock
                || block instanceof BedBlock
                || block instanceof ShulkerBoxBlock
                || block instanceof HopperBlock
                || block instanceof TrapDoorBlock
                || block instanceof FlowerPotBlock
                || block instanceof LanternBlock
        ) {
            return true;
        }

        return state.isSolid();
    }
}
