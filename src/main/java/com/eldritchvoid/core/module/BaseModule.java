package com.eldritchvoid.core.module;

import com.eldritchvoid.EldritchVoid;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

/**
 * Base implementation of IModule.
 * Provides common functionality for all modules.
 */
public abstract class BaseModule implements IModule {
    
    protected final String id;
    protected final String name;
    protected final String description;
    protected boolean enabled = true;
    
    /**
     * Create a new module.
     *
     * @param id The module ID
     * @param name The module name
     * @param description The module description
     */
    protected BaseModule(String id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
    }
    
    @Override
    public String getId() {
        return id;
    }
    
    @Override
    public String getName() {
        return name;
    }
    
    @Override
    public String getDescription() {
        return description;
    }
    
    @Override
    public boolean isEnabled() {
        return enabled;
    }
    
    @Override
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
        
        if (enabled) {
            EldritchVoid.LOGGER.info("Enabled module: {}", name);
        } else {
            EldritchVoid.LOGGER.info("Disabled module: {}", name);
        }
    }
    
    @Override
    public void initialize(FMLCommonSetupEvent event) {
        if (!enabled) return;
        
        EldritchVoid.LOGGER.info("Initializing module: {}", name);
        registerContent();
        
        // Module-specific initialization
        onInitialize(event);
    }
    
    /**
     * Called during initialization.
     * Implement this to add module-specific initialization logic.
     *
     * @param event The common setup event
     */
    protected abstract void onInitialize(FMLCommonSetupEvent event);
    
    /**
     * Log a message with the module's prefix.
     *
     * @param message The message to log
     */
    protected void log(String message) {
        EldritchVoid.LOGGER.info("[{}] {}", name, message);
    }
}