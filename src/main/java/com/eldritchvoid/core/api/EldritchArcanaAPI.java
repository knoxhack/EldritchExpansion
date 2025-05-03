package com.eldritchvoid.core.api;

import net.minecraft.resources.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * API for interacting with the Eldritch Arcana module.
 */
public class EldritchArcanaAPI {
    private static final Logger LOGGER = LogManager.getLogger();
    private static EldritchArcanaAPI instance;
    
    private final Map<ResourceLocation, ResearchEntry> researchEntries = new HashMap<>();
    private final Set<ResourceLocation> researchCategories = new HashSet<>();
    
    /**
     * Private constructor to enforce singleton pattern.
     */
    private EldritchArcanaAPI() {
        // Initialize with default values
    }
    
    /**
     * Get the API instance.
     * 
     * @return The API instance
     */
    public static EldritchArcanaAPI getInstance() {
        if (instance == null) {
            instance = new EldritchArcanaAPI();
        }
        return instance;
    }
    
    /**
     * Registers a research category.
     * 
     * @param category The category ID
     */
    public void registerResearchCategory(ResourceLocation category) {
        researchCategories.add(category);
        LOGGER.debug("Registered research category: {}", category);
    }
    
    /**
     * Registers a research entry.
     * 
     * @param id The research ID
     * @param category The category this research belongs to
     * @param name The display name
     * @param description The description
     * @param icon The icon to display
     * @param x The x position in the research GUI
     * @param y The y position in the research GUI
     * @param prerequisites The prerequisite research IDs
     */
    public void registerResearch(ResourceLocation id, ResourceLocation category, String name, String description, 
                                 ResourceLocation icon, int x, int y, ResourceLocation... prerequisites) {
        if (!researchCategories.contains(category)) {
            LOGGER.warn("Attempted to register research {} in non-existent category {}", id, category);
            return;
        }
        
        ResearchEntry entry = new ResearchEntry(id, category, name, description, icon, x, y, prerequisites);
        researchEntries.put(id, entry);
        LOGGER.debug("Registered research: {}", id);
    }
    
    /**
     * Gets a research entry by ID.
     * 
     * @param id The research ID
     * @return The research entry, or null if not found
     */
    public ResearchEntry getResearch(ResourceLocation id) {
        return researchEntries.get(id);
    }
    
    /**
     * Gets all research entries in a category.
     * 
     * @param category The category ID
     * @return Set of research entries in the category
     */
    public Set<ResearchEntry> getResearchInCategory(ResourceLocation category) {
        Set<ResearchEntry> entries = new HashSet<>();
        
        for (ResearchEntry entry : researchEntries.values()) {
            if (entry.category.equals(category)) {
                entries.add(entry);
            }
        }
        
        return entries;
    }
    
    /**
     * Class representing a research entry.
     */
    public static class ResearchEntry {
        private final ResourceLocation id;
        private final ResourceLocation category;
        private final String name;
        private final String description;
        private final ResourceLocation icon;
        private final int x, y;
        private final ResourceLocation[] prerequisites;
        
        public ResearchEntry(ResourceLocation id, ResourceLocation category, String name, String description, 
                             ResourceLocation icon, int x, int y, ResourceLocation... prerequisites) {
            this.id = id;
            this.category = category;
            this.name = name;
            this.description = description;
            this.icon = icon;
            this.x = x;
            this.y = y;
            this.prerequisites = prerequisites;
        }
        
        public ResourceLocation getId() {
            return id;
        }
        
        public ResourceLocation getCategory() {
            return category;
        }
        
        public String getName() {
            return name;
        }
        
        public String getDescription() {
            return description;
        }
        
        public ResourceLocation getIcon() {
            return icon;
        }
        
        public int getX() {
            return x;
        }
        
        public int getY() {
            return y;
        }
        
        public ResourceLocation[] getPrerequisites() {
            return prerequisites;
        }
    }
}
