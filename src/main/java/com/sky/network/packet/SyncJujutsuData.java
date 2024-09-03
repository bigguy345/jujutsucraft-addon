package com.sky.network.packet;

import com.sky.data.JujutsuData;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class SyncJujutsuData {

    public JujutsuData data;
    public int senderID;

    public SyncJujutsuData(JujutsuData data) {
        this.data = data;
        senderID = data.player.getId();
    }

    public static void encode(SyncJujutsuData packet, FriendlyByteBuf buf) {
        buf.writeNbt((CompoundTag) packet.data.writeNBT());
        buf.writeInt(packet.senderID);
    }

    public static SyncJujutsuData decode(FriendlyByteBuf buf) {
        return new SyncJujutsuData(buf);
    }

    public SyncJujutsuData(FriendlyByteBuf buf) {
        data = new JujutsuData();
        data.readNBT(buf.readNbt());
        senderID = buf.readInt();
    }

    public static void handle(SyncJujutsuData packet, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        Entity entity = Minecraft.getInstance().level.getEntity(packet.senderID);
        if (entity != null && entity instanceof Player player) {
            context.enqueueWork(() -> {
                JujutsuData jujutsuData = JujutsuData.get(player);
                jujutsuData.readNBT(packet.data.writeNBT());
            });
        }
        context.setPacketHandled(true);
    }
}