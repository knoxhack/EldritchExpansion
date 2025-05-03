package com.eldritchvoid.core.capability;

import com.eldritchvoid.EldritchVoid;
import com.eldritchvoid.core.api.EldritchVoidAPI;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.common.capabilities.Capability;
import net.neoforged.neoforge.common.capabilities.CapabilityManager;
import net.neoforged.neoforge.common.capabilities.CapabilityToken;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Handles capabilities for the Eldritch Void mod.
 */
public class CapabilityHandler {
    private static final Logger LOGGER = LogManager.getLogger();
    
    // Register attachment types
    public static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES = 
            DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, EldritchVoid.MOD_ID);
    
    // Player research capability
    public static final Capability<IPlayerResearch> PLAYER_RESEARCH = CapabilityManager.get(new CapabilityToken<>(){});
    
    // Player void corruption capability
    public static final Capability<IVoidCorruption> VOID_CORRUPTION = CapabilityManager.get(new CapabilityToken<>(){});
    
    // Player void energy capability
    public static final Capability<IVoidEnergy> VOID_ENERGY = CapabilityManager.get(new CapabilityToken<>(){});
    
    /**
     * Initialize capability handler.
     * 
     * @param modBus The mod event bus
     */
    public static void init(IEventBus modBus) {
        LOGGER.info("Initializing capabilities");
        
        // Register attachment types
        ATTACHMENT_TYPES.register(modBus);
        
        // Register capability attached/detached listeners
        NeoForge.EVENT_BUS.addListener(CapabilityHandler::onPlayerCloned);
        NeoForge.EVENT_BUS.addListener(CapabilityHandler::onPlayerLoggedIn);
    }
    
    /**
     * Handle player data cloning (death, dimension change, etc.)
     */
    private static void onPlayerCloned(PlayerEvent.Clone event) {
        if (event.isWasDeath()) {
            // Handle research capability
            if (EldritchVoidAPI.getInstance().isModuleLoaded("eldritcharcana")) {
                Player original = event.getOriginal();
                Player player = event.getEntity();
                
                original.getCapability(PLAYER_RESEARCH).ifPresent(oldResearch -> {
                    player.getCapability(PLAYER_RESEARCH).ifPresent(newResearch -> {
                        newResearch.copyFrom(oldResearch);
                    });
                });
            }
            
            // Handle void corruption capability
            if (EldritchVoidAPI.getInstance().isModuleLoaded("voidcorruption")) {
                Player original = event.getOriginal();
                Player player = event.getEntity();
                
                original.getCapability(VOID_CORRUPTION).ifPresent(oldCorruption -> {
                    player.getCapability(VOID_CORRUPTION).ifPresent(newCorruption -> {
                        newCorruption.copyFrom(oldCorruption);
                    });
                });
            }
        }
    }
    
    /**
     * Handle player login.
     */
    private static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        Player player = event.getEntity();
        
        // Sync research capability to client
        if (EldritchVoidAPI.getInstance().isModuleLoaded("eldritcharcana")) {
            player.getCapability(PLAYER_RESEARCH).ifPresent(research -> {
                // Send research sync packet
            });
        }
        
        // Sync void corruption capability to client
        if (EldritchVoidAPI.getInstance().isModuleLoaded("voidcorruption")) {
            player.getCapability(VOID_CORRUPTION).ifPresent(corruption -> {
                // Send corruption sync packet
            });
        }
    }
    
    /**
     * Interface for player research capability.
     */
    public interface IPlayerResearch {
        /**
         * Check if the player has completed a research.
         * 
         * @param researchId The research ID
         * @return True if the research is complete
         */
        boolean hasResearch(ResourceLocation researchId);
        
        /**
         * Add a completed research.
         * 
         * @param researchId The research ID
         */
        void addResearch(ResourceLocation researchId);
        
        /**
         * Remove a research.
         * 
         * @param researchId The research ID
         */
        void removeResearch(ResourceLocation researchId);
        
        /**
         * Copy research data from another capability.
         * 
         * @param other The other capability
         */
        void copyFrom(IPlayerResearch other);
    }
    
    /**
     * Interface for void corruption capability.
     */
    public interface IVoidCorruption {
        /**
         * Get the current corruption level.
         * 
         * @return The corruption level
         */
        float getCorruptionLevel();
        
        /**
         * Set the corruption level.
         * 
         * @param level The new corruption level
         */
        void setCorruptionLevel(float level);
        
        /**
         * Add corruption.
         * 
         * @param amount The amount to add
         */
        void addCorruption(float amount);
        
        /**
         * Remove corruption.
         * 
         * @param amount The amount to remove
         */
        void removeCorruption(float amount);
        
        /**
         * Copy corruption data from another capability.
         * 
         * @param other The other capability
         */
        void copyFrom(IVoidCorruption other);
    }
    
    /**
     * Interface for void energy capability.
     */
    public interface IVoidEnergy {
        /**
         * Get the current void energy.
         * 
         * @return The void energy
         */
        int getVoidEnergy();
        
        /**
         * Set the void energy.
         * 
         * @param energy The new void energy
         */
        void setVoidEnergy(int energy);
        
        /**
         * Add void energy.
         * 
         * @param amount The amount to add
         * @return The amount that was actually added
         */
        int addVoidEnergy(int amount);
        
        /**
         * Use void energy.
         * 
         * @param amount The amount to use
         * @param simulate If true, only simulate the extraction
         * @return The amount that was actually used
         */
        int useVoidEnergy(int amount, boolean simulate);
        
        /**
         * Get the maximum void energy.
         * 
         * @return The maximum void energy
         */
        int getMaxVoidEnergy();
    }
}
