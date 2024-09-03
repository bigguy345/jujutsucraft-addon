package com.jujutsucraftaddon.network;

import com.jujutsucraftaddon.Main;
import com.jujutsucraftaddon.network.packet.KeyInputPacket;
import com.jujutsucraftaddon.network.packet.SyncJujutsuData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

public class PacketHandler {
    public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(new ResourceLocation(Main.MODID, "main"), () -> "1.0", "1.0"::equals, "1.0"::equals);

    public static void init(FMLCommonSetupEvent event) {
        int id = 0;
        CHANNEL.messageBuilder(KeyInputPacket.class, id++, NetworkDirection.PLAY_TO_SERVER).encoder(KeyInputPacket::encode).decoder(KeyInputPacket::new).consumerMainThread(KeyInputPacket::handle).add();
        CHANNEL.messageBuilder(SyncJujutsuData.class, id++, NetworkDirection.PLAY_TO_CLIENT).encoder(SyncJujutsuData::encode).decoder(SyncJujutsuData::new).consumerMainThread(SyncJujutsuData::handle).add();
    }

    public static void sendToServer(Packet msg) {
        CHANNEL.send(PacketDistributor.SERVER.noArg(), msg);
    }

    public static void sendToAll(Packet msg) {
        CHANNEL.send(PacketDistributor.ALL.noArg(), msg);
    }

    public static void sendToPlayer(Player player, Packet msg) {
        CHANNEL.send(PacketDistributor.PLAYER.with(() -> (ServerPlayer) player), msg);
    }

    public static void sendToTracking(Player player, Packet msg) {
        CHANNEL.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> player), msg);
    }
}