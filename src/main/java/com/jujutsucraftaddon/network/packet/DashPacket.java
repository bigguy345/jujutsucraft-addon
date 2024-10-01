package com.jujutsucraftaddon.network.packet;

import com.jujutsucraftaddon.capabilities.data.JujutsuData;
import com.jujutsucraftaddon.entity.ILivingEntity;
import com.jujutsucraftaddon.network.Packet;
import com.jujutsucraftaddon.skill.DashSkill;
import com.jujutsucraftaddon.utility.BlockUtil;
import com.jujutsucraftaddon.utility.ValueUtil;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
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
            data.currentDash.init(data);
        } else {
            data.currentDash = null;
            ((ILivingEntity) player).setIsDashing(false);
            System.out.println("velocity " + dash.speed);
            if (dash.speed > 5) {
                float speedRatio = dash.speed / 23;
                BlockUtil.doSphericalExplosion(player, player.blockPosition(), (int) (ValueUtil.lerp(4f, 12, speedRatio)));
                world.playSound(null, player.blockPosition(), SoundEvents.HOSTILE_BIG_FALL, SoundSource.BLOCKS, 4, (1.0F + (world.random.nextFloat() - world.random.nextFloat()) * 0.2F) * 0.7F);
                world.playSound(null, player.blockPosition(), SoundEvents.GENERIC_EXPLODE, SoundSource.BLOCKS, dash.speed / 2, (1.0F + (world.random.nextFloat() - world.random.nextFloat()) * 0.2F) * 0.7F);
            }
        }
    }
}