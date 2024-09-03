package com.jujutsucraftaddon.Items;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import static com.jujutsucraftaddon.Main.MODID;
import static com.jujutsucraftaddon.blocks.ModBlocks.SAPPHIRE_BLOCK_ITEM;

public class ModItems {

    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MODID);

    public static final RegistryObject<Item> SAPPHIRE = ITEMS.register("sapphire", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> RAW_SAPPHIRE = ITEMS.register("raw_sapphire", () -> new Item(new Item.Properties()));

    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MODID);
    public static final RegistryObject<CreativeModeTab> ITEMS_TAB = CREATIVE_MODE_TABS.register("items_tab", () -> CreativeModeTab.builder().withTabsBefore(CreativeModeTabs.COMBAT).title(Component.translatable("creativetab.item_tab")).icon(() -> Items.TNT.getDefaultInstance()).displayItems((parameters, output) -> {
        output.accept(SAPPHIRE_BLOCK_ITEM.get());
    }).build());

    public static void registerAllItems(IEventBus eventBus) {
        ITEMS.register(eventBus);
        CREATIVE_MODE_TABS.register(eventBus);
    }

    public static void addCreative(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.BUILDING_BLOCKS)

            if (event.getTabKey() == CreativeModeTabs.INGREDIENTS) {
                event.accept(ModItems.SAPPHIRE);
                event.accept(ModItems.RAW_SAPPHIRE);
            }
    }
}
