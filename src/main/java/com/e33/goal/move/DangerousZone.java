package com.e33.goal.move;

import com.google.common.collect.Lists;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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

    public List<BlockPos> getRedBlocksPos() {
        if (this.cachedVioletBocks == null) {
            this.cachedVioletBocks = this.createBlocksPos(this.violetRadius, this.getCenterBlocksPos(), this.getCenterBlocksPos());
        }

        return this.cachedVioletBocks;
    }

    public List<BlockPos> getOrangeBlocksPos() {
        if (this.cachedRedBocks == null) {
            List<BlockPos> redBlocks = this.getRedBlocksPos();
            List<BlockPos> whereToCheck = this.concatLists(this.getCenterBlocksPos(), redBlocks);

            this.cachedRedBocks = this.createBlocksPos(this.redRadius, redBlocks, whereToCheck);
        }

        return this.cachedRedBocks;
    }

    public List<BlockPos> getYellowBlocksPos() {
        if (this.cachedYellowBocks == null) {
            List<BlockPos> orangeBlocks = this.getOrangeBlocksPos();
            List<BlockPos> whereToCheck = this.concatLists(this.getCenterBlocksPos(), this.getRedBlocksPos());
            whereToCheck = this.concatLists(whereToCheck, orangeBlocks);

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
            blocksPos = this.concatLists(blocksPos, currentCircle);
            whereToCheck = this.concatLists(whereToCheck, currentCircle);

        }

        return blocksPos;
    }

    private void addUniqueBlockPos(List<BlockPos> whereToAdd, List<BlockPos> whereToCheck, BlockPos block) {
        if (!whereToAdd.contains(block) && !whereToCheck.contains(block)) {
            whereToAdd.add(block);
        }
    }

    private List<BlockPos> concatLists(List<BlockPos> list1, List<BlockPos> list2) {
        return Stream.concat(list1.stream(), list2.stream())
                .collect(Collectors.toList());
    }
}
