package com.jujutsucraftaddon.mixin.jujutsu;

import net.mcreator.jujutsucraft.procedures.InstantSpiritBodyofDistortedKillingEffectOnEffectActiveTickProcedure;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(InstantSpiritBodyofDistortedKillingEffectOnEffectActiveTickProcedure.class)
public class MahitoFix2 {
    @Redirect( method = "execute(Lnet/minecraft/world/level/LevelAccessor;Lnet/minecraft/world/entity/Entity;)V", at = @At(value = "INVOKE", target = "Lnet/minecraftforge/items/ItemHandlerHelper;giveItemToPlayer(Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/item/ItemStack;)V", ordinal = 0), remap = false)
    private static void removeSpeed1(Player player, ItemStack stack){
    }
    @Redirect( method = "execute(Lnet/minecraft/world/level/LevelAccessor;Lnet/minecraft/world/entity/Entity;)V", at = @At(value = "INVOKE", target = "Lnet/minecraftforge/items/ItemHandlerHelper;giveItemToPlayer(Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/item/ItemStack;)V", ordinal = 1), remap = false)
    private static void removeSpeed2(Player player, ItemStack stack){
    }
    @Redirect( method = "execute(Lnet/minecraft/world/level/LevelAccessor;Lnet/minecraft/world/entity/Entity;)V", at = @At(value = "INVOKE", target = "Lnet/minecraftforge/items/ItemHandlerHelper;giveItemToPlayer(Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/item/ItemStack;)V", ordinal = 2), remap = false)
    private static void removeSpeed3(Player player, ItemStack stack){
    }
}
