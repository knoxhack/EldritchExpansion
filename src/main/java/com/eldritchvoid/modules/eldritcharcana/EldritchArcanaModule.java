package com.eldritchvoid.modules.eldritcharcana;

import com.eldritchvoid.EldritchVoid;
import com.eldritchvoid.core.IEldritchModule;
import com.eldritchvoid.core.api.EldritchArcanaAPI;
import com.eldritchvoid.modules.eldritcharcana.research.ResearchManager;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.event.lifecycle.InterModProcessEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Collections;
import java.util.List;

/**
 * The Eldritch Arcana module for Eldritch Void.
 * Handles Thaumcraft-inspired research and magic systems.
 */
public class EldritchArcanaModule implements IEldritchModule {
    private static final Logger LOGGER = LogManager.getLogger();
    private final IEventBus modEventBus;
    private ResearchManager researchManager;
    
    public EldritchArcanaModule(IEventBus modEventBus) {
        this.modEventBus = modEventBus;
    }
    
    @Override
    public String getModuleId() {
        return "eldritcharcana";
    }
    
    @Override
    public String getDisplayName() {
        return "Eldritch Arcana";
    }
    
    @Override
    public String getDescription() {
        return "Study forbidden knowledge and perform arcane research";
    }
    
    @Override
    public List<String> getDependencies() {
        return Collections.emptyList(); // No dependencies
    }
    
    @Override
    public void initialize() {
        LOGGER.info("Initializing Eldritch Arcana Module");
        
        // Register event handlers
        modEventBus.addListener(this::onCommonSetup);
        modEventBus.addListener(this::onClientSetup);
        
        // Register player events
        NeoForge.EVENT_BUS.addListener(this::onPlayerLoggedIn);
        NeoForge.EVENT_BUS.addListener(this::onPlayerRespawn);
        
        // Initialize research manager
        researchManager = new ResearchManager();
        
        // Register research categories
        EldritchArcanaAPI api = EldritchArcanaAPI.getInstance();
        api.registerResearchCategory(new ResourceLocation(EldritchVoid.MOD_ID, "basics"));
        api.registerResearchCategory(new ResourceLocation(EldritchVoid.MOD_ID, "alchemy"));
        api.registerResearchCategory(new ResourceLocation(EldritchVoid.MOD_ID, "eldritch"));
        api.registerResearchCategory(new ResourceLocation(EldritchVoid.MOD_ID, "void"));
        api.registerResearchCategory(new ResourceLocation(EldritchVoid.MOD_ID, "artifice"));
        
        // Register basic research entries
        registerResearchEntries();
    }
    
    @Override
    public void onCommonSetup() {
        LOGGER.info("Eldritch Arcana: Common Setup");
        
        // Initialize research capabilities and network
    }
    
    @Override
    public void onClientSetup() {
        LOGGER.info("Eldritch Arcana: Client Setup");
        
        // Register research book GUI
    }
    
    @Override
    public void onInterModEnqueue() {
        // Send IMC messages to other mods
    }
    
    @Override
    public void onInterModProcess(InterModProcessEvent event) {
        // Process IMC messages from other mods
    }
    
    /**
     * Register research entries with the API.
     */
    private void registerResearchEntries() {
        EldritchArcanaAPI api = EldritchArcanaAPI.getInstance();
        
        // Basics category research
        api.registerResearch(
                new ResourceLocation(EldritchVoid.MOD_ID, "research/arcane_fundamentals"),
                new ResourceLocation(EldritchVoid.MOD_ID, "basics"),
                "Research: Arcane Fundamentals",
                "Your first steps into arcane research, understanding the basics of void energy.",
                new ResourceLocation(EldritchVoid.MOD_ID, "textures/research/arcane_fundamentals"),
                0, 0
        );
        
        api.registerResearch(
                new ResourceLocation(EldritchVoid.MOD_ID, "research/void_attunement"),
                new ResourceLocation(EldritchVoid.MOD_ID, "basics"),
                "Research: Void Attunement",
                "Learn to attune yourself to the energies of the void.",
                new ResourceLocation(EldritchVoid.MOD_ID, "textures/research/void_attunement"),
                1, 1,
                new ResourceLocation(EldritchVoid.MOD_ID, "research/arcane_fundamentals")
        );
        
        // Alchemy category research
        api.registerResearch(
                new ResourceLocation(EldritchVoid.MOD_ID, "research/void_essence"),
                new ResourceLocation(EldritchVoid.MOD_ID, "alchemy"),
                "Research: Void Essence",
                "Discover the properties of fluid void essence and how to extract it.",
                new ResourceLocation(EldritchVoid.MOD_ID, "textures/research/void_essence"),
                0, 0,
                new ResourceLocation(EldritchVoid.MOD_ID, "research/arcane_fundamentals")
        );
        
        // Eldritch category research
        api.registerResearch(
                new ResourceLocation(EldritchVoid.MOD_ID, "research/eldritch_knowledge"),
                new ResourceLocation(EldritchVoid.MOD_ID, "eldritch"),
                "Research: Eldritch Knowledge",
                "Delve into forbidden knowledge that man was not meant to know.",
                new ResourceLocation(EldritchVoid.MOD_ID, "textures/research/eldritch_knowledge"),
                0, 0,
                new ResourceLocation(EldritchVoid.MOD_ID, "research/void_attunement")
        );
        
        // More research entries would be registered here
    }
    
    /**
     * Handle player login for syncing research.
     */
    private void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        researchManager.syncResearchData(event.getEntity());
    }
    
    /**
     * Handle player respawn for syncing research.
     */
    private void onPlayerRespawn(PlayerEvent.PlayerRespawnEvent event) {
        researchManager.syncResearchData(event.getEntity());
    }
    
    /**
     * Get the research manager.
     * 
     * @return The research manager
     */
    public ResearchManager getResearchManager() {
        return researchManager;
    }
}
