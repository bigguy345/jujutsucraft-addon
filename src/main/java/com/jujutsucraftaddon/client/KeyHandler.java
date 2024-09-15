package com.jujutsucraftaddon.client;

import com.jujutsucraftaddon.capabilities.data.JujutsuData;
import com.jujutsucraftaddon.effects.ModEffects;
import com.jujutsucraftaddon.events.custom.client.KeyMappingDownEvent;
import com.jujutsucraftaddon.network.PacketHandler;
import com.jujutsucraftaddon.network.packet.ReversedCTPacket;
import com.jujutsucraftaddon.utility.Utility;
import com.mojang.blaze3d.platform.InputConstants;
import net.mcreator.jujutsucraft.init.JujutsucraftModKeyMappings;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.lwjgl.glfw.GLFW;

public class KeyHandler {
    public static final KeyMapping Second_FN = new KeyMapping("key.second_fn", // The translation key of the keybinding
            GLFW.GLFW_KEY_LEFT_SHIFT, // The default key
            "key.category.jujutsucraftaddon" // The translation key of the category
    );

    //Registers all of this mod's keys on game startup
    public static void registerKeyMappings(RegisterKeyMappingsEvent event) {
        event.register(Second_FN);
    }

    //This fires whenever a key is pressed (either once, or held down continuously) in game
    @SubscribeEvent
    public void onKeyInput(InputEvent.Key event) {
        InputConstants.Key key = InputConstants.getKey(event.getKey(), event.getScanCode());
        Minecraft mc = Minecraft.getInstance();
        if (mc.screen != null)
            return;

        //Checks if TNT toggle key is pressed only once (hence GLFW_PRESS. Use GLFW_REPEAT if you want it to fire constantly as long as key is held down)
        //        if (Second_FN.isActiveAndMatches(key) && event.getAction() == GLFW.GLFW_PRESS) {
        //            PacketHandler.CHANNEL.sendToServer(new KeyInputPacket("hi")); //sends a packet to server that says "hi"
        //        }
    }

    @SubscribeEvent
    public void onKeyTick(TickEvent.ClientTickEvent event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.screen != null)
            return;

        KeyMapping rctKey = JujutsucraftModKeyMappings.KEY_REVERSE_CURSED_TECHNIQUE;
        rctKey.setDown(InputConstants.isKeyDown(mc.getWindow().getWindow(), rctKey.getKey().getValue()));
        JujutsuData data = JujutsuData.get(mc.player);
        boolean stopOtherRCT = false;
        if (rctKey.isDown() && Second_FN.isDown() && data.canHealOthers) {
            //Fetches the entity looked at
            Entity toHeal = Utility.raytraceEntity(mc.player, 5);
            if (toHeal != null && toHeal instanceof LivingEntity && data.data.PlayerCursePower >= 10)
                PacketHandler.sendToServer(new ReversedCTPacket(toHeal.getId()));
            else if (data.toHealID != -1)
                stopOtherRCT = true;
        } else if (data.toHealID != -1)
            stopOtherRCT = true;

        if (stopOtherRCT) {
            PacketHandler.sendToServer(new ReversedCTPacket(-1));
            rctKey.setDown(false);
        }
    }

    @SubscribeEvent
    public void onInteractionKey(InputEvent.InteractionKeyMappingTriggered event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player.hasEffect(ModEffects.KNOCKOUT_EFFECT.get())) {
            event.setCanceled(true);
            event.setSwingHand(false);
        }
    }

    @SubscribeEvent
    public void onMouseScroll(InputEvent.MouseScrollingEvent event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player.hasEffect(ModEffects.KNOCKOUT_EFFECT.get()))
            event.setCanceled(true);
    }

    @SubscribeEvent
    public void onKeyMappingDown(KeyMappingDownEvent event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null)
            return;

        KeyMapping key = event.key;
        boolean knockoutKey = key.getCategory() == "key.categories.movement" || (key.getCategory() == "key.categories.inventory" && key.getName() != "key.inventory");
        if (knockoutKey && mc.player.hasEffect(ModEffects.KNOCKOUT_EFFECT.get())) {
            event.isDown = false;
            event.setCanceled(true);
        }
    }
}
