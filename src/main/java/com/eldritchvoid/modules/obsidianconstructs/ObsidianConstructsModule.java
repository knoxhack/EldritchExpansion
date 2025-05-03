package com.eldritchvoid.modules.obsidianconstructs;

import com.eldritchvoid.EldritchVoid;
import com.eldritchvoid.core.IEldritchModule;
import com.eldritchvoid.core.registry.ModEntities;
import com.eldritchvoid.modules.obsidianconstructs.entity.ConstructEntity;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.level.levelgen.Heightmap;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.event.lifecycle.InterModProcessEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Collections;
import java.util.List;

/**
 * The Obsidian Constructs module for Eldritch Void.
 * Handles automation entities and construct mechanics.
 */
public class ObsidianConstructsModule implements IEldritchModule {
    private static final Logger LOGGER = LogManager.getLogger();
    private final IEventBus modEventBus;
    
    public ObsidianConstructsModule(IEventBus modEventBus) {
        this.modEventBus = modEventBus;
    }
    
    @Override
    public String getModuleId() {
        return "obsidianconstructs";
    }
    
    @Override
    public String getDisplayName() {
        return "Obsidian Constructs";
    }
    
    @Override
    public String getDescription() {
        return "Create autonomous constructs to automate tasks";
    }
    
    @Override
    public List<String> getDependencies() {
        return Collections.emptyList(); // No dependencies
    }
    
    @Override
    public void initialize() {
        LOGGER.info("Initializing Obsidian Constructs Module");
        
        // Register event handlers
        modEventBus.addListener(this::onCommonSetup);
        modEventBus.addListener(this::onClientSetup);
        modEventBus.addListener(this::onEntityAttributeCreation);
        modEventBus.addListener(this::onRegisterRenderers);
        
        // Register entity-related events
        NeoForge.EVENT_BUS.addListener(this::onEntityInteract);
    }
    
    @Override
    public void onCommonSetup() {
        LOGGER.info("Obsidian Constructs: Common Setup");
        
        // Register spawn placements
        SpawnPlacements.register(
                ModEntities.OBSIDIAN_GOLEM.get(),
                SpawnPlacements.Type.ON_GROUND,
                Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                ConstructEntity::checkConstructSpawnRules
        );
    }
    
    @Override
    public void onClientSetup() {
        LOGGER.info("Obsidian Constructs: Client Setup");
        
        // Register entity renderers will be done in onRegisterRenderers
    }
    
    /**
     * Register entity attributes.
     */
    private void onEntityAttributeCreation(EntityAttributeCreationEvent event) {
        event.put(ModEntities.OBSIDIAN_GOLEM.get(), ConstructEntity.createAttributes().build());
    }
    
    /**
     * Register entity renderers.
     */
    private void onRegisterRenderers(EntityRenderersEvent.RegisterRenderers event) {
        // Register entity renderer
        // event.registerEntityRenderer(ModEntities.OBSIDIAN_GOLEM.get(), ConstructRenderer::new);
        
        // This would be a proper implementation with a real renderer
        // For now, we'll use a placeholder
    }
    
    /**
     * Handle entity interactions.
     */
    private void onEntityInteract(net.neoforged.neoforge.event.entity.player.PlayerInteractEvent.EntityInteract event) {
        // Handle interaction with constructs
        if (event.getTarget() instanceof ConstructEntity construct) {
            // Handle construct interactions, like giving orders
        }
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
