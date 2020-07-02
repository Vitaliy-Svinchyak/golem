package e33.guardy.util;

import e33.guardy.entity.ShootyEntity;
import net.minecraft.entity.EntityPredicate;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.util.EntityPredicates;
import net.minecraft.util.math.AxisAlignedBB;

import java.util.List;
import java.util.stream.Collectors;

public class EnemyRadar {

    public static List<MobEntity> getHostileEnemies(ShootyEntity shooty, Double range) {
        return getAvailableEnemies(shooty, range).stream()
                .filter(mobEntity -> mobEntity.getAttackTarget() == shooty)
                .collect(Collectors.toList());
    }

    public static List<MobEntity> getAvailableEnemies(ShootyEntity shooty, Double range) {
        double radius = range != null ? range : shooty.getAttribute(SharedMonsterAttributes.FOLLOW_RANGE).getValue();
        double heightGap = 15;

        AxisAlignedBB targetableArea = shooty.getBoundingBox().grow(radius, heightGap, radius);
        List<MonsterEntity> enemies = shooty.world.getEntitiesWithinAABB(MonsterEntity.class, targetableArea, EntityPredicates.NOT_SPECTATING);

        return enemies.stream()
                .filter(mobEntity -> shooty.func_213344_a(mobEntity, EntityPredicate.DEFAULT))
                .filter(LivingEntity::isAlive)
                .collect(Collectors.toList());
    }
}
