package com.jujutsucraftaddon.mixin.mc;

import com.jujutsucraftaddon.Config;
import com.jujutsucraftaddon.entity.ILivingEntity;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalFloatRef;
import net.minecraft.network.protocol.game.ServerboundMovePlayerPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = ServerGamePacketListenerImpl.class, remap = true)
public class ServerGamePacketListenerMixin {

    @Shadow
    public ServerPlayer player;

    @Inject(method = "handleMovePlayer", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerPlayer;isFallFlying()Z", ordinal = 1))
    private void disablePlayerTurning(ServerboundMovePlayerPacket p_9874_, CallbackInfo ci, @Local(name = "f2",print = true) LocalFloatRef maxAllowedVelocity) {
        if (((ILivingEntity) player).isDashing())
            maxAllowedVelocity.set(500);
    }

    @ModifyVariable(method = "handleMovePlayer", at = @At("STORE"),ordinal = 2)
    private float increaseMaxAllowedVelocity(float x) {
        if (((ILivingEntity) player).isDashing())
            return Config.ALLOWED_DASH_VELOCITY;
        return x;
    }
}