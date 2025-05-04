package com.eldritchvoid.modules.voidalchemy;

import com.eldritchvoid.EldritchVoid;
import com.eldritchvoid.core.registry.Registration;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredHolder;

/**
 * Registers all items for the Void Alchemy module.
 */
public class VoidAlchemyItems {
    // Void alchemy reagents and materials
    public static final DeferredHolder<Item, Item> VOID_CRYSTAL = Registration.ITEMS.register("void_crystal",
            () -> new Item(new Item.Properties()));
            
    public static final DeferredHolder<Item, Item> VOID_DUST = Registration.ITEMS.register("void_dust",
            () -> new Item(new Item.Properties()));
            
    public static final DeferredHolder<Item, Item> VOID_ESSENCE_VIAL = Registration.ITEMS.register("void_essence_vial",
            () -> new Item(new Item.Properties().stacksTo(16)));
    
    /**
     * Register all items.
     */
    public static void register() {
        EldritchVoid.LOGGER.info("Registering Void Alchemy items");
    }
}