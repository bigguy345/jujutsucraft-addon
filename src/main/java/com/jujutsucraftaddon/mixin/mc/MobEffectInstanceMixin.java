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
    public IMobEffectInstance setDuration(int duration) {
        this.duration = duration;
        return this;
    }

    @Unique
    @Override
    public IMobEffectInstance setAmplifier(int duration) {
        this.amplifier = duration;
        return this;
    }

    @Unique
    @Override
    public IMobEffectInstance updateClient(LivingEntity entity) {
        if (entity instanceof ServerPlayer player)
            player.connection.send(new ClientboundUpdateMobEffectPacket(player.getId(), (MobEffectInstance) (Object) this));
        return this;
    }

    @Inject(method = "applyEffect", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/effect/MobEffect;applyEffectTick(Lnet/minecraft/world/entity/LivingEntity;I)V", shift = At.Shift.AFTER))
    public void initImprovedMobEffect(LivingEntity entity, CallbackInfo ci) {
        if (this.effect instanceof ImprovedMobEffect effect)
            effect.applyEffectTick(entity, (MobEffectInstance) (Object) this);
    }
}