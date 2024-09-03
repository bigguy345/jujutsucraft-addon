package com.jujutsucraftaddon.mixin.jujutsu;

import net.mcreator.jujutsucraft.procedures.InstantSpiritBodyofDistortedKillingEffectEffectStartedappliedProcedure;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(InstantSpiritBodyofDistortedKillingEffectEffectStartedappliedProcedure.class)
public class MahitoFix {
    @Redirect( method = "execute", at = @At(value = "INVOKE", target = "Lnet/minecraft/commands/Commands;performPrefixedCommand(Lnet/minecraft/commands/CommandSourceStack;Ljava/lang/String;)I", ordinal = 0))
    private static int removeSpeed1(Commands instance, CommandSourceStack p_230958_, String p_230959_){
        return 1;
    }
    @Redirect( method = "execute", at = @At(value = "INVOKE", target = "Lnet/minecraft/commands/Commands;performPrefixedCommand(Lnet/minecraft/commands/CommandSourceStack;Ljava/lang/String;)I", ordinal = 1))
    private static int removeSpeed2(Commands instance, CommandSourceStack p_230958_, String p_230959_){
        return 1;
    }
    @Redirect( method = "execute", at = @At(value = "INVOKE", target = "Lnet/minecraft/commands/Commands;performPrefixedCommand(Lnet/minecraft/commands/CommandSourceStack;Ljava/lang/String;)I", ordinal = 2))
    private static int removeSpeed3(Commands instance, CommandSourceStack p_230958_, String p_230959_){
        return 1;
    }
    @Redirect( method = "execute", at = @At(value = "INVOKE", target = "Lnet/minecraft/commands/Commands;performPrefixedCommand(Lnet/minecraft/commands/CommandSourceStack;Ljava/lang/String;)I", ordinal = 3))
    private static int removeSpeed4(Commands instance, CommandSourceStack p_230958_, String p_230959_){
        return 1;
    }
    @Redirect( method = "execute", at = @At(value = "INVOKE", target = "Lnet/minecraft/commands/Commands;performPrefixedCommand(Lnet/minecraft/commands/CommandSourceStack;Ljava/lang/String;)I", ordinal = 4))
    private static int removeSpeed5(Commands instance, CommandSourceStack p_230958_, String p_230959_){
        return 1;
    }
    @Redirect( method = "execute", at = @At(value = "INVOKE", target = "Lnet/minecraft/commands/Commands;performPrefixedCommand(Lnet/minecraft/commands/CommandSourceStack;Ljava/lang/String;)I", ordinal = 5))
    private static int removeSpeed6(Commands instance, CommandSourceStack p_230958_, String p_230959_){
        return 1;
    }
    @Redirect( method = "execute", at = @At(value = "INVOKE", target = "Lnet/minecraftforge/items/ItemHandlerHelper;giveItemToPlayer(Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/item/ItemStack;)V", ordinal = 0), remap = false)
    private static void removeSpeed1(Player player, ItemStack stack){
    }
    @Redirect( method = "execute", at = @At(value = "INVOKE", target = "Lnet/minecraftforge/items/ItemHandlerHelper;giveItemToPlayer(Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/item/ItemStack;)V", ordinal = 1), remap = false)
    private static void removeSpeed2(Player player, ItemStack stack){
    }
    @Redirect( method = "execute", at = @At(value = "INVOKE", target = "Lnet/minecraftforge/items/ItemHandlerHelper;giveItemToPlayer(Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/item/ItemStack;)V", ordinal = 2), remap = false)
    private static void removeSpeed3(Player player, ItemStack stack){
    }
}
