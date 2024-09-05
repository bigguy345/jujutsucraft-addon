package com.jujutsucraftaddon.effects;

import com.jujutsucraftaddon.Main;
import com.jujutsucraftaddon.effects.effect.CooldownReductionEffect;
import com.jujutsucraftaddon.effects.effect.CostReductionEffect;
import com.jujutsucraftaddon.effects.effect.CursedEnergyRegenEffect;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModEffects {

    public static final DeferredRegister<MobEffect> MOB_EFFECTS = DeferredRegister.create(ForgeRegistries.MOB_EFFECTS, Main.MODID);

    public static final RegistryObject<MobEffect> CURSED_ENERGY_REGEN = MOB_EFFECTS.register("cursed_energy_regen", () -> new CursedEnergyRegenEffect(MobEffectCategory.BENEFICIAL, -6684673));
    public static final RegistryObject<MobEffect> COST_REDUCTION = MOB_EFFECTS.register("cost_reduction", () -> new CostReductionEffect(MobEffectCategory.BENEFICIAL, -6684673));
    public static final RegistryObject<MobEffect> COOLDOWN_REDUCTION = MOB_EFFECTS.register("cooldown_reduction", () -> new CooldownReductionEffect(MobEffectCategory.BENEFICIAL, -6684673));

    public static void registerAllEffects(IEventBus eventBus) {
        MOB_EFFECTS.register(eventBus);
    }
}
