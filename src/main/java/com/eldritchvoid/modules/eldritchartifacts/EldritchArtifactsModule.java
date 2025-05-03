package com.eldritchvoid.modules.eldritchartifacts;

import com.eldritchvoid.EldritchVoid;
import com.eldritchvoid.core.IEldritchModule;
import com.eldritchvoid.modules.eldritchartifacts.worldgen.ShrineGenerator;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.event.lifecycle.InterModProcessEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Collections;
import java.util.List;

/**
 * The Eldritch Artifacts module for Eldritch Void.
 * Handles procedural shrine generation and artifact mechanics.
 */
public class EldritchArtifactsModule implements IEldritchModule {
    private static final Logger LOGGER = LogManager.getLogger();
    private final IEventBus modEventBus;
    
    public EldritchArtifactsModule(IEventBus modEventBus) {
        this.modEventBus = modEventBus;
    }
    
    @Override
    public String getModuleId() {
        return "eldritchartifacts";
    }
    
    @Override
    public String getDisplayName() {
        return "Eldritch Artifacts";
    }
    
    @Override
    public String getDescription() {
        return "Discover ancient artifacts with mysterious powers";
    }
    
    @Override
    public List<String> getDependencies() {
        return Collections.emptyList(); // No dependencies
    }
    
    @Override
    public void initialize() {
        LOGGER.info("Initializing Eldritch Artifacts Module");
        
        // Register event handlers
        modEventBus.addListener(this::onCommonSetup);
        modEventBus.addListener(this::onClientSetup);
        
        // Initialize shrine generator
        ShrineGenerator.register(modEventBus);
    }
    
    @Override
    public void onCommonSetup() {
        LOGGER.info("Eldritch Artifacts: Common Setup");
        
        // Initialize artifact effects
    }
    
    @Override
    public void onClientSetup() {
        LOGGER.info("Eldritch Artifacts: Client Setup");
        
        // Register client-side rendering for artifacts
    }
    
    @Override
    public void onInterModEnqueue() {
        // Send IMC messages to other mods
    }
    
    @Override
    public void onInterModProcess(InterModProcessEvent event) {
        // Process IMC messages from other mods
    }
}
