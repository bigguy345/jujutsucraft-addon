package com.jujutsucraftaddon.mixin.mc;

import com.jujutsucraftaddon.effects.IMobEffectInstance;
import com.jujutsucraftaddon.effects.ModEffects;
import net.mcreator.jujutsucraft.init.JujutsucraftModMobEffects;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = LivingEntity.class)
public class LivingEntityMixin {

    @Inject(method = "addEffect(Lnet/minecraft/world/effect/MobEffectInstance;Lnet/minecraft/world/entity/Entity;)Z", at = @At("HEAD"))
    public final void implementCooldownReductionEffect(MobEffectInstance effectInstance, Entity p_147209_, CallbackInfoReturnable<Boolean> cir) {
        LivingEntity entity = (LivingEntity) (Object) this;
        if (effectInstance.getEffect() == JujutsucraftModMobEffects.COOLDOWN_TIME.get()) {
            MobEffectInstance cooldownReduction = entity.getEffect(ModEffects.COOLDOWN_REDUCTION.get());
            if (cooldownReduction != null) {
                IMobEffectInstance effect = (IMobEffectInstance) effectInstance;

                float currentDuration = (float) effectInstance.getDuration();
                int newDuration = (int) (currentDuration * (1 - (double) Math.min(cooldownReduction.getAmplifier() + 1, 10) / 10));

                effect.setDuration(newDuration);
                effect.updateClient(entity);
            }
        }
    }

    @Inject(method = "addEffect(Lnet/minecraft/world/effect/MobEffectInstance;Lnet/minecraft/world/entity/Entity;)Z", at = @At("HEAD"))
    public final void onCooldownReductionEffect(MobEffectInstance effectInstance, Entity p_147209_, CallbackInfoReturnable<Boolean> cir) {
        LivingEntity entity = (LivingEntity) (Object) this;
        if (effectInstance.getEffect() == ModEffects.COOLDOWN_REDUCTION.get()) {
            MobEffectInstance cooldown = entity.getEffect(JujutsucraftModMobEffects.COOLDOWN_TIME.get());
            if (cooldown != null) {
                IMobEffectInstance effect = (IMobEffectInstance) cooldown;

                float currentDuration = (float) cooldown.getDuration();
                int newDuration = (int) (currentDuration * (1 - (double) Math.min(effectInstance.getAmplifier() + 1, 10) / 10));

                effect.setDuration(newDuration);
                effect.updateClient(entity);
            }
        }
    }
}