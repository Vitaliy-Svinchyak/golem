package e33.guardy.goal.move;

import e33.guardy.E33;
import e33.guardy.client.detail.AnimationState;
import e33.guardy.client.listener.AnimationStateListener;
import e33.guardy.event.MoveEvent;
import e33.guardy.event.NoActionEvent;
import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.EntityPredicate;
import net.minecraft.entity.ai.goal.RandomWalkingGoal;
import net.minecraft.entity.monster.ZombieEntity;
import net.minecraft.util.EntityPredicates;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nullable;
import java.util.List;

public class AvoidingDangerGoal extends RandomWalkingGoal {
    private World world;

    private final static Logger LOGGER = LogManager.getLogger();

    public AvoidingDangerGoal(CreatureEntity creatureIn, double speedIn) {
        super(creatureIn, speedIn, 1);

        this.world = this.creature.getEntityWorld();
    }

    public boolean shouldExecute() {
        return super.shouldExecute();
    }

    @Nullable
    protected Vec3d getPosition() {
        List<ZombieEntity> closestEnemies = this.world.getEntitiesWithinAABB(
                ZombieEntity.class,
                this.creature.getBoundingBox().grow(10D),
                EntityPredicates.NOT_SPECTATING
        );


        for (ZombieEntity enemy : closestEnemies) {
            enemy.setAttackTarget(this.creature);

            Vec3d golemPos = this.creature.getPositionVec();
            Vec3d enemyPos = enemy.getPositionVec();


            if (//enemy.getAttackTarget() == this.creature
                    this.creature.func_213344_a(enemy, EntityPredicate.DEFAULT) //canTarget
                //&& this.creature.getDistanceSq(enemy) < 3D
            ) {
                DangerousZone dangerousZone = new DangerousZone(enemy, 2, 2, 2);
                E33.dangerousZoneDebugRenderer.addZone(dangerousZone);
                for (BlockPos redBlock : dangerousZone.getRedBlocks()) {
                    if (this.creature.getBoundingBox().contains(redBlock.getX(), redBlock.getY(), redBlock.getZ())) {
                        return this.calculatePos(golemPos, enemyPos);
                    }
                }

                for (BlockPos orangeBlock : dangerousZone.getOrangeBlocks()) {
                    if (this.creature.getBoundingBox().contains(orangeBlock.getX(), orangeBlock.getY(), orangeBlock.getZ())) {
                        return this.calculatePos(golemPos, enemyPos);
                    }
                }

                for (BlockPos yellowBlock : dangerousZone.getYellowBlocksPos()) {
                    if (this.creature.getBoundingBox().contains(yellowBlock.getX(), yellowBlock.getY(), yellowBlock.getZ())) {
                        return this.calculatePos(golemPos, enemyPos);
                    }
                }
            }
        }

        this.stop();

        return null;
    }


    private Vec3d calculatePos(Vec3d golemPos, Vec3d enemyPos) {
        if (0 != Double.compare(enemyPos.y, golemPos.y)) {
            return null;
        }

        Direction direction = Direction.getByVerticalAngle(golemPos, enemyPos);
        if (Direction.SE == direction || Direction.NE == direction) {
            this.move();
            return new Vec3d(golemPos.x - 2D, golemPos.y, golemPos.z - 2D);
        }
        if (Direction.SW == direction || Direction.NW == direction) {
            this.move();
            return new Vec3d(golemPos.x + 2D, golemPos.y, golemPos.z + 2D);
        }

        if (Direction.N == direction || Direction.W == direction) {
            this.move();
            return new Vec3d(golemPos.x, golemPos.y, golemPos.z + 2D);
        }

        if (Direction.S == direction || Direction.E == direction) {
            this.move();
            return new Vec3d(golemPos.x - 2D, golemPos.y, golemPos.z - 2D);
        }

        return null;
    }

    protected static enum Direction {
        NE(337.5, 22.5, "North East"),
        E(22.5, 67.5, "East"),
        SE(67.5, 112.5, "South East"),
        S(112.5, 157.5, "South"),
        SW(157.5, 202.5, "South West"),
        W(202.5, 247.5, "West"),
        NW(247.5, 292.5, "North West"),
        N(292.5, 337.5, "North");

        private final double startDegree;
        private final double endDegree;
        private final String description;

        public String getDescription() {
            return this.description;
        }

        private Direction(double startDegree, double endDegree, String description) {
            this.startDegree = startDegree;
            this.endDegree = endDegree;
            this.description = description;
        }

        public static Direction getByVerticalAngle(Vec3d targetPos, Vec3d attackerPos) {
            double verticalAngle = Direction.calcVerticalAngle(targetPos.x, targetPos.z, attackerPos.x, attackerPos.z);

            return Direction.getByAngle(verticalAngle);
        }

        private static double calcVerticalAngle(double ax, double ay, double bx, double by) {
            double angle = Math.atan2(by - ay, bx - ax) * 180 / Math.PI;

            if (angle < 0) {
                angle += 360;
            }

            return angle;
        }

        public static Direction getByAngle(double angle) {

            if ((angle > NE.startDegree && angle <= 360) || (angle >= 0 && angle <= NE.endDegree)) {
                return NE;
            }

            if (angle > E.startDegree && angle <= E.endDegree) {
                return E;
            }

            if (angle > SE.startDegree && angle <= SE.endDegree) {
                return SE;
            }

            if (angle > S.startDegree && angle <= S.endDegree) {
                return S;
            }

            if (angle > SW.startDegree && angle <= SW.endDegree) {
                return SW;
            }

            if (angle > W.startDegree && angle <= W.endDegree) {
                return W;
            }

            if (angle > NW.startDegree && angle <= NW.endDegree) {
                return NW;
            }

            if (angle > N.startDegree && angle <= N.endDegree) {
                return N;
            }

            return null;
        }
    }

    public boolean isPreemptible() {
        return false;
    }

    private void move() {
        E33.internalEventBus.post(new MoveEvent(this.creature));
    }

    private void stop() {
        if (AnimationStateListener.getAnimationState(this.creature) == AnimationState.MOVE) {
            E33.internalEventBus.post(new NoActionEvent(this.creature));
        }
    }
}