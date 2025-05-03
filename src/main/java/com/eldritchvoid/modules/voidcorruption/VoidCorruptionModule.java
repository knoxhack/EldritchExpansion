package com.eldritchvoid.modules.voidcorruption;

import com.eldritchvoid.EldritchVoid;
import com.eldritchvoid.core.IEldritchModule;
import com.eldritchvoid.core.capability.CapabilityHandler;
import com.eldritchvoid.core.config.ConfigHandler;
import com.eldritchvoid.modules.voidcorruption.biome.CorruptedBiomeModifier;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.event.lifecycle.InterModProcessEvent;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.TickEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The Void Corruption module for Eldritch Void.
 * Handles biome modification and corruption spreading mechanisms.
 */
public class VoidCorruptionModule implements IEldritchModule {
    private static final Logger LOGGER = LogManager.getLogger();
    private final IEventBus modEventBus;
    
    // Corruption centers - positions where corruption is actively spreading from
    private final Map<Level, Map<ChunkPos, Float>> corruptionCenters = new HashMap<>();
    
    // Cached corruption levels for chunks
    private final Map<Level, Map<ChunkPos, Float>> chunkCorruptionLevels = new HashMap<>();
    
    public VoidCorruptionModule(IEventBus modEventBus) {
        this.modEventBus = modEventBus;
    }
    
    @Override
    public String getModuleId() {
        return "voidcorruption";
    }
    
    @Override
    public String getDisplayName() {
        return "Void Corruption";
    }
    
    @Override
    public String getDescription() {
        return "The void spreads its influence throughout the world";
    }
    
    @Override
    public List<String> getDependencies() {
        return Collections.emptyList(); // No dependencies
    }
    
    @Override
    public void initialize() {
        LOGGER.info("Initializing Void Corruption Module");
        
        // Register event handlers
        modEventBus.addListener(this::onCommonSetup);
        modEventBus.addListener(this::onClientSetup);
        
        // Register world events for corruption spread
        NeoForge.EVENT_BUS.addListener(this::onWorldTick);
        NeoForge.EVENT_BUS.addListener(this::onPlayerTick);
        NeoForge.EVENT_BUS.addListener(this::onPlayerLoggedIn);
        
        // Initialize biome modifier
        CorruptedBiomeModifier.register(modEventBus);
    }
    
    @Override
    public void onCommonSetup() {
        LOGGER.info("Void Corruption: Common Setup");
        
        // Initialize corruption blocks and effects
    }
    
    @Override
    public void onClientSetup() {
        LOGGER.info("Void Corruption: Client Setup");
        
        // Register client-side rendering for corruption effects
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
     * Handle world tick for corruption spread.
     */
    private void onWorldTick(TickEvent.LevelTickEvent event) {
        if (event.phase != TickEvent.Phase.END || !(event.level instanceof ServerLevel level)) {
            return;
        }
        
        // Only process every 20 ticks (1 second)
        if (level.getGameTime() % 20 != 0) {
            return;
        }
        
        // Get spread rate from config
        float spreadRate = (float) ConfigHandler.SERVER.voidCorruptionSpreadRate.get();
        
        // Process corruption centers
        Map<ChunkPos, Float> centersInThisWorld = corruptionCenters.computeIfAbsent(level, k -> new HashMap<>());
        Map<ChunkPos, Float> corruptionInThisWorld = chunkCorruptionLevels.computeIfAbsent(level, k -> new HashMap<>());
        
        for (Map.Entry<ChunkPos, Float> entry : centersInThisWorld.entrySet()) {
            ChunkPos center = entry.getKey();
            float strength = entry.getValue();
            
            // Skip if chunk isn't loaded
            if (!level.isLoaded(new BlockPos(center.getMiddleBlockX(), 0, center.getMiddleBlockZ()))) {
                continue;
            }
            
            // Spread to adjacent chunks
            for (int xOffset = -1; xOffset <= 1; xOffset++) {
                for (int zOffset = -1; zOffset <= 1; zOffset++) {
                    if (xOffset == 0 && zOffset == 0) continue; // Skip self
                    
                    ChunkPos adjacent = new ChunkPos(center.x + xOffset, center.z + zOffset);
                    float currentCorruption = corruptionInThisWorld.getOrDefault(adjacent, 0.0f);
                    float newCorruption = Math.min(1.0f, currentCorruption + strength * spreadRate * 0.1f);
                    
                    if (newCorruption > currentCorruption) {
                        corruptionInThisWorld.put(adjacent, newCorruption);
                        
                        // If corruption is high enough, it becomes a new corruption center
                        if (newCorruption > 0.7f && !centersInThisWorld.containsKey(adjacent)) {
                            centersInThisWorld.put(adjacent, newCorruption * 0.8f);
                        }
                        
                        // Apply visual corruption to the chunk
                        applyCorruptionToChunk(level, adjacent, newCorruption);
                    }
                }
            }
        }
    }
    
    /**
     * Apply corruption effects to a chunk.
     */
    private void applyCorruptionToChunk(ServerLevel level, ChunkPos pos, float corruption) {
        // Skip if corruption is too low to be visible
        if (corruption < 0.3f) return;
        
        // Calculate how many blocks to corrupt based on corruption level
        int blocksToCorrupt = (int)(corruption * corruption * 10);
        if (blocksToCorrupt <= 0) return;
        
        // Get a random position within the chunk
        for (int i = 0; i < blocksToCorrupt; i++) {
            int x = pos.getMinBlockX() + level.getRandom().nextInt(16);
            int z = pos.getMinBlockZ() + level.getRandom().nextInt(16);
            int y = level.getHeight(net.minecraft.world.level.levelgen.Heightmap.Types.WORLD_SURFACE, x, z);
            
            BlockPos blockPos = new BlockPos(x, y, z);
            BlockState currentState = level.getBlockState(blockPos);
            
            // Apply corruption to appropriate blocks
            if (canBeCorrupted(currentState)) {
                level.setBlock(blockPos, getCorruptedBlock(currentState, corruption), 2);
                
                // Maybe corrupt blocks below this one
                if (level.getRandom().nextFloat() < corruption * 0.2f) {
                    BlockPos below = blockPos.below();
                    BlockState belowState = level.getBlockState(below);
                    
                    if (canBeCorrupted(belowState)) {
                        level.setBlock(below, getCorruptedBlock(belowState, corruption), 2);
                    }
                }
            }
        }
    }
    
    /**
     * Check if a block can be corrupted.
     */
    private boolean canBeCorrupted(BlockState state) {
        return !state.isAir() && !state.liquid() && 
                state.getBlock() != net.minecraft.world.level.block.Blocks.BEDROCK;
    }
    
    /**
     * Get the corrupted version of a block.
     */
    private BlockState getCorruptedBlock(BlockState originalState, float corruption) {
        // For now, transform blocks based on their types
        // This would be expanded with custom corrupted blocks
        
        if (originalState.is(net.minecraft.world.level.block.Blocks.STONE)) {
            return net.minecraft.world.level.block.Blocks.COBBLESTONE.defaultBlockState();
        }
        
        if (originalState.is(net.minecraft.world.level.block.Blocks.GRASS_BLOCK)) {
            return net.minecraft.world.level.block.Blocks.DIRT.defaultBlockState();
        }
        
        if (originalState.is(net.minecraft.world.level.block.Blocks.DIRT)) {
            return net.minecraft.world.level.block.Blocks.COARSE_DIRT.defaultBlockState();
        }
        
        if (originalState.is(net.minecraft.world.level.block.Blocks.SAND)) {
            return net.minecraft.world.level.block.Blocks.SOUL_SAND.defaultBlockState();
        }
        
        if (corruption > 0.8f && originalState.is(net.minecraft.world.level.block.Blocks.COBBLESTONE)) {
            return net.minecraft.world.level.block.Blocks.OBSIDIAN.defaultBlockState();
        }
        
        // Return original if no specific transformation
        return originalState;
    }
    
    /**
     * Handle player tick to apply corruption effects.
     */
    private void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END || !(event.player instanceof Player player)) {
            return;
        }
        
        // Check if player has corruption capability
        player.getCapability(CapabilityHandler.VOID_CORRUPTION).ifPresent(corruption -> {
            float level = corruption.getCorruptionLevel();
            
            // Apply effects based on corruption level
            if (level > 0.25f) {
                // Minor visual effects
                if (player.level().getRandom().nextFloat() < level * 0.1f) {
                    // Visual effect code would go here
                }
            }
            
            if (level > 0.5f) {
                // Moderate negative effects
                if (player.level().getRandom().nextFloat() < level * 0.05f) {
                    player.addEffect(new net.minecraft.world.effect.MobEffectInstance(
                            net.minecraft.world.effect.MobEffects.CONFUSION, 100, 0));
                }
            }
            
            if (level > 0.75f) {
                // Major negative effects
                if (player.level().getRandom().nextFloat() < level * 0.02f) {
                    player.addEffect(new net.minecraft.world.effect.MobEffectInstance(
                            net.minecraft.world.effect.MobEffects.WITHER, 60, 0));
                }
            }
            
            // Slowly decay corruption over time if not in corrupted area
            ChunkPos playerChunk = new ChunkPos(player.blockPosition());
            Map<ChunkPos, Float> corruptionInWorld = chunkCorruptionLevels.getOrDefault(player.level(), Collections.emptyMap());
            float environmentCorruption = corruptionInWorld.getOrDefault(playerChunk, 0.0f);
            
            if (environmentCorruption < level && player.tickCount % 100 == 0) {
                corruption.removeCorruption(0.01f);
            }
        });
    }
    
    /**
     * Sync corruption data when player logs in.
     */
    private void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        // Sync corruption data to client
        if (event.getEntity() instanceof Player player) {
            player.getCapability(CapabilityHandler.VOID_CORRUPTION).ifPresent(corruption -> {
                // Send sync packet
                // Would be implemented with PacketHandler
            });
        }
    }
    
    /**
     * Add a corruption center at a position.
     * 
     * @param level The level
     * @param pos The position
     * @param strength The corruption strength
     */
    public void addCorruptionCenter(Level level, BlockPos pos, float strength) {
        ChunkPos chunkPos = new ChunkPos(pos);
        Map<ChunkPos, Float> centersInWorld = corruptionCenters.computeIfAbsent(level, k -> new HashMap<>());
        
        // Update existing center or add new one
        centersInWorld.compute(chunkPos, (k, v) -> v == null ? strength : Math.max(v, strength));
        
        LOGGER.debug("Added corruption center at {} with strength {}", pos, strength);
    }
    
    /**
     * Get the corruption level at a position.
     * 
     * @param level The level
     * @param pos The position
     * @return The corruption level (0.0-1.0)
     */
    public float getCorruptionLevel(Level level, BlockPos pos) {
        ChunkPos chunkPos = new ChunkPos(pos);
        Map<ChunkPos, Float> corruptionInWorld = chunkCorruptionLevels.getOrDefault(level, Collections.emptyMap());
        return corruptionInWorld.getOrDefault(chunkPos, 0.0f);
    }
}
