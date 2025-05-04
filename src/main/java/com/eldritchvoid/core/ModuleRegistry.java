package com.eldritchvoid.core;

import com.eldritchvoid.EldritchVoid;
import com.eldritchvoid.core.module.IModule;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

import java.util.ArrayList;
import java.util.List;

/**
 * Registry for all modules in the mod.
 * Manages module initialization and communication.
 */
public class ModuleRegistry {
    
    private final List<IModule> modules = new ArrayList<>();
    
    /**
     * Register a module with the registry.
     *
     * @param module The module to register
     */
    public void registerModule(IModule module) {
        if (getModule(module.getId()) != null) {
            EldritchVoid.LOGGER.warn("Module with ID {} already registered, skipping", module.getId());
            return;
        }
        
        modules.add(module);
        EldritchVoid.LOGGER.info("Registered module: {}", module.getName());
    }
    
    /**
     * Get a module by its ID.
     *
     * @param id The module ID
     * @return The module, or null if not found
     */
    public IModule getModule(String id) {
        return modules.stream()
                .filter(m -> m.getId().equals(id))
                .findFirst()
                .orElse(null);
    }
    
    /**
     * Initialize all registered modules.
     * 
     * @param event The common setup event
     */
    public void initializeAll(FMLCommonSetupEvent event) {
        EldritchVoid.LOGGER.info("Initializing {} modules", modules.size());
        
        for (IModule module : modules) {
            if (module.isEnabled()) {
                try {
                    module.initialize(event);
                    EldritchVoid.LOGGER.info("Initialized module: {}", module.getName());
                } catch (Exception e) {
                    EldritchVoid.LOGGER.error("Failed to initialize module {}: {}", module.getName(), e.getMessage());
                    e.printStackTrace();
                }
            } else {
                EldritchVoid.LOGGER.info("Skipping disabled module: {}", module.getName());
            }
        }
    }
    
    /**
     * Get all registered modules.
     *
     * @return List of all modules
     */
    public List<IModule> getAllModules() {
        return new ArrayList<>(modules); // Return a copy to prevent modification
    }
}