package com.jujutsucraftaddon.effects;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Unique;

public interface IMobEffectInstance {

    IMobEffectInstance setDuration(int duration);

    @Unique
    IMobEffectInstance setAmplifier(int duration);

    @Unique
    IMobEffectInstance updateClient(LivingEntity entity);
}
