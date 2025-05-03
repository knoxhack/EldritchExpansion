package com.eldritchvoid.modules.obsidianforgemaster;

import com.eldritchvoid.core.Module;
import com.eldritchvoid.core.config.ModuleConfig;
import net.minecraft.world.item.CreativeModeTabs;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;

/**
 * The Obsidian Forgemaster module enables players to create and upgrade tools and weapons
 * using obsidian-infused forging techniques. It provides special forge blocks, tools and recipes.
 */
public class ObsidianForgemasterModule extends Module {

    public ObsidianForgemasterModule(IEventBus modBus) {
        super("obsidianforgemaster", modBus);
        
        // Register additional events
        modBus.addListener(this::addCreative);
    }
    
    @Override
    protected void setupConfig(ModuleConfig config) {
        // Configure module settings
        config.addBooleanParameter("enable_forging", true, "Enable forging mechanics");
        config.addIntParameter("forge_durability", 2000, "Base durability for obsidian forges");
    }
    
    @Override
    protected void registerContent() {
        // Register blocks and items
        ObsidianForgemasterBlocks.register();
        ObsidianForgemasterItems.register();
        
        log("Registered Obsidian Forgemaster content");
    }
    
    @Override
    protected void init(FMLCommonSetupEvent event) {
        // Setup common functionality
        log("Setting up Obsidian Forgemaster module");
    }
    
    @Override
    protected void clientInit(FMLClientSetupEvent event) {
        // Client-side initialization
        log("Setting up Obsidian Forgemaster client features");
    }
    
    /**
     * Add items to creative tabs.
     *
     * @param event The creative tab contents event
     */
    private void addCreative(BuildCreativeModeTabContentsEvent event) {
        if (!isEnabled()) return;
        
        if (event.getTabKey() == CreativeModeTabs.BUILDING_BLOCKS) {
            // Add forge blocks to the building blocks tab
            // Will be expanded when proper registrations are implemented
        }
        
        if (event.getTabKey() == CreativeModeTabs.TOOLS_AND_UTILITIES) {
            // Add forging tools to the tools tab
            // Will be expanded when proper registrations are implemented
        }
    }
    
    /**
     * Get the display name of the module.
     *
     * @return The module display name
     */
    public String getDisplayName() {
        return "Obsidian Forgemaster";
    }
}