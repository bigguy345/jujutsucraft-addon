package com.jujutsucraftaddon.mixin.jujutsu.attacks;

import com.jujutsucraftaddon.events.CommonEvents;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalDoubleRef;
import net.mcreator.jujutsucraft.procedures.AttackWeakProcedure;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.LevelAccessor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = AttackWeakProcedure.class, remap = false)
public class AttackWeakProcedureMixin {

    @Inject(method = "execute", at = @At(value = "FIELD", target = "Lnet/minecraft/core/particles/ParticleTypes;EXPLOSION:Lnet/minecraft/core/particles/SimpleParticleType;", shift = At.Shift.BEFORE, remap = true))
    private static void onBlockAttack(LevelAccessor world, double x, double y, double z, Entity entity, CallbackInfo ci, @Local(name = "x_pos") LocalDoubleRef x_pos, @Local(name = "y_pos") LocalDoubleRef y_pos, @Local(name = "z_pos") LocalDoubleRef z_pos) {
        if (entity instanceof LivingEntity ent) {
            BlockPos toBreak = BlockPos.containing(x_pos.get(), y_pos.get(), z_pos.get());
            CommonEvents.domainBlockBreak(ent,toBreak);
        }
    }
}