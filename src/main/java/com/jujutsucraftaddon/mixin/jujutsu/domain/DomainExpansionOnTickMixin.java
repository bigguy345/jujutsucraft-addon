package com.jujutsucraftaddon.mixin.jujutsu.domain;

import net.mcreator.jujutsucraft.init.JujutsucraftModMobEffects;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = net.mcreator.jujutsucraft.procedures.DomainExpansionOnEffectActiveTickProcedure.class, remap = false)
public class DomainExpansionOnTickMixin {

    @Redirect(method = "execute", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;removeEffect(Lnet/minecraft/world/effect/MobEffect;)Z", ordinal = 5, remap = true))
    private static boolean onDomainRemoveEffect(LivingEntity instance, MobEffect p_21196_) {
        if (instance.getPersistentData().getBoolean("FORCE_DOMAIN"))
            return false;
        return instance.removeEffect(JujutsucraftModMobEffects.DOMAIN_EXPANSION.get());
    }
}