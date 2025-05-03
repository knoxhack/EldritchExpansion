package com.eldritchvoid.modules.eldritchartifacts;

import com.eldritchvoid.core.Module;
import com.eldritchvoid.core.config.ModuleConfig;
import net.minecraft.world.item.CreativeModeTabs;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;

/**
 * The Eldritch Artifacts module provides players with powerful magical items
 * that grant unique abilities and effects inspired by Lovecraftian lore.
 */
public class EldritchArtifactsModule extends Module {

    public EldritchArtifactsModule(IEventBus modBus) {
        super("eldritchartifacts", modBus);
        
        // Register additional events
        modBus.addListener(this::addCreative);
    }
    
    @Override
    protected void setupConfig(ModuleConfig config) {
        // Configure module settings
        config.addBooleanParameter("enable_artifacts", true, "Enable artifact items");
        config.addIntParameter("artifact_durability", 250, "Base durability for artifacts");
    }
    
    @Override
    protected void registerContent() {
        // Register artifacts
        EldritchArtifactsItems.register();
        
        log("Registered Eldritch Artifacts content");
    }
    
    @Override
    protected void init(FMLCommonSetupEvent event) {
        // Setup common functionality
        log("Setting up Eldritch Artifacts module");
    }
    
    @Override
    protected void clientInit(FMLClientSetupEvent event) {
        // Client-side initialization
        log("Setting up Eldritch Artifacts client features");
    }
    
    /**
     * Add artifacts to creative tabs.
     *
     * @param event The creative tab contents event
     */
    private void addCreative(BuildCreativeModeTabContentsEvent event) {
        if (!isEnabled()) return;
        
        if (event.getTabKey() == CreativeModeTabs.TOOLS_AND_UTILITIES) {
            // Add utility artifacts
            // Will be expanded when proper registrations are implemented
        }
        
        if (event.getTabKey() == CreativeModeTabs.COMBAT) {
            // Add combat artifacts
            // Will be expanded when proper registrations are implemented
        }
    }
    
    /**
     * Get the display name of the module.
     *
     * @return The module display name
     */
    public String getDisplayName() {
        return "Eldritch Artifacts";
    }
}