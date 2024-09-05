package com.jujutsucraftaddon.mixin.jujutsu;

import com.jujutsucraftaddon.capabilities.data.JujutsuData;
import com.jujutsucraftaddon.events.custom.BlackFlashEvent;
import com.jujutsucraftaddon.utility.ValueUtil;
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
                blackFlashChance += 0.0125f;

            MobEffectInstance zone = player.getEffect(JujutsucraftModMobEffects.ZONE.get());
            if (zone != null) {
                switch (zone.getAmplifier()) {
                    case 0:
                        blackFlashChance += 0.025f;
                    case 1:
                        blackFlashChance += 0.05f;
                        break;
                    case 2:
                        blackFlashChance += 0.075f;
                        break;
                    case 3:
                        blackFlashChance += 0.1f;
                        break;
                    default:
                        blackFlashChance += ValueUtil.randomBetween(0.2f, 0.6f);
                        break;
                }
            }

            double weakCharge = entity.getPersistentData().getDouble("cnt5");
            blackFlashChance += weakCharge * (data.landedFirstBlackFlash ? 0.001 : 0);

            double strongCharge = entity.getPersistentData().getDouble("cnt6");
            if (strongCharge >= 1 && strongCharge < 2)
                blackFlashChance += data.landedFirstBlackFlash ? ValueUtil.randomBetween(0.0125f, 0.0375f) : 0;
            else if (strongCharge >= 2 && strongCharge < 3)
                blackFlashChance += data.landedFirstBlackFlash ? ValueUtil.randomBetween(0.025f, 0.075f) : 0;
            else if (strongCharge >= 3)
                blackFlashChance += data.landedFirstBlackFlash ? ValueUtil.randomBetween(0.075f, 0.15f) : 0;


            boolean blackFlash = Math.random() < blackFlashChance ? true : false;
            BlackFlash.set(blackFlash);
            blackflashable.set(blackFlash);
        }
    }

    @Inject(method = "execute", at = @At(value = "INVOKE", target = "Lnet/mcreator/jujutsucraft/procedures/CursedToolsAbilityProcedure;execute(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/entity/Entity;)V", ordinal = 0, shift = At.Shift.BEFORE))
    private static void blackFlashEvent(LevelAccessor world, double x, double y, double z, Entity entity, CallbackInfo ci, @Local(name = "knockback") LocalDoubleRef knockback, @Local(name = "entityiterator") LocalRef<Entity> attacked, @Local(name = "damage_sorce") LocalDoubleRef damage_sorce, @Local(name = "highPower") LocalBooleanRef blackFlash) {
        if (!blackFlash.get())
            return;

        BlackFlashEvent event = new BlackFlashEvent((LivingEntity) entity, (LivingEntity) attacked.get(), damage_sorce.get() / 8, knockback.get());
        MinecraftForge.EVENT_BUS.post(event);

        damage_sorce.set(event.damage);
        knockback.set(event.knockback);
    }

    @Redirect(method = "execute", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;addEffect(Lnet/minecraft/world/effect/MobEffectInstance;)Z", ordinal = 5))
    private static boolean removeGivingZone(LivingEntity instance, MobEffectInstance p_21165_) {
        if (instance instanceof Player)
            return false;

        return instance.addEffect(new MobEffectInstance(JujutsucraftModMobEffects.ZONE.get(), 3600, 0, true, true));
    }
}