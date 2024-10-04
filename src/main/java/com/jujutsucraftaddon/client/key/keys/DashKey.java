package com.jujutsucraftaddon.client.key.keys;

import com.jujutsucraftaddon.capabilities.data.JujutsuData;
import com.jujutsucraftaddon.client.ClientCache;
import com.jujutsucraftaddon.client.animation.AnimationController;
import com.jujutsucraftaddon.client.key.ImprovedKeyMapping;
import com.jujutsucraftaddon.client.key.KeyHandler;
import com.jujutsucraftaddon.network.PacketHandler;
import com.jujutsucraftaddon.network.packet.DashPacket;
import com.jujutsucraftaddon.skill.DashSkill;
import com.jujutsucraftaddon.utility.ValueUtil;
import dev.kosmx.playerAnim.core.util.Ease;
import dev.kosmx.playerAnim.core.util.Easing;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;

import static com.jujutsucraftaddon.client.animation.Animations.*;
import static com.jujutsucraftaddon.skill.DashSkill.*;

public class DashKey extends ImprovedKeyMapping {
    public DashKey(String p_90821_, int p_90822_, String p_90823_) {
        super(p_90821_, p_90822_, p_90823_);
    }

    public void onRelease() {
        JujutsuData data = JujutsuData.getClient();
        if (DASH_CHARGE > 0) {

            Vec3 lookvec = mc.player.getLookAngle();

            //Decreases velocity in Y direction anything above 0.35+ is too powerful
            double newX = lookvec.x, newZ = lookvec.z, newY = lookvec.y;
            float xzMin = mc.player.onGround() && lookvec.y < 0.2f ? 0.5f : -1;
            if (Math.abs(lookvec.x) >= Math.abs(lookvec.z))
                newX = Math.copySign(Math.max(Math.abs(lookvec.x), xzMin), lookvec.x);
            else
                newZ = Math.copySign(Math.max(Math.abs(lookvec.z), xzMin), lookvec.z);

            if (mc.player.onGround())
                newY = Mth.clamp(lookvec.y, 0.2f, 0.35f);
            else
                newY = Math.copySign(Math.max(Math.abs(lookvec.y), 0.25), lookvec.y) * ValueUtil.lerp(0.5, 1, DASH_CHARGE);

            Vec3 vec1 = new Vec3(newX, newY, newZ);


            float dashLevel = data.levels.getDashLevel();
            double strength = calculateStrength(DASH_CHARGE, dashLevel); //DASH LEVEL AS 2ND ARG
            Vec3 vec2 = vec1.multiply(strength, Math.max(1, strength * Easing.inQuad(dashLevel / ClientCache.DASH_MAX_LEVEL)), strength);

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

    public void onTick() {
        JujutsuData data = JujutsuData.getClient();
        if (isDown() && (data.cooldowns.DASH == 0 || mc.player.isCreative()) && DASH_CHARGE < 1) {
            float strength = DashSkill.calculateStrength(DASH_CHARGE, 100);
            ENERGY_NEEDED = DashSkill.calculateEnergyConsumed(strength, DASH_SUPER_CHARGE, ClientCache.DASH_ENERGY_CONSUMPTION, ClientCache.DASH_SUPERCHARGE_ENERGY_CONSUMPTION_MULTI);

            Vec3 lookvec = mc.player.getLookAngle();

            if (data.data.PlayerCursePower >= ENERGY_NEEDED) {
                boolean secondFN = KeyHandler.Second_FN.isDown();

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
        } else if (!isDown() && DASH_CHARGE > 0) {
            DASH_CHARGE = DASH_SUPER_CHARGE = 0;
            OUT_OF_ENERGY = false;
        }

        if (data.currentDash != null) {
            Vec3 velocity = mc.player.getDeltaMovement();
            float currentSpeed = (float) velocity.length();

            if (data.infinityOn && KeyHandler.Second_FN.isDown() && data.currentDash.airtime >= 10) {
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
}
