package com.jujutsucraftaddon.network.packet;

import com.jujutsucraftaddon.network.Packet;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.network.NetworkEvent;

public class KeyInputPacket extends Packet {

    public String keyName;

    public KeyInputPacket(String keyName) {
        this.keyName = keyName;
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeUtf(keyName);
    }

    public KeyInputPacket(FriendlyByteBuf buf) {
        keyName = buf.readUtf();
    }

    public void handle(Player player, NetworkEvent.Context context) {
        ServerLevel world = (ServerLevel) player.level();
        switch (keyName.toLowerCase()) {
            case "hi":
                break;
        }
    }
}