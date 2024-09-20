package com.jujutsucraftaddon.network.packet.animation;

import com.jujutsucraftaddon.network.Packet;
import com.jujutsucraftaddon.network.PacketHandler;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.network.NetworkEvent;

public class C2SAnimationPacket extends Packet {

    public AnimationPackets.Type type;
    public CompoundTag data;

    public C2SAnimationPacket(AnimationPackets.Type type, CompoundTag data) {
        this.type = type;
        this.data = data;
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeInt(type.ordinal());
        buf.writeNbt(data);
    }

    public C2SAnimationPacket(FriendlyByteBuf buf) {
        type = AnimationPackets.Type.values()[buf.readInt()];
        data = buf.readNbt();
    }

    public void handle(Player player, NetworkEvent.Context context) {
        PacketHandler.sendToTracking(player, new S2CAnimationPacket(player.getId(), type, data));
    }
}
