package com.eldritchvoid.core.api;

import com.eldritchvoid.EldritchVoid;
import com.eldritchvoid.core.ModuleManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Main API class for the Eldritch Void mod.
 * Provides access to module functionality for other mods to interact with.
 */
public class EldritchVoidAPI {
    private static final Logger LOGGER = LogManager.getLogger();
    private static EldritchVoidAPI instance;
    
    /**
     * Initialize the API.
     */
    public static void init() {
        if (instance == null) {
            instance = new EldritchVoidAPI();
            LOGGER.info("Eldritch Void API initialized");
        }
    }
    
    /**
     * Get the API instance.
     * 
     * @return The API instance
     */
    public static EldritchVoidAPI getInstance() {
        if (instance == null) {
            LOGGER.error("Eldritch Void API accessed before initialization");
            init();
        }
        return instance;
    }
    
    /**
     * Gets the module manager.
     * 
     * @return The module manager
     */
    public ModuleManager getModuleManager() {
        return EldritchVoid.instance.getModuleManager();
    }
    
    /**
     * Checks if a module is loaded and enabled.
     * 
     * @param moduleId The module ID
     * @return True if the module is loaded and enabled
     */
    public boolean isModuleLoaded(String moduleId) {
        return getModuleManager().getModule(moduleId) != null;
    }
    
    /**
     * Gets the Void Alchemy API for interacting with void alchemy functionality.
     * 
     * @return The Void Alchemy API
     */
    public VoidAlchemyAPI getVoidAlchemyAPI() {
        if (!isModuleLoaded("voidalchemy")) {
            LOGGER.warn("Void Alchemy module is not loaded, API calls will not function");
        }
        return VoidAlchemyAPI.getInstance();
    }
    
    /**
     * Gets the Eldritch Arcana API for interacting with research and magic.
     * 
     * @return The Eldritch Arcana API
     */
    public EldritchArcanaAPI getEldritchArcanaAPI() {
        if (!isModuleLoaded("eldritcharcana")) {
            LOGGER.warn("Eldritch Arcana module is not loaded, API calls will not function");
        }
        return EldritchArcanaAPI.getInstance();
    }
}
