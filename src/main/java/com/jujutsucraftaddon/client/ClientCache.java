package com.jujutsucraftaddon.client;

import com.jujutsucraftaddon.capabilities.data.JujutsuData;
import com.jujutsucraftaddon.client.key.KeyHandler;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;

public class ClientCache {
    public static float DASH_MAX_LEVEL, DASH_MAX_STRENGTH, DASH_SUPERCHARGE_SPEED, DASH_ENERGY_CONSUMPTION, DASH_SUPERCHARGE_ENERGY_CONSUMPTION_MULTI;

    private static JujutsuData data;

    public static JujutsuData getJujutsuData() {
        if (data == null)
            data = JujutsuData.get(KeyHandler.mc.player);

        return data;
    }

    public static void clientDisconnect(ClientPlayerNetworkEvent.LoggingOut event) {
        data = null;
    }
}
