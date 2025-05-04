package com.eldritchvoid.core.module;

import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

/**
 * Interface for all modules.
 * Each module represents a distinct feature set within the mod.
 */
public interface IModule {
    
    /**
     * Get the unique ID of this module.
     * Used for internal references and configurations.
     *
     * @return The module ID
     */
    String getId();
    
    /**
     * Get the display name of this module.
     * Used for UI and logging.
     *
     * @return The module name
     */
    String getName();
    
    /**
     * Get the description of this module.
     * Explains what this module adds to the game.
     *
     * @return The module description
     */
    String getDescription();
    
    /**
     * Check if this module is enabled.
     * Disabled modules are not initialized.
     *
     * @return True if the module is enabled
     */
    boolean isEnabled();
    
    /**
     * Enable or disable this module.
     *
     * @param enabled Whether the module should be enabled
     */
    void setEnabled(boolean enabled);
    
    /**
     * Initialize this module.
     * Called during common setup.
     *
     * @param event The common setup event
     */
    void initialize(FMLCommonSetupEvent event);
    
    /**
     * Register all content for this module.
     * This includes items, blocks, entities, etc.
     */
    void registerContent();
}