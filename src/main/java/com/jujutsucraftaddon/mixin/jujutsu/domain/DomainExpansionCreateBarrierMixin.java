package com.jujutsucraftaddon.mixin.jujutsu.domain;

import com.jujutsucraftaddon.capabilities.data.JujutsuData;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalDoubleRef;
import net.mcreator.jujutsucraft.init.JujutsucraftModMobEffects;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.LevelAccessor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

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

    @Inject(method = "execute", at = @At(value = "FIELD", target = "Lnet/mcreator/jujutsucraft/network/JujutsucraftModVariables;PLAYER_VARIABLES_CAPABILITY:Lnet/minecraftforge/common/capabilities/Capability;", ordinal = 5, shift = At.Shift.BEFORE))
    private static void preventTechniqueReset(LevelAccessor world, double x, double y, double z, Entity entity, CallbackInfo ci, @Local(name = "_setval") LocalDoubleRef val) {
        if (entity instanceof Player player) {
            if (player.getPersistentData().getBoolean("addonDomainDontResetTechnique")) {
                val.set(JujutsuData.get(player).data.PlayerSelectCurseTechnique);
                player.getPersistentData().remove("addonDomainDontResetTechnique");
            }
        }
    }
}