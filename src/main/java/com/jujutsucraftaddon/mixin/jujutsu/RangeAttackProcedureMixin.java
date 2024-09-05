package com.jujutsucraftaddon.mixin.jujutsu;

import com.jujutsucraftaddon.capabilities.data.JujutsuData;
import com.jujutsucraftaddon.events.custom.BlackFlashEvent;
import com.jujutsucraftaddon.utility.ValueUtil;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalBooleanRef;
import com.llamalad7.mixinextras.sugar.ref.LocalDoubleRef;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import net.mcreator.jujutsucraft.init.JujutsucraftModMobEffects;
import net.mcreator.jujutsucraft.network.JujutsucraftModVariables;
import net.mcreator.jujutsucraft.procedures.RangeAttackProcedure;
import net.minecraft.world.effect.MobEffect;
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

            double playerCurseTechnique = player.getCapability(JujutsucraftModVariables.PLAYER_VARIABLES_CAPABILITY, null).orElse(new JujutsucraftModVariables.PlayerVariables()).PlayerCurseTechnique2;

            float blackFlashChance = data.blackFlashChance == -1 ? 0.002f : data.blackFlashChance;

            //Yuji Fix, extra 0.55% chance to black flash if ur yuji
            if(playerCurseTechnique==21){blackFlashChance+=0.005f;}

            //Nanami Fix, I think? ???
            if(player.hasEffect(JujutsucraftModMobEffects.SPECIAL.get())){
                if(player.getEffect(JujutsucraftModMobEffects.SPECIAL.get()).getAmplifier()>0){blackFlashChance+=0.007f;}}

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
                        blackFlashChance += ValueUtil.randomBetween(0.3f,0.6f);
                        break;
                }
            }

            double weakCharge = entity.getPersistentData().getDouble("cnt5");
            blackFlashChance += (float) (weakCharge * (data.landedFirstBlackFlash ? 0.001 : 0));

            double strongCharge = entity.getPersistentData().getDouble("cnt6");


            if(strongCharge>9000)
                //Todo fix
                blackFlashChance = 1;
            else if (strongCharge >= 1 && strongCharge < 2)
                blackFlashChance += data.landedFirstBlackFlash ? ValueUtil.randomBetween(0.0125f, 0.0375f) : 0;
            else if (strongCharge >= 2 && strongCharge < 3)
                blackFlashChance += data.landedFirstBlackFlash ? ValueUtil.randomBetween(0.025f, 0.075f) : 0;
            else if (strongCharge >= 3)
                blackFlashChance += data.landedFirstBlackFlash ? ValueUtil.randomBetween(0.075f, 0.15f) : 0;


            //Maki fix
            if(playerCurseTechnique==-1){blackFlashChance=-1;}



            boolean blackFlash = Math.random() <= blackFlashChance;
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

    //To make the heal work more properly, since rn the fatigue and brain damage heal auto apply from the mod itself, Also u should add a brain dmg heal to the black flash event
    @Redirect(method = "execute", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;removeEffect(Lnet/minecraft/world/effect/MobEffect;)Z", ordinal = 3))
    private static boolean removeBaseModFatigueHeal1(LivingEntity instance, MobEffect p_21196_) {
        return true;
    }
    @Redirect(method = "execute", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;addEffect(Lnet/minecraft/world/effect/MobEffectInstance;)Z", ordinal = 6))
    private static boolean removeBaseModFatigueHeal2(LivingEntity instance, MobEffectInstance p_21165_) {
        return true;
    }
    @Redirect(method = "execute", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;removeEffect(Lnet/minecraft/world/effect/MobEffect;)Z", ordinal = 4))
    private static boolean removeBaseModBrainDamageHeal1(LivingEntity instance, MobEffect p_21196_) {
        return true;
    }
    @Redirect(method = "execute", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;addEffect(Lnet/minecraft/world/effect/MobEffectInstance;)Z", ordinal = 7))
    private static boolean removeBaseModBrainDamageHeal2(LivingEntity instance, MobEffectInstance p_21165_) {
        return true;
    }
}