package com.eldritchvoid.core;

import com.eldritchvoid.EldritchVoid;
import com.eldritchvoid.modules.voidalchemy.VoidAlchemyModule;
import com.eldritchvoid.modules.eldritchartifacts.EldritchArtifactsModule;
import com.eldritchvoid.modules.obsidianforgemaster.ObsidianForgemasterModule;
import com.eldritchvoid.modules.voidcorruption.VoidCorruptionModule;
import com.eldritchvoid.modules.eldritcharcana.EldritchArcanaModule;
import com.eldritchvoid.modules.obsidianconstructs.ObsidianConstructsModule;

import java.util.HashMap;
import java.util.Map;

/**
 * Manages all modules in the Eldritch Void mod.
 * Handles registration, dependency resolution, and lifecycle events.
 */
public class ModuleManager {
    private static final ModuleManager INSTANCE = new ModuleManager();
    private final Map<String, Module> modules = new HashMap<>();
    
    /**
     * Get the singleton instance of the ModuleManager.
     *
     * @return The ModuleManager instance
     */
    public static ModuleManager getInstance() {
        return INSTANCE;
    }
    
    /**
     * Initialize all modules.
     */
    public void initializeModules() {
        EldritchVoid.LOGGER.info("Initializing modules...");
        
        // Register all modules
        registerModule(new VoidAlchemyModule());
        registerModule(new EldritchArtifactsModule());
        registerModule(new ObsidianForgemasterModule());
        registerModule(new VoidCorruptionModule());
        registerModule(new EldritchArcanaModule());
        registerModule(new ObsidianConstructsModule());
        
        // Initialize registered modules
        modules.values().forEach(Module::init);
        
        // Enable all modules
        modules.values().forEach(Module::onEnable);
        
        EldritchVoid.LOGGER.info("Modules initialized: " + modules.size());
    }
    
    /**
     * Register a module.
     *
     * @param module The module to register
     */
    public void registerModule(Module module) {
        String id = module.getId();
        modules.put(id, module);
        EldritchVoid.LOGGER.info("Registered module: " + module.getDisplayName() + " (ID: " + id + ")");
    }
    
    /**
     * Get a module by ID.
     *
     * @param id The ID of the module to get
     * @return The module, or null if not found
     */
    public Module getModule(String id) {
        return modules.get(id);
    }
    
    /**
     * Shutdown all modules.
     */
    public void shutdownModules() {
        EldritchVoid.LOGGER.info("Shutting down modules...");
        modules.values().forEach(Module::onDisable);
    }
    
    /**
     * Get all registered modules.
     *
     * @return A map of module IDs to modules
     */
    public Map<String, Module> getModules() {
        return modules;
    }
}