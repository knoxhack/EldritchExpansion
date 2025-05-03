package com.eldritchvoid.core.api;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.Map;

/**
 * API for interacting with the Void Alchemy module.
 */
public class VoidAlchemyAPI {
    private static final Logger LOGGER = LogManager.getLogger();
    private static VoidAlchemyAPI instance;
    
    private final Map<String, Integer> essenceValues = new HashMap<>();
    
    /**
     * Private constructor to enforce singleton pattern.
     */
    private VoidAlchemyAPI() {
        // Initialize with default values
    }
    
    /**
     * Get the API instance.
     * 
     * @return The API instance
     */
    public static VoidAlchemyAPI getInstance() {
        if (instance == null) {
            instance = new VoidAlchemyAPI();
        }
        return instance;
    }
    
    /**
     * Registers an item as a source of void essence.
     * 
     * @param itemId The item registry name
     * @param essenceValue The amount of essence the item provides
     */
    public void registerEssenceSource(String itemId, int essenceValue) {
        if (essenceValue <= 0) {
            LOGGER.warn("Attempted to register {} with invalid essence value {}. Values must be positive.", itemId, essenceValue);
            return;
        }
        
        essenceValues.put(itemId, essenceValue);
        LOGGER.debug("Registered {} as essence source with value {}", itemId, essenceValue);
    }
    
    /**
     * Gets the essence value for an item.
     * 
     * @param itemId The item registry name
     * @return The essence value, or 0 if not registered
     */
    public int getEssenceValue(String itemId) {
        return essenceValues.getOrDefault(itemId, 0);
    }
    
    /**
     * Check if an item is registered as an essence source.
     * 
     * @param itemId The item registry name
     * @return True if the item is registered
     */
    public boolean isEssenceSource(String itemId) {
        return essenceValues.containsKey(itemId);
    }
}
