package com.eldritchvoid.modules.eldritchartifacts;

import com.eldritchvoid.EldritchVoid;
import com.eldritchvoid.core.registry.Registration;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredHolder;

/**
 * Registers all artifact items for the Eldritch Artifacts module.
 */
public class EldritchArtifactsItems {
    // Eldritch Artifacts - these will be expanded with proper functionality
    
    // The Necronomicon - an ancient tome of forbidden knowledge
    public static final DeferredHolder<Item, Item> NECRONOMICON = Registration.ITEMS.register("necronomicon",
            () -> new Item(new Item.Properties().stacksTo(1)));
            
    // The Eye of Yog-Sothoth - allows seeing into other dimensions
    public static final DeferredHolder<Item, Item> EYE_OF_YOG_SOTHOTH = Registration.ITEMS.register("eye_of_yog_sothoth",
            () -> new Item(new Item.Properties().stacksTo(1)));
            
    // The Void Amulet - protects the wearer from void corruption
    public static final DeferredHolder<Item, Item> VOID_AMULET = Registration.ITEMS.register("void_amulet", 
            () -> new Item(new Item.Properties().stacksTo(1)));
    
    /**
     * Register all artifact items.
     */
    public static void register() {
        EldritchVoid.LOGGER.info("Registering Eldritch Artifacts items");
    }
}