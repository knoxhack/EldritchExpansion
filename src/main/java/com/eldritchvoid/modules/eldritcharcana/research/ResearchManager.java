package com.eldritchvoid.modules.eldritcharcana.research;

import com.eldritchvoid.EldritchVoid;
import com.eldritchvoid.core.api.EldritchArcanaAPI;
import com.eldritchvoid.core.capability.CapabilityHandler;
import com.eldritchvoid.core.config.ConfigHandler;
import com.eldritchvoid.core.network.PacketHandler;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Manages research for players in the Eldritch Arcana module.
 */
public class ResearchManager {
    private static final Logger LOGGER = LogManager.getLogger();
    
    // Cache for research prerequisites and requirements
    private final Map<ResourceLocation, List<ResearchRequirement>> researchRequirements = new HashMap<>();
    
    /**
     * Constructor for the research manager.
     */
    public ResearchManager() {
        // Register events
        NeoForge.EVENT_BUS.addListener(this::onPlayerAdvancement);
        
        // Initialize research requirements
        initializeResearchRequirements();
    }
    
    /**
     * Initialize research requirements for each research entry.
     */
    private void initializeResearchRequirements() {
        // Define requirements for each research
        
        // Arcane Fundamentals (starter research)
        addResearchRequirement(
                new ResourceLocation(EldritchVoid.MOD_ID, "research/arcane_fundamentals"),
                new ItemResearchRequirement(new ResourceLocation("minecraft:book"), 1)
        );
        
        // Void Attunement
        addResearchRequirement(
                new ResourceLocation(EldritchVoid.MOD_ID, "research/void_attunement"),
                new ItemResearchRequirement(new ResourceLocation("minecraft:ender_pearl"), 4)
        );
        
        // Void Essence
        addResearchRequirement(
                new ResourceLocation(EldritchVoid.MOD_ID, "research/void_essence"),
                new ItemResearchRequirement(new ResourceLocation("minecraft:glass_bottle"), 3),
                new ItemResearchRequirement(new ResourceLocation("minecraft:ender_pearl"), 2)
        );
        
        // Eldritch Knowledge
        addResearchRequirement(
                new ResourceLocation(EldritchVoid.MOD_ID, "research/eldritch_knowledge"),
                new ItemResearchRequirement(new ResourceLocation("minecraft:ender_eye"), 2),
                new ExperienceResearchRequirement(10)
        );
        
        // More research requirements would be defined here
    }
    
    /**
     * Add requirements for a research entry.
     * 
     * @param researchId The research ID
     * @param requirements The research requirements
     */
    private void addResearchRequirement(ResourceLocation researchId, ResearchRequirement... requirements) {
        List<ResearchRequirement> requirementList = new ArrayList<>();
        for (ResearchRequirement requirement : requirements) {
            requirementList.add(requirement);
        }
        
        researchRequirements.put(researchId, requirementList);
    }
    
    /**
     * Check if a player has completed a research.
     * 
     * @param player The player
     * @param researchId The research ID
     * @return True if research is complete
     */
    public boolean hasResearch(Player player, ResourceLocation researchId) {
        return player.getCapability(CapabilityHandler.PLAYER_RESEARCH)
                .map(research -> research.hasResearch(researchId))
                .orElse(false);
    }
    
    /**
     * Attempt to complete a research for a player.
     * 
     * @param player The player
     * @param researchId The research ID
     * @return True if the research was completed
     */
    public boolean completeResearch(Player player, ResourceLocation researchId) {
        // Check if research already completed
        if (hasResearch(player, researchId)) {
            return false;
        }
        
        // Check if research exists
        EldritchArcanaAPI.ResearchEntry entry = EldritchArcanaAPI.getInstance().getResearch(researchId);
        if (entry == null) {
            LOGGER.warn("Attempted to complete non-existent research: {}", researchId);
            return false;
        }
        
        // Check prerequisites
        for (ResourceLocation prerequisite : entry.getPrerequisites()) {
            if (!hasResearch(player, prerequisite)) {
                LOGGER.debug("Player {} is missing prerequisite {} for research {}", 
                        player.getName().getString(), prerequisite, researchId);
                return false;
            }
        }
        
        // Check requirements
        List<ResearchRequirement> requirements = researchRequirements.getOrDefault(researchId, new ArrayList<>());
        
        // If hardcore research mode is enabled, requirements are more difficult
        boolean hardcoreMode = ConfigHandler.SERVER.hardcoreResearch.get();
        
        for (ResearchRequirement requirement : requirements) {
            if (!requirement.check(player, hardcoreMode)) {
                LOGGER.debug("Player {} does not meet requirement {} for research {}", 
                        player.getName().getString(), requirement, researchId);
                return false;
            }
        }
        
        // All checks passed, complete the research
        player.getCapability(CapabilityHandler.PLAYER_RESEARCH).ifPresent(research -> {
            research.addResearch(researchId);
            
            // Consume required items
            for (ResearchRequirement requirement : requirements) {
                requirement.consume(player, hardcoreMode);
            }
            
            // Notify player
            player.displayClientMessage(Component.translatable(
                    "eldritchvoid.research.complete", entry.getName()), false);
            
            // Sync to client
            if (player instanceof ServerPlayer serverPlayer) {
                syncResearchData(serverPlayer);
            }
            
            LOGGER.info("Player {} completed research {}", player.getName().getString(), researchId);
        });
        
        return true;
    }
    
    /**
     * Sync a player's research data to the client.
     * 
     * @param player The player
     */
    public void syncResearchData(Player player) {
        if (player instanceof ServerPlayer serverPlayer) {
            player.getCapability(CapabilityHandler.PLAYER_RESEARCH).ifPresent(research -> {
                // Would send a packet with the research data
                // PacketHandler.sendToPlayer(new ResearchSyncPacket(research), serverPlayer);
            });
        }
    }
    
    /**
     * Handle player advancements for research progression.
     */
    private void onPlayerAdvancement(PlayerEvent.PlayerAdvancementEvent event) {
        // Could trigger research based on advancements
    }
    
    /**
     * Interface for research requirements.
     */
    public interface ResearchRequirement {
        /**
         * Check if the requirement is met.
         * 
         * @param player The player
         * @param hardcoreMode Whether hardcore mode is enabled
         * @return True if the requirement is met
         */
        boolean check(Player player, boolean hardcoreMode);
        
        /**
         * Consume the requirement.
         * 
         * @param player The player
         * @param hardcoreMode Whether hardcore mode is enabled
         */
        void consume(Player player, boolean hardcoreMode);
    }
    
    /**
     * Requirement for having an item.
     */
    public static class ItemResearchRequirement implements ResearchRequirement {
        private final ResourceLocation itemId;
        private final int count;
        
        public ItemResearchRequirement(ResourceLocation itemId, int count) {
            this.itemId = itemId;
            this.count = count;
        }
        
        @Override
        public boolean check(Player player, boolean hardcoreMode) {
            net.minecraft.world.item.Item item = net.minecraft.core.registries.BuiltInRegistries.ITEM.get(itemId);
            int requiredCount = hardcoreMode ? count * 2 : count;
            
            int playerCount = 0;
            for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
                net.minecraft.world.item.ItemStack stack = player.getInventory().getItem(i);
                if (stack.getItem() == item) {
                    playerCount += stack.getCount();
                }
            }
            
            return playerCount >= requiredCount;
        }
        
        @Override
        public void consume(Player player, boolean hardcoreMode) {
            net.minecraft.world.item.Item item = net.minecraft.core.registries.BuiltInRegistries.ITEM.get(itemId);
            int requiredCount = hardcoreMode ? count * 2 : count;
            
            // Only consume in survival mode
            if (player.getAbilities().instabuild) {
                return;
            }
            
            // Find and remove items
            int remaining = requiredCount;
            for (int i = 0; i < player.getInventory().getContainerSize() && remaining > 0; i++) {
                net.minecraft.world.item.ItemStack stack = player.getInventory().getItem(i);
                if (stack.getItem() == item) {
                    int toRemove = Math.min(stack.getCount(), remaining);
                    stack.shrink(toRemove);
                    remaining -= toRemove;
                }
            }
        }
        
        @Override
        public String toString() {
            return "ItemRequirement[" + itemId + " x" + count + "]";
        }
    }
    
    /**
     * Requirement for having experience levels.
     */
    public static class ExperienceResearchRequirement implements ResearchRequirement {
        private final int levels;
        
        public ExperienceResearchRequirement(int levels) {
            this.levels = levels;
        }
        
        @Override
        public boolean check(Player player, boolean hardcoreMode) {
            int requiredLevels = hardcoreMode ? levels * 2 : levels;
            return player.experienceLevel >= requiredLevels;
        }
        
        @Override
        public void consume(Player player, boolean hardcoreMode) {
            int requiredLevels = hardcoreMode ? levels * 2 : levels;
            
            // Only consume in survival mode
            if (player.getAbilities().instabuild) {
                return;
            }
            
            player.giveExperienceLevels(-requiredLevels);
        }
        
        @Override
        public String toString() {
            return "ExperienceRequirement[" + levels + " levels]";
        }
    }
}
