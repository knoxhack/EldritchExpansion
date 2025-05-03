package com.eldritchvoid;

import com.eldritchvoid.core.ModuleManager;
import com.eldritchvoid.core.capability.ElderCapability;
import com.eldritchvoid.core.data.ModuleDataProvider;
import com.eldritchvoid.core.documentation.ModuleDocs;
import com.eldritchvoid.core.network.ModuleNetwork;
import com.eldritchvoid.core.registry.Registration;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.javafmlmod.FMLJavaModLoadingContext;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Main class for the Eldritch Void mod.
 * Initializes all systems and modules.
 */
@Mod(EldritchVoid.MOD_ID)
public class EldritchVoid {
    /**
     * The mod ID.
     */
    public static final String MOD_ID = "eldritchvoid";
    
    /**
     * The mod logger.
     */
    public static final Logger LOGGER = LoggerFactory.getLogger("EldritchVoid");
    
    private static ModuleManager moduleManager;
    
    /**
     * Create the mod instance.
     */
    public EldritchVoid() {
        LOGGER.info("Initializing Eldritch Void Mod");
        
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        
        // Initialize registration system
        Registration.init();
        
        // Initialize network system
        ModuleNetwork.init();
        
        // Initialize module manager
        moduleManager = new ModuleManager(modEventBus);
        
        // Register event handlers
        modEventBus.addListener(this::onRegisterCapabilities);
        modEventBus.addListener(this::onGatherData);
        
        // Initialize modules here
        initializeModules();
        
        // Load documentation
        ModuleDocs.loadDocs();
        
        LOGGER.info("Eldritch Void Mod initialized");
    }
    
    /**
     * Initialize all modules.
     */
    private void initializeModules() {
        // Register modules here
        moduleManager.registerModule(new com.eldritchvoid.modules.voidalchemy.VoidAlchemyModule(FMLJavaModLoadingContext.get().getModEventBus()));
        
        // Initialize all registered modules
        moduleManager.initializeModules();
    }
    
    /**
     * Handle the register capabilities event.
     *
     * @param event The register capabilities event
     */
    private void onRegisterCapabilities(RegisterCapabilitiesEvent event) {
        ElderCapability.registerCapabilities(event);
        LOGGER.info("Registered capabilities");
    }
    
    /**
     * Handle the gather data event.
     *
     * @param event The gather data event
     */
    private void onGatherData(GatherDataEvent event) {
        ModuleDataProvider.registerAll(event);
        LOGGER.info("Registered data providers");
    }
    
    /**
     * Get the module manager.
     *
     * @return The module manager
     */
    public static ModuleManager getModuleManager() {
        return moduleManager;
    }
}