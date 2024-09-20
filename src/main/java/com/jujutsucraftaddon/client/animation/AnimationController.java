package com.jujutsucraftaddon.client.animation;

import dev.kosmx.playerAnim.api.layered.IAnimation;
import dev.kosmx.playerAnim.api.layered.KeyframeAnimationPlayer;
import dev.kosmx.playerAnim.api.layered.ModifierLayer;
import dev.kosmx.playerAnim.api.layered.modifier.AbstractFadeModifier;
import dev.kosmx.playerAnim.api.layered.modifier.AbstractModifier;
import dev.kosmx.playerAnim.api.layered.modifier.SpeedModifier;
import dev.kosmx.playerAnim.core.data.KeyframeAnimation;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class AnimationController<T extends IAnimation> extends ModifierLayer {

    public AbstractClientPlayer player;
    public ResourceLocation currentAnimation;
    private float speed = 1;

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

    public void setSpeed(float speed) {
        if (this.speed == speed)
            return;
        if (!isActive())
            return;

        this.speed = speed;
        for (AbstractModifier modif : getModifiers()) {
            if (modif instanceof SpeedModifier speedModif) {
                speedModif.speed = speed;
                return;
            }
        }

        addModifierLast(new SpeedModifier(speed));
    }

    public void play(ResourceLocation name) {
        this.setAnimation(Animations.createAnimationPlayer(currentAnimation = name));
    }

    public void play(ResourceLocation name, AbstractModifier... modifiers) {
        play(name);
        for (AbstractModifier modifier : modifiers)
            addModifierLast(modifier);
    }

    public void stop(AbstractFadeModifier fade) {
        replace(null, fade);
    }

    public void replace(ResourceLocation name, AbstractFadeModifier fade) {
        if (fade != null)
            replaceAnimationWithFade(fade, Animations.createAnimationPlayer(currentAnimation = name));
        else
            play(name);
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
