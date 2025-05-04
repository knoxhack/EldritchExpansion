package com.eldritchvoid.modules.voidcorruption;

import com.eldritchvoid.EldritchVoid;
import com.eldritchvoid.core.registry.Registration;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredHolder;

/**
 * Registers all items for the Void Corruption module.
 */
public class VoidCorruptionItems {
    // Void Corruption items - these will be expanded with proper functionality
    
    // Void Fragment - a basic corruption material
    public static final DeferredHolder<Item, Item> VOID_FRAGMENT = Registration.ITEMS.register("void_fragment",
            () -> new Item(new Item.Properties()));
            
    // Corruption Detector - used to detect corruption levels in an area
    public static final DeferredHolder<Item, Item> CORRUPTION_DETECTOR = Registration.ITEMS.register("corruption_detector",
            () -> new Item(new Item.Properties().stacksTo(1)));
            
    // Anti-Corruption Charm - reduces corruption effects on the player
    public static final DeferredHolder<Item, Item> ANTI_CORRUPTION_CHARM = Registration.ITEMS.register("anti_corruption_charm", 
            () -> new Item(new Item.Properties().stacksTo(1)));
    
    /**
     * Register all items.
     */
    public static void register() {
        EldritchVoid.LOGGER.info("Registering Void Corruption items");
    }
}