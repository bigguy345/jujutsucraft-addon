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

    public static float lerp(float start, float end, float t) {
        return start + (end - start) * t;
    }

    public static double lerp(double start, double end, double t) {
        return start + (end - start) * t;
    }

    public static int lerp(int start, int end, float t) {
        return (int) (start + (end - start) * t);
    }

    public static float clamp(float value, float min, float max) {
        return Math.max(min, Math.min(max, value));
    }

    public static double clamp(double value, double min, double max) {
        return Math.max(min, Math.min(max, value));
    }

    public static int clamp(int value, int min, int max) {
        return Math.max(min, Math.min(max, value));
    }
}
