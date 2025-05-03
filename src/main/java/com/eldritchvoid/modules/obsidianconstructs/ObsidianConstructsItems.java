package com.eldritchvoid.modules.obsidianconstructs;

import com.eldritchvoid.EldritchVoid;
import com.eldritchvoid.core.registry.Registration;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredHolder;

/**
 * Registers all items for the Obsidian Constructs module.
 */
public class ObsidianConstructsItems {
    // Obsidian Constructs items - these will be expanded with proper functionality
    
    // Construct Binding Tool - used to assemble and configure constructs
    public static final DeferredHolder<Item, Item> CONSTRUCT_BINDING_TOOL = Registration.ITEMS.register("construct_binding_tool",
            () -> new Item(new Item.Properties().stacksTo(1).durability(128)));
            
    // Void Resonator - used to infuse constructs with void energy
    public static final DeferredHolder<Item, Item> VOID_RESONATOR = Registration.ITEMS.register("void_resonator",
            () -> new Item(new Item.Properties().stacksTo(1)));
            
    // Obsidian Golem Heart - core component for animated obsidian golems
    public static final DeferredHolder<Item, Item> OBSIDIAN_GOLEM_HEART = Registration.ITEMS.register("obsidian_golem_heart", 
            () -> new Item(new Item.Properties()));
    
    /**
     * Register all items.
     */
    public static void register() {
        EldritchVoid.LOGGER.info("Registering Obsidian Constructs items");
    }
}