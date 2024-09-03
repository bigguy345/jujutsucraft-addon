package com.jujutsucraftaddon.network;

import net.minecraft.world.entity.player.Player;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class Packet {

    public static void handle(Packet packet, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            packet.handle(context.getSender(), context);
        });
        context.setPacketHandled(true);
    }

    public void handle(Player player, NetworkEvent.Context context) {

    }
}