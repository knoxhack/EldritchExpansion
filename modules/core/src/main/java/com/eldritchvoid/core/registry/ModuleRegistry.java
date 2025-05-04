package com.eldritchvoid.core.registry;

import com.eldritchvoid.EldritchVoid;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * A registry system that allows modules to register objects while maintaining
 * a unified registration system. This helps organize registrations by module.
 *
 * @param <T> The type of object being registered
 */
public class ModuleRegistry<T> {
    private final String registryName;
    private final ResourceKey<? extends Registry<T>> registryKey;
    private final DeferredRegister<T> baseRegistry;
    private final Map<String, DeferredHolder<T, T>> entries = new HashMap<>();
    
    /**
     * Create a new module registry.
     *
     * @param registryName Human-readable name for this registry (for logging)
     * @param registryKey The registry key
     */
    public ModuleRegistry(String registryName, ResourceKey<? extends Registry<T>> registryKey) {
        this.registryName = registryName;
        this.registryKey = registryKey;
        this.baseRegistry = DeferredRegister.create(registryKey, EldritchVoid.MOD_ID);
        
        EldritchVoid.LOGGER.info("Created module registry for {}", registryName);
    }
    
    /**
     * Register an object from a specific module.
     *
     * @param moduleName The name of the module registering the object
     * @param objectName The name of the object
     * @param supplier The supplier for the object
     * @return The registered object holder
     */
    public DeferredHolder<T, T> register(String moduleName, String objectName, Supplier<T> supplier) {
        String fullName = moduleName + "_" + objectName;
        DeferredHolder<T, T> holder = baseRegistry.register(fullName, supplier);
        entries.put(fullName, holder);
        EldritchVoid.LOGGER.debug("Registered {} {} from module {}", registryName, objectName, moduleName);
        return holder;
    }
    
    /**
     * Get the underlying deferred register.
     *
     * @return The deferred register
     */
    public DeferredRegister<T> getBaseRegistry() {
        return baseRegistry;
    }
    
    /**
     * Get all entries for a specific module.
     *
     * @param moduleName The name of the module
     * @return A collection of entries for the module
     */
    public Collection<DeferredHolder<T, T>> getEntriesForModule(String moduleName) {
        return entries.entrySet().stream()
                .filter(e -> e.getKey().startsWith(moduleName + "_"))
                .map(Map.Entry::getValue)
                .collect(Collectors.toList());
    }
    
    /**
     * Get the registry key.
     *
     * @return The registry key
     */
    public ResourceKey<? extends Registry<T>> getRegistryKey() {
        return registryKey;
    }
    
    /**
     * Get all entries in this registry.
     *
     * @return A map of entry names to holders
     */
    public Map<String, DeferredHolder<T, T>> getAllEntries() {
        return entries;
    }
    
    /**
     * Register this registry with the given event bus.
     *
     * @param eventBus The event bus to register with
     */
    public void register(IEventBus eventBus) {
        baseRegistry.register(eventBus);
        EldritchVoid.LOGGER.info("Registered {} registry to event bus", registryName);
    }
}