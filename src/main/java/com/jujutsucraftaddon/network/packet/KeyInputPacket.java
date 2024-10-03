package com.jujutsucraftaddon.network.packet;

import com.jujutsucraftaddon.network.Packet;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.network.NetworkEvent;

public class KeyInputPacket extends Packet {

    public static byte RELEASE = 0, PRESS = 1, HOLD = 2;
    public String keyName;
    public byte action;

    public KeyInputPacket(String keyName, int action) {
        this.keyName = keyName;
        this.action = (byte) action;
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeUtf(keyName);
        buf.writeByte(action);
    }

    public KeyInputPacket(FriendlyByteBuf buf) {
        keyName = buf.readUtf();
        action = buf.readByte();
    }

    public void handle(Player player, NetworkEvent.Context context) {
        ServerLevel world = (ServerLevel) player.level();
        switch (keyName.toLowerCase()) {
 
            case "hi":
                //                data.levels.incrementDashLevel(100);
                //     player.setHealth(1);
                //data.data.PlayerSelectCurseTechnique = 20;
                //  player.getPersistentData().putInt("addonSelectTechnique", 20);
                // world.getServer().getCommands().performPrefixedCommand(player.getServer().createCommandSourceStack(), "kill @e[type=!minecraft:player]");
                break;
        }
    }
}