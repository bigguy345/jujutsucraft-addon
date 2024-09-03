package com.sky.network;

import com.sky.Main;
import com.sky.network.packet.KeyInputPacket;
import com.sky.network.packet.SyncJujutsuData;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

public class PacketHandler {
    public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(new ResourceLocation(Main.MODID, "main"), () -> "1.0", "1.0"::equals, "1.0"::equals);

    public static void init(FMLCommonSetupEvent event) {
        int id = 0;
        CHANNEL.messageBuilder(KeyInputPacket.class, id++, NetworkDirection.PLAY_TO_SERVER).encoder(KeyInputPacket::encode).decoder(KeyInputPacket::decode).consumerMainThread(KeyInputPacket::handle).add();
        CHANNEL.messageBuilder(SyncJujutsuData.class, id++, NetworkDirection.PLAY_TO_CLIENT).encoder(SyncJujutsuData::encode).decoder(SyncJujutsuData::decode).consumerMainThread(SyncJujutsuData::handle).add();
    }
}