package com.jujutsucraftaddon.mixin.jujutsu;

import com.jujutsucraftaddon.capabilities.data.JujutsuData;
import com.jujutsucraftaddon.effects.ModEffects;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalDoubleRef;
import net.mcreator.jujutsucraft.network.JujutsucraftModVariables;
import net.mcreator.jujutsucraft.procedures.KeyChangeTechniqueOnKeyPressedProcedure;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.LevelAccessor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = KeyChangeTechniqueOnKeyPressedProcedure.class, remap = false)
public class KeyChangeTechniqueMixin {

    @Inject(method = "execute", at = @At(value = "FIELD", target = "Lnet/mcreator/jujutsucraft/init/JujutsucraftModMobEffects;SUKUNA_EFFECT:Lnet/minecraftforge/registries/RegistryObject;", shift = At.Shift.BEFORE))
    private static void implementCostReductionEffect(LevelAccessor world, double x, double y, double z, Entity entity, CallbackInfo ci, @Local(name = "cost") LocalDoubleRef cost) {


        if (entity instanceof Player player) {
            MobEffectInstance costReduction = player.getEffect(ModEffects.COST_REDUCTION.get());
            if (costReduction == null)
                return;

            JujutsucraftModVariables.PlayerVariables vars = JujutsuData.get(player).getPlayerVariables();
            vars.PlayerSelectCurseTechniqueCost *= 1 - (double) Math.min(costReduction.getAmplifier() + 1, 10) / 10;
        }
    }
}