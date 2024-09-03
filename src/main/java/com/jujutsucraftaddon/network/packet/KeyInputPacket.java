package com.jujutsucraftaddon.network.packet;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class KeyInputPacket {

    private final String keyName;

    public KeyInputPacket(String keyName) {
        this.keyName = keyName;
    }

    public static void encode(KeyInputPacket packet, FriendlyByteBuf buf) {
        buf.writeUtf(packet.keyName);
    }

    public static KeyInputPacket decode(FriendlyByteBuf buf) {
        return new KeyInputPacket(buf.readUtf());
    }

    public static void handle(KeyInputPacket packet, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            handlePacket(packet, context);
        });
        context.setPacketHandled(true);
    }

    public static void handlePacket(KeyInputPacket packet, NetworkEvent.Context context) {
        Player player = context.getSender();
        ServerLevel world = (ServerLevel) player.level();
        switch (packet.keyName.toLowerCase()) {
            case "hi":
              break;
        }
    }
}