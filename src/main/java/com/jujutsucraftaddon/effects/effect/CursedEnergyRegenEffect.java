package com.jujutsucraftaddon.effects.effect;

import com.jujutsucraftaddon.capabilities.data.JujutsuData;
import net.mcreator.jujutsucraft.network.JujutsucraftModVariables;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

public class CursedEnergyRegenEffect extends MobEffect {
    public CursedEnergyRegenEffect(MobEffectCategory category, int color) {
        super(category, color);
    }

    public void applyEffectTick(LivingEntity entity, int amplifier) {
        if (entity.level().isClientSide)
            return;

        if (entity instanceof Player player) {
            JujutsucraftModVariables.PlayerVariables vars = JujutsuData.get(player).getPlayerVariables();
            vars.PlayerCursePowerChange += 10 * (amplifier + 1);
        }
    }

    @Override
    public boolean isDurationEffectTick(int duration, int amplifier) {
        if (amplifier == 0)
            return duration % 20 == 0;
        else if (amplifier == 1)
            return duration % 16 == 0;
        else if (amplifier == 2)
            return duration % 12 == 0;
        else if (amplifier == 3)
            return duration % 8 == 0;
        else if (amplifier > 3 && amplifier < 10)
            return duration % 5 == 0;
        else
            return duration % 1 == 0;
    }
}
