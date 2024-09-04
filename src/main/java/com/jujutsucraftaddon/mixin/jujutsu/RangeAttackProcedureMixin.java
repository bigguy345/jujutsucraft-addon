package com.jujutsucraftaddon.mixin.jujutsu;

import com.jujutsucraftaddon.capabilities.data.JujutsuData;
import com.jujutsucraftaddon.events.custom.BlackFlashEvent;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalBooleanRef;
import com.llamalad7.mixinextras.sugar.ref.LocalDoubleRef;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import net.mcreator.jujutsucraft.init.JujutsucraftModMobEffects;
import net.mcreator.jujutsucraft.procedures.RangeAttackProcedure;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.LevelAccessor;
import net.minecraftforge.common.MinecraftForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = RangeAttackProcedure.class, remap = false)
public class RangeAttackProcedureMixin {

    @Inject(method = "execute", at = @At(value = "INVOKE", target = "Lnet/minecraft/nbt/CompoundTag;getBoolean(Ljava/lang/String;)Z", ordinal = 2, shift = At.Shift.BEFORE))
    private static void blackFlashChance(LevelAccessor world, double x, double y, double z, Entity entity, CallbackInfo ci, @Local(name = "BlackFlash") LocalBooleanRef BlackFlash, @Local(name = "blackflashable") LocalBooleanRef blackflashable, @Local(name = "damage_source_player") LocalDoubleRef damage_source_player) {
        if (entity instanceof Player player) {
            JujutsuData data = JujutsuData.get(player);


            float blackFlashChance = data.blackFlashChance == -1 ? 0.002f : data.blackFlashChance;
            if (data.landedFirstBlackFlash)
                blackFlashChance += 0.025f;

            MobEffectInstance zone = player.getEffect(JujutsucraftModMobEffects.ZONE.get());
            if (zone != null) {
                switch (zone.getAmplifier()) {
                    case 0:
                        blackFlashChance += 0.05f;
                    case 1:
                        blackFlashChance += 0.075f;
                        break;
                    case 2:
                        blackFlashChance += 0.1f;
                        break;
                    case 3:
                        blackFlashChance += 0.2f;
                        break;
                    default:
                        blackFlashChance += 0.45f;
                        break;
                }
            }

            double weakCharge = entity.getPersistentData().getDouble("cnt5");
            blackFlashChance += weakCharge * (data.landedFirstBlackFlash ? 0.0075 : 0.0025f);

            double strongCharge = entity.getPersistentData().getDouble("cnt6");
            if (strongCharge >= 1 && strongCharge < 2)
                blackFlashChance += data.landedFirstBlackFlash ? 0.0375 : 0.0125;
            else if (strongCharge >= 2 && strongCharge < 3)
                blackFlashChance += data.landedFirstBlackFlash ? 0.075 : 0.025;
            else if (strongCharge >= 3)
                blackFlashChance += data.landedFirstBlackFlash ? 0.15 : 0.05;


            boolean blackFlash = Math.random() < blackFlashChance ? true : false;
            BlackFlash.set(blackFlash);
            blackflashable.set(blackFlash);
        }
    }

    @Inject(method = "execute", at = @At(value = "INVOKE", target = "Lnet/mcreator/jujutsucraft/procedures/CursedToolsAbilityProcedure;execute(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/entity/Entity;)V", ordinal = 0, shift = At.Shift.BEFORE))
    private static void blackFlashEvent(LevelAccessor world, double x, double y, double z, Entity entity, CallbackInfo ci, @Local(name = "knockback") LocalDoubleRef knockback, @Local(name = "entityiterator") LocalRef<Entity> attacked, @Local(name = "damage_sorce") LocalDoubleRef damage_sorce, @Local(name = "highPower") LocalBooleanRef blackFlash) {
        if (!blackFlash.get())
            return;

        BlackFlashEvent event = new BlackFlashEvent(entity, attacked.get(), damage_sorce.get() /4, knockback.get());
        MinecraftForge.EVENT_BUS.post(event);

        damage_sorce.set(event.damage);
        knockback.set(event.knockback);
    }

    @Redirect(method = "execute", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;addEffect(Lnet/minecraft/world/effect/MobEffectInstance;)Z", ordinal = 5))
    private static boolean blackFlashIncreasesZone(LivingEntity instance, MobEffectInstance p_21165_) {
        MobEffectInstance zone = instance.getEffect(JujutsucraftModMobEffects.ZONE.get());

        if (zone != null) {
            if (zone.getAmplifier() < 4) {
                instance.removeEffect(JujutsucraftModMobEffects.ZONE.get());
                return instance.addEffect(new MobEffectInstance(JujutsucraftModMobEffects.ZONE.get(), 6000 - (zone.getAmplifier() + 1) * 1200, zone.getAmplifier() + 1, true, true));
            } else
                return false;
        } else
            return instance.addEffect(new MobEffectInstance(JujutsucraftModMobEffects.ZONE.get(), 6000, 0, true, true));
    }
}