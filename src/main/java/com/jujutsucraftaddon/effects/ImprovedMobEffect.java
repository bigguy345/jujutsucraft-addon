package com.jujutsucraftaddon.effects;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;

public class ImprovedMobEffect extends MobEffect {

    public ImprovedMobEffect(MobEffectCategory category, int color) {
        super(category, color);
    }

    public void onInstancedAdded(LivingEntity entity, MobEffectInstance instance) {
    }

    public void applyEffectTick(LivingEntity entity, MobEffectInstance instance) {
    }

    public void onInstanceRemove(LivingEntity entity, MobEffectInstance instance) {
    }

    public boolean isDurationEffectTick(int duration, int amplifier) {
        return true;
    }
}
