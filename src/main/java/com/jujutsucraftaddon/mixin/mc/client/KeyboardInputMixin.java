package com.jujutsucraftaddon.mixin.mc.client;

import com.jujutsucraftaddon.client.animation.Animations;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.Input;
import net.minecraft.client.player.KeyboardInput;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = KeyboardInput.class)
public abstract class KeyboardInputMixin extends Input {

    @Inject(method = "tick", at = @At("TAIL"))
    private void onTick(boolean p_234118_, float p_234119_, CallbackInfo ci) {
        if (!Animations.getController(Minecraft.getInstance().player).canMove()) {
            up = down = left = right = shiftKeyDown = jumping = false;
            forwardImpulse = leftImpulse = 0;
        }
    }
}