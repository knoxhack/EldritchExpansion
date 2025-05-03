package com.eldritchvoid.core.registry;

import com.eldritchvoid.EldritchVoid;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

/**
 * Handles all registry operations for the Eldritch Void mod.
 */
public class RegistryHandler {
    // DeferredRegisters for all content types
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(Registries.BLOCK, EldritchVoid.MOD_ID);
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(Registries.ITEM, EldritchVoid.MOD_ID);
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, EldritchVoid.MOD_ID);
    public static final DeferredRegister<MenuType<?>> CONTAINERS = DeferredRegister.create(Registries.MENU, EldritchVoid.MOD_ID);
    public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(Registries.ENTITY_TYPE, EldritchVoid.MOD_ID);
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, EldritchVoid.MOD_ID);
    
    /**
     * Initialize all registries.
     * 
     * @param eventBus The mod event bus
     */
    public static void init(IEventBus eventBus) {
        EldritchVoid.LOGGER.info("Initializing registries");
        
        // Register all deferred registers to the event bus
        BLOCKS.register(eventBus);
        ITEMS.register(eventBus);
        BLOCK_ENTITIES.register(eventBus);
        CONTAINERS.register(eventBus);
        ENTITIES.register(eventBus);
        CREATIVE_MODE_TABS.register(eventBus);
        
        // Initialize registry classes
        ModBlocks.register();
        ModItems.register();
        ModFluids.register();
        ModEntities.register();
        
        // Register creative mode tab
        CREATIVE_MODE_TABS.register("eldritchvoid", () -> CreativeModeTab.builder()
            .title(net.minecraft.network.chat.Component.translatable("itemGroup.eldritchvoid"))
            .icon(() -> new ItemStack(ModItems.ARCANE_TOME.get()))
            .displayItems((parameters, output) -> {
                // Add all mod items to the creative tab
                ITEMS.getEntries().forEach(item -> output.accept(item.get()));
            })
            .build());
        
        EldritchVoid.LOGGER.info("Registry initialization complete");
    }
    
    /**
     * Creates a ResourceLocation with the Eldritch Void mod ID.
     * 
     * @param path The path for the resource
     * @return A ResourceLocation with the Eldritch Void mod ID
     */
    public static ResourceLocation modLoc(String path) {
        return new ResourceLocation(EldritchVoid.MOD_ID, path);
    }
}
