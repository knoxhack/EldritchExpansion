package com.eldritchvoid.modules.obsidianforgemaster;

import com.eldritchvoid.core.Module;
import net.minecraft.world.item.CreativeModeTabs;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;

/**
 * The Obsidian Forgemaster module enables players to create and upgrade tools and weapons
 * using obsidian-infused forging techniques. It provides special forge blocks, tools and recipes.
 */
public class ObsidianForgemasterModule extends Module {

    /**
     * Initialize the module.
     */
    @Override
    public void init() {
        super.init();
        
        // Get the mod event bus
        IEventBus modEventBus = ModLoadingContext.get().getActiveContainer().getEventBus();
        
        // Register lifecycle events
        modEventBus.addListener(this::setup);
        modEventBus.addListener(this::addCreative);
        
        // Register blocks and items
        ObsidianForgemasterBlocks.register();
        ObsidianForgemasterItems.register();
        
        log("Initialized Obsidian Forgemaster module");
    }
    
    /**
     * Setup the module during the common setup phase.
     *
     * @param event The common setup event
     */
    private void setup(final FMLCommonSetupEvent event) {
        // Setup common functionality
        log("Setting up Obsidian Forgemaster module");
    }
    
    /**
     * Add items to creative tabs.
     *
     * @param event The creative tab contents event
     */
    private void addCreative(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.BUILDING_BLOCKS) {
            // Add forge blocks to the building blocks tab
            // Will be expanded when proper registrations are implemented
        }
        
        if (event.getTabKey() == CreativeModeTabs.TOOLS_AND_UTILITIES) {
            // Add forging tools to the tools tab
            // Will be expanded when proper registrations are implemented
        }
    }
    
    /**
     * Get the ID of the module.
     *
     * @return The module ID
     */
    @Override
    public String getId() {
        return "obsidianforgemaster";
    }
    
    /**
     * Get the display name of the module.
     *
     * @return The module display name
     */
    @Override
    public String getDisplayName() {
        return "Obsidian Forgemaster";
    }
    
    /**
     * Called when the module is enabled.
     */
    @Override
    public void onEnable() {
        super.onEnable();
        // Additional enabling logic
    }
    
    /**
     * Called when the module is disabled.
     */
    @Override
    public void onDisable() {
        super.onDisable();
        // Additional disabling logic
    }
}