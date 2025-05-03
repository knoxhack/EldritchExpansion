package com.eldritchvoid.modules.obsidianforgemaster;

import com.eldritchvoid.EldritchVoid;
import com.eldritchvoid.core.IEldritchModule;
import com.eldritchvoid.modules.obsidianforgemaster.gui.ForgeScreen;
import net.minecraft.client.gui.screens.MenuScreens;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.event.lifecycle.InterModProcessEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Collections;
import java.util.List;

/**
 * The Obsidian Forgemaster module for Eldritch Void.
 * Handles custom crafting GUI and mechanics.
 */
public class ObsidianForgemasterModule implements IEldritchModule {
    private static final Logger LOGGER = LogManager.getLogger();
    private final IEventBus modEventBus;
    
    public ObsidianForgemasterModule(IEventBus modEventBus) {
        this.modEventBus = modEventBus;
    }
    
    @Override
    public String getModuleId() {
        return "obsidianforgemaster";
    }
    
    @Override
    public String getDisplayName() {
        return "Obsidian Forgemaster";
    }
    
    @Override
    public String getDescription() {
        return "Craft powerful items using the heat of obsidian forges";
    }
    
    @Override
    public List<String> getDependencies() {
        return Collections.emptyList(); // No dependencies
    }
    
    @Override
    public void initialize() {
        LOGGER.info("Initializing Obsidian Forgemaster Module");
        
        // Register event handlers
        modEventBus.addListener(this::onCommonSetup);
        modEventBus.addListener(this::onClientSetup);
        
        // Register containers, block entities, etc.
    }
    
    @Override
    public void onCommonSetup() {
        LOGGER.info("Obsidian Forgemaster: Common Setup");
        
        // Register crafting recipes
    }
    
    @Override
    public void onClientSetup() {
        LOGGER.info("Obsidian Forgemaster: Client Setup");
        
        // Register screens
        MenuScreens.register(null, ForgeScreen::new); // Register with menu type once created
    }
    
    @Override
    public void onInterModEnqueue() {
        // Send IMC messages to other mods
    }
    
    @Override
    public void onInterModProcess(InterModProcessEvent event) {
        // Process IMC messages from other mods
    }
}
