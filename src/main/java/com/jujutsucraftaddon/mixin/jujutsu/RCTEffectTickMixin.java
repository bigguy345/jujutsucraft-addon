package com.jujutsucraftaddon.mixin.jujutsu;

import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalDoubleRef;
import net.mcreator.jujutsucraft.init.JujutsucraftModMobEffects;
import net.mcreator.jujutsucraft.procedures.ReverseCursedTechniqueOnEffectActiveTickProcedure;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.LevelAccessor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = ReverseCursedTechniqueOnEffectActiveTickProcedure.class, remap = false)
public class RCTEffectTickMixin {

    @Inject(method = "execute", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;getCapability(Lnet/minecraftforge/common/capabilities/Capability;Lnet/minecraft/core/Direction;)Lnet/minecraftforge/common/util/LazyOptional;", ordinal = 0, shift = At.Shift.BEFORE,remap = true))
    private static void decreaseRCTEnergyDrain(LevelAccessor world, double x, double y, double z, Entity entity, CallbackInfo ci, @Local(name = "Amount") LocalDoubleRef Amount, @Local(name = "fatigue") LocalDoubleRef Fatigue) {

        if (entity instanceof LivingEntity ent) {
            MobEffectInstance zone = ent.getEffect(JujutsucraftModMobEffects.ZONE.get());
            if (zone != null) {
                int amplifier = zone.getAmplifier();
                double ammount = Amount.get();
                if (amplifier == 0)
                    ammount = 5;
                else if (amplifier == 1)
                    ammount = 5;
                else if (amplifier == 2)
                    ammount = 5;
                else if (amplifier == 3)
                    ammount = 5;
                else if (amplifier == 4)
                    ammount = 5;
                
                Amount.set(ammount / Fatigue.get());
            }
        }
    }
}