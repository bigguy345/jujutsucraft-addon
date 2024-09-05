package com.jujutsucraftaddon.mixin.mc;

import com.jujutsucraftaddon.effects.IMobEffectInstance;
import net.minecraft.network.protocol.game.ClientboundUpdateMobEffectPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

@Mixin(MobEffectInstance.class)
public class MobEffectInstanceMixin implements IMobEffectInstance {
    @Shadow
    private int duration;

    @Shadow
    private int amplifier;

    @Unique
    @Override
    public void setDuration(int duration) {
        this.duration = duration;
    }

    @Unique
    @Override
    public void setAmplifier(int duration) {
        this.amplifier = duration;
    }

    @Unique
    @Override
    public void updateClient(LivingEntity entity) {
        if (entity instanceof ServerPlayer player)
            player.connection.send(new ClientboundUpdateMobEffectPacket(player.getId(), (MobEffectInstance) (Object) this));
    }
}