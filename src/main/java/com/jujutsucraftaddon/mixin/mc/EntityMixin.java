package com.jujutsucraftaddon.mixin.mc;

import com.jujutsucraftaddon.effects.ModEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = Entity.class)
public class EntityMixin {

    @Inject(method = "turn", at = @At("HEAD"), cancellable = true)
    private void disablePlayerTurning(double p_19885_, double p_19886_, CallbackInfo ci) {
        LivingEntity mob = (LivingEntity) (Object) this;
        if (mob.hasEffect(ModEffects.KNOCKOUT_EFFECT.get()))
            ci.cancel();
    }
}