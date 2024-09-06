package com.jujutsucraftaddon.effects.effect;

import com.jujutsucraftaddon.effects.ImprovedMobEffect;
import net.mcreator.jujutsucraft.init.JujutsucraftModMobEffects;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Monster;

public class KnockoutEffect extends ImprovedMobEffect {
    public KnockoutEffect(MobEffectCategory category, int color) {
        super(category, color);
        updateClient = true;
    }

    public void onInstancedAdded(LivingEntity entity, MobEffectInstance instance) {
        entity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, instance.getDuration() + 20, 99, false, false));
     //   entity.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, instance.getDuration(), 99, false, false));
        entity.addEffect(new MobEffectInstance(JujutsucraftModMobEffects.UNSTABLE.get(), instance.getDuration(), 99, false, false));
        entity.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, instance.getDuration() * 2, 99, false, false));

        if (entity instanceof Monster monster)
            monster.setTarget(null);

        super.onInstancedAdded(entity, instance);
    }

    public void applyEffectTick(LivingEntity entity, MobEffectInstance instance) {
        entity.setXRot(70);
    }

    public void onInstanceRemove(LivingEntity entity, MobEffectInstance instance) {
        if (entity instanceof Monster monster)
            monster.setNoAi(false);

        entity.setXRot(0);
        super.onInstanceRemove(entity, instance);
    }
}
