package com.eldritchvoid.modules.obsidianconstructs;

import com.eldritchvoid.EldritchVoid;
import com.eldritchvoid.core.registry.Registration;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

/**
 * Registers all entities for the Obsidian Constructs module.
 */
public class ObsidianConstructsEntities {
    // Register a DeferredRegister for spawn eggs
    public static final DeferredRegister<Item> SPAWN_EGGS = DeferredRegister.create(
            net.minecraft.core.registries.Registries.ITEM, EldritchVoid.MOD_ID);
    
    // Obsidian Golem - a strong but slow guardian construct
    public static final DeferredHolder<EntityType<?>, EntityType<Entity>> OBSIDIAN_GOLEM = Registration.ENTITIES.register(
            "obsidian_golem", 
            () -> EntityType.Builder.of((type, level) -> null, MobCategory.MISC)
                    .sized(1.4F, 2.7F)
                    .clientTrackingRange(10)
                    .build("obsidian_golem"));
    
    /**
     * Register all entities.
     */
    public static void register() {
        EldritchVoid.LOGGER.info("Registering Obsidian Constructs entities");
        
        // Get the mod event bus
        IEventBus modEventBus = net.neoforged.fml.ModLoadingContext.get().getActiveContainer().getEventBus();
        
        // Register event listeners for entity attributes
        modEventBus.addListener(ObsidianConstructsEntities::registerEntityAttributes);
        
        // Register spawn eggs
        SPAWN_EGGS.register(modEventBus);
    }
    
    /**
     * Register entity attributes.
     *
     * @param event The entity attribute creation event
     */
    private static void registerEntityAttributes(EntityAttributeCreationEvent event) {
        // We'll create attribute providers for our entities when they're fully implemented
        // For now, this is just a placeholder
        event.put((EntityType<? extends Monster>) OBSIDIAN_GOLEM.get(), 
                Monster.createMonsterAttributes()
                        .add(Attributes.MAX_HEALTH, 100.0D)
                        .add(Attributes.MOVEMENT_SPEED, 0.2D)
                        .add(Attributes.ATTACK_DAMAGE, 15.0D)
                        .add(Attributes.ARMOR, 10.0D)
                        .add(Attributes.KNOCKBACK_RESISTANCE, 1.0D)
                        .build());
    }
    
    /**
     * Register spawn placements.
     * Note: In NeoForge 1.21.5, this may be handled differently or through another event
     */
    private static void registerSpawnPlacements() {
        // We'll implement this when necessary using the appropriate method for NeoForge 1.21.5
    }
}