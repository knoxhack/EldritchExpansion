package com.eldritchvoid.modules.voidalchemy;

import com.eldritchvoid.EldritchVoid;
import com.eldritchvoid.core.AbstractModule;
import com.eldritchvoid.modules.voidalchemy.fluid.VoidEssenceFluid;
import net.minecraft.world.item.CreativeModeTabs;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;

/**
 * The Void Alchemy module enables players to create and use various void-infused
 * potions, reagents, and alchemical processes. It provides new materials and
 * fluids related to void essence.
 */
public class VoidAlchemyModule extends AbstractModule {
    /**
     * Initialize the module.
     */
    @Override
    public void init() {
        super.init();
        
        // Get the mod event bus
        IEventBus modEventBus = net.neoforged.fml.ModLoadingContext.get().getActiveContainer().getEventBus();
        
        // Register lifecycle events
        modEventBus.addListener(this::setup);
        modEventBus.addListener(this::addCreative);
        
        // Register fluids and items
        VoidAlchemyItems.register();
        VoidAlchemyFluids.register();
        
        log("Initialized Void Alchemy module");
    }
    
    /**
     * Setup the module during the common setup phase.
     *
     * @param event The common setup event
     */
    private void setup(final FMLCommonSetupEvent event) {
        // Setup common functionality
        log("Setting up Void Alchemy module");
    }
    
    /**
     * Add items to creative tabs.
     *
     * @param event The creative tab contents event
     */
    private void addCreative(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.INGREDIENTS) {
            // Add void alchemy ingredients to the ingredients tab
            event.accept(VoidAlchemyItems.VOID_ESSENCE_VIAL.get());
            event.accept(VoidAlchemyItems.VOID_CRYSTAL.get());
        }
    }
    
    /**
     * Get the ID of the module.
     *
     * @return The module ID
     */
    @Override
    public String getId() {
        return "voidalchemy";
    }
    
    /**
     * Get the display name of the module.
     *
     * @return The module display name
     */
    @Override
    public String getDisplayName() {
        return "Void Alchemy";
    }
}