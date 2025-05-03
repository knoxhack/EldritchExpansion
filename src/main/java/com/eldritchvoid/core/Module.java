package com.eldritchvoid.core;

/**
 * Interface that all modules in the Eldritch Void mod must implement.
 * Modules are self-contained features that can be enabled/disabled independently.
 */
public interface Module {
    /**
     * Initialize the module. This is called during mod initialization.
     */
    void init();
    
    /**
     * Get the ID of the module. This should be a lowercase string with no spaces.
     *
     * @return The module ID
     */
    String getId();
    
    /**
     * Get the display name of the module. This is used in UIs and logs.
     *
     * @return The module display name
     */
    String getDisplayName();
    
    /**
     * Check if this module depends on another module.
     *
     * @param moduleId The ID of the module to check dependency on
     * @return True if this module depends on the specified module
     */
    boolean dependsOn(String moduleId);
    
    /**
     * Called when the module is enabled.
     */
    void onEnable();
    
    /**
     * Called when the module is disabled.
     */
    void onDisable();
    
    /**
     * Check if the module is enabled.
     *
     * @return True if the module is enabled
     */
    boolean isEnabled();
}