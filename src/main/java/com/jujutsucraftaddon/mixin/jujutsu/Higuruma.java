package arai.jjkaddon.mixin;

import net.mcreator.jujutsucraft.init.JujutsucraftModMobEffects;
import net.mcreator.jujutsucraft.procedures.DeadlySentencingActiveProcedure;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.LevelAccessor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(DeadlySentencingActiveProcedure.class)
public class Higuruma {
    @Redirect(method = "execute", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;addEffect(Lnet/minecraft/world/effect/MobEffectInstance;)Z", ordinal = 1))
    private static boolean removeSpeed1(LivingEntity instance, MobEffectInstance p_21165_) {
         instance.addEffect(new MobEffectInstance(new MobEffectInstance((MobEffect)JujutsucraftModMobEffects.UNSTABLE.get(), 1800, 2, false, false)));
        return instance.addEffect(new MobEffectInstance(new MobEffectInstance((MobEffect)JujutsucraftModMobEffects.BRAIN_DAMAGE.get(), 1800, 2, false, false)));
    }
}
