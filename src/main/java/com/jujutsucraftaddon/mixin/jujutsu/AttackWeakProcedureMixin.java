package com.jujutsucraftaddon.mixin.jujutsu;

import com.jujutsucraftaddon.events.CommonEvents;
import net.mcreator.jujutsucraft.procedures.AttackWeakProcedure;
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
    private static void onBlockAttack(LevelAccessor world, double x, double y, double z, Entity entity, CallbackInfo ci) {
        if (entity instanceof LivingEntity ent)
            CommonEvents.domainBlockBreak(ent);
    }
}