package com.jujutsucraftaddon.skill;

import com.jujutsucraftaddon.Config;
import com.jujutsucraftaddon.capabilities.data.JujutsuData;
import com.jujutsucraftaddon.client.ClientCache;
import com.jujutsucraftaddon.entity.ILivingEntity;
import com.jujutsucraftaddon.utility.ValueUtil;
import dev.kosmx.playerAnim.core.util.Easing;
import net.minecraft.world.entity.player.Player;

import java.util.Deque;
import java.util.LinkedList;

public class DashSkill {

    public static Deque<Float> velocityHistory = new LinkedList<>();
    public static float DASH_CHARGE, DASH_SUPER_CHARGE, ENERGY_NEEDED;
    public static byte LAST_DASH_ANIMATION;
    public static boolean OUT_OF_ENERGY;
    public float strength, charge, superCharge, speed;
    public int airtime;

    public DashSkill(float strength, float charge, float superCharge) {
        this.strength = strength;
        this.charge = charge;
        this.superCharge = superCharge;
    }

    public void init(JujutsuData data) {
        Player player = data.player;
        data.levels.incrementDashLevel((float) (strength * Config.DASH_LEVEL_GAIN.get()));
        data.data.PlayerCursePowerChange -= DashSkill.calculateEnergyConsumed(strength, superCharge, Config.DASH_ENERGY_CONSUMPTION.get(), Config.DASH_SUPERCHARGE_ENERGY_CONSUMPTION_MULTI.get());

        if (!player.isCreative() && !(data.infinityOn && Config.DASH_INFINITY_BYPASSES_COOLDOWN.get())) {
            double cooldownTicks = 0;
            if (strength < 2.51)
                cooldownTicks = 20;
            else
                cooldownTicks = (strength * Config.DASH_COOLDOWN.get()) * 20;
            data.cooldowns.DASH += cooldownTicks;
        }

        ((ILivingEntity) player).setIsDashing(true);
        player.getPersistentData().putBoolean("killFallDamage", true);
        System.out.println("strength " + strength);
    }

    public void tick(JujutsuData data) {

        airtime++;
    }

    public static float calculateEnergyConsumed(float strength, float superCharge, double energyConsumption, double superChargeEnergyConsumption) {
        double energyConsumed = strength * energyConsumption * (1 - superCharge);
        if (superCharge > 0)
            energyConsumed += strength * energyConsumption * superCharge * superChargeEnergyConsumption;
        return (float) energyConsumed;
    }

    public static float calculateStrength(float charge, float dashLevel) {
        return 2.5f + charge * ValueUtil.lerp(1, ClientCache.DASH_MAX_STRENGTH, dashLevel / ClientCache.DASH_MAX_LEVEL) * Easing.inQuad(charge);
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
