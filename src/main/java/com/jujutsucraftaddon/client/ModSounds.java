package com.jujutsucraftaddon.client;

import com.jujutsucraftaddon.Main;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModSounds {
    public static final DeferredRegister<SoundEvent> SOUND_EVENTS = DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, Main.MODID);

    public static final RegistryObject<SoundEvent> DOMAIN_SHATTER = SOUND_EVENTS.register("domain_shatter", () -> SoundEvent.createFixedRangeEvent(new ResourceLocation(Main.MODID, "domain_shatter"), 32f));
}
