package e33.guardy.goal.move;

import e33.guardy.util.Helper;
import com.google.common.collect.Lists;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;

import java.util.List;
import java.util.UUID;

public class DangerousZone {

    final private LivingEntity entity;
    final private int redRadius;
    final private int orangeRadius;
    final private int yellowRadius;

    private List<BlockPos> cachedCenterBocks = null;
    private List<BlockPos> cachedRedBlocks = null;
    private List<BlockPos> cachedOrangeBlocks = null;
    private List<BlockPos> cachedYellowBocks = null;

    public DangerousZone(LivingEntity entity, int redRadius, int orangeRadius, int yellowRadius) {
        this.entity = entity;
        this.redRadius = redRadius;
        this.orangeRadius = orangeRadius;
        this.yellowRadius = yellowRadius;
    }

    public boolean entityIsAlive() {
        return this.entity.isAlive();
    }

    public UUID getEntityUniqueId() {
        return this.entity.getUniqueID();
    }

    public void clearCache() {
        this.cachedCenterBocks = null;
        this.cachedRedBlocks = null;
        this.cachedOrangeBlocks = null;
        this.cachedYellowBocks = null;
    }

    public List<BlockPos> getCenterBlocksPos() {
        if (this.cachedCenterBocks != null) {
            return this.cachedCenterBocks;
        }

        List<BlockPos> centerBlocks = Lists.newArrayList();
        AxisAlignedBB boundingBox = this.entity.getBoundingBox();

        // TODO what if bigger then 2x2. Or just 1x1?
        centerBlocks.add(new BlockPos(boundingBox.minX, this.entity.posY, boundingBox.minZ));
        centerBlocks.add(new BlockPos(boundingBox.minX, this.entity.posY, boundingBox.maxZ));
        centerBlocks.add(new BlockPos(boundingBox.maxX, this.entity.posY, boundingBox.minZ));
        centerBlocks.add(new BlockPos(boundingBox.maxX, this.entity.posY, boundingBox.maxZ));

        this.cachedCenterBocks = centerBlocks;

        return centerBlocks;
    }

    public List<BlockPos> getRedBlocks() {
        if (this.cachedRedBlocks == null) {
            this.cachedRedBlocks = this.createBlocksPos(this.redRadius, this.getCenterBlocksPos(), this.getCenterBlocksPos());
        }

        return this.cachedRedBlocks;
    }

    public List<BlockPos> getOrangeBlocks() {
        if (this.cachedOrangeBlocks == null) {
            List<BlockPos> redBlocks = this.getRedBlocks();
            List<BlockPos> whereToCheck = Helper.concatLists(this.getCenterBlocksPos(), redBlocks);

            this.cachedOrangeBlocks = this.createBlocksPos(this.orangeRadius, redBlocks, whereToCheck);
        }

        return this.cachedOrangeBlocks;
    }

    public List<BlockPos> getYellowBlocksPos() {
        if (this.cachedYellowBocks == null) {
            List<BlockPos> orangeBlocks = this.getOrangeBlocks();
            List<BlockPos> whereToCheck = Helper.concatLists(this.getCenterBlocksPos(), this.getRedBlocks());
            whereToCheck = Helper.concatLists(whereToCheck, orangeBlocks);

            this.cachedYellowBocks = this.createBlocksPos(this.yellowRadius, orangeBlocks, whereToCheck);
        }

        return this.cachedYellowBocks;
    }

    private List<BlockPos> createBlocksPos(int radius, List<BlockPos> previousBlocks, List<BlockPos> whereToCheck) {
        if (radius == 0) {
            return Lists.newArrayList();
        }

        List<BlockPos> blocksPos = Lists.newArrayList();

        for (int currentRadius = 1; currentRadius <= radius; currentRadius++) {
            List<BlockPos> currentCircle = Lists.newArrayList();
            for (BlockPos previousBlock : previousBlocks) {
                // todo use previously created blocks
                this.addUniqueBlockPos(currentCircle, whereToCheck, new BlockPos(previousBlock.getX() + 1, previousBlock.getY(), previousBlock.getZ()));
                this.addUniqueBlockPos(currentCircle, whereToCheck, new BlockPos(previousBlock.getX() + 1, previousBlock.getY(), previousBlock.getZ() + 1));
                this.addUniqueBlockPos(currentCircle, whereToCheck, new BlockPos(previousBlock.getX(), previousBlock.getY(), previousBlock.getZ() + 1));
                this.addUniqueBlockPos(currentCircle, whereToCheck, new BlockPos(previousBlock.getX() - 1, previousBlock.getY(), previousBlock.getZ() + 1));
                this.addUniqueBlockPos(currentCircle, whereToCheck, new BlockPos(previousBlock.getX() - 1, previousBlock.getY(), previousBlock.getZ()));
                this.addUniqueBlockPos(currentCircle, whereToCheck, new BlockPos(previousBlock.getX() - 1, previousBlock.getY(), previousBlock.getZ() - 1));
                this.addUniqueBlockPos(currentCircle, whereToCheck, new BlockPos(previousBlock.getX(), previousBlock.getY(), previousBlock.getZ() - 1));
                this.addUniqueBlockPos(currentCircle, whereToCheck, new BlockPos(previousBlock.getX() + 1, previousBlock.getY(), previousBlock.getZ() - 1));
            }
            previousBlocks = currentCircle;
            blocksPos = Helper.concatLists(blocksPos, currentCircle);
            whereToCheck = Helper.concatLists(whereToCheck, currentCircle);

        }

        return blocksPos;
    }

    private void addUniqueBlockPos(List<BlockPos> whereToAdd, List<BlockPos> whereToCheck, BlockPos block) {
        if (!whereToAdd.contains(block) && !whereToCheck.contains(block)) {
            whereToAdd.add(block);
        }
    }
}
