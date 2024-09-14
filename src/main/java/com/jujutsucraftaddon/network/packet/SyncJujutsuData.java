package com.jujutsucraftaddon.network.packet;

import com.jujutsucraftaddon.capabilities.data.JujutsuData;
import com.jujutsucraftaddon.network.Packet;
import net.mcreator.jujutsucraft.network.JujutsucraftModVariables;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.network.NetworkEvent;

public class SyncJujutsuData extends Packet {

    public JujutsuData data;
    public int senderID;

    public SyncJujutsuData(JujutsuData data, int senderID) {
        this.data = data;
        this.senderID = senderID;
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeNbt((CompoundTag) data.writeNBT());
        buf.writeInt(senderID);
    }

    public SyncJujutsuData(FriendlyByteBuf buf) {
        data = new JujutsuData();
        data.readNBT(buf.readNbt());
        senderID = buf.readInt();
    }

    public void handle(Player player, NetworkEvent.Context context) {
        Entity entity = Minecraft.getInstance().level.getEntity(senderID);
        if (entity != null && entity instanceof Player pl) {
            data.player = pl;
            JujutsuData jujutsuData = JujutsuData.get(pl);
            jujutsuData.readNBT(data.writeNBT());
            jujutsuData.data = entity.getCapability(JujutsucraftModVariables.PLAYER_VARIABLES_CAPABILITY, null).orElse(new JujutsucraftModVariables.PlayerVariables());
        }
    }
}