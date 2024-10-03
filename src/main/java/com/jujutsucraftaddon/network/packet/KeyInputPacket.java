package com.jujutsucraftaddon.network.packet;

import com.jujutsucraftaddon.network.Packet;
import com.jujutsucraftaddon.utility.JujuUtil;
import net.mcreator.jujutsucraft.init.JujutsucraftModMobEffects;
import net.mcreator.jujutsucraft.procedures.KeyStartTechniqueOnKeyPressedProcedure;
import net.mcreator.jujutsucraft.procedures.KeyStartTechniqueOnKeyReleasedProcedure;
import net.mcreator.jujutsucraft.procedures.LogicStartProcedure;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
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

    private ServerPlayer player;
    private ServerLevel world;
    private JujutsuData data;

    public void handle(Player player, NetworkEvent.Context context) {
        ServerLevel world = (ServerLevel) player.level();
        switch (keyName.toLowerCase()) {
            case "domain_expansion":
                domainExpansionKey();
                break;
            case "hi":
                //                data.levels.incrementDashLevel(100);
                //     player.setHealth(1);
                //data.data.PlayerSelectCurseTechnique = 20;
                //  player.getPersistentData().putInt("addonSelectTechnique", 20);
                // world.getServer().getCommands().performPrefixedCommand(player.getServer().createCommandSourceStack(), "kill @e[type=!minecraft:player]");
                break;
        }
    }

    public void domainExpansionKey() {
        if (!JujuUtil.hasDomainExpansion(data))
            return;

        double old = data.data.PlayerSelectCurseTechnique;
        CompoundTag nbt = player.getPersistentData();
        if (action == PRESS) {
            nbt.putInt("addonOldTechnique", (int) old);
            nbt.putBoolean("addonDomainDontResetTechnique", true);
        }


        boolean domainActive = player.hasEffect(JujutsucraftModMobEffects.DOMAIN_EXPANSION.get());
        if (action == HOLD && LogicStartProcedure.execute(player)) {
            if (!domainActive)
                data.data.PlayerSelectCurseTechnique = 20;
            else
                data.data.PlayerSelectCurseTechnique = 21;
            data.setSelectedTechnique(!domainActive ? 20 : 21);

            if (data.data.PlayerSelectCurseTechnique == 20 || data.data.PlayerSelectCurseTechnique == 21)
                KeyStartTechniqueOnKeyPressedProcedure.execute(world, player.getX(), player.getY(), player.getZ(), player);
        } else if (action == RELEASE) {
            if (nbt.contains("addonOldTechnique")) {
                old = nbt.getInt("addonOldTechnique");
                data.setSelectedTechnique(old);
                nbt.remove("addonOldTechnique");
            }
            KeyStartTechniqueOnKeyReleasedProcedure.execute(player);
        }
    }
}