package e33.guardy.goal.move;

import e33.guardy.debug.TimeMeter;
import e33.guardy.entity.ShootyEntity;
import e33.guardy.util.EnemyRadar;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.pathfinding.Path;

public class AvoidingDangerGoal extends MovementGoal {

    public AvoidingDangerGoal(ShootyEntity shooty) {
        super(shooty);
    }

    public boolean shouldExecute() {
        if (this.shooty.isBeingRidden()) {
            return false;
        }

        return this.enemiesAreTooClose();
    }

    protected boolean enemiesAreTooClose() {
        return EnemyRadar.getHostileEnemies(this.shooty, 5D).size() > 0;
    }

    public boolean shouldContinueExecuting() {
        boolean continueExecuting = this.enemiesAreTooClose();

        if (continueExecuting && this.shooty.getNavigator().noPath()) {
            this.createPath();
        }

        return continueExecuting;
    }

    protected void createPath() {
        TimeMeter.moduleStart(TimeMeter.MODULE_PATH_BUILDING);
        this.world.getProfiler().startSection("pathfind_my");
        Path path = this.shooty.pathCreator.getSafePath(EnemyRadar.getHostileEnemies(this.shooty, 10D));
        this.shooty.getNavigator().setPath(path, this.shooty.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).getValue());
        this.world.getProfiler().endSection();
        TimeMeter.moduleEnd(TimeMeter.MODULE_PATH_BUILDING);
    }

    public void startExecuting() {
        this.createPath();
        super.startExecuting();
    }

    public boolean isPreemptible() {
        return false;
    }

    public void resetTask() {
        this.shooty.getNavigator().clearPath();
        super.resetTask();
    }
}