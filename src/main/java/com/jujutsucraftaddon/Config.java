package com.jujutsucraftaddon;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.config.ModConfigEvent;

@Mod.EventBusSubscriber(modid = Main.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class Config {
    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();

    public static ForgeConfigSpec.IntValue LIMITLESS_FLIGHT_CE_CONSUMPTION;

    public static ForgeConfigSpec.DoubleValue MAX_RCT_LEVEL;
    public static ForgeConfigSpec.IntValue MAX_RCT_LEVEL_AMPLIFIER;
    public static ForgeConfigSpec.DoubleValue RCT_LEVEL_GAIN;
    public static ForgeConfigSpec.DoubleValue RCT_BLACKFLASH_UNLOCK_CHANCE;
    public static ForgeConfigSpec.DoubleValue RCT_OTHERS_UNLOCK_CHANCE;

    public static ForgeConfigSpec.IntValue BLACKFLASH_MAX_CE_GAIN_AMOUNT;
    public static ForgeConfigSpec.DoubleValue BLACKFLASH_MAX_CE_GAIN_CHANCE;

    public static ForgeConfigSpec.DoubleValue DASH_MAX_LEVEL;
    public static ForgeConfigSpec.DoubleValue DASH_MAX_STRENGTH;
    public static ForgeConfigSpec.DoubleValue DASH_LEVEL_GAIN;
    public static ForgeConfigSpec.IntValue DASH_ENERGY_CONSUMPTION;
    public static ForgeConfigSpec.DoubleValue DASH_SUPERCHARGE_SPEED;
    public static ForgeConfigSpec.DoubleValue DASH_SUPERCHARGE_ENERGY_CONSUMPTION_MULTI;
    public static ForgeConfigSpec.DoubleValue DASH_COOLDOWN;
    public static ForgeConfigSpec.BooleanValue DASH_INFINITY_BYPASSES_COOLDOWN;

    public static ForgeConfigSpec.DoubleValue MAX_ALLOWED_DASH_VELOCITY;
    public static float ALLOWED_DASH_VELOCITY;

    static {

        BUILDER.push("Techniques");

        BUILDER.push("Reversed Cursed Technique");
        MAX_RCT_LEVEL = BUILDER.comment("Max Reversed Cursed Technique level").defineInRange("maxRCTLLevel", 100f, 0, 1000);
        MAX_RCT_LEVEL_AMPLIFIER = BUILDER.comment("Reversed Cursed Technique effect amplifier at max level (linearly interpolates between 0 and this value depending on level to max level ratio)").defineInRange("maxRCTAmplifier", 20, 0, 99);
        RCT_LEVEL_GAIN = BUILDER.comment("Amount of RCT lvl gain per heart healed. i.e healing 1 heart with default config (0.02) gains 0.02 levels, 50 hearts is 1 level").defineInRange("rctLevelGain", 0.02f, 0, 100f);

        RCT_BLACKFLASH_UNLOCK_CHANCE = BUILDER.comment("Chance of unlocking \"Reversed Cursed Technique\" upon landing a black flash (default 0.2 === 20%)").defineInRange("blackflashRCTUnlockChance", 0.2, 0, 1);
        RCT_OTHERS_UNLOCK_CHANCE = BUILDER.comment("Chance of unlocking \"Reversed Cursed Tech. Others\" upon first time unlocking \"Reversed Cursed Technique\" (default 0.25 === 25%)").defineInRange("othersRCTUnlockChance", 0.25, 0, 1);
        BUILDER.pop();

        BUILDER.push("Black Flash");
        BLACKFLASH_MAX_CE_GAIN_CHANCE = BUILDER.comment("Chance of gaining CE upon landing a black flash (default 0.25 === 25%)").defineInRange("blackflashMaxCEGainChance", 0.25, 0, 1);
        BLACKFLASH_MAX_CE_GAIN_AMOUNT = BUILDER.comment("Player MAX Cursed Energy pool will be increased by this amount upon landing a black flash and succeeding the above blackflashMaxCEGainChance").defineInRange("blackflashMaxCEGainAmount", 500, 0, 10000);
        BUILDER.pop();

        BUILDER.push("Dash");
        DASH_MAX_LEVEL = BUILDER.comment("Max Dash level").defineInRange("maxDashLevel", 100f, 0, 1000);
        DASH_MAX_STRENGTH = BUILDER.comment("Strength of fully charged Dash at max level (linearly interpolates between 0 and this value depending on level to max level ratio)").defineInRange("maxDashStrength", 30, 0, 50f);
        DASH_LEVEL_GAIN = BUILDER.comment("Amount of Dash lvl gain per Dash strength. i.e a max level strength 20 * 0.05 (default) gains 1 level").defineInRange("dashLevelGain", 0.05f, 0, 100f);
        DASH_ENERGY_CONSUMPTION = BUILDER.comment("Amount of CE consumed per Dash strength. i.e if strength is 20 (max), consume 20 * 5 = 80 CE by default").defineInRange("dashEnergyConsumption", 5, 0, 1000);
        DASH_COOLDOWN = BUILDER.comment("Seconds of cooldown per dash strength i.e A 20 strength dash gets 20 seconds by default").defineInRange("dashCooldown", 1, 0, 1000f);
        DASH_INFINITY_BYPASSES_COOLDOWN = BUILDER.comment("Whether infinity bypasses dash cooldown or not").define("infinityBypassesCooldown", true);

        BUILDER.comment("Holding 2nd FN while charging dash increases charge speed (Supercharge)");
        DASH_SUPERCHARGE_SPEED = BUILDER.comment("Supercharge speed i.e increases charging speed by 4x (default) while supercharging").defineInRange("dashSuperchargeSpeed", 4, 0, 1000f);
        DASH_SUPERCHARGE_ENERGY_CONSUMPTION_MULTI = BUILDER.comment("If player super charges 25% of dash, then 25% of dashEnergyConsumption will be multiplied by 5(default)").defineInRange("dashSuperchargeEnergyMulti", 5, 0, 1000f);

        BUILDER.pop();

        LIMITLESS_FLIGHT_CE_CONSUMPTION = BUILDER.comment("Amount of CE Limitless flight consumes per second").defineInRange("limitlessFlightConsumption", 30, 0, 10000);
        BUILDER.pop();


        BUILDER.push("Server settings");
        MAX_ALLOWED_DASH_VELOCITY = BUILDER.comment("Server will invalidate the movement and report in console if it goes above this number! Increase accordingly with maxDashStrength").defineInRange("maxAllowedDashVelocity", 1000, 0, 5000f);
        BUILDER.pop();
    }

    static final ForgeConfigSpec SPEC = BUILDER.build();

    @SubscribeEvent
    static void onLoad(final ModConfigEvent event) {
        ALLOWED_DASH_VELOCITY = (float) (double) MAX_ALLOWED_DASH_VELOCITY.get();
    }
}
