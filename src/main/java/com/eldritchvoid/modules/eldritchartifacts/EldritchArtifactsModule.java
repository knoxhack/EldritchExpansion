package com.eldritchvoid.modules.eldritchartifacts;

import com.eldritchvoid.core.AbstractModule;
import net.minecraft.world.item.CreativeModeTabs;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;

/**
 * The Eldritch Artifacts module provides players with powerful magical items
 * that grant unique abilities and effects inspired by Lovecraftian lore.
 */
public class EldritchArtifactsModule extends AbstractModule {

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
        
        // Register artifacts
        EldritchArtifactsItems.register();
        
        log("Initialized Eldritch Artifacts module");
    }
    
    /**
     * Setup the module during common setup phase.
     *
     * @param event The common setup event
     */
    private void setup(final FMLCommonSetupEvent event) {
        // Setup common functionality
        log("Setting up Eldritch Artifacts module");
    }
    
    /**
     * Add artifacts to creative tabs.
     *
     * @param event The creative tab contents event
     */
    private void addCreative(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.TOOLS_AND_UTILITIES) {
            // Add utility artifacts
            // Will be expanded when proper registrations are implemented
        }
        
        if (event.getTabKey() == CreativeModeTabs.COMBAT) {
            // Add combat artifacts
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
        return "eldritchartifacts";
    }
    
    /**
     * Get the display name of the module.
     *
     * @return The module display name
     */
    @Override
    public String getDisplayName() {
        return "Eldritch Artifacts";
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