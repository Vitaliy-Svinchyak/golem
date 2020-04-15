package e33.guardy.fight;

import com.google.common.collect.Lists;
import net.minecraft.entity.LivingEntity;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.List;

public class ShootExpectations {
    private final static List<String> markedAsDead = Lists.newArrayList();
    private final static HashMap<String, String> markedAsBusy = new HashMap<>();

    public static boolean shouldAttack(@Nonnull LivingEntity target, @Nonnull LivingEntity owner) {
        return !ShootExpectations.isMarkedAsDead(target) && !ShootExpectations.isMarkedAsBusy(target, owner);
    }

    public static void forgetTarget(@Nonnull LivingEntity target) {
        ShootExpectations.removeFromDeadList(target);
        ShootExpectations.removeFromBusyList(target);
    }

    public static void markAsDead(@Nonnull LivingEntity target) {
        ShootExpectations.markedAsDead.add(target.getUniqueID().toString());
    }

    public static void markAsBusy(@Nonnull LivingEntity target, @Nonnull LivingEntity owner) {
        ShootExpectations.markedAsBusy.put(target.getUniqueID().toString(), owner.getUniqueID().toString());
    }

    public static void removeFromDeadList(@Nonnull LivingEntity target) {
        ShootExpectations.markedAsDead.remove(target.getUniqueID().toString());
    }

    private static boolean isMarkedAsDead(@Nonnull LivingEntity target) {
        return ShootExpectations.markedAsDead.contains(target.getUniqueID().toString());
    }

    private static boolean isMarkedAsBusy(@Nonnull LivingEntity target, @Nonnull LivingEntity owner) {
        String ownerId = ShootExpectations.markedAsBusy.get(target.getUniqueID().toString());
        if (ownerId == null) {
            return false;
        }

        return !ownerId.equals(owner.getUniqueID().toString());
    }

    public static void removeFromBusyList(@Nonnull LivingEntity target) {
        ShootExpectations.markedAsBusy.remove(target.getUniqueID().toString());
    }

}
