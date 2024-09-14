package com.jujutsucraftaddon.mixin.jujutsu;

import com.jujutsucraftaddon.capabilities.data.JujutsuData;
import com.jujutsucraftaddon.effects.ModEffects;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalDoubleRef;
import net.mcreator.jujutsucraft.procedures.KeyReverseCursedTechniqueOnKeyPressedProcedure;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = KeyReverseCursedTechniqueOnKeyPressedProcedure.class, remap = false)
public class RCTKeyBindPressedMixin {

    @Inject(method = "execute", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;getServer()Lnet/minecraft/server/MinecraftServer;", ordinal = 1, shift = At.Shift.BEFORE, remap = true))
    private static void changeRCTStrength(Entity entity, CallbackInfo ci, @Local(name = "level") LocalDoubleRef level) {
        if (entity instanceof Player player) {
            JujutsuData data = JujutsuData.get(player);
            level.set(data.levels.getRCTAmplifier());
        }
    }

    @Inject(method = "execute", at = @At("HEAD"), cancellable = true)
    private static void disableRCTonKO(Entity entity, CallbackInfo ci) {
        if (entity instanceof LivingEntity ent) {
            MobEffectInstance ko = ent.getEffect(ModEffects.KNOCKOUT_EFFECT.get());
            if (ko != null)
                ci.cancel();
        }
    }
}