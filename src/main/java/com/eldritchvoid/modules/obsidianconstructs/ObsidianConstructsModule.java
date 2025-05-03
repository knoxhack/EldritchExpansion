package com.eldritchvoid.modules.obsidianconstructs;

import com.eldritchvoid.core.AbstractModule;
import net.minecraft.world.item.CreativeModeTabs;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;

/**
 * The Obsidian Constructs module allows players to build and animate obsidian-based
 * golems, guardians, and structures. These constructs can be used for protection,
 * automation, or aesthetic purposes.
 */
public class ObsidianConstructsModule extends AbstractModule {

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
        
        // Register blocks, items and entities
        ObsidianConstructsBlocks.register();
        ObsidianConstructsItems.register();
        ObsidianConstructsEntities.register();
        
        log("Initialized Obsidian Constructs module");
    }
    
    /**
     * Setup the module during common setup phase.
     *
     * @param event The common setup event
     */
    private void setup(final FMLCommonSetupEvent event) {
        // Setup common functionality
        log("Setting up Obsidian Constructs module");
    }
    
    /**
     * Add items to creative tabs.
     *
     * @param event The creative tab contents event
     */
    private void addCreative(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.BUILDING_BLOCKS) {
            // Add construct blocks to the building blocks tab
            // Will be expanded when proper registrations are implemented
        }
        
        if (event.getTabKey() == CreativeModeTabs.TOOLS_AND_UTILITIES) {
            // Add construct creation tools
            // Will be expanded when proper registrations are implemented
        }
        
        if (event.getTabKey() == CreativeModeTabs.SPAWN_EGGS) {
            // Add construct spawn eggs
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
        return "obsidianconstructs";
    }
    
    /**
     * Get the display name of the module.
     *
     * @return The module display name
     */
    @Override
    public String getDisplayName() {
        return "Obsidian Constructs";
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