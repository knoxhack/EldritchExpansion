package com.eldritchvoid.core;

import com.eldritchvoid.EldritchVoid;
import com.eldritchvoid.core.event.ModuleEventBus;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;

import java.util.*;

/**
 * Manages all modules for the mod.
 * Handles lifecycle events and module dependencies.
 */
public class ModuleManager {
    private final Map<String, Module> modules = new LinkedHashMap<>();
    private final IEventBus modBus;
    private boolean modulesInitialized = false;
    
    /**
     * Create a new module manager.
     *
     * @param modBus The mod event bus
     */
    public ModuleManager(IEventBus modBus) {
        this.modBus = modBus;
        
        // Register event handlers
        modBus.addListener(this::onCommonSetup);
        modBus.addListener(this::onClientSetup);
        
        EldritchVoid.LOGGER.info("Module manager initialized");
    }
    
    /**
     * Register a module with the manager.
     *
     * @param module The module to register
     */
    public void registerModule(Module module) {
        String moduleName = module.getModuleName();
        
        if (modules.containsKey(moduleName)) {
            EldritchVoid.LOGGER.error("Module with name {} already registered, skipping", moduleName);
            return;
        }
        
        modules.put(moduleName, module);
        module.setManager(this);
        
        EldritchVoid.LOGGER.info("Registered module: {}", moduleName);
    }
    
    /**
     * Initialize all registered modules.
     */
    public void initializeModules() {
        if (modulesInitialized) {
            EldritchVoid.LOGGER.warn("Modules already initialized, skipping");
            return;
        }
        
        // Sort modules by dependencies (not implemented yet)
        List<Module> sortedModules = sortModulesByDependencies();
        
        // Register content for all modules
        for (Module module : sortedModules) {
            if (module.isEnabled()) {
                try {
                    module.registerContent();
                    EldritchVoid.LOGGER.info("Registered content for module: {}", module.getModuleName());
                } catch (Exception e) {
                    EldritchVoid.LOGGER.error("Error registering content for module {}: {}", module.getModuleName(), e.getMessage());
                    e.printStackTrace();
                }
            }
        }
        
        // Notify modules that registration is complete
        for (Module module : sortedModules) {
            if (module.isEnabled()) {
                try {
                    module.onRegistrationComplete();
                } catch (Exception e) {
                    EldritchVoid.LOGGER.error("Error in post-registration for module {}: {}", module.getModuleName(), e.getMessage());
                    e.printStackTrace();
                }
            }
        }
        
        modulesInitialized = true;
        EldritchVoid.LOGGER.info("All modules initialized");
    }
    
    /**
     * Sort modules by dependencies.
     * This is a placeholder for a proper dependency sorting algorithm.
     *
     * @return A list of modules sorted by dependencies
     */
    private List<Module> sortModulesByDependencies() {
        // For now, just return all modules in registration order
        return new ArrayList<>(modules.values());
    }
    
    /**
     * Handle the common setup event.
     *
     * @param event The common setup event
     */
    private void onCommonSetup(FMLCommonSetupEvent event) {
        EldritchVoid.LOGGER.info("Common setup event received");
        
        for (Module module : modules.values()) {
            if (module.isEnabled()) {
                try {
                    module.onCommonSetup(event);
                } catch (Exception e) {
                    EldritchVoid.LOGGER.error("Error in common setup for module {}: {}", module.getModuleName(), e.getMessage());
                    e.printStackTrace();
                }
            }
        }
    }
    
    /**
     * Handle the client setup event.
     *
     * @param event The client setup event
     */
    private void onClientSetup(FMLClientSetupEvent event) {
        EldritchVoid.LOGGER.info("Client setup event received");
        
        for (Module module : modules.values()) {
            if (module.isEnabled()) {
                try {
                    module.onClientSetup(event);
                } catch (Exception e) {
                    EldritchVoid.LOGGER.error("Error in client setup for module {}: {}", module.getModuleName(), e.getMessage());
                    e.printStackTrace();
                }
            }
        }
    }
    
    /**
     * Get a module by name.
     *
     * @param moduleName The name of the module
     * @return The module, or null if not found
     */
    public Module getModule(String moduleName) {
        return modules.get(moduleName);
    }
    
    /**
     * Get all registered modules.
     *
     * @return A collection of all modules
     */
    public Collection<Module> getAllModules() {
        return modules.values();
    }
    
    /**
     * Get all enabled modules.
     *
     * @return A collection of enabled modules
     */
    public Collection<Module> getEnabledModules() {
        List<Module> enabledModules = new ArrayList<>();
        
        for (Module module : modules.values()) {
            if (module.isEnabled()) {
                enabledModules.add(module);
            }
        }
        
        return enabledModules;
    }
    
    /**
     * Check if all modules are initialized.
     *
     * @return True if all modules are initialized
     */
    public boolean areModulesInitialized() {
        return modulesInitialized;
    }
    
    /**
     * Post a module event.
     *
     * @param event The event to post
     * @return The posted event
     */
    public net.neoforged.bus.api.Event postModuleEvent(net.neoforged.bus.api.Event event) {
        return ModuleEventBus.post(event);
    }
}