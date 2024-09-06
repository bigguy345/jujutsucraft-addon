package com.jujutsucraftaddon;

import com.jujutsucraftaddon.Items.ModItems;
import com.jujutsucraftaddon.blocks.ModBlocks;
import com.jujutsucraftaddon.capabilities.ModCapabilities;
import com.jujutsucraftaddon.client.KeyHandler;
import com.jujutsucraftaddon.effects.ModEffects;
import com.jujutsucraftaddon.entity.ModEntities;
import com.jujutsucraftaddon.events.ClientEvents;
import com.jujutsucraftaddon.events.CommonEvents;
import com.jujutsucraftaddon.events.MobEffectEvents;
import com.jujutsucraftaddon.network.PacketHandler;
import com.mojang.logging.LogUtils;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;
import software.bernie.geckolib.GeckoLib;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(Main.MODID)
public class Main {
    public static final String MODID = "jujutsucraftaddon";
    private static final Logger LOGGER = LogUtils.getLogger();
    public static IEventBus MOD_EVENT_BUS = FMLJavaModLoadingContext.get().getModEventBus();

    public Main() {
        MOD_EVENT_BUS.addListener(this::commonSetup);

        // Blocks & Item registry
        ModItems.registerAllItems(MOD_EVENT_BUS);
        ModBlocks.registerAllBlocks(MOD_EVENT_BUS);
        ModEntities.registerAllEntities(MOD_EVENT_BUS);
        ModEffects.registerAllEffects(MOD_EVENT_BUS);
        GeckoLib.initialize();
    }

    //Commons means both client & server.
    private void commonSetup(final FMLCommonSetupEvent event) {
        ////////////////////////////////
        //Mod's Config registry
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Config.SPEC);

        ////////////////////////////////
        //Event registry
        MinecraftForge.EVENT_BUS.register(this);
        MinecraftForge.EVENT_BUS.register(new CommonEvents());
        MinecraftForge.EVENT_BUS.register(new MobEffectEvents());
        MinecraftForge.EVENT_BUS.addGenericListener(Entity.class, ModCapabilities::onAttachCapabilities);

        MOD_EVENT_BUS.addListener(ModItems::addCreative);

        ////////////////////////////////
        //Network registry
        PacketHandler.init(event);
    }

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        LOGGER.info("HELLO from server starting");
    }

    // Client only
    @Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {
            //Client only events
            MinecraftForge.EVENT_BUS.register(new ClientEvents());
            MinecraftForge.EVENT_BUS.register(new KeyHandler());

            MOD_EVENT_BUS.addListener(KeyHandler::registerKeyMappings);
        }

        @SubscribeEvent
        public static void registerKeyMappings(RegisterKeyMappingsEvent event) {
            KeyHandler.registerKeyMappings(event);
        }
    }
}


