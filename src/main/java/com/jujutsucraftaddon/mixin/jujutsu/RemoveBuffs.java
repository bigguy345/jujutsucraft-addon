package com.jujutsucraftaddon.mixin.jujutsu;

import net.mcreator.jujutsucraft.procedures.PhysicalGiftedEffectOnEffectActiveTickProcedure;
import net.mcreator.jujutsucraft.procedures.PlayerPhysicalAbilityProcedure;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(PlayerPhysicalAbilityProcedure.class)
public class RemoveBuffs {

    @Redirect( method = "execute(Lnet/minecraftforge/eventbus/api/Event;Lnet/minecraft/world/entity/Entity;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/commands/Commands;performPrefixedCommand(Lnet/minecraft/commands/CommandSourceStack;Ljava/lang/String;)I", ordinal = 0))
    private static int removeSpeed(Commands instance, CommandSourceStack p_230958_, String p_230959_){
        return 1;
    }

    @Redirect( method = "execute(Lnet/minecraftforge/eventbus/api/Event;Lnet/minecraft/world/entity/Entity;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/commands/Commands;performPrefixedCommand(Lnet/minecraft/commands/CommandSourceStack;Ljava/lang/String;)I", ordinal = 1))
    private static int removeSpeed1(Commands instance, CommandSourceStack p_230958_, String p_230959_){
        return 1;
    }

    @Redirect( method = "execute(Lnet/minecraftforge/eventbus/api/Event;Lnet/minecraft/world/entity/Entity;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/commands/Commands;performPrefixedCommand(Lnet/minecraft/commands/CommandSourceStack;Ljava/lang/String;)I", ordinal = 2))
    private static int removeSpeed2(Commands instance, CommandSourceStack p_230958_, String p_230959_){
        return 1;
    }

    @Redirect( method = "execute(Lnet/minecraftforge/eventbus/api/Event;Lnet/minecraft/world/entity/Entity;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;addEffect(Lnet/minecraft/world/effect/MobEffectInstance;)Z", ordinal = 0))
    private static boolean removeSpeed3(LivingEntity instance, MobEffectInstance p_21165_){
        return false;
    }

    @Redirect( method = "execute(Lnet/minecraftforge/eventbus/api/Event;Lnet/minecraft/world/entity/Entity;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;addEffect(Lnet/minecraft/world/effect/MobEffectInstance;)Z", ordinal = 1))
    private static boolean removeSpeed4(LivingEntity instance, MobEffectInstance p_21165_){
        return false;
    }

    @Redirect( method = "execute(Lnet/minecraftforge/eventbus/api/Event;Lnet/minecraft/world/entity/Entity;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;addEffect(Lnet/minecraft/world/effect/MobEffectInstance;)Z", ordinal = 2))
    private static boolean removeSpeed5(LivingEntity instance, MobEffectInstance p_21165_){
        return false;
    }
}

