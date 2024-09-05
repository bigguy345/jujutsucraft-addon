package com.jujutsucraftaddon.events;

import com.jujutsucraftaddon.capabilities.data.JujutsuData;
import com.jujutsucraftaddon.events.custom.BlackFlashEvent;
import com.jujutsucraftaddon.utility.ValueUtil;
import net.mcreator.jujutsucraft.init.JujutsucraftModMobEffects;
import net.mcreator.jujutsucraft.network.JujutsucraftModVariables;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Abilities;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;

// Events that fire on both client and server side
public class CommonEvents {

    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent event) {
        Player player = event.player;
        Abilities abilities = player.getAbilities();
        if (!abilities.mayfly)
            abilities.mayfly = true;

        //If this event is firing on the client side, cancel it. Ensures everything after this line fires only on server side
        if (event.side == LogicalSide.CLIENT)
            return;

        if (player.tickCount % 10 == 0)
            JujutsuData.get(player).syncTracking();
    }

    @SubscribeEvent
    public void onBlackFlash(BlackFlashEvent event) {
        if (event.attacker.level().isClientSide())
            return;

        if (event.attacker instanceof Player player) {
            JujutsuData data = JujutsuData.get(player);
            JujutsucraftModVariables.PlayerVariables jjcData = data.getPlayerVariables();
            MobEffectInstance zone = player.getEffect(JujutsucraftModMobEffects.ZONE.get());

            if (!data.landedFirstBlackFlash)
                data.landedFirstBlackFlash = true;


            int debuffRecoverAmmount = 5;
            float damageMulti = data.blackFlashDamageMulti;
            float zoneDamageMulti = 1f, zoneKnockbackMulti = 1f, debuffRecoverChance = 0.3f, unstableRecoverChance = 0.1f;
            if (zone != null) {
                switch (zone.getAmplifier()) {
                    case 0:
                        debuffRecoverAmmount = 6;
                        debuffRecoverChance = 0.4f;
                        zoneDamageMulti = ValueUtil.randomBetween(1, 1.25f);
                        jjcData.PlayerCursePower += jjcData.PlayerCursePowerMAX * 0.25f;
                        zoneKnockbackMulti = 5;
                        break;
                    case 1:
                        debuffRecoverAmmount = 8;
                        debuffRecoverChance = 0.6f;
                        zoneDamageMulti = ValueUtil.randomBetween(1, 1.5f);
                        jjcData.PlayerCursePower += jjcData.PlayerCursePowerMAX * 0.15f;
                        zoneKnockbackMulti = 6;
                        break;
                    case 2:
                        debuffRecoverAmmount = 10;
                        debuffRecoverChance = 0.8f;
                        zoneDamageMulti = ValueUtil.randomBetween(1, 1.75f);
                        jjcData.PlayerCursePower += jjcData.PlayerCursePowerMAX * 0.1f;
                        zoneKnockbackMulti = 7;
                        break;
                    case 3:
                        debuffRecoverChance = 12;
                        zoneDamageMulti = ValueUtil.randomBetween(1, 2f);
                        jjcData.PlayerCursePower += jjcData.PlayerCursePowerMAX * 0.05f;
                        zoneKnockbackMulti = 8;
                        break;
                    default:
                        debuffRecoverAmmount = 15;
                        debuffRecoverChance = 1f;
                        zoneDamageMulti = ValueUtil.randomBetween(1, 2.5f);
                        zoneKnockbackMulti = 10;
                        break;
                }
            } else {
                zoneKnockbackMulti = 5;
                jjcData.PlayerCursePower += jjcData.PlayerCursePowerMAX * 0.3f;
            }

            if (jjcData.PlayerCursePower > jjcData.PlayerCursePowerMAX) {
                jjcData.PlayerCursePower = jjcData.PlayerCursePowerMAX;
            }

            double random = Math.random();

            debuffRecoverAmmount = debuffRecoverAmmount * 20;
            
            
            //I want it so debuffRecoverChance succeeding removes all debuffs, and not just certain ones
            if (random <= debuffRecoverChance) {

                //                boolean recoverUnstable = false;
                //                boolean recoverFatigue = false;
                //                if (fatigue != null && unstable != null) {
                //                    if (random <= (unstableRecoverChance * 0.5)) {
                //                        recoverUnstable = true;
                //                        recoverFatigue = true;
                //                    } else if (random <= unstableRecoverChance) {
                //                        recoverUnstable = true;
                //                    } else {
                //                        recoverFatigue = true;
                //                    }
                //                } else if (unstable != null) {
                //                    recoverFatigue = true;
                //                } else {
                //                    recoverUnstable = true;
                //                }

                //////////////////////////////////////////////
                //////////////////////////////////////////////
                // I removed fatigueDuration, unstableDuration, fatigueAmplifer and unstableAmplifier
                // Below is a more  efficient way of checking these status effects. If the below  doesn't do what you intended with the above vars lemme know
                MobEffectInstance fatigue = player.getEffect(JujutsucraftModMobEffects.FATIGUE.get());
                if (fatigue != null) {
                    player.removeEffect(JujutsucraftModMobEffects.FATIGUE.get());
                    player.addEffect(new MobEffectInstance(JujutsucraftModMobEffects.FATIGUE.get(), (int) (fatigue.getDuration() - debuffRecoverAmmount * ValueUtil.randomBetween(1, 1.2f)), fatigue.getAmplifier(), false, true));
                }

                MobEffectInstance unstable = player.getEffect(JujutsucraftModMobEffects.UNSTABLE.get());
                if (unstable != null) {
                    player.removeEffect(JujutsucraftModMobEffects.UNSTABLE.get());
                    player.addEffect(new MobEffectInstance(JujutsucraftModMobEffects.UNSTABLE.get(), (int) (unstable.getDuration() - debuffRecoverAmmount * ValueUtil.randomBetween(1, 1.2f)), unstable.getAmplifier(), false, false));
                }
            }


            event.damage *= damageMulti * zoneDamageMulti;

            double weakCharge = event.attacker.getPersistentData().getDouble("cnt5");
            double weakChargeKnockback = weakCharge == 0 ? 1 : weakCharge * 0.0025f;
            double strongCharge = event.attacker.getPersistentData().getDouble("cnt6");
            double strongChargeKnockback = 1;

            if (strongCharge >= 1 && strongCharge < 2) {
                strongChargeKnockback = 0.05f;
                event.damage *= ValueUtil.randomBetween(1, 1.25f) / 1.3f;
            } else if (strongCharge >= 2 && strongCharge < 3) {
                strongChargeKnockback = 0.1f;
                event.damage *= ValueUtil.randomBetween(1.25f, 1.5f) / 1.4f;
            } else if (strongCharge >= 3) {
                strongChargeKnockback = 0.4f;
                event.damage *= ValueUtil.randomBetween(1.5f, 2) / 1.6f;
            }

            if (weakCharge >= 1 || strongCharge >= 1)
                event.knockback *= 1 + (zoneKnockbackMulti * weakChargeKnockback * strongChargeKnockback);
        }
    }
}
