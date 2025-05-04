package com.eldritchvoid.modules.obsidianconstructs;

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
 * Registers all blocks for the Obsidian Constructs module.
 */
public class ObsidianConstructsBlocks {
    // Obsidian Constructs blocks - these will be expanded with proper functionality
    
    // Construct Core - the central block for building constructs
    public static final DeferredHolder<Block, Block> CONSTRUCT_CORE = Registration.BLOCKS.register("construct_core",
            () -> new Block(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_BLACK)
                    .instrument(NoteBlockInstrument.BASEDRUM)
                    .requiresCorrectToolForDrops()
                    .strength(40.0F, 1200.0F)
                    .sound(SoundType.STONE)
                    .lightLevel(state -> 4))); // Emits a faint light
                    
    // Void-Infused Obsidian - special block used in construct creation
    public static final DeferredHolder<Block, Block> VOID_INFUSED_OBSIDIAN = Registration.BLOCKS.register("void_infused_obsidian",
            () -> new Block(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_PURPLE)
                    .instrument(NoteBlockInstrument.BASEDRUM)
                    .requiresCorrectToolForDrops()
                    .strength(50.0F, 1500.0F)
                    .sound(SoundType.STONE)
                    .lightLevel(state -> 6))); // Emits a soft purple glow
    
    // Block Items
    public static final DeferredHolder<Item, Item> CONSTRUCT_CORE_ITEM = Registration.ITEMS.register("construct_core",
            () -> new BlockItem(CONSTRUCT_CORE.get(), new Item.Properties()));
            
    public static final DeferredHolder<Item, Item> VOID_INFUSED_OBSIDIAN_ITEM = Registration.ITEMS.register("void_infused_obsidian",
            () -> new BlockItem(VOID_INFUSED_OBSIDIAN.get(), new Item.Properties()));
    
    /**
     * Register all blocks.
     */
    public static void register() {
        EldritchVoid.LOGGER.info("Registering Obsidian Constructs blocks");
    }
}