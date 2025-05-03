package com.eldritchvoid.core;

import com.eldritchvoid.EldritchVoid;
import java.util.HashSet;
import java.util.Set;

/**
 * Base implementation of the Module interface with common functionality.
 * Modules should extend this class rather than implementing Module directly.
 */
public abstract class AbstractModule implements Module {
    private boolean enabled = false;
    private final Set<String> dependencies = new HashSet<>();
    
    /**
     * Initialize the module.
     */
    @Override
    public void init() {
        log("Initializing module: " + getDisplayName());
    }
    
    /**
     * Log a message with the module's prefix.
     *
     * @param message The message to log
     */
    protected void log(String message) {
        EldritchVoid.LOGGER.info("[" + getDisplayName() + "] " + message);
    }
    
    /**
     * Add a dependency for this module.
     *
     * @param moduleId The ID of the module this module depends on
     */
    protected void addDependency(String moduleId) {
        dependencies.add(moduleId);
    }
    
    /**
     * Check if this module depends on another module.
     *
     * @param moduleId The ID of the module to check dependency on
     * @return True if this module depends on the specified module
     */
    @Override
    public boolean dependsOn(String moduleId) {
        return dependencies.contains(moduleId);
    }
    
    /**
     * Enable the module.
     */
    @Override
    public void onEnable() {
        if (!enabled) {
            enabled = true;
            log("Module enabled");
        }
    }
    
    /**
     * Disable the module.
     */
    @Override
    public void onDisable() {
        if (enabled) {
            enabled = false;
            log("Module disabled");
        }
    }
    
    /**
     * Check if the module is enabled.
     *
     * @return True if the module is enabled
     */
    @Override
    public boolean isEnabled() {
        return enabled;
    }
}