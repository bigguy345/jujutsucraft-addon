package com.jujutsucraftaddon.mixin.jujutsu.attacks;

import com.jujutsucraftaddon.events.CommonEvents;
import com.jujutsucraftaddon.utility.Utility;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalDoubleRef;
import net.mcreator.jujutsucraft.procedures.AttackStrongSwordProcedure;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.LevelAccessor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = AttackStrongSwordProcedure.class, remap = false)
public class AttackStrongSwordProcedureMixin {

    @Inject(method = "execute", at = @At(value = "INVOKE", target = "Lnet/mcreator/jujutsucraft/procedures/RangeAttackProcedure;execute(Lnet/minecraft/world/level/LevelAccessor;DDDLnet/minecraft/world/entity/Entity;)V", shift = At.Shift.AFTER, remap = true))
    private static void onBlockAttack(LevelAccessor world, double x, double y, double z, Entity entity, CallbackInfo ci, @Local(name = "x_pos") LocalDoubleRef x_pos, @Local(name = "y_pos") LocalDoubleRef y_pos, @Local(name = "z_pos") LocalDoubleRef z_pos) {
        if (world.isClientSide())
            return;

        if (entity instanceof LivingEntity ent) {
            double reach = 4;
            if (ent instanceof Player player)
                reach = player.getBlockReach();

            BlockPos toBreak = Utility.rayTraceBlock(ent, reach);
            if (toBreak != null) 
                CommonEvents.domainBlockBreak(ent, toBreak);
            
        }
    }
}