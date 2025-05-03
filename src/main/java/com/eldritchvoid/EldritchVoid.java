package com.eldritchvoid;

import com.eldritchvoid.core.ModuleLoader;
import com.eldritchvoid.core.ModuleManager;
import com.eldritchvoid.core.api.EldritchVoidAPI;
import com.eldritchvoid.core.config.ConfigHandler;
import com.eldritchvoid.core.datagen.DataGenerators;
import com.eldritchvoid.core.network.PacketHandler;
import com.eldritchvoid.core.registry.RegistryHandler;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.event.lifecycle.InterModEnqueueEvent;
import net.neoforged.fml.event.lifecycle.InterModProcessEvent;
import net.neoforged.fml.javafmlmod.FMLJavaModLoadingContext;
import net.neoforged.fml.loading.FMLPaths;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(EldritchVoid.MOD_ID)
public class EldritchVoid {
    public static final String MOD_ID = "eldritchvoid";
    public static final Logger LOGGER = LogManager.getLogger();
    
    public static EldritchVoid instance;
    private ModuleManager moduleManager;
    
    public EldritchVoid() {
        instance = this;
        
        // Initialize API
        EldritchVoidAPI.init();
        
        // Register configuration
        NeoForge.registerConfig(MOD_ID, ModConfig.Type.COMMON, ConfigHandler.COMMON_CONFIG);
        NeoForge.registerConfig(MOD_ID, ModConfig.Type.CLIENT, ConfigHandler.CLIENT_CONFIG);
        NeoForge.registerConfig(MOD_ID, ModConfig.Type.SERVER, ConfigHandler.SERVER_CONFIG);
        
        // Get the event bus
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        
        // Register lifecycle events
        modEventBus.addListener(this::setup);
        modEventBus.addListener(this::clientSetup);
        modEventBus.addListener(this::enqueueIMC);
        modEventBus.addListener(this::processIMC);
        modEventBus.addListener(this::gatherData);
        
        // Initialize registry handler
        RegistryHandler.init(modEventBus);
        
        // Initialize module management system
        moduleManager = new ModuleManager();
        ModuleLoader.loadModules(moduleManager, modEventBus);
        
        // Initialize config
        ConfigHandler.loadConfig(ConfigHandler.COMMON_CONFIG, 
                FMLPaths.CONFIGDIR.get().resolve(MOD_ID + "-common.toml").toString());
        
        // LOGGER message to confirm proper loading
        LOGGER.info("Eldritch Void mod initialized. Prepare for eldritchness!");
    }
    
    private void setup(final FMLCommonSetupEvent event) {
        LOGGER.info("Eldritch Void: Common Setup");
        
        // Initialize network handler
        PacketHandler.init();
        
        // Initialize modules for common setup
        event.enqueueWork(() -> {
            moduleManager.onCommonSetup();
        });
    }
    
    private void clientSetup(final FMLClientSetupEvent event) {
        LOGGER.info("Eldritch Void: Client Setup");
        
        // Initialize modules for client setup
        event.enqueueWork(() -> {
            moduleManager.onClientSetup();
        });
    }
    
    private void enqueueIMC(final InterModEnqueueEvent event) {
        // Send InterModComms to other mods
        moduleManager.onInterModEnqueue();
    }
    
    private void processIMC(final InterModProcessEvent event) {
        // Process InterModComms from other mods
        moduleManager.onInterModProcess(event);
    }
    
    private void gatherData(GatherDataEvent event) {
        DataGenerators.gatherData(event);
    }
    
    /**
     * Get the module manager instance
     */
    public ModuleManager getModuleManager() {
        return moduleManager;
    }
}
