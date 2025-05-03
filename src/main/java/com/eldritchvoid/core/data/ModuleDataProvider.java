package com.eldritchvoid.core.data;

import com.eldritchvoid.EldritchVoid;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.LootTable;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.data.event.GatherDataEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * Base class for module data providers.
 * Provides a standard framework for data generation across modules.
 */
public abstract class ModuleDataProvider {
    protected final DataGenerator generator;
    protected final PackOutput output;
    protected final String modId;
    protected final String moduleName;
    protected final ExistingFileHelper existingFileHelper;
    
    private static final Map<String, ModuleDataProvider> PROVIDERS = new HashMap<>();
    
    /**
     * Create a new module data provider.
     *
     * @param generator The data generator
     * @param output The pack output
     * @param modId The mod ID
     * @param moduleName The module name
     * @param existingFileHelper The existing file helper
     */
    protected ModuleDataProvider(DataGenerator generator, PackOutput output, String modId, String moduleName, ExistingFileHelper existingFileHelper) {
        this.generator = generator;
        this.output = output;
        this.modId = modId;
        this.moduleName = moduleName;
        this.existingFileHelper = existingFileHelper;
        
        PROVIDERS.put(moduleName, this);
    }
    
    /**
     * Generate recipes for this module.
     *
     * @param consumer The recipe consumer
     */
    protected abstract void generateRecipes(Consumer<FinishedRecipe> consumer);
    
    /**
     * Generate loot tables for this module.
     *
     * @param consumer The loot table consumer
     */
    protected abstract void generateLootTables(BiConsumer<ResourceLocation, LootTable.Builder> consumer);
    
    /**
     * Generate block states for this module.
     *
     * @param helper The existing file helper
     */
    protected abstract void generateBlockStates(ExistingFileHelper helper);
    
    /**
     * Generate item models for this module.
     *
     * @param helper The existing file helper
     */
    protected abstract void generateItemModels(ExistingFileHelper helper);
    
    /**
     * Generate language files for this module.
     */
    protected abstract void generateLang();
    
    /**
     * Generate tags for this module.
     */
    protected abstract void generateTags();
    
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
        return new ResourceLocation(modId, "modules/" + moduleName + "/" + path);
    }
    
    /**
     * Register all data providers for the given event.
     *
     * @param event The gather data event
     */
    public static void registerAll(GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();
        PackOutput output = generator.getPackOutput();
        ExistingFileHelper existingFileHelper = event.getExistingFileHelper();
        
        for (ModuleDataProvider provider : PROVIDERS.values()) {
            // Register recipe provider
            if (event.includeServer()) {
                generator.addProvider(true, new RecipeProvider(output) {
                    @Override
                    protected void buildRecipes(Consumer<FinishedRecipe> consumer) {
                        provider.generateRecipes(consumer);
                    }
                });
                
                // Other server data providers would be registered here
            }
            
            if (event.includeClient()) {
                // Client data providers would be registered here
                provider.generateBlockStates(existingFileHelper);
                provider.generateItemModels(existingFileHelper);
                provider.generateLang();
            }
            
            EldritchVoid.LOGGER.info("Registered data providers for module: {}", provider.moduleName);
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