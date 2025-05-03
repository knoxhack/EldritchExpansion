package com.eldritchvoid.modules.eldritchartifacts.worldgen;

import com.eldritchvoid.EldritchVoid;
import com.eldritchvoid.core.config.ConfigHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraft.world.level.levelgen.placement.*;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;
import net.minecraft.world.level.storage.loot.LootTable;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

/**
 * Generates procedural Eldritch Shrines in the world.
 */
public class ShrineGenerator {
    private static final Logger LOGGER = LogManager.getLogger();
    
    // Register features
    private static final DeferredRegister<Feature<?>> FEATURES = 
            DeferredRegister.create(Registries.FEATURE, EldritchVoid.MOD_ID);
    
    // Register the shrine feature
    public static final DeferredHolder<Feature<?>, Feature<NoneFeatureConfiguration>> ELDRITCH_SHRINE = 
            FEATURES.register("eldritch_shrine", () -> new EldritchShrineFeature(NoneFeatureConfiguration.CODEC));
    
    // Structure templates
    private static final ResourceLocation[] SHRINE_TEMPLATES = {
            new ResourceLocation(EldritchVoid.MOD_ID, "shrines/small_shrine"),
            new ResourceLocation(EldritchVoid.MOD_ID, "shrines/medium_shrine"),
            new ResourceLocation(EldritchVoid.MOD_ID, "shrines/large_shrine")
    };
    
    // Loot table for shrine chests
    public static final ResourceLocation SHRINE_LOOT_TABLE = 
            new ResourceLocation(EldritchVoid.MOD_ID, "chests/eldritch_shrine");
    
    /**
     * Register the shrine generator.
     * 
     * @param eventBus The event bus to register on
     */
    public static void register(IEventBus eventBus) {
        FEATURES.register(eventBus);
    }
    
    /**
     * Feature for generating Eldritch Shrines.
     */
    public static class EldritchShrineFeature extends Feature<NoneFeatureConfiguration> {
        
        public EldritchShrineFeature(com.mojang.serialization.Codec<NoneFeatureConfiguration> codec) {
            super(codec);
        }
        
        @Override
        public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context) {
            WorldGenLevel level = context.level();
            RandomSource random = context.random();
            BlockPos origin = context.origin();
            
            // Check if shrines are enabled in config
            if (!ConfigHandler.SERVER.generateShrines.get()) {
                return false;
            }
            
            // Find surface position
            BlockPos surfacePos = level.getHeightmapPos(Heightmap.Types.WORLD_SURFACE_WG, origin);
            
            // Don't generate in water
            if (level.getBlockState(surfacePos).getBlock() == Blocks.WATER) {
                return false;
            }
            
            // Select a random shrine template
            int templateIndex = random.nextInt(SHRINE_TEMPLATES.length);
            ResourceLocation templateId = SHRINE_TEMPLATES[templateIndex];
            
            try {
                // Get the structure template
                StructureTemplateManager templateManager = level.getLevel().getServer().getStructureManager();
                StructureTemplate template = templateManager.getOrCreate(templateId);
                
                if (template == null) {
                    LOGGER.error("Failed to load shrine template: {}", templateId);
                    return false;
                }
                
                // Ensure there's enough space
                BlockPos size = template.getSize();
                
                // Place the structure
                StructurePlaceSettings placeSettings = new StructurePlaceSettings()
                        .setRandom(random)
                        .setRotation(net.minecraft.world.level.block.Rotation.getRandom(random))
                        .setMirror(random.nextBoolean() ? 
                                net.minecraft.world.level.block.Mirror.FRONT_BACK : 
                                net.minecraft.world.level.block.Mirror.NONE);
                
                BlockPos placementPos = surfacePos.offset(-size.getX() / 2, 0, -size.getZ() / 2);
                
                // Place the template
                template.placeInWorld(level, placementPos, placementPos, placeSettings, random, 2);
                
                // Add loot to chests
                placeLoot(level, placementPos, size, random, template, placeSettings);
                
                LOGGER.debug("Generated Eldritch Shrine at {}", surfacePos);
                return true;
                
            } catch (Exception e) {
                LOGGER.error("Error generating Eldritch Shrine: {}", e.getMessage());
                e.printStackTrace();
                return false;
            }
        }
        
        /**
         * Place loot in chests within the shrine.
         */
        private void placeLoot(WorldGenLevel level, BlockPos pos, BlockPos size, 
                              RandomSource random, StructureTemplate template, 
                              StructurePlaceSettings placeSettings) {
            
            // Find all chest positions in the template
            List<StructureTemplate.StructureBlockInfo> blocks = template.filterBlocks(
                    BlockPos.ZERO, 
                    new StructurePlaceSettings(), 
                    Blocks.CHEST);
            
            for (StructureTemplate.StructureBlockInfo blockInfo : blocks) {
                // Transform the position based on rotation and mirror
                BlockPos chestPos = StructureTemplate.calculateRelativePosition(placeSettings, blockInfo.pos());
                chestPos = chestPos.offset(pos);
                
                // Check if there's a chest at this position
                BlockEntity blockEntity = level.getBlockEntity(chestPos);
                if (blockEntity instanceof ChestBlockEntity chest) {
                    // Set the loot table
                    chest.setLootTable(SHRINE_LOOT_TABLE, random.nextLong());
                }
            }
        }
    }
}
