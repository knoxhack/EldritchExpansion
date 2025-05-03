package com.eldritchvoid.modules.voidalchemy;

import com.eldritchvoid.EldritchVoid;
import com.eldritchvoid.core.IEldritchModule;
import com.eldritchvoid.core.api.VoidAlchemyAPI;
import com.eldritchvoid.modules.voidalchemy.fluid.VoidEssenceFluid;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.event.lifecycle.InterModProcessEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Collections;
import java.util.List;

/**
 * The Void Alchemy module for Eldritch Void.
 * Handles fluid systems and "Void Pee Essence" mechanics.
 */
public class VoidAlchemyModule implements IEldritchModule {
    private static final Logger LOGGER = LogManager.getLogger();
    private final IEventBus modEventBus;
    
    public VoidAlchemyModule(IEventBus modEventBus) {
        this.modEventBus = modEventBus;
    }
    
    @Override
    public String getModuleId() {
        return "voidalchemy";
    }
    
    @Override
    public String getDisplayName() {
        return "Void Alchemy";
    }
    
    @Override
    public String getDescription() {
        return "Manipulate the essence of the void to create powerful compounds";
    }
    
    @Override
    public List<String> getDependencies() {
        return Collections.emptyList(); // No dependencies
    }
    
    @Override
    public void initialize() {
        LOGGER.info("Initializing Void Alchemy Module");
        
        // Register event handlers
        modEventBus.addListener(this::onCommonSetup);
        modEventBus.addListener(this::onClientSetup);
        
        // Initialize registry entries and fluids
        VoidEssenceFluid.register(modEventBus);
        
        // Register default essence sources
        VoidAlchemyAPI api = VoidAlchemyAPI.getInstance();
        api.registerEssenceSource("minecraft:ender_pearl", 20);
        api.registerEssenceSource("minecraft:ender_eye", 40);
        api.registerEssenceSource("minecraft:dragon_breath", 100);
        api.registerEssenceSource("minecraft:chorus_fruit", 10);
        api.registerEssenceSource("minecraft:chorus_flower", 15);
        api.registerEssenceSource("minecraft:obsidian", 5);
    }
    
    @Override
    public void onCommonSetup() {
        LOGGER.info("Void Alchemy: Common Setup");
        
        // Register fluid interactions
        // This would be done in the common setup
    }
    
    @Override
    public void onClientSetup() {
        LOGGER.info("Void Alchemy: Client Setup");
        
        // Register fluid rendering
        // Register special client-side handlers for fluid effects
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
