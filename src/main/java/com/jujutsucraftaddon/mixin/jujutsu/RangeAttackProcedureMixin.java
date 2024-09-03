package com.jujutsucraftaddon.mixin.jujutsu;

import com.jujutsucraftaddon.capabilities.data.JujutsuData;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalBooleanRef;
import net.mcreator.jujutsucraft.procedures.RangeAttackProcedure;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.LevelAccessor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = RangeAttackProcedure.class, remap = false)
public class RangeAttackProcedureMixin {

    @Inject(method = "execute", at = @At(value = "INVOKE", target = "Lnet/minecraft/nbt/CompoundTag;getBoolean(Ljava/lang/String;)Z", ordinal = 2, shift = At.Shift.BEFORE))
    private static void blackFlashChance(LevelAccessor world, double x, double y, double z, Entity entity, CallbackInfo ci, @Local(name = "BlackFlash") LocalBooleanRef BlackFlash, @Local(name = "blackflashable") LocalBooleanRef blackflashable) {
        if (entity instanceof Player player) {
            JujutsuData data = JujutsuData.get(player);

            if (data.blackFlashChance == -1)
                return;

            boolean blackFlash = Math.random() < data.blackFlashChance ? true : false;
            BlackFlash.set(blackFlash);
            blackflashable.set(blackFlash);
        }
    }
}
