package com.eldritchvoid.modules.voidcorruption;

import com.eldritchvoid.core.Module;
import com.eldritchvoid.core.config.ModuleConfig;
import net.minecraft.world.item.CreativeModeTabs;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;

/**
 * The Void Corruption module introduces a spreading corruption mechanic that can
 * infect blocks, entities, and even players. This corruption provides both challenges
 * and benefits depending on how players interact with it.
 */
public class VoidCorruptionModule extends Module {

    public VoidCorruptionModule(IEventBus modBus) {
        super("voidcorruption", modBus);
        
        // Register additional events
        modBus.addListener(this::addCreative);
    }
    
    @Override
    protected void setupConfig(ModuleConfig config) {
        // Configure module settings
        config.addBooleanParameter("enable_corruption_spread", true, "Enable corruption spread mechanic");
        config.addIntParameter("corruption_spread_rate", 5, "Rate at which corruption spreads (blocks per minute)");
        config.addDoubleParameter("corruption_damage", 1.0, "Damage dealt by corruption per second");
    }
    
    @Override
    protected void registerContent() {
        // Register blocks and items
        VoidCorruptionBlocks.register();
        VoidCorruptionItems.register();
        
        log("Registered Void Corruption content");
    }
    
    @Override
    protected void init(FMLCommonSetupEvent event) {
        // Setup common functionality
        log("Setting up Void Corruption module");
    }
    
    @Override
    protected void clientInit(FMLClientSetupEvent event) {
        // Client-side initialization
        log("Setting up Void Corruption client features");
    }
    
    /**
     * Add items to creative tabs.
     *
     * @param event The creative tab contents event
     */
    private void addCreative(BuildCreativeModeTabContentsEvent event) {
        if (!isEnabled()) return;
        
        if (event.getTabKey() == CreativeModeTabs.BUILDING_BLOCKS) {
            // Add corrupt blocks to the building blocks tab
            // Will be expanded when proper registrations are implemented
        }
        
        if (event.getTabKey() == CreativeModeTabs.INGREDIENTS) {
            // Add corruption-related items to the ingredients tab
            // Will be expanded when proper registrations are implemented
        }
    }
    
    /**
     * Get the display name of the module.
     *
     * @return The module display name
     */
    public String getDisplayName() {
        return "Void Corruption";
    }
}