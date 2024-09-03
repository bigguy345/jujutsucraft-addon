package com.sky.client;

import com.mojang.blaze3d.platform.InputConstants;
import com.sky.network.PacketHandler;
import com.sky.network.packet.KeyInputPacket;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import org.lwjgl.glfw.GLFW;

public class KeyHandler {
    public static final KeyMapping TNT_TOGGLE = new KeyMapping("key.temp", // The translation key of the keybinding
            GLFW.GLFW_KEY_X, // The default key
            "key.category.jjc_addon" // The translation key of the category
    );

    //Registers all of this mod's keys on game startup
    public static void registerKeyMappings(RegisterKeyMappingsEvent event) {
        event.register(TNT_TOGGLE);
    }

    //This fires whenever a key is pressed (either once, or held down continuously) in game
    public static void onKeyInput(InputEvent.Key event) {
        InputConstants.Key key = InputConstants.getKey(event.getKey(), event.getScanCode());
        Minecraft mc = Minecraft.getInstance();
        if (mc.screen != null)
            return;

        //Checks if TNT toggle key is pressed only once (hence GLFW_PRESS. Use GLFW_REPEAT if you want it to fire constantly as long as key is held down)
        if (TNT_TOGGLE.isActiveAndMatches(key) && event.getAction() == GLFW.GLFW_PRESS) {
            PacketHandler.CHANNEL.sendToServer(new KeyInputPacket("hi")); //sends a packet to server that says "hi"
        }
    }
}
