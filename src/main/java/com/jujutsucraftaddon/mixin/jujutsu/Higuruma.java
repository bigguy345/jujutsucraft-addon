package com.jujutsucraftaddon.mixin.jujutsu;

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
    @Redirect(method = "execute", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;addEffect(Lnet/minecraft/world/effect/MobEffectInstance;)Z", ordinal = 3))
    private static boolean removeSpeed1(LivingEntity instance, MobEffectInstance p_21165_) {
        instance.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 1800, 2, false, false));
        return instance.addEffect(new MobEffectInstance(new MobEffectInstance((MobEffect)JujutsucraftModMobEffects.BRAIN_DAMAGE.get(), 1800, 2, false, false)));
    }
    @Inject(method = "execute", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;addEffect(Lnet/minecraft/world/effect/MobEffectInstance;)Z", ordinal = 3, shift = At.Shift.AFTER), locals = LocalCapture.CAPTURE_FAILHARD)
    private static void removeSpeed2(LevelAccessor world, double x, double y, double z, Entity entity, CallbackInfo ci) {

        //return instance.addEffect(new MobEffectInstance((MobEffect) JujutsucraftModMobEffects.BRAIN_DAMAGE.get(), 600, 0, false, false));
    }
    //@Redirect(method = "execute", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;addEffect(Lnet/minecraft/world/effect/MobEffectInstance;)Z", ordinal = 2))
    private static boolean removeSpeed3(LivingEntity instance, MobEffectInstance p_21165_) {
        instance.sendSystemMessage(Component.nullToEmpty("3"));
        return false;
       // return instance.addEffect(new MobEffectInstance((MobEffect) JujutsucraftModMobEffects.BRAIN_DAMAGE.get(), 600, 0, false, false));
    }
    //@Redirect(method = "execute", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;addEffect(Lnet/minecraft/world/effect/MobEffectInstance;)Z", ordinal = 3))
    private static boolean removeSpeed4(LivingEntity instance, MobEffectInstance p_21165_) {
        instance.sendSystemMessage(Component.nullToEmpty("4"));
        return false;
        //return instance.addEffect(new MobEffectInstance((MobEffect) JujutsucraftModMobEffects.BRAIN_DAMAGE.get(), 600, 0, false, false));
    }
    //@Redirect(method = "execute", at = @At(value = "INVOKE", target = "Lnet/minecraft/commands/Commands;performPrefixedCommand(Lnet/minecraft/commands/CommandSourceStack;Ljava/lang/String;)I", ordinal = 3))
    private static int removeSpeed41(Commands instance, CommandSourceStack p_230958_, String p_230959_) {
        return 1;
    }
    //@Redirect(method = "execute", at = @At(value = "INVOKE", target = "Lnet/minecraft/commands/Commands;performPrefixedCommand(Lnet/minecraft/commands/CommandSourceStack;Ljava/lang/String;)I", ordinal = 4))
    private static int removeSpeed42(Commands instance, CommandSourceStack p_230958_, String p_230959_) {
        return 1;
    }
    //@Redirect(method = "execute", at = @At(value = "INVOKE", target = "Lnet/minecraft/commands/Commands;performPrefixedCommand(Lnet/minecraft/commands/CommandSourceStack;Ljava/lang/String;)I", ordinal = 5))
    private static int removeSpeed421(Commands instance, CommandSourceStack p_230958_, String p_230959_) {
        return 1;
    }
    //@Redirect(method = "execute", at = @At(value = "INVOKE", target = "Lnet/minecraft/commands/Commands;performPrefixedCommand(Lnet/minecraft/commands/CommandSourceStack;Ljava/lang/String;)I", ordinal = 6))
    private static int removeSpeed43(Commands instance, CommandSourceStack p_230958_, String p_230959_) {
        return 1;
    }
    //@Redirect(method = "execute", at = @At(value = "INVOKE", target = "Lnet/minecraft/commands/Commands;performPrefixedCommand(Lnet/minecraft/commands/CommandSourceStack;Ljava/lang/String;)I", ordinal = 7))
    private static int removeSpeed44(Commands instance, CommandSourceStack p_230958_, String p_230959_) {
        return 1;
    }
}
