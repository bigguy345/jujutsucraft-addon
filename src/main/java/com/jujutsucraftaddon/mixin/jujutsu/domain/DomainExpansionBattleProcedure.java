package com.jujutsucraftaddon.mixin.jujutsu.domain;

import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = net.mcreator.jujutsucraft.procedures.DomainExpansionBattleProcedure.class, remap = false)
public class DomainExpansionBattleProcedure {

    @Inject(method = "execute", at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/world/level/LevelAccessor;getBlockEntity(Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/level/block/entity/BlockEntity;", shift = At.Shift.AFTER, remap = true))
    private static void setBarrierCaster(LevelAccessor world, double x, double y, double z, Entity entity, CallbackInfo ci, @Local(name = "_blockEntity") LocalRef<BlockEntity> barrierBlock) {
        BlockEntity barrierEntity = barrierBlock.get();
        if (barrierEntity != null) {
            barrierEntity.getPersistentData().putString("caster", entity.getUUID().toString());
        }
    }
    @Inject(method = "execute", at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/world/entity/EntityType;spawn(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/entity/MobSpawnType;)Lnet/minecraft/world/entity/Entity;", shift = At.Shift.AFTER, remap = true))
    private static void setDomainEntityCaster(LevelAccessor world, double x, double y, double z, Entity entity, CallbackInfo ci, @Local(name = "entityToSpawn") LocalRef<Entity> entityToSpawn) {
        Entity domainEntity = entityToSpawn.get();
        if (domainEntity != null) {
            domainEntity.getPersistentData().putString("caster", entity.getUUID().toString());
        }
    }
}