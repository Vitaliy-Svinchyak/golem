package e33.guardy.util.mobComparator;

import e33.guardy.entity.ShootyEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.merchant.villager.AbstractVillagerEntity;
import net.minecraft.entity.monster.SkeletonEntity;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.inventory.EquipmentSlotType;

import javax.annotation.Nonnull;
import java.util.Comparator;

public class SkeletonComparator implements Comparator<SkeletonEntity> {
    private MobEntity creature;

    public SkeletonComparator(MobEntity creature) {
        this.creature = creature;
    }

    public int compare(@Nonnull SkeletonEntity mob1, @Nonnull SkeletonEntity mob2) {
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

    private int getHazardPoints(@Nonnull SkeletonEntity mob) {
        int hazardPoints = 0;

        if (mob.isBurning()) {
            return -100;
        }

        if (!mob.hasItemInSlot(EquipmentSlotType.MAINHAND)) {
            return -100;
        }

        if (mob.getHealth() < mob.getMaxHealth()) {
            hazardPoints += 1;
        }

        if (mob.getAttackTarget() instanceof ShootyEntity) {
            hazardPoints += 10;
        }

        LivingEntity target = mob.getAttackTarget();
        if (target instanceof ShootyEntity || target instanceof IronGolemEntity || target instanceof AbstractVillagerEntity) {
            hazardPoints += 10;
        }

        double distanceToMob = this.creature.getDistance(mob);

        if (distanceToMob <= 15) {
            hazardPoints += 3;
        }
        if (distanceToMob > 15) {
            hazardPoints -= 3;
        }

        if (mob.hurtResistantTime <= 10.0F) {
            hazardPoints += 1;
        }
        if (mob.hurtResistantTime > 10.0F) {
            hazardPoints -= 1;
        }

        final int[] equipmentPower = {0};
        mob.getArmorInventoryList().forEach(itemStack -> {
            equipmentPower[0]++;
            if (itemStack.isEnchanted()) {
                equipmentPower[0]++;
            }
        });
        mob.getHeldEquipment().forEach(itemStack -> {
            equipmentPower[0]++;
            if (itemStack.isEnchanted()) {
                equipmentPower[0]++;
            }
        });
        hazardPoints += equipmentPower[0];

        return hazardPoints;
    }
}