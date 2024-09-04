package com.jujutsucraftaddon.utility;

import java.util.concurrent.ThreadLocalRandom;

public class ValueUtil {

    public static float randomBetween(float x, float y) {
        if (x > y) {
            float temp = x;
            x = y;
            y = temp;
        }
        return ThreadLocalRandom.current().nextFloat() * (y - x) + x;
    }

}
