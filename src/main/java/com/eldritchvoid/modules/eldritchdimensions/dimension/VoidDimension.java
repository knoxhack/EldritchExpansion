package com.eldritchvoid.modules.eldritchdimensions.dimension;

import com.eldritchvoid.EldritchVoid;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BiomeDefaultFeatures;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeGenerationSettings;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.biome.Climate;
import net.minecraft.world.level.biome.FixedBiomeSource;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.dimension.BuiltinDimensionTypes;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.NoiseBasedChunkGenerator;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;
import net.minecraft.world.level.levelgen.RandomState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.data.DatapackBuiltinEntriesProvider;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

/**
 * The Void Realm dimension.
 * A barren dimension with floating islands and eldritch structures.
 */
public class VoidDimension {
    private static final Logger LOGGER = LogManager.getLogger();
    
    // Dimension resources
    public static final ResourceKey<Level> VOID_REALM_KEY = 
            ResourceKey.create(Registries.DIMENSION, new ResourceLocation(EldritchVoid.MOD_ID, "void_realm"));
    
    public static final ResourceLocation VOID_DIMENSION_TYPE_ID = 
            new ResourceLocation(EldritchVoid.MOD_ID, "void_dimension_type");
    
    public static final ResourceKey<DimensionType> VOID_DIMENSION_TYPE = 
            ResourceKey.create(Registries.DIMENSION_TYPE, VOID_DIMENSION_TYPE_ID);
    
    // Biome resources
    public static final ResourceKey<Biome> VOID_WASTES = 
            ResourceKey.create(Registries.BIOME, new ResourceLocation(EldritchVoid.MOD_ID, "void_wastes"));
    
    public static final ResourceKey<Biome> CORRUPTED_ISLANDS = 
            ResourceKey.create(Registries.BIOME, new ResourceLocation(EldritchVoid.MOD_ID, "corrupted_islands"));
    
    public static final ResourceKey<Biome> ELDRITCH_EXPANSE = 
            ResourceKey.create(Registries.BIOME, new ResourceLocation(EldritchVoid.MOD_ID, "eldritch_expanse"));
    
    // Island feature
    private static final DeferredRegister<Feature<?>> FEATURES = 
            DeferredRegister.create(Registries.FEATURE, EldritchVoid.MOD_ID);
    
    /**
     * Register the dimension.
     * 
     * @param eventBus The event bus to register on
     */
    public static void register(IEventBus eventBus) {
        FEATURES.register(eventBus);
        
        // Register the floating island feature
        FEATURES.register("floating_island", () -> new FloatingIslandFeature(NoneFeatureConfiguration.CODEC));
    }
    
    /**
     * Find a safe spawn location in the Void Dimension.
     * 
     * @param level The level
     * @param x The x coordinate
     * @param y The y coordinate
     * @param z The z coordinate
     * @return A safe spawn position
     */
    public static BlockPos findSafeSpawnLocation(LevelReader level, int x, int y, int z) {
        // Find the highest solid block
        BlockPos.MutableBlockPos mutablePos = new BlockPos.MutableBlockPos(x, y, z);
        
        // Start from y and search downward
        for (int searchY = y; searchY >= 0; searchY--) {
            mutablePos.setY(searchY);
            BlockState state = level.getBlockState(mutablePos);
            
            if (!state.isAir() && !state.liquid() && state.isSolid()) {
                // Found solid block, return position above it
                return mutablePos.above().immutable();
            }
        }
        
        // If no solid block found, create a safe platform at y
        return new BlockPos(x, y, z);
    }
    
    /**
     * Bootstrap the dimension type.
     */
    public static void bootstrapDimensionType(BootstapContext<DimensionType> context) {
        context.register(VOID_DIMENSION_TYPE, new DimensionType(
                OptionalLong.of(12000L), // Fixed time
                false, // No skylight
                true, // Has ceiling
                false, // Ultra warm
                false, // Natural
                1.0, // Coordinate scale (1:1 with overworld)
                true, // Bed works
                false, // Respawn anchor works
                -64, // Min Y
                384, // Height
                384, // Logical height
                BlockTags.INFINIBURN_OVERWORLD, // Infiniburn
                VOID_DIMENSION_TYPE_ID, // Effects
                0.1f, // Ambient light
                new DimensionType.MonsterSettings(false, false, UniformInt.of(0, 7), 0)
        ));
    }
    
    /**
     * Bootstrap the biomes.
     */
    public static void bootstrapBiomes(BootstapContext<Biome> context) {
        // Void Wastes biome
        context.register(VOID_WASTES, createVoidWastesBiome());
        
        // Corrupted Islands biome
        context.register(CORRUPTED_ISLANDS, createCorruptedIslandsBiome());
        
        // Eldritch Expanse biome
        context.register(ELDRITCH_EXPANSE, createEldritchExpanseBiome());
    }
    
    /**
     * Create the Void Wastes biome.
     */
    private static Biome createVoidWastesBiome() {
        MobSpawnSettings.Builder spawnBuilder = new MobSpawnSettings.Builder();
        BiomeGenerationSettings.Builder genBuilder = new BiomeGenerationSettings.Builder();
        
        // Add enderman spawns
        spawnBuilder.addSpawn(MobCategory.MONSTER, 
                new MobSpawnSettings.SpawnerData(net.minecraft.world.entity.EntityType.ENDERMAN, 10, 1, 4));
        
        // Minimal features
        BiomeDefaultFeatures.addDefaultCarversAndLakes(genBuilder);
        
        // Add the floating island feature
        genBuilder.addFeature(GenerationStep.Decoration.RAW_GENERATION, 
                PlacedFeatures.FLOATING_ISLAND_PLACED);
        
        return new Biome.BiomeBuilder()
                .precipitation(Biome.Precipitation.NONE)
                .temperature(0.2f)
                .downfall(0.0f)
                .specialEffects(new BiomeSpecialEffects.Builder()
                        .fogColor(0x000000)
                        .waterColor(0x3F3F3F)
                        .waterFogColor(0x050533)
                        .skyColor(0x050505)
                        .build())
                .mobSpawnSettings(spawnBuilder.build())
                .generationSettings(genBuilder.build())
                .build();
    }
    
    /**
     * Create the Corrupted Islands biome.
     */
    private static Biome createCorruptedIslandsBiome() {
        MobSpawnSettings.Builder spawnBuilder = new MobSpawnSettings.Builder();
        BiomeGenerationSettings.Builder genBuilder = new BiomeGenerationSettings.Builder();
        
        // Add enderman and other hostile mob spawns
        spawnBuilder.addSpawn(MobCategory.MONSTER, 
                new MobSpawnSettings.SpawnerData(net.minecraft.world.entity.EntityType.ENDERMAN, 20, 2, 4));
        spawnBuilder.addSpawn(MobCategory.MONSTER, 
                new MobSpawnSettings.SpawnerData(net.minecraft.world.entity.EntityType.PHANTOM, 5, 1, 2));
        
        // More common features
        BiomeDefaultFeatures.addDefaultCarversAndLakes(genBuilder);
        BiomeDefaultFeatures.addDefaultMonsterRoom(genBuilder);
        
        // Add the floating island feature more frequently
        genBuilder.addFeature(GenerationStep.Decoration.RAW_GENERATION, 
                PlacedFeatures.FLOATING_ISLAND_PLACED);
        
        return new Biome.BiomeBuilder()
                .precipitation(Biome.Precipitation.NONE)
                .temperature(0.0f)
                .downfall(0.0f)
                .specialEffects(new BiomeSpecialEffects.Builder()
                        .fogColor(0x050533)
                        .waterColor(0x3F00FF)
                        .waterFogColor(0x050533)
                        .skyColor(0x070033)
                        .build())
                .mobSpawnSettings(spawnBuilder.build())
                .generationSettings(genBuilder.build())
                .build();
    }
    
    /**
     * Create the Eldritch Expanse biome.
     */
    private static Biome createEldritchExpanseBiome() {
        MobSpawnSettings.Builder spawnBuilder = new MobSpawnSettings.Builder();
        BiomeGenerationSettings.Builder genBuilder = new BiomeGenerationSettings.Builder();
        
        // Most dangerous mobs
        spawnBuilder.addSpawn(MobCategory.MONSTER, 
                new MobSpawnSettings.SpawnerData(net.minecraft.world.entity.EntityType.ENDERMAN, 30, 2, 4));
        spawnBuilder.addSpawn(MobCategory.MONSTER, 
                new MobSpawnSettings.SpawnerData(net.minecraft.world.entity.EntityType.PHANTOM, 10, 1, 3));
        
        // Complex features
        BiomeDefaultFeatures.addDefaultCarversAndLakes(genBuilder);
        BiomeDefaultFeatures.addDefaultMonsterRoom(genBuilder);
        BiomeDefaultFeatures.addDefaultUndergroundVariety(genBuilder);
        
        // Add the floating island feature very frequently
        genBuilder.addFeature(GenerationStep.Decoration.RAW_GENERATION, 
                PlacedFeatures.FLOATING_ISLAND_PLACED);
        
        return new Biome.BiomeBuilder()
                .precipitation(Biome.Precipitation.NONE)
                .temperature(-0.5f)
                .downfall(0.0f)
                .specialEffects(new BiomeSpecialEffects.Builder()
                        .fogColor(0x200030)
                        .waterColor(0x4B0082)
                        .waterFogColor(0x200030)
                        .skyColor(0x290033)
                        .build())
                .mobSpawnSettings(spawnBuilder.build())
                .generationSettings(genBuilder.build())
                .build();
    }
    
    /**
     * Feature for generating floating islands in the void dimension.
     */
    public static class FloatingIslandFeature extends Feature<NoneFeatureConfiguration> {
        
        public FloatingIslandFeature(Codec<NoneFeatureConfiguration> codec) {
            super(codec);
        }
        
        @Override
        public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context) {
            BlockPos origin = context.origin();
            ServerLevelAccessor level = context.level();
            RandomSource random = context.random();
            
            // Determine island size and shape
            int radius = random.nextInt(5) + 5; // 5-10 block radius
            int height = random.nextInt(3) + 2; // 2-4 block height
            
            // Generate the main island mass
            for (int x = -radius; x <= radius; x++) {
                for (int z = -radius; z <= radius; z++) {
                    // Create circular island with fuzzy edges
                    double distance = Math.sqrt(x * x + z * z) / radius;
                    if (distance > 1.0) continue; // Outside radius
                    
                    // More likely to place blocks near center
                    if (random.nextDouble() < distance * 1.2 && distance > 0.8) continue;
                    
                    // Determine height based on distance from center
                    int maxY = Math.max(1, (int)(height * (1.0 - distance * 0.8)));
                    
                    for (int y = 0; y < maxY; y++) {
                        BlockPos pos = origin.offset(x, -y, z);
                        
                        // Top layer is grass/dirt, inner is stone, bottom can be special
                        BlockState state;
                        if (y == 0) {
                            state = random.nextFloat() < 0.8 ? 
                                    Blocks.GRASS_BLOCK.defaultBlockState() : 
                                    Blocks.DIRT.defaultBlockState();
                        } else if (y == maxY - 1) {
                            // Bottom layer has a chance for special blocks
                            float chance = random.nextFloat();
                            if (chance < 0.05) {
                                state = Blocks.OBSIDIAN.defaultBlockState();
                            } else if (chance < 0.15) {
                                state = Blocks.DEEPSLATE.defaultBlockState();
                            } else {
                                state = Blocks.STONE.defaultBlockState();
                            }
                        } else {
                            state = Blocks.STONE.defaultBlockState();
                        }
                        
                        level.setBlock(pos, state, 3);
                    }
                }
            }
            
            // Add decorations
            addIslandDecorations(level, origin, radius, random);
            
            return true;
        }
        
        /**
         * Add decorations to the island.
         */
        private void addIslandDecorations(ServerLevelAccessor level, BlockPos origin, int radius, RandomSource random) {
            // Add trees
            int numTrees = random.nextInt(3) + (radius > 7 ? 2 : 0);
            for (int i = 0; i < numTrees; i++) {
                // Find a random position on the island
                int treeX = random.nextInt(radius * 2) - radius;
                int treeZ = random.nextInt(radius * 2) - radius;
                
                if (Math.sqrt(treeX * treeX + treeZ * treeZ) <= radius * 0.8) {
                    BlockPos treePos = origin.offset(treeX, 1, treeZ);
                    
                    // Find the ground level
                    while (!level.getBlockState(treePos.below()).isSolid() && treePos.getY() > origin.getY() - radius) {
                        treePos = treePos.below();
                    }
                    
                    // If on solid ground and has air above, place a tree
                    if (level.getBlockState(treePos.below()).isSolid() && level.getBlockState(treePos).isAir()) {
                        // Chance for different tree types
                        float treeType = random.nextFloat();
                        if (treeType < 0.4) {
                            // Twisted dead tree with no leaves
                            generateDeadTree(level, treePos, random);
                        } else {
                            // Regular dark oak
                            generateSmallTree(level, treePos, random);
                        }
                    }
                }
            }
            
            // Add some small features like flowers, mushrooms, or rocks
            int numFeatures = random.nextInt(8) + 4;
            for (int i = 0; i < numFeatures; i++) {
                int featureX = random.nextInt(radius * 2) - radius;
                int featureZ = random.nextInt(radius * 2) - radius;
                
                if (Math.sqrt(featureX * featureX + featureZ * featureZ) <= radius * 0.9) {
                    BlockPos featurePos = origin.offset(featureX, 1, featureZ);
                    
                    // Find the ground level
                    while (!level.getBlockState(featurePos.below()).isSolid() && featurePos.getY() > origin.getY() - radius) {
                        featurePos = featurePos.below();
                    }
                    
                    // If on solid ground and has air above, place a feature
                    if (level.getBlockState(featurePos.below()).isSolid() && level.getBlockState(featurePos).isAir()) {
                        float featureType = random.nextFloat();
                        
                        if (featureType < 0.3) {
                            // Mushroom
                            level.setBlock(featurePos, random.nextBoolean() ? 
                                    Blocks.RED_MUSHROOM.defaultBlockState() : 
                                    Blocks.BROWN_MUSHROOM.defaultBlockState(), 3);
                        } else if (featureType < 0.6) {
                            // Rock
                            level.setBlock(featurePos, random.nextBoolean() ? 
                                    Blocks.COBBLESTONE.defaultBlockState() : 
                                    Blocks.MOSSY_COBBLESTONE.defaultBlockState(), 3);
                        } else if (featureType < 0.9) {
                            // Tall grass or dead bush
                            level.setBlock(featurePos, random.nextBoolean() ? 
                                    Blocks.GRASS.defaultBlockState() : 
                                    Blocks.DEAD_BUSH.defaultBlockState(), 3);
                        } else {
                            // Rare obsidian or amethyst
                            level.setBlock(featurePos, random.nextBoolean() ? 
                                    Blocks.OBSIDIAN.defaultBlockState() : 
                                    Blocks.AMETHYST_CLUSTER.defaultBlockState(), 3);
                        }
                    }
                }
            }
        }
        
        /**
         * Generate a small tree.
         */
        private void generateSmallTree(ServerLevelAccessor level, BlockPos pos, RandomSource random) {
            // Simple tree with trunk and leaves
            int height = random.nextInt(3) + 3;
            
            // Trunk
            for (int y = 0; y < height; y++) {
                level.setBlock(pos.above(y), Blocks.DARK_OAK_LOG.defaultBlockState(), 3);
            }
            
            // Leaves
            for (int x = -2; x <= 2; x++) {
                for (int z = -2; z <= 2; z++) {
                    for (int y = height - 2; y <= height + 1; y++) {
                        if (y == height + 1 && (Math.abs(x) > 1 || Math.abs(z) > 1)) continue;
                        
                        BlockPos leafPos = pos.offset(x, y, z);
                        if (Math.abs(x) == 2 && Math.abs(z) == 2) continue;
                        
                        if (level.getBlockState(leafPos).isAir()) {
                            level.setBlock(leafPos, Blocks.DARK_OAK_LEAVES.defaultBlockState(), 3);
                        }
                    }
                }
            }
        }
        
        /**
         * Generate a dead twisted tree.
         */
        private void generateDeadTree(ServerLevelAccessor level, BlockPos pos, RandomSource random) {
            // Main trunk
            int height = random.nextInt(4) + 4;
            
            for (int y = 0; y < height; y++) {
                level.setBlock(pos.above(y), Blocks.DARK_OAK_LOG.defaultBlockState(), 3);
            }
            
            // Add some twisted branches
            int numBranches = random.nextInt(3) + 1;
            for (int i = 0; i < numBranches; i++) {
                int branchY = random.nextInt(height - 2) + 2;
                int branchX = random.nextBoolean() ? 1 : -1;
                int branchZ = random.nextBoolean() ? 1 : -1;
                
                int branchLength = random.nextInt(2) + 2;
                
                BlockPos branchPos = pos.above(branchY);
                for (int j = 0; j < branchLength; j++) {
                    if (j > 0) {
                        branchY += random.nextInt(2) * (random.nextBoolean() ? 1 : -1);
                    }
                    
                    branchPos = branchPos.offset(j == 0 ? branchX : 0, j == 0 ? 0 : branchY, j == 0 ? branchZ : 0);
                    
                    if (level.getBlockState(branchPos).isAir()) {
                        level.setBlock(branchPos, Blocks.DARK_OAK_LOG.defaultBlockState(), 3);
                    }
                }
            }
        }
    }
}
