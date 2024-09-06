package com.jujutsucraftaddon.client;

import com.jujutsucraftaddon.effects.ModEffects;
import com.jujutsucraftaddon.events.custom.client.KeyMappingDownEvent;
import com.jujutsucraftaddon.network.PacketHandler;
import com.jujutsucraftaddon.network.packet.KeyInputPacket;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.lwjgl.glfw.GLFW;

public class KeyHandler {
    public static final KeyMapping EXAMPLE_KEY = new KeyMapping("key.temp", // The translation key of the keybinding
            GLFW.GLFW_KEY_X, // The default key
            "key.category.jujutsucraftaddon" // The translation key of the category
    );

    //Registers all of this mod's keys on game startup
    public static void registerKeyMappings(RegisterKeyMappingsEvent event) {
        event.register(EXAMPLE_KEY);
    }

    //This fires whenever a key is pressed (either once, or held down continuously) in game
    @SubscribeEvent
    public void onKeyInput(InputEvent.Key event) {
        InputConstants.Key key = InputConstants.getKey(event.getKey(), event.getScanCode());
        Minecraft mc = Minecraft.getInstance();
        if (mc.screen != null)
            return;

        //Checks if TNT toggle key is pressed only once (hence GLFW_PRESS. Use GLFW_REPEAT if you want it to fire constantly as long as key is held down)
        if (EXAMPLE_KEY.isActiveAndMatches(key) && event.getAction() == GLFW.GLFW_PRESS) {
            PacketHandler.CHANNEL.sendToServer(new KeyInputPacket("hi")); //sends a packet to server that says "hi"
        }
    }

    @SubscribeEvent
    public void onKeyInput(InputEvent.InteractionKeyMappingTriggered event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player.hasEffect(ModEffects.KNOCKOUT_EFFECT.get())) {
            event.setCanceled(true);
            event.setSwingHand(false);
        }
    }

    @SubscribeEvent
    public void onKeyMappingDown(KeyMappingDownEvent event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null)
            return;

        boolean knockoutKey = event.key == mc.options.keyUp || event.key == mc.options.keyDown || event.key == mc.options.keyLeft || event.key == mc.options.keyRight || event.key == mc.options.keyShift || event.key == mc.options.keyJump;
        if (knockoutKey && mc.player.hasEffect(ModEffects.KNOCKOUT_EFFECT.get())) {
            event.isDown = false;
            event.release = true;
            event.setCanceled(true);
        }
    }
}
