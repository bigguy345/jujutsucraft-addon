package com.jujutsucraftaddon.mixin.jujutsu;

import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalDoubleRef;
import net.mcreator.jujutsucraft.init.JujutsucraftModMobEffects;
import net.mcreator.jujutsucraft.procedures.KeyReverseCursedTechniqueOnKeyPressedProcedure;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = KeyReverseCursedTechniqueOnKeyPressedProcedure.class, remap = false)
public class RCTKeyBindPressedMixin {

    @Inject(method = "execute", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;getServer()Lnet/minecraft/server/MinecraftServer;", ordinal = 1, shift = At.Shift.BEFORE,remap = true))
    private static void changeRCTStrengthWithZone(Entity entity, CallbackInfo ci, @Local(name = "level") LocalDoubleRef level) {

        if (entity instanceof LivingEntity ent) {
            MobEffectInstance zone = ent.getEffect(JujutsucraftModMobEffects.ZONE.get());

            if (zone != null) {
                int amplifier = zone.getAmplifier();
                if (amplifier == 0)
                    level.set(10);
                else if (amplifier == 1)
                    level.set(11);
                else if (amplifier == 2)
                    level.set(12);
                else if (amplifier == 3)
                    level.set(14);
                else if (amplifier == 4)
                    level.set(16);
            }
        }
    }
}