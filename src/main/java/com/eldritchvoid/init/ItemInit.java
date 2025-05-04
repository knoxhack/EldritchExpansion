package com.eldritchvoid.init;

import com.eldritchvoid.EldritchVoid;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

/**
 * Registry for all mod items.
 * Contains placeholder items for each module.
 */
public class ItemInit {
    // Master item registry for all modules
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, EldritchVoid.MODID);
    
    // Common properties
    private static final Item.Properties DEFAULT_PROPERTIES = new Item.Properties();
    
    // CORE MODULE ITEMS
    public static final RegistryObject<Item> VOID_ESSENCE = ITEMS.register("void_essence",
            () -> new Item(DEFAULT_PROPERTIES));
    
    public static final RegistryObject<Item> ELDRITCH_CRYSTAL = ITEMS.register("eldritch_crystal",
            () -> new Item(DEFAULT_PROPERTIES));
    
    public static final RegistryObject<Item> OBSIDIAN_SHARD = ITEMS.register("obsidian_shard",
            () -> new Item(DEFAULT_PROPERTIES));
    
    // VOID ALCHEMY MODULE ITEMS
    public static final RegistryObject<Item> VOID_PEE_ESSENCE = ITEMS.register("void_pee_essence",
            () -> new Item(DEFAULT_PROPERTIES));
    
    public static final RegistryObject<Item> ALCHEMICAL_CATALYST = ITEMS.register("alchemical_catalyst",
            () -> new Item(DEFAULT_PROPERTIES));
    
    public static final RegistryObject<Item> TRANSMUTATION_STONE = ITEMS.register("transmutation_stone",
            () -> new Item(DEFAULT_PROPERTIES));
    
    // ELDRITCH ARTIFACTS MODULE ITEMS
    public static final RegistryObject<Item> VOID_AMULET = ITEMS.register("void_amulet",
            () -> new Item(DEFAULT_PROPERTIES));
    
    public static final RegistryObject<Item> ELDRITCH_TOME = ITEMS.register("eldritch_tome",
            () -> new Item(DEFAULT_PROPERTIES));
    
    public static final RegistryObject<Item> DIMENSIONAL_KEY = ITEMS.register("dimensional_key",
            () -> new Item(DEFAULT_PROPERTIES));
    
    // OBSIDIAN FORGEMASTER MODULE ITEMS
    public static final RegistryObject<Item> OBSIDIAN_HAMMER = ITEMS.register("obsidian_hammer",
            () -> new Item(DEFAULT_PROPERTIES));
    
    public static final RegistryObject<Item> FORGE_BLUEPRINT = ITEMS.register("forge_blueprint",
            () -> new Item(DEFAULT_PROPERTIES));
    
    public static final RegistryObject<Item> MOLTEN_CORE = ITEMS.register("molten_core",
            () -> new Item(DEFAULT_PROPERTIES));
    
    // VOID CORRUPTION MODULE ITEMS
    public static final RegistryObject<Item> CORRUPTION_SAMPLE = ITEMS.register("corruption_sample",
            () -> new Item(DEFAULT_PROPERTIES));
    
    public static final RegistryObject<Item> PURIFICATION_TALISMAN = ITEMS.register("purification_talisman",
            () -> new Item(DEFAULT_PROPERTIES));
    
    public static final RegistryObject<Item> VOID_HEART = ITEMS.register("void_heart",
            () -> new Item(DEFAULT_PROPERTIES));
    
    // ELDRITCH ARCANA MODULE ITEMS
    public static final RegistryObject<Item> ARCANE_FOCUS = ITEMS.register("arcane_focus",
            () -> new Item(DEFAULT_PROPERTIES));
    
    public static final RegistryObject<Item> SPELL_PAGE = ITEMS.register("spell_page",
            () -> new Item(DEFAULT_PROPERTIES));
    
    public static final RegistryObject<Item> RITUAL_DAGGER = ITEMS.register("ritual_dagger",
            () -> new Item(DEFAULT_PROPERTIES));
    
    // OBSIDIAN CONSTRUCTS MODULE ITEMS
    public static final RegistryObject<Item> CONSTRUCT_CORE = ITEMS.register("construct_core",
            () -> new Item(DEFAULT_PROPERTIES));
    
    public static final RegistryObject<Item> ANIMATION_RUNE = ITEMS.register("animation_rune",
            () -> new Item(DEFAULT_PROPERTIES));
    
    public static final RegistryObject<Item> CONTROL_SCEPTER = ITEMS.register("control_scepter",
            () -> new Item(DEFAULT_PROPERTIES));
}