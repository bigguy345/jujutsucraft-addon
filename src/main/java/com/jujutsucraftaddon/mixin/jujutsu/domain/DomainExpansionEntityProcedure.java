package com.jujutsucraftaddon.mixin.jujutsu.domain;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.LevelAccessor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = net.mcreator.jujutsucraft.procedures.AIDomainExpansionEntityProcedure.class, remap = false)
public class DomainExpansionEntityProcedure {

    @Inject(method = "execute", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;discard()V", shift = At.Shift.BEFORE))
    private static void afterDomainCompletelyFinished(LevelAccessor world, double x, double y, double z, Entity entity, CallbackInfo ci) {

    }
}