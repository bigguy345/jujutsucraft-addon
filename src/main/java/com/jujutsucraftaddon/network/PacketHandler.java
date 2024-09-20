package com.jujutsucraftaddon.network;

import com.jujutsucraftaddon.Main;
import com.jujutsucraftaddon.network.packet.*;
import com.jujutsucraftaddon.network.packet.animation.C2SAnimationPacket;
import com.jujutsucraftaddon.network.packet.animation.S2CAnimationPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerChunkCache;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

public class PacketHandler {
    public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(new ResourceLocation(Main.MODID, "main"), () -> "1.0", "1.0"::equals, "1.0"::equals);

    public static void init(FMLCommonSetupEvent event) {
        int id = 0;

        //C2S
        CHANNEL.messageBuilder(KeyInputPacket.class, id++, NetworkDirection.PLAY_TO_SERVER).encoder(KeyInputPacket::encode).decoder(KeyInputPacket::new).consumerMainThread(KeyInputPacket::handle).add();
        CHANNEL.messageBuilder(ReversedCTPacket.class, id++, NetworkDirection.PLAY_TO_SERVER).encoder(ReversedCTPacket::encode).decoder(ReversedCTPacket::new).consumerMainThread(ReversedCTPacket::handle).add();
        CHANNEL.messageBuilder(DashPacket.class, id++, NetworkDirection.PLAY_TO_SERVER).encoder(DashPacket::encode).decoder(DashPacket::new).consumerMainThread(DashPacket::handle).add();

        //S2C
        CHANNEL.messageBuilder(ClientConfigPacket.class, id++, NetworkDirection.PLAY_TO_CLIENT).encoder(ClientConfigPacket::encode).decoder(ClientConfigPacket::new).consumerMainThread(ClientConfigPacket::handle).add();
        CHANNEL.messageBuilder(SyncJujutsuData.class, id++, NetworkDirection.PLAY_TO_CLIENT).encoder(SyncJujutsuData::encode).decoder(SyncJujutsuData::new).consumerMainThread(SyncJujutsuData::handle).add();
        CHANNEL.messageBuilder(BarrierBreakProgessPacket.class, id++, NetworkDirection.PLAY_TO_CLIENT).encoder(BarrierBreakProgessPacket::encode).decoder(BarrierBreakProgessPacket::new).consumerMainThread(BarrierBreakProgessPacket::handle).add();

        //Animations
        CHANNEL.messageBuilder(C2SAnimationPacket.class, id++, NetworkDirection.PLAY_TO_SERVER).encoder(C2SAnimationPacket::encode).decoder(C2SAnimationPacket::new).consumerMainThread(C2SAnimationPacket::handle).add();
        CHANNEL.messageBuilder(S2CAnimationPacket.class, id++, NetworkDirection.PLAY_TO_CLIENT).encoder(S2CAnimationPacket::encode).decoder(S2CAnimationPacket::new).consumerMainThread(S2CAnimationPacket::handle).add();
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

    public static void sendToTracking(Entity entity, Packet msg) {
        CHANNEL.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> entity), msg);
    }

    public static void sendToTrackingOnly(Entity entity, Packet msg) {
        CHANNEL.send(PacketDistributor.TRACKING_ENTITY.with(() -> entity), msg);
    }

    public static void sendToTrackingChunk(LevelChunk chunk, Packet msg) {
        CHANNEL.send(PacketDistributor.TRACKING_CHUNK.with(() -> chunk), msg);
    }

    public static void sendToTrackingVanilla(Entity entity, net.minecraft.network.protocol.Packet packet) {
        ((ServerChunkCache) entity.getCommandSenderWorld().getChunkSource()).broadcast(entity, packet);
    }
}