package com.eldritchvoid.core.module;

import com.eldritchvoid.EldritchVoid;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

/**
 * Core module of the Eldritch Void mod.
 * Provides shared functionality for all other modules.
 */
public class CoreModule extends BaseModule {
    
    public CoreModule() {
        super(
            "core",
            "Eldritch Core",
            "Core components and shared resources for all Eldritch Void modules"
        );
    }
    
    @Override
    public void registerContent() {
        log("Registering Core content");
        // Core content is registered in ItemInit and BlockInit
    }
    
    @Override
    protected void onInitialize(FMLCommonSetupEvent event) {
        log("Initializing Core systems");
        
        // Example of Core initialization logic
        registerNetworkChannels();
        setupVoidEssenceConversion();
    }
    
    /**
     * Register network channels for module communication.
     */
    private void registerNetworkChannels() {
        log("Registering network channels");
        // Network registration would happen here
    }
    
    /**
     * Set up conversion recipes for Void Essence.
     */
    private void setupVoidEssenceConversion() {
        log("Setting up Void Essence conversion recipes");
        // Recipe registration would happen here
    }
}