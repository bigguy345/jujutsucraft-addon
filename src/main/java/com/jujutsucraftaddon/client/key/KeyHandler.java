package com.jujutsucraftaddon.client.key;

import com.jujutsucraftaddon.capabilities.data.JujutsuData;
import com.jujutsucraftaddon.client.key.keys.DashKey;
import com.jujutsucraftaddon.effects.ModEffects;
import com.jujutsucraftaddon.events.custom.client.KeyMappingDownEvent;
import com.jujutsucraftaddon.network.PacketHandler;
import com.jujutsucraftaddon.network.packet.KeyInputPacket;
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
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.lwjgl.glfw.GLFW;

public class KeyHandler {
    public static final KeyMapping Second_FN = new KeyMapping("key.second_fn", GLFW.GLFW_KEY_LEFT_SHIFT, "key.category.jujutsucraftaddon");
    public static final KeyMapping Dash = new DashKey("key.dash", GLFW.GLFW_KEY_X, "key.category.jujutsucraftaddon");
    public static final KeyMapping Debug = new KeyMapping("Debugging key", GLFW.GLFW_KEY_UNKNOWN, "key.category.jujutsucraftaddon");

    public static final ImprovedKeyMapping Domain_Expansion = new ImprovedKeyMapping("key.domain_expansion", GLFW.GLFW_KEY_H, "key.category.jujutsucraftaddon") {
        public void onAction(int action) {
            super.onAction(action);
            PacketHandler.CHANNEL.sendToServer(new KeyInputPacket("domain_expansion", action));
        }
    };

    public static Minecraft mc = Minecraft.getInstance();

    //Registers all of this mod's keys on game startup
    public static void registerKeyMappings(RegisterKeyMappingsEvent event) {
        event.register(Second_FN);
        event.register(Dash);
        event.register(Debug);
        event.register(Domain_Expansion);
    }

    //This fires whenever a key is pressed (either once, or held down continuously) in game
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onKeyInput(InputEvent.Key event) {
        InputConstants.Key key = InputConstants.getKey(event.getKey(), event.getScanCode());
        Minecraft mc = Minecraft.getInstance();
        if (mc.screen != null)
            return;

        //Checks if TNT toggle key is pressed only once (hence GLFW_PRESS. Use GLFW_REPEAT if you want it to fire constantly as long as key is held down)
        if (Debug.isActiveAndMatches(key) && event.getAction() == GLFW.GLFW_PRESS)
            PacketHandler.CHANNEL.sendToServer(new KeyInputPacket("hi", GLFW.GLFW_PRESS)); //sends a packet to server that says "hi"
    }

    @SubscribeEvent
    public void onKeyTick(TickEvent.ClientTickEvent event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.screen != null || event.phase == TickEvent.Phase.START)
            return;

        ImprovedKeyMapping.tick();

        ////////////////////////////////////////////////////////////
        ////////////////////////////////////////////////////////////
        //Heal others
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
