package com.eldritchvoid.core.event;

import com.eldritchvoid.EldritchVoid;
import com.eldritchvoid.core.Module;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.NeoForge;

import java.util.function.Consumer;

/**
 * Event bus system for module communication.
 * Allows modules to communicate with each other without direct dependencies.
 */
public class ModuleEventBus {
    private static final IEventBus MODULE_BUS = NeoForge.EVENT_BUS;
    
    /**
     * Post an event to all modules.
     *
     * @param event The event to post
     * @param <T> The event type
     * @return The posted event
     */
    public static <T extends Event> T post(T event) {
        MODULE_BUS.post(event);
        return event;
    }
    
    /**
     * Register a module to listen for events.
     *
     * @param module The module to register
     */
    public static void register(Object module) {
        MODULE_BUS.register(module);
        EldritchVoid.LOGGER.info("Registered module event listener: {}", module.getClass().getSimpleName());
    }
    
    /**
     * Add a listener for a specific event type.
     *
     * @param eventClass The event class to listen for
     * @param consumer The event consumer
     * @param <T> The event type
     */
    public static <T extends Event> void addListener(Class<T> eventClass, Consumer<T> consumer) {
        MODULE_BUS.addListener(EventPriority.NORMAL, false, eventClass, consumer);
    }
    
    /**
     * Add a listener with specific priority for a specific event type.
     *
     * @param priority The event priority
     * @param eventClass The event class to listen for
     * @param consumer The event consumer
     * @param <T> The event type
     */
    public static <T extends Event> void addListener(EventPriority priority, Class<T> eventClass, Consumer<T> consumer) {
        MODULE_BUS.addListener(priority, false, eventClass, consumer);
    }
    
    /**
     * Event fired when a module is loaded.
     */
    public static class ModuleLoadedEvent extends Event {
        private final Module module;
        
        /**
         * Create a new module loaded event.
         *
         * @param module The module that was loaded
         */
        public ModuleLoadedEvent(Module module) {
            this.module = module;
        }
        
        /**
         * Get the module that was loaded.
         *
         * @return The module
         */
        public Module getModule() {
            return module;
        }
    }
    
    /**
     * Event fired when a module's entity is spawned.
     */
    public static class EntitySpawnedEvent extends Event {
        private final Entity entity;
        private final String moduleId;
        
        /**
         * Create a new entity spawned event.
         *
         * @param entity The entity that was spawned
         * @param moduleId The ID of the module that spawned the entity
         */
        public EntitySpawnedEvent(Entity entity, String moduleId) {
            this.entity = entity;
            this.moduleId = moduleId;
        }
        
        /**
         * Get the entity that was spawned.
         *
         * @return The entity
         */
        public Entity getEntity() {
            return entity;
        }
        
        /**
         * Get the ID of the module that spawned the entity.
         *
         * @return The module ID
         */
        public String getModuleId() {
            return moduleId;
        }
    }
    
    /**
     * Event fired when a module's item is used.
     */
    public static class ItemUsedEvent extends Event {
        private final ItemStack itemStack;
        private final String moduleId;
        private final Entity user;
        
        /**
         * Create a new item used event.
         *
         * @param itemStack The item stack that was used
         * @param moduleId The ID of the module that owns the item
         * @param user The entity that used the item
         */
        public ItemUsedEvent(ItemStack itemStack, String moduleId, Entity user) {
            this.itemStack = itemStack;
            this.moduleId = moduleId;
            this.user = user;
        }
        
        /**
         * Get the item stack that was used.
         *
         * @return The item stack
         */
        public ItemStack getItemStack() {
            return itemStack;
        }
        
        /**
         * Get the ID of the module that owns the item.
         *
         * @return The module ID
         */
        public String getModuleId() {
            return moduleId;
        }
        
        /**
         * Get the entity that used the item.
         *
         * @return The user entity
         */
        public Entity getUser() {
            return user;
        }
    }
    
    /**
     * Event fired when a module's block is activated.
     */
    public static class BlockActivatedEvent extends Event {
        private final BlockState blockState;
        private final BlockPos pos;
        private final Level level;
        private final String moduleId;
        private final Entity activator;
        
        /**
         * Create a new block activated event.
         *
         * @param blockState The block state that was activated
         * @param pos The position of the block
         * @param level The level the block is in
         * @param moduleId The ID of the module that owns the block
         * @param activator The entity that activated the block
         */
        public BlockActivatedEvent(BlockState blockState, BlockPos pos, Level level, String moduleId, Entity activator) {
            this.blockState = blockState;
            this.pos = pos;
            this.level = level;
            this.moduleId = moduleId;
            this.activator = activator;
        }
        
        /**
         * Get the block state that was activated.
         *
         * @return The block state
         */
        public BlockState getBlockState() {
            return blockState;
        }
        
        /**
         * Get the position of the block.
         *
         * @return The block position
         */
        public BlockPos getPos() {
            return pos;
        }
        
        /**
         * Get the level the block is in.
         *
         * @return The level
         */
        public Level getLevel() {
            return level;
        }
        
        /**
         * Get the ID of the module that owns the block.
         *
         * @return The module ID
         */
        public String getModuleId() {
            return moduleId;
        }
        
        /**
         * Get the entity that activated the block.
         *
         * @return The activator entity
         */
        public Entity getActivator() {
            return activator;
        }
    }
}