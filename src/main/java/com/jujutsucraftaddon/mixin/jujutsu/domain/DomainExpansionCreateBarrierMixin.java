package com.jujutsucraftaddon.mixin.jujutsu.domain;

import net.mcreator.jujutsucraft.init.JujutsucraftModMobEffects;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = net.mcreator.jujutsucraft.procedures.DomainExpansionCreateBarrierProcedure.class, remap = false)
public class DomainExpansionCreateBarrierMixin {

    @Redirect(method = "execute", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;addEffect(Lnet/minecraft/world/effect/MobEffectInstance;)Z", ordinal = 3, remap = true))
    private static boolean setDomainTime(LivingEntity instance, MobEffectInstance p_21165_) {
        int duration = 0;
        if (instance.getPersistentData().getBoolean("FORCE_DOMAIN"))
            duration = instance.getPersistentData().getInt("DOMAIN_TIME");
        else
            duration = instance.getPersistentData().getDouble("select") == 29.0 ? 3600 : 1200;

        return instance.addEffect(new MobEffectInstance(JujutsucraftModMobEffects.DOMAIN_EXPANSION.get(), duration, (int) instance.getPersistentData().getDouble("cnt2"), true, false));
    }
}