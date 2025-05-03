package com.eldritchvoid.modules.voidcorruption;

import com.eldritchvoid.core.Module;
import net.minecraft.world.item.CreativeModeTabs;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;

/**
 * The Void Corruption module introduces a spreading corruption mechanic that can
 * infect blocks, entities, and even players. This corruption provides both challenges
 * and benefits depending on how players interact with it.
 */
public class VoidCorruptionModule extends Module {

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
        VoidCorruptionBlocks.register();
        VoidCorruptionItems.register();
        
        log("Initialized Void Corruption module");
    }
    
    /**
     * Setup the module during common setup phase.
     *
     * @param event The common setup event
     */
    private void setup(final FMLCommonSetupEvent event) {
        // Setup common functionality
        log("Setting up Void Corruption module");
    }
    
    /**
     * Add items to creative tabs.
     *
     * @param event The creative tab contents event
     */
    private void addCreative(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.BUILDING_BLOCKS) {
            // Add corrupt blocks to the building blocks tab
            // Will be expanded when proper registrations are implemented
        }
        
        if (event.getTabKey() == CreativeModeTabs.INGREDIENTS) {
            // Add corruption-related items to the ingredients tab
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
        return "voidcorruption";
    }
    
    /**
     * Get the display name of the module.
     *
     * @return The module display name
     */
    @Override
    public String getDisplayName() {
        return "Void Corruption";
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