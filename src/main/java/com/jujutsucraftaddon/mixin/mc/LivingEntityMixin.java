package com.jujutsucraftaddon.mixin.mc;

import com.jujutsucraftaddon.effects.IMobEffectInstance;
import com.jujutsucraftaddon.effects.ModEffects;
import com.jujutsucraftaddon.entity.ILivingEntity;
import net.mcreator.jujutsucraft.init.JujutsucraftModMobEffects;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = LivingEntity.class)
public abstract class LivingEntityMixin implements ILivingEntity {

    @Shadow
    private float absorptionAmount;
    public boolean isDashing;

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

    /**
     * Returns damage that will be reduced from health after all sorts of mc damage calculations
     */
    @Unique
    @Override
    public double calculateFinalDamage(DamageSource damageSource, float damage) {
        if (damage == 0)
            return 0;

        damage = getDamageAfterArmorAbsorb(damageSource, damage);
        damage = getDamageAfterMagicAbsorb(damageSource, damage);
        return Math.max(damage - absorptionAmount, 0.0F);
    }

    @Unique
    @Override
    public void setIsDashing(boolean dashing) {
        isDashing = dashing;
    }

    @Unique
    @Override
    public boolean isDashing() {
        return isDashing;
    }

    @Shadow
    protected float getDamageAfterArmorAbsorb(DamageSource p_21162_, float p_21163_) {


        return p_21163_;
    }

    @Shadow
    protected float getDamageAfterMagicAbsorb(DamageSource p_21193_, float p_21194_) {
        return 0;
    }
}