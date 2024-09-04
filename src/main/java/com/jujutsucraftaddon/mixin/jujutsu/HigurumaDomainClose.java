package arai.jjkaddon.mixin;

import net.mcreator.jujutsucraft.init.JujutsucraftModMobEffects;
import net.mcreator.jujutsucraft.procedures.DomainExpansionEffectExpiresProcedure;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.LevelAccessor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(DomainExpansionEffectExpiresProcedure.class)
public class HigurumaDomainClose {
    @Inject(method = "execute", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;addEffect(Lnet/minecraft/world/effect/MobEffectInstance;)Z", ordinal = 6, shift = At.Shift.AFTER), locals = LocalCapture.CAPTURE_FAILHARD)
    private static void removeSpeed2(LevelAccessor world, double x, double y, double z, Entity entity, CallbackInfo ci) {
        LivingEntity _entity = (LivingEntity) entity;
        _entity.addEffect(new MobEffectInstance((MobEffect) JujutsucraftModMobEffects.UNSTABLE.get(), 1000, 0, false, false));

    }
}
