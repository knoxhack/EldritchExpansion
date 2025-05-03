package com.eldritchvoid.core.config;

import com.eldritchvoid.EldritchVoid;
import com.google.common.collect.Lists;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.loading.FMLPaths;
import net.neoforged.neoforge.common.ModConfigSpec;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

/**
 * Handles configuration for the Eldritch Void mod.
 */
public class ConfigHandler {
    private static final Logger LOGGER = LogManager.getLogger();
    
    // Config specs
    public static final ModConfigSpec COMMON_CONFIG;
    public static final ModConfigSpec CLIENT_CONFIG;
    public static final ModConfigSpec SERVER_CONFIG;
    
    // Common config
    public static final CommonConfig COMMON;
    
    // Client config
    public static final ClientConfig CLIENT;
    
    // Server config
    public static final ServerConfig SERVER;
    
    // Initialize configs
    static {
        Pair<CommonConfig, ModConfigSpec> commonPair = new ModConfigSpec.Builder().configure(CommonConfig::new);
        COMMON = commonPair.getLeft();
        COMMON_CONFIG = commonPair.getRight();
        
        Pair<ClientConfig, ModConfigSpec> clientPair = new ModConfigSpec.Builder().configure(ClientConfig::new);
        CLIENT = clientPair.getLeft();
        CLIENT_CONFIG = clientPair.getRight();
        
        Pair<ServerConfig, ModConfigSpec> serverPair = new ModConfigSpec.Builder().configure(ServerConfig::new);
        SERVER = serverPair.getLeft();
        SERVER_CONFIG = serverPair.getRight();
    }
    
    /**
     * Register all configuration handlers.
     */
    public static void init() {
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, COMMON_CONFIG);
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, CLIENT_CONFIG);
        ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, SERVER_CONFIG);
        
        // Create the config directory if it doesn't exist
        Path configDir = FMLPaths.CONFIGDIR.get().resolve(EldritchVoid.MOD_ID);
        try {
            if (!Files.exists(configDir)) {
                Files.createDirectories(configDir);
                LOGGER.info("Created config directory: {}", configDir);
            }
        } catch (Exception e) {
            LOGGER.error("Failed to create config directory: {}", e.getMessage());
        }
    }
    
    /**
     * Load a configuration file.
     * 
     * @param spec The config spec
     * @param configPath The path to the config file
     */
    public static void loadConfig(ModConfigSpec spec, String configPath) {
        LOGGER.info("Loading config file: {}", configPath);
        
        final Path path = Paths.get(configPath);
        
        try {
            if (!Files.exists(path)) {
                // Create default config if it doesn't exist
                spec.acceptDefaults();
                spec.save();
            }
        } catch (Exception e) {
            LOGGER.error("Error loading config file {}: {}", configPath, e.getMessage());
        }
    }
    
    /**
     * Common configuration settings.
     */
    public static class CommonConfig {
        // General settings
        public final ModConfigSpec.BooleanValue debugMode;
        
        // Module settings
        public final ModConfigSpec.ConfigValue<List<? extends String>> enabledModules;
        
        public CommonConfig(ModConfigSpec.Builder builder) {
            builder.comment("Common configuration settings for Eldritch Void")
                   .push("general");
            
            debugMode = builder
                    .comment("Enable debug mode")
                    .define("debugMode", false);
            
            builder.pop();
            
            builder.comment("Module settings")
                   .push("modules");
            
            enabledModules = builder
                    .comment("List of enabled modules (disabling core modules may cause issues)")
                    .defineList("enabledModules", 
                            Lists.newArrayList(
                                "voidalchemy",
                                "obsidianforgemaster",
                                "eldritchartifacts",
                                "voidcorruption",
                                "eldritcharcana",
                                "obsidianconstructs",
                                "eldritchdimensions",
                                "voidtech",
                                "eldritchbestiary",
                                "voidcultists"
                            ), s -> s instanceof String);
            
            builder.pop();
        }
    }
    
    /**
     * Client-side configuration settings.
     */
    public static class ClientConfig {
        // Visual settings
        public final ModConfigSpec.BooleanValue fancyEffects;
        public final ModConfigSpec.IntValue particleDensity;
        
        // UI settings
        public final ModConfigSpec.BooleanValue showResearchNotifications;
        
        public ClientConfig(ModConfigSpec.Builder builder) {
            builder.comment("Client configuration settings for Eldritch Void")
                   .push("visual");
            
            fancyEffects = builder
                    .comment("Enable fancy visual effects")
                    .define("fancyEffects", true);
            
            particleDensity = builder
                    .comment("Particle effect density (0-100)")
                    .defineInRange("particleDensity", 50, 0, 100);
            
            builder.pop();
            
            builder.comment("UI settings")
                   .push("ui");
            
            showResearchNotifications = builder
                    .comment("Show notifications for new research")
                    .define("showResearchNotifications", true);
            
            builder.pop();
        }
    }
    
    /**
     * Server-side configuration settings.
     */
    public static class ServerConfig {
        // World generation settings
        public final ModConfigSpec.BooleanValue generateShrines;
        public final ModConfigSpec.IntValue shrineRarity;
        
        // Gameplay settings
        public final ModConfigSpec.DoubleValue voidCorruptionSpreadRate;
        public final ModConfigSpec.BooleanValue hardcoreResearch;
        
        public ServerConfig(ModConfigSpec.Builder builder) {
            builder.comment("Server configuration settings for Eldritch Void")
                   .push("worldgen");
            
            generateShrines = builder
                    .comment("Generate Eldritch Shrines in the world")
                    .define("generateShrines", true);
            
            shrineRarity = builder
                    .comment("Rarity of Eldritch Shrines (higher = more rare)")
                    .defineInRange("shrineRarity", 25, 1, 100);
            
            builder.pop();
            
            builder.comment("Gameplay settings")
                   .push("gameplay");
            
            voidCorruptionSpreadRate = builder
                    .comment("Rate at which Void Corruption spreads (0.0-1.0)")
                    .defineInRange("voidCorruptionSpreadRate", 0.05, 0.0, 1.0);
            
            hardcoreResearch = builder
                    .comment("Enable hardcore research mode (research requirements are more difficult)")
                    .define("hardcoreResearch", false);
            
            builder.pop();
        }
    }
}
