package com.jujutsucraftaddon.mixin.jujutsu;

import net.mcreator.jujutsucraft.init.JujutsucraftModMobEffects;
import net.mcreator.jujutsucraft.procedures.JackpotEffectStartedappliedProcedure;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(JackpotEffectStartedappliedProcedure.class)
public class HakariZone {
    @Redirect( method = "execute", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;addEffect(Lnet/minecraft/world/effect/MobEffectInstance;)Z"))
    private static boolean addInitialEffects(LivingEntity instance, MobEffectInstance p_21165_){
        int zoneAmpf = instance.hasEffect(JujutsucraftModMobEffects.ZONE.get()) ? instance.getEffect(JujutsucraftModMobEffects.ZONE.get()).getAmplifier() : 0;
        return instance.addEffect(new MobEffectInstance(JujutsucraftModMobEffects.ZONE.get(), instance.getEffect(JujutsucraftModMobEffects.JACKPOT.get()).getDuration(),zoneAmpf, false, false));
    }
}
