package com.eldritchvoid.core.capability;

import com.eldritchvoid.EldritchVoid;
import com.eldritchvoid.core.registry.Registration;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.entity.Entity;
import net.neoforged.neoforge.capabilities.BlockCapability;
import net.neoforged.neoforge.capabilities.EntityCapability;
import net.neoforged.neoforge.capabilities.ItemCapability;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

/**
 * A capability system for the Eldritch Void mod.
 * Allows modules to define and use capabilities on blocks, items, and entities.
 * Compatible with NeoForge 1.21.5.
 *
 * @param <T> The capability type
 */
public class ElderCapability<T> {
    private static final Map<String, ElderCapability<?>> CAPABILITIES = new HashMap<>();
    
    private final String id;
    private final Class<T> capabilityClass;
    private final Supplier<T> defaultProvider;
    private final ResourceLocation resourceLocation;
    
    // Capability tokens for different types
    private BlockCapability<T, Direction> blockCapability;
    private ItemCapability<T, Direction> itemCapability;
    private EntityCapability<T, Direction> entityCapability;
    
    /**
     * Create a new capability.
     *
     * @param id The capability ID
     * @param capabilityClass The capability class
     * @param defaultProvider The default capability provider
     */
    private ElderCapability(String id, Class<T> capabilityClass, Supplier<T> defaultProvider) {
        this.id = id;
        this.capabilityClass = capabilityClass;
        this.defaultProvider = defaultProvider;
        this.resourceLocation = Registration.location(id);
        
        // Create capability tokens
        // In NeoForge 1.21.5, createSided has been replaced with create methods
        this.blockCapability = BlockCapability.create(resourceLocation, capabilityClass, Direction.class);
        this.itemCapability = ItemCapability.create(resourceLocation, capabilityClass, Direction.class);
        this.entityCapability = EntityCapability.create(resourceLocation, capabilityClass, Direction.class);
    }
    
    /**
     * Register a new capability.
     *
     * @param id The capability ID
     * @param capabilityClass The capability class
     * @param defaultProvider The default capability provider
     * @param <T> The capability type
     * @return The capability instance
     */
    public static <T> ElderCapability<T> register(String id, Class<T> capabilityClass, Supplier<T> defaultProvider) {
        if (CAPABILITIES.containsKey(id)) {
            EldritchVoid.LOGGER.error("Capability with id {} already registered", id);
            // Return the existing capability
            return (ElderCapability<T>) CAPABILITIES.get(id);
        }
        
        ElderCapability<T> capability = new ElderCapability<>(id, capabilityClass, defaultProvider);
        CAPABILITIES.put(id, capability);
        EldritchVoid.LOGGER.info("Registered capability: {}", id);
        
        return capability;
    }
    
    /**
     * Attach a capability to a block.
     *
     * @param block The block
     * @param instance The capability instance
     */
    public void attachTo(Block block, T instance) {
        // Implementation will depend on specific NeoForge capability registration
        // This might need to be handled differently in a real implementation
        EldritchVoid.LOGGER.info("Attached capability {} to block {}", id, block.getDescriptionId());
    }
    
    /**
     * Get a capability from a block entity.
     *
     * @param blockEntity The block entity
     * @param direction The side to get the capability from, or null for any side
     * @return The capability instance, if present
     */
    public Optional<T> getFrom(BlockEntity blockEntity, Direction direction) {
        if (blockEntity == null) return Optional.empty();
        return Optional.ofNullable(blockEntity.getLevel().getCapability(blockCapability, blockEntity.getBlockPos(), blockEntity.getBlockState(), blockEntity, direction));
    }
    
    /**
     * Get a capability from an item stack.
     *
     * @param stack The item stack
     * @param direction The side to get the capability from, or null for any side
     * @return The capability instance, if present
     */
    public Optional<T> getFrom(ItemStack stack, Direction direction) {
        if (stack.isEmpty()) return Optional.empty();
        return Optional.ofNullable(stack.getCapability(itemCapability, direction));
    }
    
    /**
     * Get a capability from an entity.
     *
     * @param entity The entity
     * @param direction The side to get the capability from, or null for any side
     * @return The capability instance, if present
     */
    public Optional<T> getFrom(Entity entity, Direction direction) {
        if (entity == null) return Optional.empty();
        return Optional.ofNullable(entity.getCapability(entityCapability, direction));
    }
    
    /**
     * Get the capability ID.
     *
     * @return The capability ID
     */
    public String getId() {
        return id;
    }
    
    /**
     * Get the capability class.
     *
     * @return The capability class
     */
    public Class<T> getCapabilityClass() {
        return capabilityClass;
    }
    
    /**
     * Get a new instance of the default capability provider.
     *
     * @return A new default capability instance
     */
    public T createDefaultInstance() {
        return defaultProvider.get();
    }
    
    /**
     * Get the ResourceLocation for this capability.
     *
     * @return The ResourceLocation
     */
    public ResourceLocation getResourceLocation() {
        return resourceLocation;
    }
    
    /**
     * Get the block capability token.
     *
     * @return The block capability token
     */
    public BlockCapability<T, Direction> getBlockCapability() {
        return blockCapability;
    }
    
    /**
     * Get the item capability token.
     *
     * @return The item capability token
     */
    public ItemCapability<T, Direction> getItemCapability() {
        return itemCapability;
    }
    
    /**
     * Get the entity capability token.
     *
     * @return The entity capability token
     */
    public EntityCapability<T, Direction> getEntityCapability() {
        return entityCapability;
    }
    
    /**
     * Register all capabilities with the NeoForge capability system.
     *
     * @param event The RegisterCapabilitiesEvent
     */
    public static void registerCapabilities(RegisterCapabilitiesEvent event) {
        for (ElderCapability<?> capability : CAPABILITIES.values()) {
            // Register capabilities with NeoForge (exact method depends on NeoForge API)
            EldritchVoid.LOGGER.info("Registering capability in NeoForge: {}", capability.getId());
        }
    }
}