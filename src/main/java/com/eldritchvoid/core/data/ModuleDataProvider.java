package com.eldritchvoid.core.data;

import com.eldritchvoid.EldritchVoid;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.data.event.GatherDataEvent;

import java.util.HashMap;
import java.util.Map;

/**
 * Base class for module data providers.
 * Provides a standard framework for data generation across modules.
 * 
 * Note: This is a simplified version for NeoForge 1.21.5 compatibility.
 * The data generation system will be expanded as needed.
 */
public abstract class ModuleDataProvider {
    protected final DataGenerator generator;
    protected final PackOutput output;
    protected final String modId;
    protected final String moduleName;
    
    private static final Map<String, ModuleDataProvider> PROVIDERS = new HashMap<>();
    
    /**
     * Create a new module data provider.
     *
     * @param generator The data generator
     * @param output The pack output
     * @param modId The mod ID
     * @param moduleName The module name
     */
    protected ModuleDataProvider(DataGenerator generator, PackOutput output, String modId, String moduleName) {
        this.generator = generator;
        this.output = output;
        this.modId = modId;
        this.moduleName = moduleName;
        
        PROVIDERS.put(moduleName, this);
    }
    
    /**
     * Generate data for this module.
     */
    protected abstract void generate();
    
    /**
     * Create a path for a resource.
     *
     * @param type The resource type
     * @param name The resource name
     * @return The resource path
     */
    protected String resourcePath(String type, String name) {
        return "modules/" + moduleName + "/" + type + "/" + name;
    }
    
    /**
     * Create a resource location for this module.
     *
     * @param path The resource path
     * @return The resource location
     */
    protected ResourceLocation modLoc(String path) {
        return ResourceLocation.parse(modId + ":modules/" + moduleName + "/" + path);
    }
    
    /**
     * Register all data providers for the given event.
     *
     * @param event The gather data event
     */
    public static void registerAll(GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();
        PackOutput output = generator.getPackOutput();
        
        for (ModuleDataProvider provider : PROVIDERS.values()) {
            if (event.includeServer() || event.includeClient()) {
                provider.generate();
            }
            
            EldritchVoid.LOGGER.info("Registered data provider for module: {}", provider.moduleName);
        }
    }
    
    /**
     * Register a module data provider.
     *
     * @param generator The data generator
     * @param output The pack output
     * @param moduleName The module name
     * @param provider The data provider
     */
    public static void register(DataGenerator generator, PackOutput output, String moduleName, ModuleDataProvider provider) {
        PROVIDERS.put(moduleName, provider);
        EldritchVoid.LOGGER.info("Registered module data provider: {}", moduleName);
    }
}