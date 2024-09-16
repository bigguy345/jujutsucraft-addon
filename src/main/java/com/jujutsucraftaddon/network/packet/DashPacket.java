package com.jujutsucraftaddon.network.packet;

import com.jujutsucraftaddon.Config;
import com.jujutsucraftaddon.capabilities.data.JujutsuData;
import com.jujutsucraftaddon.entity.ILivingEntity;
import com.jujutsucraftaddon.network.Packet;
import com.jujutsucraftaddon.skill.DashSkill;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.network.NetworkEvent;

public class DashPacket extends Packet {

    public DashSkill dash;

    public DashPacket(DashSkill dash) {
        this.dash = dash;
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeFloat(dash.strength);
        buf.writeFloat(dash.charge);
        buf.writeFloat(dash.superCharge);
        buf.writeFloat(dash.speed);
    }

    public DashPacket(FriendlyByteBuf buf) {
        dash = new DashSkill(buf.readFloat(), buf.readFloat(), buf.readFloat());
        dash.speed = buf.readFloat();
    }

    public void handle(Player player, NetworkEvent.Context context) {
        ServerLevel world = (ServerLevel) player.level();
        JujutsuData data = JujutsuData.get(player);

        if (dash.strength != 0) {
            data.currentDash = dash;
            data.levels.incrementDashLevel((float) (dash.strength * Config.DASH_LEVEL_GAIN.get()));
            data.data.PlayerCursePowerChange -= DashSkill.calculateEnergyConsumed(dash.strength, dash.superCharge, Config.DASH_ENERGY_CONSUMPTION.get(), Config.DASH_SUPERCHARGE_ENERGY_CONSUMPTION_MULTI.get());

            ((ILivingEntity) player).setIsDashing(true);
            player.getPersistentData().putBoolean("killFallDamage", true);
        } else {
            data.currentDash = null;
            ((ILivingEntity) player).setIsDashing(false);
            System.out.println("velocity " + dash.speed);
        }
    }
}