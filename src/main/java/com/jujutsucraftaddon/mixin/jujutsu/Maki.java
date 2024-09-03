package com.jujutsucraftaddon.mixin.jujutsu;

import net.mcreator.jujutsucraft.procedures.PhysicalGiftedEffectOnEffectActiveTickProcedure;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(PhysicalGiftedEffectOnEffectActiveTickProcedure.class)
public class Maki{

    @Redirect( method = "execute(Lnet/minecraft/world/level/LevelAccessor;Lnet/minecraft/world/entity/Entity;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/commands/Commands;performPrefixedCommand(Lnet/minecraft/commands/CommandSourceStack;Ljava/lang/String;)I", ordinal = 2))
    private static int removeSpeed3(Commands instance, CommandSourceStack p_230958_, String p_230959_){
        return 1;
    }
}
