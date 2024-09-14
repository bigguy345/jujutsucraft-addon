package com.jujutsucraftaddon.network.packet;

import com.jujutsucraftaddon.capabilities.data.JujutsuData;
import com.jujutsucraftaddon.network.Packet;
import net.mcreator.jujutsucraft.init.JujutsucraftModMobEffects;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.network.NetworkEvent;

public class ReversedCTPacket extends Packet {

    public int toHealID;

    public ReversedCTPacket(int heal) {
        this.toHealID = heal;
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeInt(toHealID);
    }

    public ReversedCTPacket(FriendlyByteBuf buf) {
        toHealID = buf.readInt();
    }

    public void handle(Player player, NetworkEvent.Context context) {
        ServerLevel world = (ServerLevel) player.level();
        JujutsuData data = JujutsuData.get(player);
        LivingEntity toHeal = (LivingEntity) world.getEntity(toHealID);

        if (toHealID != -1) {
            if (toHeal != null) {
                toHeal.getPersistentData().putInt("healedByID", player.getId());
                toHeal.addEffect(new MobEffectInstance(JujutsucraftModMobEffects.REVERSE_CURSED_TECHNIQUE.get(),-1 , data.levels.getRCTAmplifier(), true, true));
                data.toHealID = toHealID;
            }
        } else {
            toHeal = (LivingEntity) data.getHealingEntity();
            if (toHeal != null) {
                toHeal.getPersistentData().remove("healedByID");
                toHeal.removeEffect(JujutsucraftModMobEffects.REVERSE_CURSED_TECHNIQUE.get());
            }
            data.toHealID = -1;
        }
        
        data.syncTracking();
    }
}