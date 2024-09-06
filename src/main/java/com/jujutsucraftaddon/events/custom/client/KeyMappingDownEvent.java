package com.jujutsucraftaddon.events.custom.client;

import net.minecraft.client.KeyMapping;
import net.minecraftforge.eventbus.api.Event;

public class KeyMappingDownEvent extends Event {
    
    public KeyMapping key;
    public boolean isDown,release;

    public KeyMappingDownEvent(KeyMapping key, boolean isDown) {
        this.key = key;
        this.isDown = isDown;
    }

    @Override
    public boolean isCancelable() {
        return true;
    }
}
