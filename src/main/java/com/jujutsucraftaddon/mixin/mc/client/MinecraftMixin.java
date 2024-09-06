package com.jujutsucraftaddon.mixin.mc.client;

import com.jujutsucraftaddon.effects.ModEffects;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = Minecraft.class)
public class MinecraftMixin {

    @Redirect(method = "handleKeybinds", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/LocalPlayer;drop(Z)Z"))
    private boolean onDrop(LocalPlayer player, boolean ctrlDown) {
        if (player.hasEffect(ModEffects.KNOCKOUT_EFFECT.get()))
            return false;

        return player.drop(ctrlDown);
    }

    @Redirect(method = "handleKeybinds", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/KeyMapping;consumeClick()Z", ordinal = 6))
    private boolean onHandSwitch(KeyMapping instance) {
        Minecraft mc = (Minecraft) (Object) this;
        if (mc.player.hasEffect(ModEffects.KNOCKOUT_EFFECT.get()))
            return false;

        return instance.consumeClick();
    }
}