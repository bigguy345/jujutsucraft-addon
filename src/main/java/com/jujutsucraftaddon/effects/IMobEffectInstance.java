package com.jujutsucraftaddon.effects;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Unique;

public interface IMobEffectInstance {
    
    void setDuration(int duration);

    @Unique
    void setAmplifier(int duration);

    @Unique
    void updateClient(LivingEntity entity);
}
