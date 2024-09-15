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

    static {

        BUILDER.push("Techniques");
        LIMITLESS_FLIGHT_CE_CONSUMPTION = BUILDER.comment("Amount of CE Limitless flight consumes per second").defineInRange("limitlessFlightConsumption", 30, 0, 10000);
        BUILDER.pop();

        BUILDER.push("Technique Levels");
        MAX_RCT_LEVEL = BUILDER.comment("Max RCT level").defineInRange("maxRCTLLevel", 100f, 0, 10000);
        MAX_RCT_LEVEL_AMPLIFIER = BUILDER.comment("RCT Amplifier at max level (linearly interpolates between 0 and this value depending on level to max level ratio)").defineInRange("maxRCTAmplifier", 20, 0, 99);
        BUILDER.pop();
    }

    static final ForgeConfigSpec SPEC = BUILDER.build();

    @SubscribeEvent
    static void onLoad(final ModConfigEvent event) {
    }
}
