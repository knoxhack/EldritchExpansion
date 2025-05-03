package com.eldritchvoid.core;

import net.neoforged.fml.event.lifecycle.InterModProcessEvent;

import java.util.List;

/**
 * Interface for all Eldritch Void modules.
 * Each module must implement this interface.
 */
public interface IEldritchModule {
    /**
     * Gets the unique identifier for this module.
     * 
     * @return The module's ID
     */
    String getModuleId();
    
    /**
     * Gets the display name of this module.
     * 
     * @return The module's display name
     */
    String getDisplayName();
    
    /**
     * Gets the description of this module.
     * 
     * @return The module's description
     */
    String getDescription();
    
    /**
     * Gets a list of module IDs that this module depends on.
     * 
     * @return List of module IDs
     */
    List<String> getDependencies();
    
    /**
     * Initializes the module.
     * Called during mod initialization.
     */
    void initialize();
    
    /**
     * Called during common setup.
     */
    void onCommonSetup();
    
    /**
     * Called during client setup.
     */
    void onClientSetup();
    
    /**
     * Called to enqueue inter-mod communication (IMC) messages.
     */
    void onInterModEnqueue();
    
    /**
     * Called to process inter-mod communication (IMC) messages.
     */
    void onInterModProcess(InterModProcessEvent event);
}
