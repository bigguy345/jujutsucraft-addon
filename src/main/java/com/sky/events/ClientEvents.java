package com.sky.events;

import com.sky.Items.ModItems;
import com.sky.network.PacketHandler;
import com.sky.network.packet.KeyInputPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

//Events that fire on client only
public class ClientEvents {

    @SubscribeEvent
    public void clientTick(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.START)
            return;
        Minecraft mc = Minecraft.getInstance();
        Player player = mc.player;
        if (player == null)
            return;

//        boolean isStickHeld = player.getMainHandItem().is(ModItems.BOOM_STICK.get());
//        boolean isRightClickDown = mc.options.keyUse.isDown();
//        if (isStickHeld && isRightClickDown) {
//            PacketHandler.CHANNEL.sendToServer(new KeyInputPacket("tntCannon"));
//        }   
//        boolean isClusterStickHeld = player.getMainHandItem().is(ModItems.CLUSTER_STICK.get());
//        if (isClusterStickHeld && isRightClickDown) {
//            PacketHandler.CHANNEL.sendToServer(new KeyInputPacket("clustercannon"));
//        }
    }
}