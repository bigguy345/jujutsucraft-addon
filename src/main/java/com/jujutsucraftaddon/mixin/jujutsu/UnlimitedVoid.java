package com.jujutsucraftaddon.mixin.jujutsu;

import net.mcreator.jujutsucraft.init.JujutsucraftModMobEffects;
import net.mcreator.jujutsucraft.procedures.UnlimitedVoidActiveProcedure;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(UnlimitedVoidActiveProcedure.class)
public class UnlimitedVoid {
    @Redirect( method = "execute", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;addEffect(Lnet/minecraft/world/effect/MobEffectInstance;)Z", ordinal = 0))
    private static boolean removeSpeed2(LivingEntity instance, MobEffectInstance p_21165_){
        return instance.addEffect(new MobEffectInstance((MobEffect) JujutsucraftModMobEffects.BRAIN_DAMAGE.get(), 600, 4, false, false));
    }
}
