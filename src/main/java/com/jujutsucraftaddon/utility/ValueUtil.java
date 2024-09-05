package com.jujutsucraftaddon.utility;

import java.util.concurrent.ThreadLocalRandom;

public class ValueUtil {

    public static int randomBetween(int x, int y) {
        if (x == y)
            return x;

        if (x > y) {
            int temp = x;
            x = y;
            y = temp;
        }
        return Math.round(ThreadLocalRandom.current().nextFloat() * (y - x) + x);
    }

    public static float randomBetween(float x, float y) {
        if (x == y)
            return x;

        if (x > y) {
            float temp = x;
            x = y;
            y = temp;
        }
        return ThreadLocalRandom.current().nextFloat() * (y - x) + x;
    }
    
    public static double randomBetween(double x, double y) {
        if (x == y)
            return x;

        if (x > y) {
            double temp = x;
            x = y;
            y = temp;
        }
        return ThreadLocalRandom.current().nextFloat() * (y - x) + x;
    }
}
