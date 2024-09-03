package com.jujutsucraftaddon.entity;

import com.jujutsucraftaddon.Main;
import net.minecraft.world.entity.EntityType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ModEntities {

    public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, Main.MODID);

    public static void registerAllEntities(IEventBus eventBus) {
        ENTITIES.register(eventBus);
    }
}
