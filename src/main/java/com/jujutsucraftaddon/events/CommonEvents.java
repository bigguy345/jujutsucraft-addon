package com.jujutsucraftaddon.events;

import com.jujutsucraftaddon.capabilities.data.JujutsuData;
import com.jujutsucraftaddon.effects.IMobEffectInstance;
import com.jujutsucraftaddon.effects.ModEffects;
import com.jujutsucraftaddon.events.custom.BlackFlashEvent;
import com.jujutsucraftaddon.utility.ValueUtil;
import net.mcreator.jujutsucraft.init.JujutsucraftModMobEffects;
import net.mcreator.jujutsucraft.network.JujutsucraftModVariables;
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

            /////////////////////////////////////////////////
            /////////////////////////////////////////////////
            //Give or update player's Zone
            if (zone != null) {
                if (zone.getAmplifier() < 4) {
                    int duration = 0;
                    switch (zone.getAmplifier()) {
                        case 0:
                            duration = 3600;
                            break;
                        case 1:
                            duration = 2400;
                            break;
                        case 2:
                            duration = 1800;
                            break;
                        default:
                            duration = 1200;
                            break;
                    }

                    IMobEffectInstance effect = (IMobEffectInstance) zone;
                    effect.setDuration(duration);
                    effect.setDuration(zone.getAmplifier() + 1);
                    effect.updateClient(player);
                }
            } else
                player.addEffect(zone = new MobEffectInstance(JujutsucraftModMobEffects.ZONE.get(), 3600, 0, true, true));

            /////////////////////////////////////////////////
            /////////////////////////////////////////////////
            //Zone multiplier and chances handling
            int debuffRecoverAmmount = 5;
            float damageMulti = data.blackFlashDamageMulti;
            float zoneDamageMulti = 1f, zoneKnockbackMulti = 1f, costReductionChance = 0, cooldownReductionChance = 0, debuffRecoverChance = 0.3f;
            int zoneCostReductionAmplifier = 0, zoneCooldownReductionAmplifier = 0;
            if (zone != null) {
                switch (zone.getAmplifier()) {
                    case 0:
                        debuffRecoverAmmount = 6;
                        debuffRecoverChance = ValueUtil.randomBetween(0.75f, 1);
                        ;
                        jjcData.PlayerCursePower += jjcData.PlayerCursePowerMAX * 0.25f;
                        zoneDamageMulti = ValueUtil.randomBetween(1, 1.25f);
                        zoneKnockbackMulti = 5;

                        costReductionChance = ValueUtil.randomBetween(0.5f, 1);
                        cooldownReductionChance = ValueUtil.randomBetween(0.5f, 1);
                        zoneCostReductionAmplifier = ValueUtil.randomBetween(0, 1);
                        zoneCooldownReductionAmplifier = ValueUtil.randomBetween(0, 1);
                        break;
                    case 1:
                        debuffRecoverAmmount = 8;
                        debuffRecoverChance = ValueUtil.randomBetween(0.5f, 1);
                        ;
                        jjcData.PlayerCursePower += jjcData.PlayerCursePowerMAX * 0.15f;
                        zoneDamageMulti = ValueUtil.randomBetween(1, 1.5f);
                        zoneKnockbackMulti = 6;

                        costReductionChance = ValueUtil.randomBetween(0.5f, 1f);
                        cooldownReductionChance = ValueUtil.randomBetween(0.5f, 1f);
                        zoneCostReductionAmplifier = ValueUtil.randomBetween(1, 2);
                        zoneCooldownReductionAmplifier = ValueUtil.randomBetween(1, 2);
                        break;
                    case 2:
                        debuffRecoverAmmount = 10;
                        debuffRecoverChance = 0.8f;
                        jjcData.PlayerCursePower += jjcData.PlayerCursePowerMAX * 0.1f;
                        zoneDamageMulti = ValueUtil.randomBetween(1, 1.75f);
                        zoneKnockbackMulti = 7;

                        costReductionChance = ValueUtil.randomBetween(0.4f, 0.8f);
                        cooldownReductionChance = ValueUtil.randomBetween(0.4f, 0.8f);
                        zoneCostReductionAmplifier = ValueUtil.randomBetween(2, 3);
                        zoneCooldownReductionAmplifier = ValueUtil.randomBetween(2, 3);
                        break;
                    case 3:
                        debuffRecoverChance = 12;
                        jjcData.PlayerCursePower += jjcData.PlayerCursePowerMAX * 0.05f;
                        zoneDamageMulti = ValueUtil.randomBetween(1, 2f);
                        zoneKnockbackMulti = 8;

                        costReductionChance = ValueUtil.randomBetween(0.25f, 0.75f);
                        cooldownReductionChance = ValueUtil.randomBetween(0.25f, 0.75f);
                        zoneCostReductionAmplifier = ValueUtil.randomBetween(3, 4);
                        zoneCooldownReductionAmplifier = ValueUtil.randomBetween(3, 4);
                        break;
                    default:
                        debuffRecoverAmmount = 15;
                        debuffRecoverChance = 1f;
                        zoneDamageMulti = ValueUtil.randomBetween(1, 2.5f);
                        zoneKnockbackMulti = 10;

                        costReductionChance = ValueUtil.randomBetween(0.1f, 0.4f);
                        cooldownReductionChance = ValueUtil.randomBetween(0.1f, 0.4f);
                        zoneCostReductionAmplifier = ValueUtil.randomBetween(4, 8);
                        zoneCooldownReductionAmplifier = ValueUtil.randomBetween(4, 8);
                        break;
                }
            } else {
                zoneKnockbackMulti = 5;
                jjcData.PlayerCursePower += jjcData.PlayerCursePowerMAX * 0.3f;
            }

            //////////////////////////////////////////////////
            //////////////////////////////////////////////////
            //Main BF damage handling
            event.damage *= damageMulti * zoneDamageMulti;

            if (jjcData.PlayerCursePower > jjcData.PlayerCursePowerMAX) {
                jjcData.PlayerCursePower = jjcData.PlayerCursePowerMAX;
            }

            double random = Math.random();

            debuffRecoverAmmount = debuffRecoverAmmount * 20;


            //////////////////////////////////////////////////
            //////////////////////////////////////////////////
            //Remove debuffs ,I want it so debuffRecoverChance succeeding removes all debuffs, and not just certain ones

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
                    IMobEffectInstance effect = (IMobEffectInstance) fatigue;
                    effect.setDuration(fatigue.getDuration() / 2);
                    effect.updateClient(player);
                }

                MobEffectInstance unstable = player.getEffect(JujutsucraftModMobEffects.UNSTABLE.get());
                if (unstable != null) {
                    IMobEffectInstance effect = (IMobEffectInstance) unstable;
                    effect.setDuration(unstable.getDuration() / 2);
                    effect.updateClient(player);
                }

                MobEffectInstance brainDamage = player.getEffect(JujutsucraftModMobEffects.BRAIN_DAMAGE.get());
                if (brainDamage != null) {
                    IMobEffectInstance effect = (IMobEffectInstance) brainDamage;
                    effect.setDuration(brainDamage.getDuration() / 2);
                    effect.updateClient(player);
                }
            }


            /////////////////////////////////////////////////
            /////////////////////////////////////////////////
            //Give player buffs 
            if (Math.random() <= costReductionChance) {
                MobEffectInstance costReduction = player.getEffect(ModEffects.COST_REDUCTION.get());
                if (costReduction != null) {
                    if (costReduction.getAmplifier() < zoneCostReductionAmplifier) {
                        IMobEffectInstance effect = (IMobEffectInstance) costReduction;
                        effect.setAmplifier(zoneCostReductionAmplifier);
                        effect.setDuration(zone.getDuration());
                        effect.updateClient(player);
                    }
                } else
                    player.addEffect(new MobEffectInstance(ModEffects.COST_REDUCTION.get(), zone.getDuration(), zoneCostReductionAmplifier, false, false));
            }

            if (Math.random() <= cooldownReductionChance) {
                MobEffectInstance cooldownReduction = player.getEffect(ModEffects.COOLDOWN_REDUCTION.get());
                if (cooldownReduction != null) {
                    if (cooldownReduction.getAmplifier() < zoneCooldownReductionAmplifier) {
                        IMobEffectInstance effect = (IMobEffectInstance) cooldownReduction;
                        effect.setAmplifier(zoneCooldownReductionAmplifier);
                        effect.setDuration(zone.getDuration());
                        effect.updateClient(player);
                    }
                } else
                    player.addEffect(new MobEffectInstance(ModEffects.COOLDOWN_REDUCTION.get(), zone.getDuration(), zoneCooldownReductionAmplifier, false, false));
            }


            ///////////////////////////////////////////////////
            ////////////////////////////////////////////////////
            //Charged attack handling (extra knockback & damage)
            double weakCharge = event.attacker.getPersistentData().getDouble("cnt5");
            double weakChargeKnockback = weakCharge == 0 ? 1 : weakCharge * 0.0025f;
            double strongCharge = event.attacker.getPersistentData().getDouble("cnt6");
            double strongChargeKnockback = 1;

            float knockoutChance = 1f;

            if (strongCharge >= 1 && strongCharge < 3) {
                strongChargeKnockback = 0.05f;
                event.damage *= ValueUtil.randomBetween(1, 1.25f) / 1.3f;
            } else if (strongCharge >= 3 && strongCharge < 5) {
                strongChargeKnockback = 0.2f;
                event.damage *= ValueUtil.randomBetween(1.25f, 1.5f) / 1.4f;
                knockoutChance = 0.25f;
            } else if (strongCharge >= 5) {
                strongChargeKnockback = 0.4f;
                event.damage *= ValueUtil.randomBetween(1.5f, 2) / 1.6f;
                knockoutChance = 0.6f;
            }

            MobEffectInstance knockout = event.attacked.getEffect(ModEffects.KNOCKOUT_EFFECT.get());
            if (Math.random() <= knockoutChance) {
                if (knockout == null) {
                    event.attacked.addEffect(new MobEffectInstance(ModEffects.KNOCKOUT_EFFECT.get(), 200, 99, false, false));
                    event.knockback = 0;
                }
            } else if (weakCharge >= 1 || strongCharge >= 1)
                event.knockback *= 1 + (zoneKnockbackMulti * weakChargeKnockback * strongChargeKnockback);
        }
    }
}
