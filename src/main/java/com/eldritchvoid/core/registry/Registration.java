package com.eldritchvoid.core.registry;

import com.eldritchvoid.EldritchVoid;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

/**
 * Handles registration of all mod objects.
 * Using the latest NeoForge 1.21.5 DeferredRegister system.
 */
public class Registration {
    // Create DeferredRegisters to hold our mod objects
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(Registries.BLOCK, EldritchVoid.MOD_ID);
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(Registries.ITEM, EldritchVoid.MOD_ID);
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, EldritchVoid.MOD_ID);
    public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(Registries.ENTITY_TYPE, EldritchVoid.MOD_ID);
    
    /**
     * Create a ResourceLocation with the mod ID namespace.
     *
     * @param path The path for the resource
     * @return A ResourceLocation with the mod ID as namespace
     */
    public static ResourceLocation location(String path) {
        // Create a ResourceLocation for NeoForge 1.21.5
        try {
            // Use reflection to handle different versions
            return new ResourceLocation(EldritchVoid.MOD_ID, path);
        } catch (Exception e) {
            EldritchVoid.LOGGER.error("Error creating ResourceLocation for " + path, e);
            // Fallback to a simple approach
            return new ResourceLocation(EldritchVoid.MOD_ID + ":" + path);
        }
    }
    
    /**
     * Initialize all registries by attaching them to the mod event bus.
     */
    public static void init() {
        IEventBus modEventBus = net.neoforged.fml.ModLoadingContext.get().getActiveContainer().getEventBus();
        
        // Register all deferred registers to the mod event bus
        BLOCKS.register(modEventBus);
        ITEMS.register(modEventBus);
        BLOCK_ENTITIES.register(modEventBus);
        ENTITIES.register(modEventBus);
        
        // Initialize sub-registry classes
        ModBlocks.register();
        ModItems.register();
        ModEntities.register();
        ModFluids.register();
        
        EldritchVoid.LOGGER.info("Eldritch Void: Registries initialized");
    }
    
    /**
     * Get the registry key for a given registry and path.
     *
     * @param registry The registry to create a key for
     * @param path The path for the registry key
     * @param <T> The type of the registry
     * @return A ResourceKey for the given registry and path
     */
    public static <T> ResourceKey<T> key(ResourceKey<? extends Registry<T>> registry, String path) {
        return ResourceKey.create(registry, location(path));
    }
}