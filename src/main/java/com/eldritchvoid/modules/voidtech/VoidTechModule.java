package com.eldritchvoid.modules.voidtech;

import com.eldritchvoid.EldritchVoid;
import com.eldritchvoid.core.IEldritchModule;
import com.eldritchvoid.core.registry.ModBlocks;
import com.eldritchvoid.modules.voidtech.energy.VoidEnergyStorage;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.event.lifecycle.InterModProcessEvent;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.common.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.energy.IEnergyStorage;
import net.neoforged.neoforge.event.TickEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The Void Tech module for Eldritch Void.
 * Handles energy systems and machinery.
 */
public class VoidTechModule implements IEldritchModule {
    private static final Logger LOGGER = LogManager.getLogger();
    private final IEventBus modEventBus;
    
    // Energy network mapping - tracks connected machines
    private final Map<Level, Map<BlockPos, List<BlockPos>>> energyNetworks = new HashMap<>();
    
    public VoidTechModule(IEventBus modEventBus) {
        this.modEventBus = modEventBus;
    }
    
    @Override
    public String getModuleId() {
        return "voidtech";
    }
    
    @Override
    public String getDisplayName() {
        return "Void Tech";
    }
    
    @Override
    public String getDescription() {
        return "Harness void energy to power machinery";
    }
    
    @Override
    public List<String> getDependencies() {
        return Collections.emptyList(); // No dependencies
    }
    
    @Override
    public void initialize() {
        LOGGER.info("Initializing Void Tech Module");
        
        // Register event handlers
        modEventBus.addListener(this::onCommonSetup);
        modEventBus.addListener(this::onClientSetup);
        modEventBus.addListener(this::registerCapabilities);
        
        // Register world events
        NeoForge.EVENT_BUS.addListener(this::onWorldTick);
        NeoForge.EVENT_BUS.addListener(this::onChunkLoad);
        NeoForge.EVENT_BUS.addListener(this::onChunkUnload);
    }
    
    @Override
    public void onCommonSetup() {
        LOGGER.info("Void Tech: Common Setup");
        
        // Initialize energy system
    }
    
    @Override
    public void onClientSetup() {
        LOGGER.info("Void Tech: Client Setup");
        
        // Register client-side rendering for machines
    }
    
    /**
     * Register capabilities.
     */
    private void registerCapabilities(RegisterCapabilitiesEvent event) {
        // Register energy capability for void tech blocks
        // This would be implemented for each block entity
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
     * Handle world tick for energy transfer.
     */
    private void onWorldTick(TickEvent.LevelTickEvent event) {
        if (event.phase != TickEvent.Phase.END || !(event.level instanceof ServerLevel level)) {
            return;
        }
        
        // Only process every 20 ticks (1 second)
        if (level.getGameTime() % 20 != 0) {
            return;
        }
        
        // Get the energy networks for this level
        Map<BlockPos, List<BlockPos>> networks = energyNetworks.computeIfAbsent(level, k -> new HashMap<>());
        
        // Process each network
        for (Map.Entry<BlockPos, List<BlockPos>> entry : networks.entrySet()) {
            BlockPos source = entry.getKey();
            List<BlockPos> targets = entry.getValue();
            
            // Get the source block entity
            BlockEntity sourceEntity = level.getBlockEntity(source);
            if (sourceEntity == null) continue;
            
            // Check if it's a void generator or energy source
            if (sourceEntity.getBlockState().is(ModBlocks.VOID_GENERATOR.get())) {
                // Get energy capability
                IEnergyStorage sourceEnergy = sourceEntity.getCapability(Capabilities.EnergyStorage.BLOCK);
                if (sourceEnergy == null) continue;
                
                // Distribute energy to connected machines
                for (BlockPos targetPos : targets) {
                    BlockEntity targetEntity = level.getBlockEntity(targetPos);
                    if (targetEntity == null) continue;
                    
                    // Get target energy capability
                    IEnergyStorage targetEnergy = targetEntity.getCapability(Capabilities.EnergyStorage.BLOCK);
                    if (targetEnergy == null || !targetEnergy.canReceive()) continue;
                    
                    // Calculate energy to transfer (up to 100 energy per tick)
                    int energyToTransfer = Math.min(100, sourceEnergy.extractEnergy(100, true));
                    
                    // Transfer energy
                    if (energyToTransfer > 0) {
                        int accepted = targetEnergy.receiveEnergy(energyToTransfer, false);
                        sourceEnergy.extractEnergy(accepted, false);
                    }
                }
            }
        }
    }
    
    /**
     * Handle chunk load event.
     */
    private void onChunkLoad(net.neoforged.neoforge.event.level.ChunkEvent.Load event) {
        // Scan chunk for energy blocks and update network
        if (!(event.getLevel() instanceof ServerLevel level)) {
            return;
        }
        
        // This would scan the chunk for machines and add them to the energy network
    }
    
    /**
     * Handle chunk unload event.
     */
    private void onChunkUnload(net.neoforged.neoforge.event.level.ChunkEvent.Unload event) {
        // Remove machines in this chunk from the energy network
        if (!(event.getLevel() instanceof ServerLevel level)) {
            return;
        }
        
        // This would remove machines in the unloaded chunk from the energy network
    }
    
    /**
     * Add a machine to the energy network.
     * 
     * @param level The level
     * @param pos The position
     * @param isSource Whether this is an energy source
     */
    public void addToEnergyNetwork(Level level, BlockPos pos, boolean isSource) {
        Map<BlockPos, List<BlockPos>> networks = energyNetworks.computeIfAbsent(level, k -> new HashMap<>());
        
        if (isSource) {
            // Register as a source
            networks.put(pos, new java.util.ArrayList<>());
            LOGGER.debug("Added energy source at {}", pos);
        } else {
            // Find closest source
            BlockPos closestSource = null;
            double closestDistance = Double.MAX_VALUE;
            
            for (BlockPos source : networks.keySet()) {
                double distance = pos.distSqr(source);
                if (distance < closestDistance && distance <= 256) { // 16 blocks max range
                    closestDistance = distance;
                    closestSource = source;
                }
            }
            
            // Add to closest source's network
            if (closestSource != null) {
                networks.get(closestSource).add(pos);
                LOGGER.debug("Added receiver at {} to source at {}", pos, closestSource);
            }
        }
    }
    
    /**
     * Remove a machine from the energy network.
     * 
     * @param level The level
     * @param pos The position
     */
    public void removeFromEnergyNetwork(Level level, BlockPos pos) {
        Map<BlockPos, List<BlockPos>> networks = energyNetworks.get(level);
        if (networks == null) return;
        
        // Check if it's a source
        if (networks.containsKey(pos)) {
            networks.remove(pos);
            LOGGER.debug("Removed energy source at {}", pos);
        } else {
            // Check if it's a receiver
            for (List<BlockPos> receivers : networks.values()) {
                if (receivers.remove(pos)) {
                    LOGGER.debug("Removed receiver at {}", pos);
                    break;
                }
            }
        }
    }
}
