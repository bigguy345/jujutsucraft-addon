package com.jujutsucraftaddon.mixin.jujutsu.domain;

import com.jujutsucraftaddon.capabilities.data.BarrierBreakProgressData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.LevelAccessor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = net.mcreator.jujutsucraft.procedures.DomainExpansionEffectExpiresProcedure.class, remap = false)
public class DomainExpansionExpiresMixin {

    @Inject(method = "execute", at = @At("HEAD"))
    private static void onDomainExpire(LevelAccessor world, double x, double y, double z, Entity entity, CallbackInfo ci) {
        if (entity instanceof LivingEntity livingEntity)
            BarrierBreakProgressData.removeAllProgress(livingEntity);
    }
}