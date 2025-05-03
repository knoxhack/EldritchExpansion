package com.eldritchvoid.core;

import com.eldritchvoid.EldritchVoid;
import com.eldritchvoid.modules.eldritcharcana.EldritchArcanaModule;
import com.eldritchvoid.modules.eldritchartifacts.EldritchArtifactsModule;
import com.eldritchvoid.modules.eldritchbestiary.EldritchBestiaryModule;
import com.eldritchvoid.modules.eldritchdimensions.EldritchDimensionsModule;
import com.eldritchvoid.modules.obsidianconstructs.ObsidianConstructsModule;
import com.eldritchvoid.modules.obsidianforgemaster.ObsidianForgemasterModule;
import com.eldritchvoid.modules.voidalchemy.VoidAlchemyModule;
import com.eldritchvoid.modules.voidcorruption.VoidCorruptionModule;
import com.eldritchvoid.modules.voidcultists.VoidCultistsModule;
import com.eldritchvoid.modules.voidtech.VoidTechModule;
import net.neoforged.bus.api.IEventBus;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Responsible for loading all modules in the Eldritch Void mod.
 */
public class ModuleLoader {
    private static final Logger LOGGER = LogManager.getLogger();
    
    /**
     * Loads all modules into the module manager.
     * 
     * @param moduleManager The module manager to load modules into
     * @param modEventBus The mod event bus to register events to
     */
    public static void loadModules(ModuleManager moduleManager, IEventBus modEventBus) {
        LOGGER.info("Loading Eldritch Void modules...");
        
        // Register all modules
        registerModule(moduleManager, new VoidAlchemyModule(modEventBus));
        registerModule(moduleManager, new ObsidianForgemasterModule(modEventBus));
        registerModule(moduleManager, new EldritchArtifactsModule(modEventBus));
        registerModule(moduleManager, new VoidCorruptionModule(modEventBus));
        registerModule(moduleManager, new EldritchArcanaModule(modEventBus));
        registerModule(moduleManager, new ObsidianConstructsModule(modEventBus));
        registerModule(moduleManager, new EldritchDimensionsModule(modEventBus));
        registerModule(moduleManager, new VoidTechModule(modEventBus));
        registerModule(moduleManager, new EldritchBestiaryModule(modEventBus));
        registerModule(moduleManager, new VoidCultistsModule(modEventBus));
        
        // Initialize all modules after registration
        moduleManager.initializeModules();
        
        LOGGER.info("Finished loading Eldritch Void modules");
    }
    
    /**
     * Registers a single module with the module manager.
     * 
     * @param moduleManager The module manager
     * @param module The module to register
     */
    private static void registerModule(ModuleManager moduleManager, IEldritchModule module) {
        try {
            moduleManager.registerModule(module);
        } catch (Exception e) {
            LOGGER.error("Failed to register module {}: {}", module.getModuleId(), e.getMessage());
            e.printStackTrace();
        }
    }
}
