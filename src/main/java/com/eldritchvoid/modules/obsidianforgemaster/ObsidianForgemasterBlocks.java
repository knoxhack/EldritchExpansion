package com.eldritchvoid.modules.obsidianforgemaster;

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
 * Registers all blocks for the Obsidian Forgemaster module.
 */
public class ObsidianForgemasterBlocks {
    // Obsidian Forgemaster blocks - these will be expanded with proper functionality
    
    // Obsidian Forge - a specialized furnace for crafting obsidian tools and weapons
    public static final DeferredHolder<Block, Block> OBSIDIAN_FORGE = Registration.BLOCKS.register("obsidian_forge",
            () -> new Block(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_BLACK)
                    .instrument(NoteBlockInstrument.BASEDRUM)
                    .requiresCorrectToolForDrops()
                    .strength(50.0F, 1200.0F)
                    .sound(SoundType.STONE)));
                    
    // Void-Infused Obsidian Block - decorative and functional block
    public static final DeferredHolder<Block, Block> VOID_OBSIDIAN_BLOCK = Registration.BLOCKS.register("void_obsidian_block",
            () -> new Block(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_PURPLE)
                    .instrument(NoteBlockInstrument.BASEDRUM)
                    .requiresCorrectToolForDrops()
                    .strength(65.0F, 1500.0F)
                    .sound(SoundType.STONE)
                    .lightLevel(state -> 5)));  // This block emits a faint purple light
    
    // Block Items
    public static final DeferredHolder<Item, Item> OBSIDIAN_FORGE_ITEM = Registration.ITEMS.register("obsidian_forge",
            () -> new BlockItem(OBSIDIAN_FORGE.get(), new Item.Properties()));
            
    public static final DeferredHolder<Item, Item> VOID_OBSIDIAN_BLOCK_ITEM = Registration.ITEMS.register("void_obsidian_block",
            () -> new BlockItem(VOID_OBSIDIAN_BLOCK.get(), new Item.Properties()));
    
    /**
     * Register all blocks.
     */
    public static void register() {
        EldritchVoid.LOGGER.info("Registering Obsidian Forgemaster blocks");
    }
}