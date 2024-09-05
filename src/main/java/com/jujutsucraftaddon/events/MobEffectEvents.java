package com.jujutsucraftaddon.events;

import com.jujutsucraftaddon.effects.ImprovedMobEffect;
import net.minecraftforge.event.entity.living.MobEffectEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class MobEffectEvents {

    @SubscribeEvent
    public void onAdded(MobEffectEvent.Added event) {
        if (event.getEffectInstance().getEffect() instanceof ImprovedMobEffect effect) {
            effect.onInstancedAdded(event.getEntity(), event.getEffectInstance());
        }
    }

    @SubscribeEvent
    public void onRemoved(MobEffectEvent.Remove event) {
        if (event.getEffect() instanceof ImprovedMobEffect effect) {
            effect.onInstanceRemove(event.getEntity(), event.getEffectInstance());
        }
    }

    @SubscribeEvent
    public void onExpired(MobEffectEvent.Expired event) {
        if (event.getEffectInstance().getEffect() instanceof ImprovedMobEffect effect) {
            effect.onInstanceRemove(event.getEntity(), event.getEffectInstance());
        }
    }
}
