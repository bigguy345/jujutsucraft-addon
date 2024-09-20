package com.jujutsucraftaddon.client.animation;

import dev.kosmx.playerAnim.api.layered.modifier.AbstractModifier;
import org.spongepowered.asm.mixin.Unique;

import java.util.List;

public interface IModifierLayer {
    @Unique
    List<AbstractModifier> getModifiersList();
    
}
