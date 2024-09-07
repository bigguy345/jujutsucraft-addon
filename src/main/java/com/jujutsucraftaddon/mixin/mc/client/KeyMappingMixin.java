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
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = KeyMapping.class)
public abstract class KeyMappingMixin {

    @Shadow
    private int clickCount;

    @Shadow
    private boolean isDown;

    @Inject(method = "consumeClick", at = @At(value = "FIELD", target = "Lnet/minecraft/client/KeyMapping;clickCount:I", ordinal = 1), cancellable = true)
    private void onConsumeClick(CallbackInfoReturnable<Boolean> cir) {
        KeyMappingDownEvent event = new KeyMappingDownEvent((KeyMapping) (Object) this, isDown, clickCount);
        MinecraftForge.EVENT_BUS.post(event);

        clickCount = event.clickCount;

        if (event.isCanceled())
            cir.setReturnValue(false);
    }

    @Inject(method = "setDown", at = @At("HEAD"), cancellable = true)
    private void onSetDown(boolean down, CallbackInfo ci, @Local(ordinal = 0) LocalBooleanRef isDown) {
        KeyMappingDownEvent event = new KeyMappingDownEvent((KeyMapping) (Object) this, down, clickCount);
        MinecraftForge.EVENT_BUS.post(event);

        isDown.set(event.isDown);
        clickCount = event.clickCount;

        if (event.isCanceled())
            ci.cancel();
    }
}