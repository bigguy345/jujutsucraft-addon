package com.jujutsucraftaddon.entity;

import net.minecraft.world.damagesource.DamageSource;
import org.spongepowered.asm.mixin.Unique;

public interface ILivingEntity {
    
    @Unique
    double calculateFinalDamage(DamageSource damageSource, float damage);

    @Unique
    void setIsDashing(boolean dashing);

    @Unique
    boolean isDashing();
}
