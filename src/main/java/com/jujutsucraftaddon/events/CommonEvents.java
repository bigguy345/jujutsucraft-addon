package com.jujutsucraftaddon.events;

import com.jujutsucraftaddon.capabilities.data.BarrierBreakProgressData;
import com.jujutsucraftaddon.capabilities.data.JujutsuData;
import com.jujutsucraftaddon.client.ModSounds;
import com.jujutsucraftaddon.effects.IMobEffectInstance;
import com.jujutsucraftaddon.effects.ModEffects;
import com.jujutsucraftaddon.entity.ILivingEntity;
import com.jujutsucraftaddon.events.custom.BlackFlashEvent;
import com.jujutsucraftaddon.utility.BlockUtil;
import com.jujutsucraftaddon.utility.JujuUtil;
import com.jujutsucraftaddon.utility.ValueUtil;
import net.mcreator.jujutsucraft.init.JujutsucraftModMobEffects;
import net.mcreator.jujutsucraft.network.JujutsucraftModVariables;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Abilities;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.registries.ForgeRegistries;

// Events that fire on both client and server side
public class CommonEvents {
    public static long LAST_BARRIER_BREAK;

    public static void domainBlockBreak(LivingEntity entity) {
        if (System.currentTimeMillis() - LAST_BARRIER_BREAK < 100)
            return;

        ServerLevel world = (ServerLevel) entity.level();
        CompoundTag entityData = entity.getPersistentData();
        BlockPos toBreak = BlockPos.containing(entityData.getDouble("x_pos"), entityData.getDouble("y_pos"), entityData.getDouble("z_pos"));

        if (JujuUtil.isBarrier(world, toBreak)) {
            BlockEntity barrierEntity = world.getBlockEntity(toBreak);
            if (barrierEntity != null) {
                LivingEntity caster = JujuUtil.getDomainCaster(world, barrierEntity);
                if (caster == null) //|| entity == caster
                    return;

                boolean playerCaster = caster instanceof Player;
                double attackerDamage = entityData.getDouble("Damage");
                float strongCharge = (float) Math.max(entityData.getDouble("cnt6"), 1);

                float effectiveDamage = (float) ((ILivingEntity) caster).calculateFinalDamage(JujuUtil.getCurseDamageSource(entity), (float) attackerDamage);
                float casterHealth = caster.getHealth() * (playerCaster ? 10 : 1), casterMaxHealth = caster.getMaxHealth() * (playerCaster ? 10 : 1);
                boolean insideDomain = JujuUtil.isWithinDomain(caster, entity);

                //Damage to do as a percent of max health to break barrier. If enemy is at full health, it's 25% of that health to be done as damage to barrier block until it breaks
                //Decreases as enemy health drops.
                float damToDoInHealthPercent = Math.min(casterMaxHealth / 1000 * casterHealth / casterMaxHealth, 0.9f);
                float damAsPercOfMaxHealth = effectiveDamage / casterMaxHealth * ValueUtil.clamp(strongCharge, 1, 3);
                if (insideDomain)
                    damAsPercOfMaxHealth *= 0.25f;

                CompoundTag blockData = barrierEntity.getPersistentData();
                float newShatter = blockData.getFloat("shatter") + damAsPercOfMaxHealth;
                blockData.putFloat("shatter", newShatter);


                float shatterPercent = Math.min(newShatter / damToDoInHealthPercent, 1);
                byte prevBreakStage = BarrierBreakProgressData.getProgress(caster.getPersistentData(), toBreak);
                byte brokeBreakStage = (byte) (Math.floor(shatterPercent * 10 - 1));

                if (brokeBreakStage > -1 && brokeBreakStage != prevBreakStage) {
                    BarrierBreakProgressData.setProgress(caster, toBreak, shatterPercent < 1 ? brokeBreakStage : -1);
                    for (int i = 0; i < (brokeBreakStage == 9 ? 0 : (brokeBreakStage + 1) * 4); i++)
                        world.playSound(null, toBreak, ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("block.glass.break")), SoundSource.PLAYERS, ValueUtil.lerp(0.3f, 1f, shatterPercent), 1F);
                }

                ////////////////////////////////////////////////////////////////
                ////////////////////////////////////////////////////////////////
                //On barrier break
                if (shatterPercent >= 1) {
                    float shatterSize = 0.11f;
                    if (damToDoInHealthPercent > 0.8) {
                        effectiveDamage *= 1.5f;
                        shatterSize = 0.02f;
                    } else if (damToDoInHealthPercent > 0.6) {
                        shatterSize = 0.03f;
                    } else if (damToDoInHealthPercent > 0.4)
                        shatterSize = 0.05f;
                    else if (damToDoInHealthPercent > 0.35)
                        shatterSize = 0.06f;
                    else if (damToDoInHealthPercent > 0.3)
                        shatterSize = 0.07f;
                    else if (damToDoInHealthPercent > 0.25)
                        shatterSize = 0.08f;
                    else if (damToDoInHealthPercent > 0.2)
                        shatterSize = 0.085f;
                    else if (damToDoInHealthPercent > 0.15)
                        shatterSize = 0.09f;

                    int radius = (int) (Math.max(ValueUtil.clamp(strongCharge, 1, 5) * effectiveDamage * shatterSize * 1.4f, 2));
                    if (insideDomain)
                        radius = Math.max(radius / 4, 2);

                    //Breaks the blocks & replaces barrier blocks with what they actually were
                    BlockUtil.doMeleeExplosionDomainBarrier(entity, toBreak, radius);

                    //Decrease remaining domain time, the bigger the hole the higher the shave.
                    //If hole big enough, completely shatter domain instead
                    MobEffectInstance domain = caster.getEffect(JujutsucraftModMobEffects.DOMAIN_EXPANSION.get());
                    if (domain != null) {
                        if (radius >= 10)
                            caster.removeEffect(domain.getEffect());
                        else {
                            int secondsToShave = Math.max(radius * 4, 10);
                            ((IMobEffectInstance) domain).setDuration(domain.getDuration() - secondsToShave * 20).updateClient(caster);
                        }
                    }

                    world.playSound(null, toBreak, ModSounds.DOMAIN_SHATTER.get(), SoundSource.PLAYERS, 1.0F, 1F);
                }
            }
        }

        LAST_BARRIER_BREAK = System.currentTimeMillis();
    }

    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent event) {
        Player player = event.player;
        Abilities abilities = player.getAbilities();
        if (!abilities.mayfly) {
            abilities.mayfly = true;
            abilities.setFlyingSpeed(0.1f);
        }

        //If this event is firing on the client side, cancel it. Ensures everything after this line fires only on server side
        if (event.side == LogicalSide.CLIENT)
            return;

        if (player.tickCount % 10 == 0)
            JujutsuData.get(player).syncTracking();

        if (player.tickCount % 100 == 0) {
            BarrierBreakProgressData.sendProgressToTracking(player);
        }
    }

    @SubscribeEvent
    public void onEntityJoin(EntityJoinLevelEvent event) {
        if (event.getEntity() instanceof LivingEntity livingEntity)
            if (livingEntity.hasEffect(JujutsucraftModMobEffects.DOMAIN_EXPANSION.get()))
                BarrierBreakProgressData.sendProgressToTracking(livingEntity);
    }

    @SubscribeEvent
    public void onEntityTick(LivingEvent.LivingTickEvent event) {
        LivingEntity entity = event.getEntity();
        if (entity.tickCount == 5 && entity.hasEffect(JujutsucraftModMobEffects.DOMAIN_EXPANSION.get()))
            BarrierBreakProgressData.sendProgressToTracking(entity);
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
