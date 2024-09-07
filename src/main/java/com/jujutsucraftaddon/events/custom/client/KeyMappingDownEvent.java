package com.jujutsucraftaddon.events.custom.client;

import net.minecraft.client.KeyMapping;
import net.minecraftforge.eventbus.api.Event;

public class KeyMappingDownEvent extends Event {
    
    public KeyMapping key;
    public boolean isDown;
    public int clickCount;

    public KeyMappingDownEvent(KeyMapping key, boolean isDown,int clickCount) {
        this.key = key;
        this.isDown = isDown;
        this.clickCount = clickCount;
    }

    @Override
    public boolean isCancelable() {
        return true;
    }
}
