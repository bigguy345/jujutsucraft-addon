package com.jujutsucraftaddon.network.packet;

import com.jujutsucraftaddon.Config;
import com.jujutsucraftaddon.network.Packet;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.network.NetworkEvent;

import static com.jujutsucraftaddon.client.ClientCache.*;

public class ClientConfigPacket extends Packet {

    public ClientConfigPacket() {
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeFloat((float) (double) Config.DASH_SUPERCHARGE_SPEED.get());
        buf.writeFloat((float) (double) Config.DASH_ENERGY_CONSUMPTION.get());
        buf.writeFloat((float) (double) Config.DASH_SUPERCHARGE_ENERGY_CONSUMPTION_MULTI.get());
        buf.writeFloat((float) (double) Config.MAX_DASH_LEVEL.get());
        buf.writeFloat((float) (double) Config.MAX_DASH_STRENGTH.get());
        buf.writeFloat((float) (double) Config.MAX_DASH_LEVEL.get());
    }

    public ClientConfigPacket(FriendlyByteBuf buf) {
        DASH_SUPERCHARGE_SPEED = buf.readFloat();
        DASH_ENERGY_CONSUMPTION = buf.readFloat();
        DASH_SUPERCHARGE_ENERGY_CONSUMPTION_MULTI = buf.readFloat();
        MAX_DASH_LEVEL = buf.readFloat();
        MAX_DASH_STRENGTH = buf.readFloat();
    }

    public void handle(Player player, NetworkEvent.Context context) {
      
    }
}