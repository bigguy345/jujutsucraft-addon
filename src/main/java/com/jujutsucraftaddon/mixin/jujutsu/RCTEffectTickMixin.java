package com.jujutsucraftaddon.mixin.jujutsu;

import com.jujutsucraftaddon.capabilities.data.JujutsuData;
import com.jujutsucraftaddon.utility.ValueUtil;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalDoubleRef;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import net.mcreator.jujutsucraft.init.JujutsucraftModMobEffects;
import net.mcreator.jujutsucraft.procedures.ReverseCursedTechniqueOnEffectActiveTickProcedure;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.LevelAccessor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = ReverseCursedTechniqueOnEffectActiveTickProcedure.class, remap = false)
public class RCTEffectTickMixin {

    @Inject(method = "execute", at = @At("HEAD"), cancellable = true)
    private static void cancelSelfRCTWhenHealingOthers(LevelAccessor world, double x, double y, double z, Entity entity, CallbackInfo ci) {
        if (entity instanceof Player player) {
            JujutsuData data = JujutsuData.get(player);
            if (data.toHealID != -1) {
                player.removeEffect(JujutsucraftModMobEffects.REVERSE_CURSED_TECHNIQUE.get());
                ci.cancel();
            }
        }
    }

    @Inject(method = "execute", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;getCapability(Lnet/minecraftforge/common/capabilities/Capability;Lnet/minecraft/core/Direction;)Lnet/minecraftforge/common/util/LazyOptional;", ordinal = 0, shift = At.Shift.BEFORE, remap = true))
    private static void decreaseRCTEnergyDrain(LevelAccessor world, double x, double y, double z, Entity entity, CallbackInfo ci, @Local(name = "Amount") LocalDoubleRef Amount, @Local(name = "fatigue") LocalDoubleRef Fatigue) {

        if (entity instanceof LivingEntity ent) {
            MobEffectInstance zone = ent.getEffect(JujutsucraftModMobEffects.ZONE.get());
            if (zone != null) {
                int amplifier = zone.getAmplifier();
                double ammount = Amount.get();
                if (amplifier == 0)
                    ammount = 5;
                else if (amplifier == 1)
                    ammount = 5;
                else if (amplifier == 2)
                    ammount = 5;
                else if (amplifier == 3)
                    ammount = 5;
                else if (amplifier == 4)
                    ammount = 5;

                Amount.set(ammount / Fatigue.get());
            }
        }
    }

    @Redirect(method = "execute", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;addEffect(Lnet/minecraft/world/effect/MobEffectInstance;)Z", ordinal = 1, remap = true))
    private static boolean onFatigue(LivingEntity instance, MobEffectInstance p_21165_) {
        int increment = 20;

        //The higher the RCT level, the lower the fatigue (highest level is 0.5x less fatigue)
        if (instance instanceof Player player) {
            JujutsuData data = JujutsuData.get(player);
            float lvlMaxLvlRatio = Math.min(data.levels.getRCTLevel() / JujutsuData.Levels.MAX_RCT_LEVEL, 1);
            increment = (int) Math.max(20 * (1 - lvlMaxLvlRatio), 10);
        }
        int fatigueDuration = instance.hasEffect(JujutsucraftModMobEffects.FATIGUE.get()) ? instance.getEffect(JujutsucraftModMobEffects.FATIGUE.get()).getDuration() : 0;

        return instance.addEffect(new MobEffectInstance(JujutsucraftModMobEffects.FATIGUE.get(), Math.min(fatigueDuration + increment, 6000), 0, false, false));
    }

    @Redirect(method = "execute", at = @At(value = "INVOKE", target = "Lnet/minecraft/commands/Commands;performPrefixedCommand(Lnet/minecraft/commands/CommandSourceStack;Ljava/lang/String;)I", remap = true))
    private static int onHealedByOther(Commands instance, CommandSourceStack stack, String string, @Local(name = "entity") LocalRef<Entity> ent, @Local(name = "NUM1") LocalDoubleRef NUM1, @Local(name = "fatigue") LocalDoubleRef Fatigue) {
        if (ent.get() instanceof LivingEntity entity) {
            double amountHealed = Math.abs(NUM1.get());
            if (entity.getPersistentData().contains("healedByID")) {
                Entity healer = entity.level().getEntity(entity.getPersistentData().getInt("healedByID"));


                //Entity doing the healing
                if (healer instanceof Player player) {
                    JujutsuData healerData = JujutsuData.get(player);
                    if (healerData.data.PlayerCursePower > 10) {

                        //Fatigue
                        double healerFatigue = healerData.levels.getFatigue();
                        amountHealed /= healerFatigue;


                        //If healing others, decrease heal amount by 4x (max rct level is only 2x)
                        amountHealed *= ValueUtil.lerp(0.25f, 0.5f, healerData.levels.getRCTLevel() / JujutsuData.Levels.MAX_RCT_LEVEL);
                        healerData.levels.incrementRCTLevel((float) (amountHealed / 50));

                        //The further the healer is from healed, the lower the healing. 0-1 where 1 is closest
                        float distance = 1 - entity.distanceTo(healer) / 5;
                        amountHealed = amountHealed * Math.max(distance, 0.05f);

                        float lvlMaxLvlRatio = Math.min(healerData.levels.getRCTLevel() / JujutsuData.Levels.MAX_RCT_LEVEL, 1);

                        //The higher the RCT level, the lower the fatigue (highest level is 0.5x less fatigue)
                        healerData.levels.incrementFatigue((int) Math.max(20 * (1 - lvlMaxLvlRatio), 10));

                        //healer CE drain
                        double amount = entity.getPersistentData().getBoolean("CursedSpirit") ? 5.0 : 10.0;
                        healerData.data.PlayerCursePowerChange -= amount * healerFatigue;

                        return instance.performPrefixedCommand(stack, "jjc_heal " + amountHealed + " " + entity.getStringUUID());
                    } else {
                        healerData.toHealID = -1;
                        entity.removeEffect(JujutsucraftModMobEffects.REVERSE_CURSED_TECHNIQUE.get());
                        entity.getPersistentData().remove("healedByID");
                        return 0;
                    }
                }
            }

            //Increment RCT level. 1 heart healed is 1/50 levels.
            if (entity instanceof Player player) {
                JujutsuData data = JujutsuData.get(player);
                data.levels.incrementRCTLevel((float) (Math.abs(NUM1.get()) / 50));
            }
        }


        return instance.performPrefixedCommand(stack, string);
    }
}