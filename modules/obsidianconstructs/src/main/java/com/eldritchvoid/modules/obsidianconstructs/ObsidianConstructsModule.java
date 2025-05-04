package com.eldritchvoid.modules.obsidianconstructs;

import com.eldritchvoid.core.Module;
import com.eldritchvoid.core.config.ModuleConfig;
import net.minecraft.world.item.CreativeModeTabs;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;

/**
 * The Obsidian Constructs module allows players to build and animate obsidian-based
 * golems, guardians, and structures. These constructs can be used for protection,
 * automation, or aesthetic purposes.
 */
public class ObsidianConstructsModule extends Module {

    public ObsidianConstructsModule(IEventBus modBus) {
        super("obsidianconstructs", modBus);
        
        // Register additional events
        modBus.addListener(this::addCreative);
    }
    
    @Override
    protected void setupConfig(ModuleConfig config) {
        // Configure module settings
        config.addBooleanParameter("enable_construct_animations", true, "Enable construct animations");
        config.addIntParameter("construct_health", 50, "Base health for obsidian constructs");
        config.addIntParameter("construct_attack_damage", 5, "Base attack damage for obsidian constructs");
    }
    
    @Override
    protected void registerContent() {
        // Register blocks, items and entities
        ObsidianConstructsBlocks.register();
        ObsidianConstructsItems.register();
        ObsidianConstructsEntities.register();
        
        log("Registered Obsidian Constructs content");
    }
    
    @Override
    protected void init(FMLCommonSetupEvent event) {
        // Setup common functionality
        log("Setting up Obsidian Constructs module");
    }
    
    @Override
    protected void clientInit(FMLClientSetupEvent event) {
        // Client-side initialization
        log("Setting up Obsidian Constructs client features");
    }
    
    /**
     * Add items to creative tabs.
     *
     * @param event The creative tab contents event
     */
    private void addCreative(BuildCreativeModeTabContentsEvent event) {
        if (!isEnabled()) return;
        
        if (event.getTabKey() == CreativeModeTabs.BUILDING_BLOCKS) {
            // Add construct blocks to the building blocks tab
            // Will be expanded when proper registrations are implemented
        }
        
        if (event.getTabKey() == CreativeModeTabs.TOOLS_AND_UTILITIES) {
            // Add construct creation tools
            // Will be expanded when proper registrations are implemented
        }
        
        if (event.getTabKey() == CreativeModeTabs.SPAWN_EGGS) {
            // Add construct spawn eggs
            // Will be expanded when proper registrations are implemented
        }
    }
    
    /**
     * Get the display name of the module.
     *
     * @return The module display name
     */
    public String getDisplayName() {
        return "Obsidian Constructs";
    }
}