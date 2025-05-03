package com.eldritchvoid.modules.eldritcharcana;

import com.eldritchvoid.core.AbstractModule;
import net.minecraft.world.item.CreativeModeTabs;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;

/**
 * The Eldritch Arcana module provides magical spells, rituals, and occult
 * knowledge systems for players to discover and master. It includes various
 * casting mechanics, spell books, and magical effects.
 */
public class EldritchArcanaModule extends AbstractModule {

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
        
        // Register items and blocks
        EldritchArcanaItems.register();
        
        log("Initialized Eldritch Arcana module");
    }
    
    /**
     * Setup the module during common setup phase.
     *
     * @param event The common setup event
     */
    private void setup(final FMLCommonSetupEvent event) {
        // Setup common functionality
        log("Setting up Eldritch Arcana module");
    }
    
    /**
     * Add items to creative tabs.
     *
     * @param event The creative tab contents event
     */
    private void addCreative(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.TOOLS_AND_UTILITIES) {
            // Add magical tools and utilities
            // Will be expanded when proper registrations are implemented
        }
        
        if (event.getTabKey() == CreativeModeTabs.INGREDIENTS) {
            // Add magical ingredients
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
        return "eldritcharcana";
    }
    
    /**
     * Get the display name of the module.
     *
     * @return The module display name
     */
    @Override
    public String getDisplayName() {
        return "Eldritch Arcana";
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