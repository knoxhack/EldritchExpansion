package com.eldritchvoid.core;

import com.eldritchvoid.EldritchVoid;
import net.neoforged.fml.event.lifecycle.InterModProcessEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Manages the modules of the Eldritch Void mod.
 * Handles module loading, dependencies, and lifecycle events.
 */
public class ModuleManager {
    private static final Logger LOGGER = LogManager.getLogger();
    
    private final Map<String, IEldritchModule> modules = new HashMap<>();
    private final List<IEldritchModule> orderedModules = new ArrayList<>();
    
    /**
     * Registers a module with the module manager.
     * 
     * @param module The module to register
     */
    public void registerModule(IEldritchModule module) {
        String moduleId = module.getModuleId();
        
        if (modules.containsKey(moduleId)) {
            LOGGER.warn("Module with ID {} is already registered. Skipping duplicate registration.", moduleId);
            return;
        }
        
        modules.put(moduleId, module);
        orderedModules.add(module);
        
        LOGGER.info("Registered module: {}", moduleId);
    }
    
    /**
     * Initializes all registered modules.
     * Checks dependencies and initializes modules in the correct order.
     */
    public void initializeModules() {
        LOGGER.info("Initializing {} Eldritch Void modules", modules.size());
        
        // Sort modules by dependency order
        sortModulesByDependency();
        
        // Initialize each module
        for (IEldritchModule module : orderedModules) {
            try {
                if (isModuleEnabled(module)) {
                    LOGGER.info("Initializing module: {}", module.getModuleId());
                    module.initialize();
                } else {
                    LOGGER.info("Module {} is disabled, skipping initialization", module.getModuleId());
                }
            } catch (Exception e) {
                LOGGER.error("Failed to initialize module {}: {}", module.getModuleId(), e.getMessage());
                e.printStackTrace();
            }
        }
    }
    
    /**
     * Checks if a module is enabled through configuration.
     * 
     * @param module The module to check
     * @return True if the module is enabled, false otherwise
     */
    private boolean isModuleEnabled(IEldritchModule module) {
        // TODO: Implement configuration-based module enabling/disabling
        return true;
    }
    
    /**
     * Sorts modules by dependency order.
     */
    private void sortModulesByDependency() {
        // Implementation of topological sort for dependency resolution
        // For now, we'll use a simple approach where core modules are loaded first
        
        List<IEldritchModule> sortedModules = new ArrayList<>();
        
        // First pass: Add modules without dependencies
        for (IEldritchModule module : orderedModules) {
            if (module.getDependencies().isEmpty()) {
                sortedModules.add(module);
            }
        }
        
        // Second pass: Add remaining modules in dependency order
        List<IEldritchModule> remainingModules = new ArrayList<>(orderedModules);
        remainingModules.removeAll(sortedModules);
        
        while (!remainingModules.isEmpty()) {
            boolean progress = false;
            
            for (int i = 0; i < remainingModules.size(); i++) {
                IEldritchModule module = remainingModules.get(i);
                boolean allDependenciesMet = true;
                
                for (String dependency : module.getDependencies()) {
                    boolean dependencyMet = false;
                    
                    for (IEldritchModule loadedModule : sortedModules) {
                        if (loadedModule.getModuleId().equals(dependency)) {
                            dependencyMet = true;
                            break;
                        }
                    }
                    
                    if (!dependencyMet) {
                        allDependenciesMet = false;
                        break;
                    }
                }
                
                if (allDependenciesMet) {
                    sortedModules.add(module);
                    remainingModules.remove(i);
                    progress = true;
                    i--;
                }
            }
            
            if (!progress && !remainingModules.isEmpty()) {
                LOGGER.error("Circular dependency detected in modules!");
                // Add remaining modules anyway to prevent getting stuck
                sortedModules.addAll(remainingModules);
                break;
            }
        }
        
        orderedModules.clear();
        orderedModules.addAll(sortedModules);
    }
    
    /**
     * Gets a registered module by its ID.
     * 
     * @param moduleId The ID of the module to get
     * @return The module, or null if not found
     */
    public IEldritchModule getModule(String moduleId) {
        return modules.get(moduleId);
    }
    
    /**
     * Called during common setup.
     */
    public void onCommonSetup() {
        for (IEldritchModule module : orderedModules) {
            if (isModuleEnabled(module)) {
                try {
                    module.onCommonSetup();
                } catch (Exception e) {
                    LOGGER.error("Error during common setup for module {}: {}", module.getModuleId(), e.getMessage());
                    e.printStackTrace();
                }
            }
        }
    }
    
    /**
     * Called during client setup.
     */
    public void onClientSetup() {
        for (IEldritchModule module : orderedModules) {
            if (isModuleEnabled(module)) {
                try {
                    module.onClientSetup();
                } catch (Exception e) {
                    LOGGER.error("Error during client setup for module {}: {}", module.getModuleId(), e.getMessage());
                    e.printStackTrace();
                }
            }
        }
    }
    
    /**
     * Called to enqueue IMC messages.
     */
    public void onInterModEnqueue() {
        for (IEldritchModule module : orderedModules) {
            if (isModuleEnabled(module)) {
                try {
                    module.onInterModEnqueue();
                } catch (Exception e) {
                    LOGGER.error("Error during IMC enqueue for module {}: {}", module.getModuleId(), e.getMessage());
                    e.printStackTrace();
                }
            }
        }
    }
    
    /**
     * Called to process IMC messages.
     */
    public void onInterModProcess(InterModProcessEvent event) {
        for (IEldritchModule module : orderedModules) {
            if (isModuleEnabled(module)) {
                try {
                    module.onInterModProcess(event);
                } catch (Exception e) {
                    LOGGER.error("Error during IMC process for module {}: {}", module.getModuleId(), e.getMessage());
                    e.printStackTrace();
                }
            }
        }
    }
}
