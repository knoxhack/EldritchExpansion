package com.eldritchvoid.init;

import com.eldritchvoid.EldritchVoid;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

/**
 * Registry for all mod blocks.
 * Contains placeholder blocks for each module.
 */
public class BlockInit {
    // Master block registry for all modules
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, EldritchVoid.MODID);
    
    // Common block properties
    private static final BlockBehaviour.Properties STONE_PROPERTIES = BlockBehaviour.Properties.of()
            .mapColor(MapColor.STONE)
            .requiresCorrectToolForDrops()
            .strength(3.0F, 6.0F)
            .sound(SoundType.STONE);
    
    private static final BlockBehaviour.Properties OBSIDIAN_PROPERTIES = BlockBehaviour.Properties.of()
            .mapColor(MapColor.COLOR_BLACK)
            .requiresCorrectToolForDrops()
            .strength(50.0F, 1200.0F)
            .sound(SoundType.STONE);
    
    private static final BlockBehaviour.Properties VOID_PROPERTIES = BlockBehaviour.Properties.of()
            .mapColor(MapColor.COLOR_PURPLE)
            .requiresCorrectToolForDrops()
            .strength(2.0F, 8.0F)
            .sound(SoundType.AMETHYST);
    
    // CORE MODULE BLOCKS
    public static final RegistryObject<Block> VOID_ALTAR = registerBlock("void_altar",
            () -> new Block(VOID_PROPERTIES));
    
    public static final RegistryObject<Block> ELDRITCH_PEDESTAL = registerBlock("eldritch_pedestal", 
            () -> new Block(STONE_PROPERTIES));
    
    public static final RegistryObject<Block> OBSIDIAN_FORGE = registerBlock("obsidian_forge",
            () -> new Block(OBSIDIAN_PROPERTIES));
    
    // VOID ALCHEMY MODULE BLOCKS
    public static final RegistryObject<Block> ALCHEMY_TABLE = registerBlock("alchemy_table",
            () -> new Block(STONE_PROPERTIES));
    
    public static final RegistryObject<Block> VOID_CAULDRON = registerBlock("void_cauldron",
            () -> new Block(VOID_PROPERTIES));
    
    public static final RegistryObject<Block> ESSENCE_EXTRACTOR = registerBlock("essence_extractor",
            () -> new Block(STONE_PROPERTIES));
    
    // ELDRITCH ARTIFACTS MODULE BLOCKS
    public static final RegistryObject<Block> ARTIFACT_PEDESTAL = registerBlock("artifact_pedestal",
            () -> new Block(STONE_PROPERTIES));
    
    public static final RegistryObject<Block> ELDRITCH_RELIQUARY = registerBlock("eldritch_reliquary",
            () -> new Block(VOID_PROPERTIES));
    
    public static final RegistryObject<Block> DIMENSIONAL_GATEWAY = registerBlock("dimensional_gateway",
            () -> new Block(VOID_PROPERTIES));
    
    // OBSIDIAN FORGEMASTER MODULE BLOCKS
    public static final RegistryObject<Block> ENHANCED_ANVIL = registerBlock("enhanced_anvil",
            () -> new Block(OBSIDIAN_PROPERTIES));
    
    public static final RegistryObject<Block> OBSIDIAN_FURNACE = registerBlock("obsidian_furnace",
            () -> new Block(OBSIDIAN_PROPERTIES));
    
    public static final RegistryObject<Block> BLUEPRINT_TABLE = registerBlock("blueprint_table",
            () -> new Block(STONE_PROPERTIES));
    
    // VOID CORRUPTION MODULE BLOCKS
    public static final RegistryObject<Block> CORRUPTED_STONE = registerBlock("corrupted_stone",
            () -> new Block(STONE_PROPERTIES));
    
    public static final RegistryObject<Block> PURIFICATION_ALTAR = registerBlock("purification_altar",
            () -> new Block(STONE_PROPERTIES));
    
    public static final RegistryObject<Block> CORRUPTION_CONTAINMENT = registerBlock("corruption_containment",
            () -> new Block(OBSIDIAN_PROPERTIES));
    
    // ELDRITCH ARCANA MODULE BLOCKS
    public static final RegistryObject<Block> ARCANE_LECTERN = registerBlock("arcane_lectern",
            () -> new Block(STONE_PROPERTIES));
    
    public static final RegistryObject<Block> RITUAL_CIRCLE = registerBlock("ritual_circle",
            () -> new Block(STONE_PROPERTIES));
    
    public static final RegistryObject<Block> MANA_FONT = registerBlock("mana_font",
            () -> new Block(VOID_PROPERTIES));
    
    // OBSIDIAN CONSTRUCTS MODULE BLOCKS
    public static final RegistryObject<Block> CONSTRUCT_ASSEMBLER = registerBlock("construct_assembler",
            () -> new Block(OBSIDIAN_PROPERTIES));
    
    public static final RegistryObject<Block> ANIMATION_CORE = registerBlock("animation_core",
            () -> new Block(OBSIDIAN_PROPERTIES));
    
    public static final RegistryObject<Block> CONTROL_INTERFACE = registerBlock("control_interface",
            () -> new Block(OBSIDIAN_PROPERTIES));
    
    /**
     * Helper method to register a block and its corresponding BlockItem.
     *
     * @param name The registry name for the block
     * @param blockSupplier The block supplier
     * @return The registry object for the block
     */
    private static <T extends Block> RegistryObject<T> registerBlock(String name, Supplier<T> blockSupplier) {
        RegistryObject<T> block = BLOCKS.register(name, blockSupplier);
        registerBlockItem(name, block);
        return block;
    }
    
    /**
     * Helper method to register a BlockItem for a block.
     *
     * @param name The registry name for the BlockItem
     * @param block The block to create the BlockItem for
     */
    private static <T extends Block> void registerBlockItem(String name, RegistryObject<T> block) {
        ItemInit.ITEMS.register(name, () -> new BlockItem(block.get(), new Item.Properties()));
    }
}