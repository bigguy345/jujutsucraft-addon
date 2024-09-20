package com.jujutsucraftaddon.skill;

import com.jujutsucraftaddon.client.ClientCache;
import com.jujutsucraftaddon.utility.ValueUtil;

import java.util.Deque;
import java.util.LinkedList;

public class DashSkill {

    public static Deque<Float> velocityHistory = new LinkedList<>();
    public static float DASH_CHARGE, DASH_SUPER_CHARGE, ENERGY_NEEDED;
    public static boolean OUT_OF_ENERGY;
    public float strength, charge, superCharge, speed;
    public int ticks;

    public DashSkill(float strength, float charge, float superCharge) {
        this.strength = strength;
        this.charge = charge;
        this.superCharge = superCharge;
    }

    public static float calculateEnergyConsumed(float strength, float superCharge, double energyConsumption, double superChargeEnergyConsumption) {
        double energyConsumed = strength * energyConsumption * (1 - superCharge);
        if (superCharge > 0)
            energyConsumed += strength * energyConsumption * superCharge * superChargeEnergyConsumption;
        return (float) energyConsumed;
    }

    public static float calculateStrength(float charge, float dashLevel) {
        return 2 + charge * ValueUtil.lerp(1, 20, dashLevel / ClientCache.MAX_DASH_LEVEL);
    }

    public static void recordVelocity(float velocity) {
        if (velocityHistory.size() >= 5)
            velocityHistory.removeFirst();

        velocityHistory.addLast(velocity);
    }

    public static float getHighestVelocity() {
        return velocityHistory.stream().sorted((v1, v2) -> Float.compare(v2, v1)).findFirst().orElse(0f);
    }
}
