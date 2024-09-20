package com.jujutsucraftaddon.mixin.playerAnim;

import com.jujutsucraftaddon.client.animation.IModifierLayer;
import dev.kosmx.playerAnim.api.layered.IAnimation;
import dev.kosmx.playerAnim.api.layered.ModifierLayer;
import dev.kosmx.playerAnim.api.layered.modifier.AbstractModifier;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import java.util.List;

@Mixin(value = ModifierLayer.class, remap = false)
public abstract class ModifierLayerMixin<T extends IAnimation> implements IModifierLayer {
    
    @Shadow
    @Final
    private List<AbstractModifier> modifiers;

    @Override
    @Unique
    public List<AbstractModifier> getModifiersList() {
        return modifiers;
    }
}