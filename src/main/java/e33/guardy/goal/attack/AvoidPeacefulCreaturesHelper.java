package e33.guardy.goal.attack;

import e33.guardy.entity.ShootyEntity;
import com.google.common.collect.Lists;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.merchant.villager.VillagerEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.BatEntity;
import net.minecraft.entity.passive.GolemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.EntityPredicates;
import net.minecraft.util.math.AxisAlignedBB;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nonnull;
import java.util.List;

public class AvoidPeacefulCreaturesHelper {
    private final static Logger LOGGER = LogManager.getLogger();
    private List<NavigationParameters> peacefulCreatures;
    private MobEntity goalOwner;

    public AvoidPeacefulCreaturesHelper(@Nonnull MobEntity goalOwner) {
        this.goalOwner = goalOwner;
    }

    public void findPeacefulCreatures() {
        this.peacefulCreatures = Lists.newArrayList();

        AxisAlignedBB targetableArea = this.getTargetableArea(this.goalOwner.getAttribute(SharedMonsterAttributes.FOLLOW_RANGE).getValue());
        List<Class<? extends LivingEntity>> avoid = Lists.newArrayList(AnimalEntity.class, VillagerEntity.class, ShootyEntity.class, GolemEntity.class, BatEntity.class, PlayerEntity.class);

        for (Class<? extends LivingEntity> mobClass : avoid) {
            List<LivingEntity> entities = this.goalOwner.world.getEntitiesWithinAABB(mobClass, targetableArea, EntityPredicates.NOT_SPECTATING);

            for (LivingEntity creature : entities) {
                if (!creature.equals(this.goalOwner)) {
                    this.peacefulCreatures.add(new NavigationParameters(this.goalOwner, creature));
                }
            }
        }
    }

    public boolean bulletPathIsClear(LivingEntity target) {
        NavigationParameters targetNavParams = new NavigationParameters(this.goalOwner, target);

        for (NavigationParameters creatureNavParams : this.peacefulCreatures) {
            if (targetNavParams.intersects(creatureNavParams)) {
                return false;
            }
        }

        return true;
    }

    private AxisAlignedBB getTargetableArea(double distance) {
        double heightGap = 15;
        return this.goalOwner.getBoundingBox().grow(distance, heightGap, distance);
    }

    private static class NavigationParameters {
        final double distance;

        final double horizontalAngleStart;
        final double horizontalAngleEnd;

        final double verticalAngleStart;
        final double verticalAngleEnd;

        final String uniqueName;

        private NavigationParameters(@Nonnull LivingEntity goalOwner, @Nonnull LivingEntity creature) {
            this.uniqueName = creature.getClass().toString() + " - " + creature.getUniqueID().toString();

            this.distance = goalOwner.getDistance(creature);

            this.horizontalAngleStart = this.getHorizontalAngle(goalOwner.posX, goalOwner.posZ, creature.getBoundingBox().minX - 0.5, creature.getBoundingBox().minZ - 0.5);
            this.horizontalAngleEnd = this.getHorizontalAngle(goalOwner.posX, goalOwner.posZ, creature.getBoundingBox().maxX + 0.5, creature.getBoundingBox().maxZ + 0.5);

            this.verticalAngleStart = this.getVerticalAngle(0, goalOwner.posY, this.distance, creature.getBoundingBox().minY - 0.5);
            this.verticalAngleEnd = this.getVerticalAngle(0, goalOwner.posY, this.distance, creature.getBoundingBox().maxY + 0.5);
        }

        private double getHorizontalAngle(double ax, double ay, double bx, double by) {
            double angle = Math.atan2(ay, ax) - Math.atan2(by, bx);
            angle = angle * 180 / Math.PI;

            if (angle < 0) {
                angle += 360;
            }

            return angle;
        }

        private double getVerticalAngle(double ax, double ay, double bx, double by) {
            double angle = Math.atan2(by - ay, bx - ax) * 180 / Math.PI;

            if (angle < 0) {
                angle += 360;
            }

            return angle;
        }

        private boolean intersects(@Nonnull NavigationParameters targetNavParams) {
            if (this.distance > targetNavParams.distance) {
                // creatures intersect by horizontal
                if (!this.angleIntervalsIntersect(this.horizontalAngleStart, this.horizontalAngleEnd, targetNavParams.horizontalAngleStart, targetNavParams.horizontalAngleEnd)) {
                    // creatures intersect by vertical
                    if (!this.angleIntervalsIntersect(this.verticalAngleStart, this.verticalAngleEnd, targetNavParams.verticalAngleStart, targetNavParams.verticalAngleEnd)) {
                        return true;
                    }
                }
            }

            return false;
        }

        private boolean angleIntervalsIntersect(double a1, double a2, double b1, double b2) {
            double da = (a2 - a1) / 2;
            double db = (b2 - b1) / 2;
            double ma = (a2 + a1) / 2;
            double mb = (b2 + b1) / 2;
            double cda = Math.cos(da);
            double cdb = Math.cos(db);

            return Math.cos(ma - b1) >= cda ||
                    Math.cos(ma - b2) >= cda ||
                    Math.cos(mb - a1) >= cdb ||
                    Math.cos(mb - a2) >= cdb;
        }

        @Override
        public String toString() {
            return this.getClass().getName() + "@{" + "" +
                    " uniqueName:" + this.uniqueName +
                    ", distance:" + this.distance +
                    ", horizontalAngleStart:" + this.horizontalAngleStart +
                    ", horizontalAngleEnd:" + this.horizontalAngleEnd +
                    ", verticalAngleStart:" + this.verticalAngleStart +
                    ", verticalAngleEnd:" + this.verticalAngleEnd +
                    "}";
        }
    }
}
