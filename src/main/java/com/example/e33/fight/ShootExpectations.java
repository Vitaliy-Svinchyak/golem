package com.example.e33.fight;

import com.google.common.collect.Lists;
import net.minecraft.entity.LivingEntity;

import java.util.List;

public class ShootExpectations {
    private final static List<String> markedAsDead = Lists.newArrayList();

    public static void markAsDead(LivingEntity target) {
        ShootExpectations.markedAsDead.add(target.getUniqueID().toString());
    }

    public static boolean isMarkedAsDead(LivingEntity target) {
        return ShootExpectations.markedAsDead.contains(target.getUniqueID().toString());
    }

    public static void removeFromDeadList(LivingEntity target) {
        ShootExpectations.markedAsDead.remove(target.getUniqueID().toString());
    }

}
