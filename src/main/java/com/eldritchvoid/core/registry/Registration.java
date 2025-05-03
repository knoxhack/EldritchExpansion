package com.eldritchvoid.core.registry;

import com.eldritchvoid.EldritchVoid;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.HashMap;
import java.util.Map;

/**
 * Central registration utility for the mod.
 * Provides access to all registries and helper methods.
 */
public class Registration {
    // Base registry for blocks - updated for NeoForge 1.21.5
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(net.minecraft.core.registries.BuiltInRegistries.BLOCK, EldritchVoid.MOD_ID);
    
    // Base registry for items - updated for NeoForge 1.21.5
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(net.minecraft.core.registries.BuiltInRegistries.ITEM, EldritchVoid.MOD_ID);
    
    // Base registry for entity types - updated for NeoForge 1.21.5
    public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(net.minecraft.core.registries.BuiltInRegistries.ENTITY_TYPE, EldritchVoid.MOD_ID);
    
    // Map of module registries
    private static final Map<String, Map<ResourceKey<? extends Registry<?>>, ModuleRegistry<?>>> MODULE_REGISTRIES = new HashMap<>();
    
    /**
     * Initialize the registration system.
     */
    public static void init() {
        EldritchVoid.LOGGER.info("Initializing registration system");
    }
    
    /**
     * Create a resource location for this mod.
     *
     * @param path The resource path
     * @return The resource location
     */
    public static ResourceLocation location(String path) {
        // NeoForge 1.21.5 requires namespace and path as separate parameters
        return new ResourceLocation(EldritchVoid.MOD_ID, path);
    }
    
    /**
     * Get or create a module registry.
     *
     * @param moduleName The name of the module
     * @param registryName The human-readable name for this registry
     * @param registryKey The registry key
     * @param <T> The registry type
     * @return The module registry
     */
    @SuppressWarnings("unchecked")
    public static <T> ModuleRegistry<T> getOrCreateModuleRegistry(String moduleName, String registryName, Object registryObj) {
        // In NeoForge 1.21.5, registry types changed, so we need to support both ResourceKey and DefaultedRegistry
        ResourceKey<? extends Registry<T>> registryKey;
        
        if (registryObj instanceof ResourceKey) {
            registryKey = (ResourceKey<? extends Registry<T>>)registryObj;
        } else {
            // If it's a DefaultedRegistry (BuiltInRegistries), get its key
            registryKey = ((Registry<T>)registryObj).key();
        }
        
        Map<ResourceKey<? extends Registry<?>>, ModuleRegistry<?>> moduleRegistryMap = MODULE_REGISTRIES.computeIfAbsent(moduleName, k -> new HashMap<>());
        
        ModuleRegistry<?> registry = moduleRegistryMap.get(registryKey);
        if (registry == null) {
            registry = new ModuleRegistry<>(registryName, registryKey);
            moduleRegistryMap.put(registryKey, registry);
        }
        
        return (ModuleRegistry<T>) registry;
    }
    
    /**
     * Get a module registry.
     *
     * @param moduleName The name of the module
     * @param registryKey The registry key
     * @param <T> The registry type
     * @return The module registry, or null if not found
     */
    @SuppressWarnings("unchecked")
    public static <T> ModuleRegistry<T> getModuleRegistry(String moduleName, ResourceKey<? extends Registry<T>> registryKey) {
        Map<ResourceKey<? extends Registry<?>>, ModuleRegistry<?>> moduleRegistryMap = MODULE_REGISTRIES.get(moduleName);
        if (moduleRegistryMap == null) {
            return null;
        }
        
        return (ModuleRegistry<T>) moduleRegistryMap.get(registryKey);
    }
    
    /**
     * Get block registry for a module.
     *
     * @param moduleName The name of the module
     * @return The block registry
     */
    public static ModuleRegistry<Block> getBlockRegistry(String moduleName) {
        // In NeoForge 1.21.5, registry references were moved to BuiltInRegistries
        return getOrCreateModuleRegistry(moduleName, "Blocks", net.minecraft.core.registries.BuiltInRegistries.BLOCK);
    }
    
    /**
     * Get item registry for a module.
     *
     * @param moduleName The name of the module
     * @return The item registry
     */
    public static ModuleRegistry<Item> getItemRegistry(String moduleName) {
        // In NeoForge 1.21.5, registry references were moved to BuiltInRegistries
        return getOrCreateModuleRegistry(moduleName, "Items", net.minecraft.core.registries.BuiltInRegistries.ITEM);
    }
    
    /**
     * Get entity registry for a module.
     *
     * @param moduleName The name of the module
     * @return The entity registry
     */
    public static ModuleRegistry<EntityType<?>> getEntityRegistry(String moduleName) {
        // In NeoForge 1.21.5, registry references were moved to BuiltInRegistries
        return getOrCreateModuleRegistry(moduleName, "Entities", net.minecraft.core.registries.BuiltInRegistries.ENTITY_TYPE);
    }
}