package com.eldritchvoid.core.registry;

import com.eldritchvoid.EldritchVoid;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.neoforge.registries.DeferredHolder;

import java.util.function.Supplier;

/**
 * Registers all blocks for the Eldritch Void mod.
 */
public class ModBlocks {
    // Void Alchemy Module
    public static final DeferredHolder<Block, Block> VOID_CAULDRON = registerBlock("void_cauldron",
            () -> new Block(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_BLACK)
                    .strength(3.0F, 6.0F)
                    .sound(SoundType.METAL)
                    .requiresCorrectToolForDrops()));
    
    // Obsidian Forgemaster Module
    public static final DeferredHolder<Block, Block> OBSIDIAN_FORGE = registerBlock("obsidian_forge",
            () -> new Block(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_BLACK)
                    .strength(50.0F, 1200.0F)
                    .sound(SoundType.STONE)
                    .requiresCorrectToolForDrops()
                    .lightLevel(state -> 13)));
    
    // Eldritch Artifacts Module
    public static final DeferredHolder<Block, Block> ELDRITCH_PEDESTAL = registerBlock("eldritch_pedestal",
            () -> new Block(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_PURPLE)
                    .strength(3.0F, 6.0F)
                    .sound(SoundType.STONE)
                    .requiresCorrectToolForDrops()));
    
    // Void Corruption Module
    public static final DeferredHolder<Block, Block> VOID_CORRUPTION_BLOCK = registerBlock("void_corruption_block",
            () -> new Block(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_BLACK)
                    .strength(0.5F, 0.5F)
                    .sound(SoundType.SLIME_BLOCK)));
    
    // Eldritch Arcana Module
    public static final DeferredHolder<Block, Block> ARCANE_WORKBENCH = registerBlock("arcane_workbench",
            () -> new Block(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_PURPLE)
                    .strength(2.5F)
                    .sound(SoundType.WOOD)));
    
    // Obsidian Constructs Module
    public static final DeferredHolder<Block, Block> CONSTRUCT_CORE = registerBlock("construct_core",
            () -> new Block(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_BLACK)
                    .strength(50.0F, 1200.0F)
                    .sound(SoundType.STONE)
                    .requiresCorrectToolForDrops()));
    
    // Eldritch Dimensions Module
    public static final DeferredHolder<Block, Block> VOID_PORTAL = registerBlock("void_portal",
            () -> new Block(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_BLACK)
                    .strength(-1.0F, 3600000.0F)
                    .noOcclusion()
                    .sound(SoundType.GLASS)
                    .lightLevel(state -> 15)));
    
    // Void Tech Module
    public static final DeferredHolder<Block, Block> VOID_GENERATOR = registerBlock("void_generator",
            () -> new Block(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_BLACK)
                    .strength(3.0F, 6.0F)
                    .sound(SoundType.METAL)
                    .requiresCorrectToolForDrops()));

    /**
     * Register the mod blocks.
     */
    public static void register() {
        EldritchVoid.LOGGER.info("Registering Eldritch Void blocks");
    }
    
    /**
     * Helper method to register a block and its item form.
     * 
     * @param name The registry name
     * @param blockSupplier The block supplier
     * @return The registered block
     */
    private static <T extends Block> DeferredHolder<Block, T> registerBlock(String name, Supplier<T> blockSupplier) {
        DeferredHolder<Block, T> block = RegistryHandler.BLOCKS.register(name, blockSupplier);
        registerBlockItem(name, block);
        return block;
    }
    
    /**
     * Helper method to register a block item.
     * 
     * @param name The registry name
     * @param blockSupplier The block supplier
     */
    private static <T extends Block> void registerBlockItem(String name, Supplier<T> blockSupplier) {
        RegistryHandler.ITEMS.register(name, () -> new BlockItem(blockSupplier.get(), new Item.Properties()));
    }
}
