package com.eldritchvoid.core.registry;

import com.eldritchvoid.EldritchVoid;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredHolder;

/**
 * Registers all items for the Eldritch Void mod.
 */
public class ModItems {
    // Core items
    public static final DeferredHolder<Item, Item> VOID_CRYSTAL = RegistryHandler.ITEMS.register("void_crystal",
            () -> new Item(new Item.Properties()));
    
    // Void Alchemy Module
    public static final DeferredHolder<Item, Item> VOID_ESSENCE_VIAL = RegistryHandler.ITEMS.register("void_essence_vial",
            () -> new Item(new Item.Properties()));
    
    // Obsidian Forgemaster Module
    public static final DeferredHolder<Item, Item> OBSIDIAN_HAMMER = RegistryHandler.ITEMS.register("obsidian_hammer",
            () -> new Item(new Item.Properties().durability(500)));
    
    // Eldritch Artifacts Module
    public static final DeferredHolder<Item, Item> ELDRITCH_ARTIFACT = RegistryHandler.ITEMS.register("eldritch_artifact",
            () -> new Item(new Item.Properties()));
    
    // Void Corruption Module
    public static final DeferredHolder<Item, Item> CORRUPTION_SAMPLE = RegistryHandler.ITEMS.register("corruption_sample",
            () -> new Item(new Item.Properties()));
    
    // Eldritch Arcana Module
    public static final DeferredHolder<Item, Item> ARCANE_TOME = RegistryHandler.ITEMS.register("arcane_tome",
            () -> new Item(new Item.Properties()));
    
    // Obsidian Constructs Module
    public static final DeferredHolder<Item, Item> CONSTRUCT_BLUEPRINT = RegistryHandler.ITEMS.register("construct_blueprint",
            () -> new Item(new Item.Properties()));
    
    // Eldritch Dimensions Module
    public static final DeferredHolder<Item, Item> VOID_KEY = RegistryHandler.ITEMS.register("void_key",
            () -> new Item(new Item.Properties()));
    
    // Void Tech Module
    public static final DeferredHolder<Item, Item> VOID_BATTERY = RegistryHandler.ITEMS.register("void_battery",
            () -> new Item(new Item.Properties()));
    
    // Eldritch Bestiary Module
    public static final DeferredHolder<Item, Item> MONSTER_CODEX = RegistryHandler.ITEMS.register("monster_codex",
            () -> new Item(new Item.Properties()));
    
    // Void Cultists Module
    public static final DeferredHolder<Item, Item> CULTIST_ROBES = RegistryHandler.ITEMS.register("cultist_robes",
            () -> new Item(new Item.Properties()));

    /**
     * Register the mod items.
     */
    public static void register() {
        EldritchVoid.LOGGER.info("Registering Eldritch Void items");
    }
}
