package e33.guardy.pathfinding.leafs;

import com.google.common.collect.Lists;
import net.minecraft.util.math.BlockPos;

import java.util.List;

public class TreeLeaf {
    private final BlockPos blockPos;
    private TreeLeaf parent;
    private List<TreeLeaf> children = Lists.newArrayList();
    private boolean alive = true;

    public TreeLeaf(BlockPos blockPos) {
        this.blockPos = blockPos;
    }

    public TreeLeaf getParent() {
        return this.parent;
    }

    public void setParent(TreeLeaf parent) {
        this.parent = parent;
    }

    public void addChild(TreeLeaf child) {
        this.children.add(child);
        child.setParent(this);
    }

    public void die() {
        boolean hasAliveChild = false;
        List<TreeLeaf> aliveChildren = Lists.newArrayList();
        for (TreeLeaf child : children) {
            if (child.isAlive()) {
                hasAliveChild = true;
                aliveChildren.add(child);
            }
        }
        this.children = aliveChildren;
        if (hasAliveChild) {
            return;
        }

        this.alive = false;
        if (this.parent != null) {
            this.parent.die();
        }
    }

    public boolean isAlive() {
        return this.alive;
    }

    public BlockPos getBlockPos() {
        return blockPos;
    }

    @Override
    public String toString() {
        return "TreeLeaf { " + this.blockPos.toString() + " }";
    }
}
