package com.jujutsucraftaddon.events.custom;

import com.llamalad7.mixinextras.sugar.ref.LocalDoubleRef;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.eventbus.api.Event;

public class BlackFlashEvent extends Event {
    public Entity attacker, attacked;
    public double damage;

    public BlackFlashEvent(Entity attacker, Entity attacked, double damage) {
        this.attacker = attacker;
        this.attacked = attacked;
        this.damage = damage;
    }

    @Override
    public boolean isCancelable() {
        return true; 
    }
}