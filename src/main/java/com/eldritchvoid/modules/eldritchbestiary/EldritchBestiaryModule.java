package com.eldritchvoid.modules.eldritchbestiary;

import com.eldritchvoid.EldritchVoid;
import com.eldritchvoid.core.IEldritchModule;
import com.eldritchvoid.core.registry.ModEntities;
import com.eldritchvoid.modules.eldritchbestiary.entity.EldritchEntity;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.levelgen.Heightmap;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.event.lifecycle.InterModProcessEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.entity.living.LivingEvent;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * The Eldritch Bestiary module for Eldritch Void.
 * Handles custom mobs and AI systems.
 */
public class EldritchBestiaryModule implements IEldritchModule {
    private static final Logger LOGGER = LogManager.getLogger();
    private final IEventBus modEventBus;
    
    // Player knowledge tracking
    private final Map<UUID, Map<String, Integer>> playerKnowledge = new HashMap<>();
    
    public EldritchBestiaryModule(IEventBus modEventBus) {
        this.modEventBus = modEventBus;
    }
    
    @Override
    public String getModuleId() {
        return "eldritchbestiary";
    }
    
    @Override
    public String getDisplayName() {
        return "Eldritch Bestiary";
    }
    
    @Override
    public String getDescription() {
        return "Encounter eldritch creatures from beyond";
    }
    
    @Override
    public List<String> getDependencies() {
        return Collections.emptyList(); // No dependencies
    }
    
    @Override
    public void initialize() {
        LOGGER.info("Initializing Eldritch Bestiary Module");
        
        // Register event handlers
        modEventBus.addListener(this::onCommonSetup);
        modEventBus.addListener(this::onClientSetup);
        modEventBus.addListener(this::onEntityAttributeCreation);
        modEventBus.addListener(this::onRegisterRenderers);
        
        // Register entity-related events
        NeoForge.EVENT_BUS.addListener(this::onEntityDeath);
        NeoForge.EVENT_BUS.addListener(this::onLivingUpdate);
    }
    
    @Override
    public void onCommonSetup() {
        LOGGER.info("Eldritch Bestiary: Common Setup");
        
        // Register spawn placements
        SpawnPlacements.register(
                ModEntities.VOID_HORROR.get(),
                SpawnPlacements.Type.ON_GROUND,
                Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                EldritchEntity::checkVoidHorrorSpawnRules
        );
    }
    
    @Override
    public void onClientSetup() {
        LOGGER.info("Eldritch Bestiary: Client Setup");
        
        // Client setup will be handled in onRegisterRenderers
    }
    
    /**
     * Register entity attributes.
     */
    private void onEntityAttributeCreation(EntityAttributeCreationEvent event) {
        event.put(ModEntities.VOID_HORROR.get(), EldritchEntity.createAttributes()
                .add(Attributes.MAX_HEALTH, 40.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.3D)
                .add(Attributes.ATTACK_DAMAGE, 6.0D)
                .add(Attributes.FOLLOW_RANGE, 32.0D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.6D)
                .build());
    }
    
    /**
     * Register entity renderers.
     */
    private void onRegisterRenderers(EntityRenderersEvent.RegisterRenderers event) {
        // Register entity renderer
        // event.registerEntityRenderer(ModEntities.VOID_HORROR.get(), VoidHorrorRenderer::new);
        
        // This would be a proper implementation with a real renderer
        // For now, we'll use a placeholder
    }
    
    /**
     * Handle entity death event to record kills in the bestiary.
     */
    private void onEntityDeath(LivingDeathEvent event) {
        // Check if the killed entity is one of our eldritch entities
        if (event.getEntity() instanceof EldritchEntity eldritchEntity) {
            // Check if killed by a player
            if (event.getSource().getEntity() instanceof net.minecraft.world.entity.player.Player player) {
                // Record the kill in the player's bestiary knowledge
                String entityKey = eldritchEntity.getType().getDescriptionId();
                recordEntityKill(player.getUUID(), entityKey);
                
                // Notify player of increased knowledge
                int knowledge = getKnowledgeLevel(player.getUUID(), entityKey);
                if (knowledge == 1) {
                    // First kill
                    player.displayClientMessage(net.minecraft.network.chat.Component.translatable(
                            "eldritchvoid.bestiary.first_kill", event.getEntity().getName()), false);
                } else if (knowledge == 5) {
                    // Enough kills to unlock basic info
                    player.displayClientMessage(net.minecraft.network.chat.Component.translatable(
                            "eldritchvoid.bestiary.knowledge_basic", event.getEntity().getName()), false);
                } else if (knowledge == 15) {
                    // Enough kills to unlock advanced info
                    player.displayClientMessage(net.minecraft.network.chat.Component.translatable(
                            "eldritchvoid.bestiary.knowledge_advanced", event.getEntity().getName()), false);
                }
            }
        }
    }
    
    /**
     * Handle living entity updates.
     */
    private void onLivingUpdate(LivingEvent event) {
        // Handle any custom entity ticking logic
        if (event.getEntity() instanceof EldritchEntity eldritchEntity) {
            // Custom behavior based on world conditions
            if (event.getEntity().level().isNight() && event.getEntity().level().getRandom().nextInt(200) == 0) {
                // Night-time power boost
                eldritchEntity.setPoweredState(true);
            }
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
    
    /**
     * Record a player killing an entity type.
     * 
     * @param playerUuid The player UUID
     * @param entityKey The entity key
     */
    public void recordEntityKill(UUID playerUuid, String entityKey) {
        Map<String, Integer> knowledgeMap = playerKnowledge.computeIfAbsent(playerUuid, k -> new HashMap<>());
        knowledgeMap.put(entityKey, knowledgeMap.getOrDefault(entityKey, 0) + 1);
    }
    
    /**
     * Get a player's knowledge level for an entity.
     * 
     * @param playerUuid The player UUID
     * @param entityKey The entity key
     * @return The knowledge level
     */
    public int getKnowledgeLevel(UUID playerUuid, String entityKey) {
        Map<String, Integer> knowledgeMap = playerKnowledge.get(playerUuid);
        if (knowledgeMap == null) {
            return 0;
        }
        
        return knowledgeMap.getOrDefault(entityKey, 0);
    }
    
    /**
     * Check if a player has enough knowledge to access basic information.
     * 
     * @param playerUuid The player UUID
     * @param entityKey The entity key
     * @return True if the player has basic knowledge
     */
    public boolean hasBasicKnowledge(UUID playerUuid, String entityKey) {
        return getKnowledgeLevel(playerUuid, entityKey) >= 5;
    }
    
    /**
     * Check if a player has enough knowledge to access advanced information.
     * 
     * @param playerUuid The player UUID
     * @param entityKey The entity key
     * @return True if the player has advanced knowledge
     */
    public boolean hasAdvancedKnowledge(UUID playerUuid, String entityKey) {
        return getKnowledgeLevel(playerUuid, entityKey) >= 15;
    }
}
