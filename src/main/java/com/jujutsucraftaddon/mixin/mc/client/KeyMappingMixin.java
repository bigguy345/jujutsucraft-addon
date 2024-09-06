package com.jujutsucraftaddon.mixin.mc.client;

import com.jujutsucraftaddon.events.custom.client.KeyMappingDownEvent;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalBooleanRef;
import net.minecraft.client.KeyMapping;
import net.minecraftforge.common.MinecraftForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = KeyMapping.class)
public abstract class KeyMappingMixin {

    @Shadow
    private int clickCount;

    @Inject(method = "setDown", at = @At("HEAD"), cancellable = true)
    private void onDrop(boolean down, CallbackInfo ci, @Local(ordinal = 0) LocalBooleanRef isDown) {
        KeyMappingDownEvent event = new KeyMappingDownEvent((KeyMapping) (Object) this, down);
        MinecraftForge.EVENT_BUS.post(event);

        isDown.set(event.isDown);
        if (event.release)
            clickCount = 0;

        if (event.isCanceled())
            ci.cancel();
    }
}