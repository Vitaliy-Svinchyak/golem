package com.example.e33.fight;

import com.example.e33.fight.shooting_navigator.SlimeShootingNavigator;
import com.example.e33.fight.shooting_navigator.ZombieShootingNavigator;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.monster.SlimeEntity;
import net.minecraft.entity.monster.ZombieEntity;
import net.minecraft.util.math.Vec3d;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ShootingNavigator {
    private final static Logger LOGGER = LogManager.getLogger();

    public static Vec3d getShootPoint(MobEntity target, MobEntity creature) {
        if (target instanceof SlimeEntity) {
            return SlimeShootingNavigator.getShootPoint(target, creature);
        }

        if (target instanceof ZombieEntity) {
            return ZombieShootingNavigator.getShootPoint(target, creature);
        }

        double attackAccelX = target.posX - creature.posX;
        double attackAccelY = (target.posY + (double) (target.getHeight() / 2)) - (creature.posY + (double) (creature.getHeight() / 2));
        double attackAccelZ = target.posZ - creature.posZ;

        return new Vec3d(attackAccelX, attackAccelY, attackAccelZ);
    }
}
