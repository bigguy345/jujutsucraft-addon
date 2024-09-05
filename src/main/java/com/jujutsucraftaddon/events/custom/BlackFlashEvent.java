package com.jujutsucraftaddon.events.custom;

import com.llamalad7.mixinextras.sugar.ref.LocalDoubleRef;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.eventbus.api.Event;

public class BlackFlashEvent extends Event {
    public LivingEntity attacker, attacked;
    public double damage,knockback;

    public BlackFlashEvent(LivingEntity attacker, LivingEntity attacked, double damage, double knockback) {
        this.attacker = attacker;
        this.attacked = attacked;
        this.damage = damage;
        this.knockback = knockback;
    }

    @Override
    public boolean isCancelable() {
        return true; 
    }
}