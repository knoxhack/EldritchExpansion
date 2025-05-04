package com.eldritchvoid.modules.eldritcharcana;

import com.eldritchvoid.core.Module;
import com.eldritchvoid.core.config.ModuleConfig;
import net.minecraft.world.item.CreativeModeTabs;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;

/**
 * The Eldritch Arcana module provides magical spells, rituals, and occult
 * knowledge systems for players to discover and master. It includes various
 * casting mechanics, spell books, and magical effects.
 */
public class EldritchArcanaModule extends Module {

    public EldritchArcanaModule(IEventBus modBus) {
        super("eldritcharcana", modBus);
        
        // Register additional events
        modBus.addListener(this::addCreative);
    }
    
    @Override
    protected void setupConfig(ModuleConfig config) {
        // Configure module settings
        config.addBooleanParameter("enable_spell_casting", true, "Enable spell casting mechanics");
        config.addIntParameter("mana_regeneration_rate", 10, "Rate at which mana regenerates (points per minute)");
        config.addIntParameter("spell_cooldown", 20, "Base cooldown for spells in ticks");
    }
    
    @Override
    protected void registerContent() {
        // Register items and blocks
        EldritchArcanaItems.register();
        
        log("Registered Eldritch Arcana content");
    }
    
    @Override
    protected void init(FMLCommonSetupEvent event) {
        // Setup common functionality
        log("Setting up Eldritch Arcana module");
    }
    
    @Override
    protected void clientInit(FMLClientSetupEvent event) {
        // Client-side initialization
        log("Setting up Eldritch Arcana client features");
    }
    
    /**
     * Add items to creative tabs.
     *
     * @param event The creative tab contents event
     */
    private void addCreative(BuildCreativeModeTabContentsEvent event) {
        if (!isEnabled()) return;
        
        if (event.getTabKey() == CreativeModeTabs.TOOLS_AND_UTILITIES) {
            // Add magical tools and utilities
            // Will be expanded when proper registrations are implemented
        }
        
        if (event.getTabKey() == CreativeModeTabs.INGREDIENTS) {
            // Add magical ingredients
            // Will be expanded when proper registrations are implemented
        }
    }
    
    /**
     * Get the display name of the module.
     *
     * @return The module display name
     */
    public String getDisplayName() {
        return "Eldritch Arcana";
    }
}