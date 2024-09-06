package com.jujutsucraftaddon.mixin.mc;

import com.jujutsucraftaddon.effects.ModEffects;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.control.LookControl;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = Mob.class)
public class MobMixin {

    @Redirect(method = "serverAiStep", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/ai/control/LookControl;tick()V"))
    private void disableMobRotating(LookControl lookControl) {
        Mob mob = (Mob) (Object) this;
        if (mob.hasEffect(ModEffects.KNOCKOUT_EFFECT.get()))
            return;

        lookControl.tick();
    }
}