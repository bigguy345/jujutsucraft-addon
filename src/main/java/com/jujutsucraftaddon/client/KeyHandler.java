package com.jujutsucraftaddon.client;

import com.jujutsucraftaddon.capabilities.data.JujutsuData;
import com.jujutsucraftaddon.client.animation.AnimationController;
import com.jujutsucraftaddon.effects.ModEffects;
import com.jujutsucraftaddon.events.custom.client.KeyMappingDownEvent;
import com.jujutsucraftaddon.network.PacketHandler;
import com.jujutsucraftaddon.network.packet.DashPacket;
import com.jujutsucraftaddon.network.packet.KeyInputPacket;
import com.jujutsucraftaddon.network.packet.ReversedCTPacket;
import com.jujutsucraftaddon.skill.DashSkill;
import com.jujutsucraftaddon.utility.Utility;
import com.mojang.blaze3d.platform.InputConstants;
import dev.kosmx.playerAnim.core.util.Ease;
import dev.kosmx.playerAnim.core.util.Easing;
import net.mcreator.jujutsucraft.init.JujutsucraftModKeyMappings;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.lwjgl.glfw.GLFW;

import static com.jujutsucraftaddon.client.animation.Animations.*;
import static com.jujutsucraftaddon.skill.DashSkill.*;

public class KeyHandler {
    public static final KeyMapping Second_FN = new KeyMapping("key.second_fn", GLFW.GLFW_KEY_LEFT_SHIFT, "key.category.jujutsucraftaddon");
    public static final KeyMapping Dash = new KeyMapping("key.dash", GLFW.GLFW_KEY_X, "key.category.jujutsucraftaddon");
    public static final KeyMapping Debug = new KeyMapping("Debugging key", GLFW.GLFW_KEY_UNKNOWN, "key.category.jujutsucraftaddon");

    //Registers all of this mod's keys on game startup
    public static void registerKeyMappings(RegisterKeyMappingsEvent event) {
        event.register(Second_FN);
        event.register(Dash);
        event.register(Debug);
    }

    //This fires whenever a key is pressed (either once, or held down continuously) in game
    @SubscribeEvent
    public void onKeyInput(InputEvent.Key event) {
        InputConstants.Key key = InputConstants.getKey(event.getKey(), event.getScanCode());
        Minecraft mc = Minecraft.getInstance();
        if (mc.screen != null)
            return;

        //Checks if TNT toggle key is pressed only once (hence GLFW_PRESS. Use GLFW_REPEAT if you want it to fire constantly as long as key is held down)
        if (Debug.isActiveAndMatches(key) && event.getAction() == GLFW.GLFW_PRESS)
            PacketHandler.CHANNEL.sendToServer(new KeyInputPacket("hi")); //sends a packet to server that says "hi"


        if (Dash.isActiveAndMatches(key) && event.getAction() == GLFW.GLFW_RELEASE) {
            JujutsuData data = JujutsuData.get(mc.player);
            if (DASH_CHARGE > 0) {

                Vec3 lookvec = mc.player.getLookAngle();

                //Decreases velocity in Y direction anything above 0.35+ is too powerful
                double newX = lookvec.x, newZ = lookvec.z, newY = lookvec.y;
                float xzMin = mc.player.onGround() && lookvec.y < 0.2f ? 0.5f : -1;
                if (Math.abs(lookvec.x) >= Math.abs(lookvec.z))
                    newX = Math.copySign(Math.max(Math.abs(lookvec.x), xzMin), lookvec.x);
                else
                    newZ = Math.copySign(Math.max(Math.abs(lookvec.z), xzMin), lookvec.z);

                if (DASH_CHARGE < 0.3)
                    newY = Mth.clamp(lookvec.y, 0.2f, 0.35f);
                else
                    newY = Math.copySign(Math.max(Math.abs(lookvec.y), 0.25), lookvec.y);

                Vec3 vec1 = new Vec3(newX, newY, newZ);


                float dashLevel = data.levels.getDashLevel();
                double strength = calculateStrength(DASH_CHARGE, dashLevel); //DASH LEVEL AS 2ND ARG
                Vec3 vec2 = vec1.multiply(strength, 2 + strength * Easing.inQuad(dashLevel / ClientCache.DASH_MAX_LEVEL), strength);

                //Limits how high up dash can go
                Vec3 launchVec = new Vec3(vec2.x, Math.min(vec2.y, 5f), vec2.z);
                mc.player.setDeltaMovement(launchVec);

                data.currentDash = new DashSkill((float) strength, DASH_CHARGE, DASH_SUPER_CHARGE);
                PacketHandler.CHANNEL.sendToServer(new DashPacket(data.currentDash));


                AnimationController animController = getController(mc.player);
                ResourceLocation dash_anim = LAST_DASH_ANIMATION == 1 ? DASH_RIGHT : DASH_LEFT;
                LAST_DASH_ANIMATION = (byte) (LAST_DASH_ANIMATION == 1 ? 2 : 1);

                if (animController.isAnimation(SUPER_DASH)) {
                    animController.setSpeed(1).stop(20, Ease.OUTCUBIC).setCanMove(true);
                    animController.replace(dash_anim, 190, Ease.OUTSINE);
                } else
                    animController.replace(dash_anim, 10, Ease.OUTCUBIC);
                DASH_CHARGE = DASH_SUPER_CHARGE = 0;
                OUT_OF_ENERGY = false;
                if (!mc.player.isCreative())
                    data.cooldowns.DASH = 20;
            }
        }
    }

    @SubscribeEvent
    public void onKeyTick(TickEvent.ClientTickEvent event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.screen != null)
            return;


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

        ////////////////////////////////////////////////////////////
        ////////////////////////////////////////////////////////////
        //Dash
        if (Dash.isDown() && (data.cooldowns.DASH == 0 || mc.player.isCreative()) && DASH_CHARGE < 1) {
            float strength = DashSkill.calculateStrength(DASH_CHARGE, 100);
            ENERGY_NEEDED = DashSkill.calculateEnergyConsumed(strength, DASH_SUPER_CHARGE, ClientCache.DASH_ENERGY_CONSUMPTION, ClientCache.DASH_SUPERCHARGE_ENERGY_CONSUMPTION_MULTI);

            Vec3 lookvec = mc.player.getLookAngle();

            if (data.data.PlayerCursePower >= ENERGY_NEEDED) {
                boolean secondFN = Second_FN.isDown();

                //If sneaking, 2x slower charge, else if second FN, 4x faster charge
                float multi = mc.options.keyShift.isDown() ? 0.25f : (secondFN ? ClientCache.DASH_SUPERCHARGE_SPEED : 1f);


                //Fully charges over the span of 8 seconds by default
                float increment = (0.05f / 10) * multi;
                DASH_CHARGE += increment;

                if (DASH_CHARGE > 0.15) {
                    AnimationController animController = getController(mc.player);
                    if (animController.isAnimation(SUPER_DASH))
                        animController.setSpeed(multi);
                    else
                        animController.play(SUPER_DASH).setCanMove(false);
                }

                if (secondFN)
                    DASH_SUPER_CHARGE += increment;

                OUT_OF_ENERGY = false;
            } else
                OUT_OF_ENERGY = true;
        } else if (!Dash.isDown() && DASH_CHARGE > 0) {
            DASH_CHARGE = DASH_SUPER_CHARGE = 0;
            OUT_OF_ENERGY = false;
        }

        if (data.currentDash != null) {
            Vec3 velocity = mc.player.getDeltaMovement();
            float currentSpeed = (float) velocity.length();

            if (data.infinityOn && Second_FN.isDown() && data.currentDash.airtime >= 10) {
                currentSpeed = 0.4F;
                mc.player.setDeltaMovement(velocity.scale(0.02f));
                if (mc.player.getAbilities().mayfly)
                    mc.player.getAbilities().flying = true;
            }


            if (mc.player.horizontalCollision || currentSpeed <= 0.4f) {
                if (mc.player.horizontalCollision)
                    mc.player.setDeltaMovement(new Vec3(0, 0, 0));

                DashSkill remove = new DashSkill(0, 0, 0);
                remove.speed = DashSkill.getHighestVelocity();
                PacketHandler.CHANNEL.sendToServer(new DashPacket(remove));
                DashSkill.velocityHistory.clear();
                data.currentDash = null;
            }

            DashSkill.recordVelocity(currentSpeed);
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
