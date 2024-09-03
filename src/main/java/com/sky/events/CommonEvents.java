package com.sky.events;

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
    }
}
