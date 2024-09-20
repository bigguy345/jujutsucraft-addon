package com.jujutsucraftaddon.client.animation;

import com.jujutsucraftaddon.network.PacketHandler;
import com.jujutsucraftaddon.network.packet.animation.AnimationPackets;
import com.jujutsucraftaddon.network.packet.animation.C2SAnimationPacket;
import dev.kosmx.playerAnim.api.layered.IAnimation;
import dev.kosmx.playerAnim.api.layered.KeyframeAnimationPlayer;
import dev.kosmx.playerAnim.api.layered.ModifierLayer;
import dev.kosmx.playerAnim.api.layered.modifier.AbstractFadeModifier;
import dev.kosmx.playerAnim.api.layered.modifier.AbstractModifier;
import dev.kosmx.playerAnim.api.layered.modifier.SpeedModifier;
import dev.kosmx.playerAnim.core.data.KeyframeAnimation;
import dev.kosmx.playerAnim.core.util.Ease;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class AnimationController<T extends IAnimation> extends ModifierLayer {

    public AbstractClientPlayer player;
    public ResourceLocation currentAnimation;
    private float speed = 1;
    
    //auto updates other clients with this client's animation state
    public boolean autoUpdate = true;

    public AnimationController(AbstractClientPlayer player) {
        this(null, player);
    }

    public AnimationController(@Nullable IAnimation animation, AbstractClientPlayer player, AbstractModifier... modifiers) {
        super(animation, modifiers);
        this.player = player;
    }

    public void tick() {
        if (getAnimationPlayer() == null && speed != 1) {
            this.speed = 1;
            for (AbstractModifier modif : getModifiers()) {
                if (modif instanceof SpeedModifier speedModif) {
                    speedModif.speed = 1;
                    return;
                }
            }
        }

        super.tick();
    }

    public AnimationController<T> setSpeed(float speed) {
        if (this.speed == speed)
            return this;
        if (!isActive())
            return this;

        this.speed = speed;
        boolean changed = false;
        for (AbstractModifier modif : getModifiers()) {
            if (modif instanceof SpeedModifier speedModif) {
                speedModif.speed = speed;
                changed = true;
                break;
            }
        }
        if (!changed)
            addModifierLast(new SpeedModifier(speed));

        if (autoUpdate) {
            CompoundTag data = AnimationPackets.generateSpeedData(currentAnimation, speed);
            PacketHandler.sendToServer(new C2SAnimationPacket(AnimationPackets.Type.SET_SPEED, data));
        }
        return this;
    }

    public AnimationController play(ResourceLocation name) {
        this.setAnimation(Animations.createAnimationPlayer(currentAnimation = name));

        if (autoUpdate) {
            CompoundTag data = AnimationPackets.generatePlayData(name);
            PacketHandler.sendToServer(new C2SAnimationPacket(AnimationPackets.Type.PLAY_ANIMATION, data));
        }
        return this;
    }

    public AnimationController play(ResourceLocation name, AbstractModifier... modifiers) {
        play(name);
        for (AbstractModifier modifier : modifiers)
            addModifierLast(modifier);
        return this;
    }

    public AnimationController stop() {
        play(null);
        return this;
    }

    public AnimationController stop(int ticks, Ease ease) {
        replace(null, ticks, ease);
        return this;
    }

    public AnimationController replace(ResourceLocation name, int ticks, Ease ease) {
        AbstractFadeModifier fade = Animations.fade(ticks, ease);
        replaceAnimationWithFade(fade, Animations.createAnimationPlayer(currentAnimation = name));

        if (autoUpdate) {
            CompoundTag data = AnimationPackets.generateReplaceData(name, ticks, ease);
            PacketHandler.sendToServer(new C2SAnimationPacket(AnimationPackets.Type.REPLACE_ANIMATION, data));
        }
        return this;
    }

    public KeyframeAnimation getPlayingAnimation() {
        if (getAnimationPlayer() == null)
            return null;

        return getAnimationPlayer().getData();
    }

    public KeyframeAnimationPlayer getAnimationPlayer() {
        return (KeyframeAnimationPlayer) getAnimation();
    }

    public List<AbstractModifier> getModifiers() {
        return ((IModifierLayer) this).getModifiersList();
    }

    public float getSpeed() {
        return speed;
    }

    public boolean isAnimation(String name) {
        return isAnimation(new ResourceLocation(name));
    }

    public boolean isAnimation(ResourceLocation animationName) {
        return currentAnimation != null && currentAnimation.equals(animationName);
    }
}
