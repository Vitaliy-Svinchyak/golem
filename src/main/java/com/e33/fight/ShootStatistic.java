package com.e33.fight;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ShootStatistic {
    private final static Logger LOGGER = LogManager.getLogger();
    private static double shotBullets = 0.0;
    private static double successBullets = 0.0;

    public static void bulletShot() {
        ShootStatistic.shotBullets++;
    }

    public static void bulletHitTheTarget() {
        ShootStatistic.successBullets++;
//        ShootStatistic.logStatistic();
    }

    public static void clear() {
        ShootStatistic.shotBullets = 0.0;
        ShootStatistic.successBullets = 0.0;
    }

    private static void logStatistic() {
        double percent = (int) (ShootStatistic.successBullets / ShootStatistic.shotBullets * 100);
        LOGGER.info((int) ShootStatistic.shotBullets + ":" + (int) ShootStatistic.successBullets + " / " + percent + "%");
    }
}
