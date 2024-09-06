package com.jujutsucraftaddon.effects;

import com.jujutsucraftaddon.network.PacketHandler;
import net.minecraft.network.protocol.game.ClientboundRemoveMobEffectPacket;
import net.minecraft.network.protocol.game.ClientboundUpdateMobEffectPacket;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

public class ImprovedMobEffect extends MobEffect {
    public boolean updateClient;

    public ImprovedMobEffect(MobEffectCategory category, int color) {
        super(category, color);
    }

    public void onInstancedAdded(LivingEntity entity, MobEffectInstance instance) {
        if (updateClient && !(entity instanceof Player))
            PacketHandler.sendToTrackingVanilla(entity, new ClientboundUpdateMobEffectPacket(entity.getId(), instance));
    }

    public void applyEffectTick(LivingEntity entity, MobEffectInstance instance) {
    }

    public void onInstanceRemove(LivingEntity entity, MobEffectInstance instance) {
        if (updateClient && !(entity instanceof Player))
            PacketHandler.sendToTrackingVanilla(entity, new ClientboundRemoveMobEffectPacket(entity.getId(), instance.getEffect()));
    }

    public boolean isDurationEffectTick(int duration, int amplifier) {
        return true;
    }
}
