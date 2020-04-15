package e33.guardy.util.mobComparator;

import e33.guardy.entity.ShootyEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.merchant.villager.AbstractVillagerEntity;
import net.minecraft.entity.monster.SlimeEntity;
import net.minecraft.entity.passive.IronGolemEntity;

import javax.annotation.Nonnull;
import java.util.Comparator;

public class SlimeComparator implements Comparator<SlimeEntity> {
    private MobEntity creature;

    public SlimeComparator(MobEntity creature) {
        this.creature = creature;
    }

    public int compare(@Nonnull SlimeEntity mob1, @Nonnull SlimeEntity mob2) {
        int mob1HazardPoints = this.getHazardPoints(mob1);
        int mob2HazardPoints = this.getHazardPoints(mob2);

        if (mob1HazardPoints > mob2HazardPoints) {
            return -1;
        } else if (mob1HazardPoints < mob2HazardPoints) {
            return 1;
        }

        double mob1Distance = this.creature.getDistanceSq(mob1);
        double mob2Distance = this.creature.getDistanceSq(mob2);

        return (int) Math.floor(mob1Distance - mob2Distance);
    }

    private int getHazardPoints(@Nonnull SlimeEntity mob) {
        int hazardPoints = 0;

        if (mob.isBurning()) {
            return -100;
        }

        if (mob.isSmallSlime()) {
            return -100;
        }

        if (mob.getAttackTarget() instanceof ShootyEntity) {
            hazardPoints += 10;
        }

        if (mob.getHealth() < mob.getMaxHealth()) {
            hazardPoints += 1;
        }

        LivingEntity target = mob.getAttackTarget();
        if (target instanceof ShootyEntity || target instanceof IronGolemEntity || target instanceof AbstractVillagerEntity) {
            hazardPoints += 10;
        }

        double distanceToMob = this.creature.getDistance(mob);

        if (distanceToMob <= 10) {
            hazardPoints += 2;
        }
        if (distanceToMob <= 5) {
            hazardPoints += 4;
        }
        if (distanceToMob > 10) {
            hazardPoints -= 3;
        }

        if (mob.hurtResistantTime <= 10.0F) {
            hazardPoints += 1;
        }
        if (mob.hurtResistantTime > 10.0F) {
            hazardPoints -= 1;
        }

        return hazardPoints;
    }
}