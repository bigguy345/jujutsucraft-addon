package com.sky;

import com.mojang.logging.LogUtils;
import com.sky.Items.ModItems;
import com.sky.blocks.ModBlocks;
import com.sky.client.KeyHandler;
import com.sky.entity.ModEntities;
import com.sky.events.ClientEvents;
import com.sky.events.CommonEvents;
import com.sky.network.PacketHandler;
import net.minecraftforge.api.distmarker.Dist;
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
    public static final String MODID = "sky";
    private static final Logger LOGGER = LogUtils.getLogger();
    public static final IEventBus EVENT_BUS = FMLJavaModLoadingContext.get().getModEventBus();


    public Main() {
        EVENT_BUS.addListener(this::commonSetup);

        // Blocks & Item registry
        ModItems.registerAllItems(EVENT_BUS);
        ModBlocks.registerAllBlocks(EVENT_BUS);
        ModEntities.registerAllEntities(EVENT_BUS);
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
        EVENT_BUS.addListener(ModItems::addCreative);

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
            IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

            //Client only events
            MinecraftForge.EVENT_BUS.register(new ClientEvents());
            MinecraftForge.EVENT_BUS.addListener(KeyHandler::onKeyInput);

            modEventBus.addListener(KeyHandler::registerKeyMappings);
        }
    }
}


