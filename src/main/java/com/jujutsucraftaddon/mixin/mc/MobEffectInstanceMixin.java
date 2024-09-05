package com.jujutsucraftaddon.mixin.mc;

import com.jujutsucraftaddon.effects.IMobEffectInstance;
import com.jujutsucraftaddon.effects.ImprovedMobEffect;
import net.minecraft.network.protocol.game.ClientboundUpdateMobEffectPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MobEffectInstance.class)
public class MobEffectInstanceMixin implements IMobEffectInstance {
    @Shadow
    private int duration;

    @Shadow
    private int amplifier;

    @Shadow
    @Final
    private MobEffect effect;

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

    @Inject(method = "applyEffect", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/effect/MobEffect;applyEffectTick(Lnet/minecraft/world/entity/LivingEntity;I)V", shift = At.Shift.AFTER))
    public void initImprovedMobEffect(LivingEntity entity, CallbackInfo ci) {
        if (this.effect instanceof ImprovedMobEffect effect)
            effect.applyEffectTick(entity, (MobEffectInstance) (Object) this);
    }
}