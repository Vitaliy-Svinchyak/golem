package com.e33.goal.move;

import com.e33.util.Helper;
import com.google.common.collect.Lists;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;

import java.util.List;
import java.util.UUID;

public class DangerousZone {

    final private LivingEntity entity;
    final private int violetRadius;
    final private int redRadius;
    final private int yellowRadius;

    private List<BlockPos> cachedCenterBocks = null;
    private List<BlockPos> cachedVioletBocks = null;
    private List<BlockPos> cachedRedBocks = null;
    private List<BlockPos> cachedYellowBocks = null;

    public DangerousZone(LivingEntity entity, int violetRadius, int redRadius, int yellowRadius) {
        this.entity = entity;
        this.violetRadius = violetRadius;
        this.redRadius = redRadius;
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
        this.cachedVioletBocks = null;
        this.cachedRedBocks = null;
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

    public List<BlockPos> getVioletBlocksPos() {
        if (this.cachedVioletBocks == null) {
            this.cachedVioletBocks = this.createBlocksPos(this.violetRadius, this.getCenterBlocksPos(), this.getCenterBlocksPos());
        }

        return this.cachedVioletBocks;
    }

    public List<BlockPos> getRedBlocksPos() {
        if (this.cachedRedBocks == null) {
            List<BlockPos> violetBlocks = this.getVioletBlocksPos();
            List<BlockPos> whereToCheck = Helper.concatLists(this.getCenterBlocksPos(), violetBlocks);

            this.cachedRedBocks = this.createBlocksPos(this.redRadius, violetBlocks, whereToCheck);
        }

        return this.cachedRedBocks;
    }

    public List<BlockPos> getYellowBlocksPos() {
        if (this.cachedYellowBocks == null) {
            List<BlockPos> redBlocks = this.getRedBlocksPos();
            List<BlockPos> whereToCheck = Helper.concatLists(this.getCenterBlocksPos(), this.getVioletBlocksPos());
            whereToCheck = Helper.concatLists(whereToCheck, redBlocks);

            this.cachedYellowBocks = this.createBlocksPos(this.yellowRadius, redBlocks, whereToCheck);
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
