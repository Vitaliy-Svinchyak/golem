package com.example.e33.goal.attack;

import com.example.e33.entity.EntityGolemShooter;
import com.google.common.collect.Lists;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.merchant.villager.VillagerEntity;
import net.minecraft.entity.passive.GolemEntity;
import net.minecraft.util.EntityPredicates;
import net.minecraft.util.math.AxisAlignedBB;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

public class AvoidPeacefulCreaturesGoal {
    private final static Logger LOGGER = LogManager.getLogger();
    private List<NavigationParameters> peacefulCreatures;
    private MobEntity goalOwner;

    public AvoidPeacefulCreaturesGoal(MobEntity goalOwner) {
        this.goalOwner = goalOwner;
    }

    public void findPeacefulCreatures() {
        this.peacefulCreatures = Lists.newArrayList();

        AxisAlignedBB targetableArea = this.getTargetableArea(this.goalOwner.getAttribute(SharedMonsterAttributes.FOLLOW_RANGE).getValue());
        List<Class<? extends MobEntity>> avoid = Lists.newArrayList(VillagerEntity.class, EntityGolemShooter.class, GolemEntity.class);

        for (Class<? extends MobEntity> mobClass : avoid) {
            List<MobEntity> entities = this.goalOwner.world.getEntitiesWithinAABB(mobClass, targetableArea, EntityPredicates.NOT_SPECTATING);

            for (MobEntity creature : entities) {
                if (!creature.equals(this.goalOwner)) {
                    this.peacefulCreatures.add(this.getNavigationParametersForCreature(creature));
                }
            }
        }
    }

    public boolean bulletPathIsClear(MobEntity target) {
        NavigationParameters targetNavParams = this.getNavigationParametersForCreature(target);

        for (NavigationParameters creatureNavParams : this.peacefulCreatures) {
            // creature is further then target
            if (creatureNavParams.distance > targetNavParams.distance) {
                continue;
            }

            // creatures intersect by horizontal
            if (!this.angleIntervalsIntersect(creatureNavParams.horizontalAngleStart, creatureNavParams.horizontalAngleEnd, targetNavParams.horizontalAngleStart, targetNavParams.horizontalAngleEnd)) {
                continue;
            }

            // creatures intersect by vertical
            if (!this.angleIntervalsIntersect(creatureNavParams.verticalAngleStart, creatureNavParams.verticalAngleEnd, targetNavParams.verticalAngleStart, targetNavParams.verticalAngleEnd)) {
                continue;
            }

            LOGGER.info("bulletPathIsClear false");
            LOGGER.info(creatureNavParams);
            LOGGER.info(targetNavParams);
            return false;
        }

        LOGGER.info("bulletPathIsClear true");
        return true;
    }

    private boolean angleIntervalsIntersect(double a1, double a2, double b1, double b2) {
        LOGGER.info("angles a:" + a1 + "," + a2 + "b: " + b1 + "," + b2);
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

    private NavigationParameters getNavigationParametersForCreature(MobEntity creature) {
        double distanceToMob = this.goalOwner.getDistanceSq(creature);

        // TODO use height too
        double horizontalAngleStart = this.getHorizontalAngle(this.goalOwner.posX, this.goalOwner.posZ, creature.getBoundingBox().minX - 0.5, creature.getBoundingBox().minZ - 0.5);
        double horizontalAngleEnd = this.getHorizontalAngle(this.goalOwner.posX, this.goalOwner.posZ, creature.getBoundingBox().maxX + 0.5, creature.getBoundingBox().maxZ + 0.5);

        double verticalAngleStart = this.getVerticalAngle(this.goalOwner.posX, this.goalOwner.posY, creature.posX, creature.getBoundingBox().minY);
        double verticalAngleEnd = this.getVerticalAngle(this.goalOwner.posX, this.goalOwner.posY, creature.posX, creature.getBoundingBox().maxY);

        return new NavigationParameters(distanceToMob, horizontalAngleStart, horizontalAngleEnd, verticalAngleStart, verticalAngleEnd, creature.getClass().getName());
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

    AxisAlignedBB getTargetableArea(double distance) {
        double heightGap = 15;
        return this.goalOwner.getBoundingBox().grow(distance, heightGap, distance);
    }

    private static class NavigationParameters {
        final double distance;

        final double horizontalAngleStart;
        final double horizontalAngleEnd;

        final double verticalAngleStart;
        final double verticalAngleEnd;

        final String className;

        private NavigationParameters(double distance, double horizontalAngleStart, double horizontalAngleEnd, double verticalAngleStart, double verticalAngleEnd, String className) {
            this.distance = distance;

            this.horizontalAngleStart = horizontalAngleStart;
            this.horizontalAngleEnd = horizontalAngleEnd;

            this.verticalAngleStart = verticalAngleStart;
            this.verticalAngleEnd = verticalAngleEnd;

            this.className = className;
        }

        @Override
        public String toString() {
            return this.getClass().getName() + "@{" + "" +
                    " className:" + this.className +
                    ", distance:" + this.distance +
                    ", horizontalAngleStart:" + this.horizontalAngleStart +
                    ", horizontalAngleEnd:" + this.horizontalAngleEnd +
                    ", verticalAngleStart:" + this.verticalAngleStart +
                    ", verticalAngleEnd:" + this.verticalAngleEnd +
                    "}";
        }
    }
}
