package com.jujutsucraftaddon.network.packet;

import com.jujutsucraftaddon.capabilities.data.BarrierBreakProgressData;
import com.jujutsucraftaddon.network.Packet;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.network.NetworkEvent;

import java.util.HashMap;

public class BarrierBreakProgessPacket extends Packet {
    public static HashMap<BlockPos, Byte> TRACKED_BARRIERS = new HashMap<>();

    private BlockPos pos;
    private byte progress;
    
    public BarrierBreakProgessPacket(BlockPos pos, byte progress) {
        this.pos = pos;
        this.progress = progress;
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeBlockPos(this.pos);
        buf.writeByte(this.progress);
    }

    public BarrierBreakProgessPacket(FriendlyByteBuf buf) {
        this.pos = buf.readBlockPos();
        this.progress = buf.readByte();
    }

    public void handle(Player player, NetworkEvent.Context context) {
        if (progress == -1)
            TRACKED_BARRIERS.remove(pos);
        else
            TRACKED_BARRIERS.put(pos, progress);
    }
}