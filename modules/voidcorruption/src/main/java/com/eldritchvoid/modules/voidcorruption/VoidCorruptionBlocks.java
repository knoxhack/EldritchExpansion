package com.eldritchvoid.modules.voidcorruption;

import com.eldritchvoid.EldritchVoid;
import com.eldritchvoid.core.registry.Registration;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.neoforge.registries.DeferredHolder;

/**
 * Registers all blocks for the Void Corruption module.
 */
public class VoidCorruptionBlocks {
    // Void Corruption blocks - these will be expanded with proper functionality
    
    // Corrupted Stone - a basic corrupted block that slowly spreads to adjacent stone
    public static final DeferredHolder<Block, Block> CORRUPTED_STONE = Registration.BLOCKS.register("corrupted_stone",
            () -> new Block(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_PURPLE)
                    .instrument(NoteBlockInstrument.BASEDRUM)
                    .requiresCorrectToolForDrops()
                    .strength(3.0F, 6.0F)
                    .sound(SoundType.STONE)));
                    
    // Void Growth - a plant-like corrupted block that can be harvested for void essence
    public static final DeferredHolder<Block, Block> VOID_GROWTH = Registration.BLOCKS.register("void_growth",
            () -> new Block(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_BLACK)
                    .instrument(NoteBlockInstrument.BASEDRUM)
                    .strength(0.5F)
                    .sound(SoundType.GRASS)
                    .lightLevel(state -> 3))); // Emits a faint light
    
    // Block Items
    public static final DeferredHolder<Item, Item> CORRUPTED_STONE_ITEM = Registration.ITEMS.register("corrupted_stone",
            () -> new BlockItem(CORRUPTED_STONE.get(), new Item.Properties()));
            
    public static final DeferredHolder<Item, Item> VOID_GROWTH_ITEM = Registration.ITEMS.register("void_growth",
            () -> new BlockItem(VOID_GROWTH.get(), new Item.Properties()));
    
    /**
     * Register all blocks.
     */
    public static void register() {
        EldritchVoid.LOGGER.info("Registering Void Corruption blocks");
    }
}