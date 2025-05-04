package com.eldritchvoid.modules.eldritcharcana;

import com.eldritchvoid.EldritchVoid;
import com.eldritchvoid.core.registry.Registration;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredHolder;

/**
 * Registers all items for the Eldritch Arcana module.
 */
public class EldritchArcanaItems {
    // Eldritch Arcana magical items - these will be expanded with proper functionality
    
    // Arcane Grimoire - a book of magical knowledge and spells
    public static final DeferredHolder<Item, Item> ARCANE_GRIMOIRE = Registration.ITEMS.register("arcane_grimoire",
            () -> new Item(new Item.Properties().stacksTo(1)));
            
    // Void Crystal - a magical focus used in spellcasting
    public static final DeferredHolder<Item, Item> VOID_CRYSTAL = Registration.ITEMS.register("void_crystal",
            () -> new Item(new Item.Properties()));
            
    // Ritual Chalk - used to draw magical circles for rituals
    public static final DeferredHolder<Item, Item> RITUAL_CHALK = Registration.ITEMS.register("ritual_chalk", 
            () -> new Item(new Item.Properties().durability(64)));
            
    // Eldritch Staff - a powerful magical staff
    public static final DeferredHolder<Item, Item> ELDRITCH_STAFF = Registration.ITEMS.register("eldritch_staff", 
            () -> new Item(new Item.Properties().stacksTo(1).durability(512)));
    
    /**
     * Register all items.
     */
    public static void register() {
        EldritchVoid.LOGGER.info("Registering Eldritch Arcana items");
    }
}