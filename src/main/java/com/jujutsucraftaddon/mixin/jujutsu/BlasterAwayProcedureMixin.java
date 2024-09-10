package com.jujutsucraftaddon.mixin.jujutsu;

import com.jujutsucraftaddon.events.CommonEvents;
import net.mcreator.jujutsucraft.procedures.BlasterAwayProcedure;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.LevelAccessor;
import net.minecraftforge.eventbus.api.Event;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = BlasterAwayProcedure.class, remap = false)
public class BlasterAwayProcedureMixin {

    @Inject(method = "execute(Lnet/minecraftforge/eventbus/api/Event;Lnet/minecraft/world/level/LevelAccessor;Lnet/minecraft/world/entity/Entity;)V", at = @At(value = "FIELD", target = "Lnet/minecraft/core/particles/ParticleTypes;EXPLOSION:Lnet/minecraft/core/particles/SimpleParticleType;", shift = At.Shift.BEFORE, remap = true))
    private static void onBlockAttack(Event event, LevelAccessor world, Entity entity, CallbackInfo ci) {
        if (entity instanceof LivingEntity ent)
            CommonEvents.domainBlockBreak(ent);
    }
}