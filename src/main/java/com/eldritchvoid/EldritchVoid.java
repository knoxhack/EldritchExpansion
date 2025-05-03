package com.eldritchvoid;

import com.eldritchvoid.core.ModuleManager;
import com.eldritchvoid.core.registry.Registration;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The main mod class for Eldritch Void.
 * This is the entry point for the mod.
 */
@Mod(EldritchVoid.MOD_ID)
public class EldritchVoid {
    /**
     * The mod ID.
     */
    public static final String MOD_ID = "eldritchvoid";
    
    /**
     * The logger instance for the mod.
     */
    public static final Logger LOGGER = LoggerFactory.getLogger("Eldritch Void");
    
    /**
     * Constructor for the mod.
     * Initializes the mod and sets up event handlers.
     *
     * @param modEventBus The mod event bus
     */
    public EldritchVoid(IEventBus modEventBus) {
        LOGGER.info("Initializing Eldritch Void mod...");
        
        // Register mod setup
        modEventBus.addListener(this::commonSetup);
        
        // Initialize registries
        Registration.init();
        
        // Initialize modules
        ModuleManager.getInstance().initializeModules();
        
        LOGGER.info("Eldritch Void initialization complete");
    }
    
    /**
     * Common setup event handler.
     * Runs after all registries are set up.
     *
     * @param event The common setup event
     */
    private void commonSetup(final FMLCommonSetupEvent event) {
        LOGGER.info("Eldritch Void: Common setup phase");
        
        // Run common setup tasks
        event.enqueueWork(() -> {
            // Setup networking
            // Setup packet handling
            // Other common setup tasks
        });
    }
}