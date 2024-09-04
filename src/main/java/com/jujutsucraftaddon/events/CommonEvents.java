package com.jujutsucraftaddon.events;

import com.jujutsucraftaddon.capabilities.data.JujutsuData;
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

        // JujutsucraftModVariables.PlayerVariables jjcData = JujutsuData.get(player).getPlayerVariables();
        //jjcData.PlayerCursePowerFormer = jjcData.PlayerCursePower;
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


            float damageMulti = data.blackFlashDamageMulti;
            float zoneDamageMulti = 1f;
            if (zone != null) {
                switch (zone.getAmplifier()) {
                    case 0:
                        zoneDamageMulti = ValueUtil.randomBetween(1, 1.5f);
                        jjcData.PlayerCursePower += jjcData.PlayerCursePowerMAX * 0.25f;
                        break;
                    case 1:
                        zoneDamageMulti = ValueUtil.randomBetween(1, 2f);
                        jjcData.PlayerCursePower += jjcData.PlayerCursePowerMAX * 0.15f;
                        break;
                    case 2:
                        zoneDamageMulti = ValueUtil.randomBetween(1, 2.5f);
                        jjcData.PlayerCursePower += jjcData.PlayerCursePowerMAX * 0.1f;
                        break;
                    case 3:
                        zoneDamageMulti = ValueUtil.randomBetween(1, 3f);
                        jjcData.PlayerCursePower += jjcData.PlayerCursePowerMAX * 0.05f;
                        break;
                    case 4:
                        zoneDamageMulti = ValueUtil.randomBetween(1, 3.5f);
                        break;
                }
            } else {
                jjcData.PlayerCursePower += jjcData.PlayerCursePowerMAX * 0.3f;
            }

            event.damage = event.damage / 4 * damageMulti * zoneDamageMulti;
        }
    }
}
