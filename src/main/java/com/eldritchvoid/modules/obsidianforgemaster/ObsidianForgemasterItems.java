package com.eldritchvoid.modules.obsidianforgemaster;

import com.eldritchvoid.EldritchVoid;
import com.eldritchvoid.core.registry.Registration;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredHolder;

/**
 * Registers all items for the Obsidian Forgemaster module.
 */
public class ObsidianForgemasterItems {
    // Obsidian Forgemaster tools and materials - these will be expanded with proper functionality
    
    // The Obsidian Hammer - used for shaping hot metals
    public static final DeferredHolder<Item, Item> OBSIDIAN_HAMMER = Registration.ITEMS.register("obsidian_hammer",
            () -> new Item(new Item.Properties().stacksTo(1).durability(1200)));
            
    // The Obsidian Tongs - used for holding hot metals
    public static final DeferredHolder<Item, Item> OBSIDIAN_TONGS = Registration.ITEMS.register("obsidian_tongs",
            () -> new Item(new Item.Properties().stacksTo(1).durability(800)));
            
    // Void-Infused Obsidian Ingot - advanced crafting material
    public static final DeferredHolder<Item, Item> VOID_OBSIDIAN_INGOT = Registration.ITEMS.register("void_obsidian_ingot",
            () -> new Item(new Item.Properties()));
    
    /**
     * Register all items.
     */
    public static void register() {
        EldritchVoid.LOGGER.info("Registering Obsidian Forgemaster items");
    }
}