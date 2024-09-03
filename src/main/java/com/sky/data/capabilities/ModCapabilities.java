package com.sky.data.capabilities;

import com.sky.Main;
import com.sky.data.JujutsuData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.event.AttachCapabilitiesEvent;

public class ModCapabilities {

    public static final Capability<JujutsuData> PLAYER_JUJUTSU_DATA = CapabilityManager.get(new CapabilityToken<JujutsuData>() {
    });

    public static void onAttachCapabilities(AttachCapabilitiesEvent<Entity> event) {
        if (event.getObject() instanceof Player player) {
            event.addCapability(new ResourceLocation(Main.MODID, "jujutsu_addon"), new JujutsuData.JujutsuDataProvider(player));
        }
    }
}

    
