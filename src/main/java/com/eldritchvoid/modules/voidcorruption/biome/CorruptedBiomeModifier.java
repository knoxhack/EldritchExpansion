package com.eldritchvoid.modules.voidcorruption.biome;

import com.eldritchvoid.EldritchVoid;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.Music;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.KeyDispatchDataCodec;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.biome.AmbientAdditionsSettings;
import net.minecraft.world.level.biome.AmbientMoodSettings;
import net.minecraft.world.level.biome.AmbientParticleSettings;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeGenerationSettings;
import net.minecraft.world.level.biome.BiomeSpecialEffects;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.world.BiomeModifier;
import net.neoforged.neoforge.common.world.ModifiableBiomeInfo;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Biome modifier for corrupted biomes.
 * Modifies existing biomes to have void corruption effects.
 */
public class CorruptedBiomeModifier implements BiomeModifier {
    private static final Logger LOGGER = LogManager.getLogger();
    
    // Register biome modifier
    private static final DeferredRegister<Codec<? extends BiomeModifier>> BIOME_MODIFIER_SERIALIZERS = 
            DeferredRegister.create(NeoForgeRegistries.Keys.BIOME_MODIFIER_SERIALIZERS, EldritchVoid.MOD_ID);
    
    // Register the corrupt biome modifier
    public static final ResourceKey<Codec<? extends BiomeModifier>> CORRUPTED_BIOME_MODIFIER = 
            ResourceKey.create(NeoForgeRegistries.Keys.BIOME_MODIFIER_SERIALIZERS, 
                    new ResourceLocation(EldritchVoid.MOD_ID, "corrupted"));
    
    // Corrupted biome sound
    private static final ResourceLocation CORRUPTED_AMBIENT_SOUND = 
            new ResourceLocation(EldritchVoid.MOD_ID, "ambient.corrupted");
    
    // Corrupted biome name
    public static final ResourceLocation CORRUPTED_LANDS = 
            new ResourceLocation(EldritchVoid.MOD_ID, "corrupted_lands");
    
    /**
     * Register the biome modifier.
     * 
     * @param eventBus The event bus to register on
     */
    public static void register(IEventBus eventBus) {
        BIOME_MODIFIER_SERIALIZERS.register(CORRUPTED_BIOME_MODIFIER.location().getPath(), 
                () -> RecordCodecBuilder.create(builder -> builder.group(
                        // Add parameters for the biome modifier
                        Codec.FLOAT.fieldOf("intensity").forGetter(modifier -> ((CorruptedBiomeModifier)modifier).intensity),
                        HolderSet.codec(Registries.BIOME).fieldOf("biomes").forGetter(modifier -> ((CorruptedBiomeModifier)modifier).biomes)
                ).apply(builder, CorruptedBiomeModifier::new)));
        
        BIOME_MODIFIER_SERIALIZERS.register(eventBus);
    }
    
    // Fields
    private final float intensity;
    private final HolderSet<Biome> biomes;
    
    /**
     * Constructor.
     * 
     * @param intensity The corruption intensity
     * @param biomes The biomes to affect
     */
    public CorruptedBiomeModifier(float intensity, HolderSet<Biome> biomes) {
        this.intensity = intensity;
        this.biomes = biomes;
    }
    
    @Override
    public void modify(Holder<Biome> biome, Phase phase, ModifiableBiomeInfo.BiomeInfo.Builder builder) {
        // Only apply in the add phase and to specified biomes
        if (phase != Phase.ADD || !biomes.contains(biome)) {
            return;
        }
        
        try {
            // Modify biome colors
            BiomeSpecialEffects.Builder effectsBuilder = new BiomeSpecialEffects.Builder();
            effectsBuilder.fogColor(0x4B0082) // Dark purple
                         .waterColor(0x3D0042) // Deep violet
                         .waterFogColor(0x2A0038) // Dark violet
                         .skyColor(0x300030) // Dark purple sky
                         .foliageColorOverride(0x6A0DAD) // Royal purple foliage
                         .grassColorOverride(0x4B0082); // Dark purple grass
            
            // Add ambient sound if intensity is high enough
            if (intensity > 0.5f) {
                SoundEvent sound = SoundEvent.createVariableRangeEvent(CORRUPTED_AMBIENT_SOUND);
                effectsBuilder.ambientMoodSound(new AmbientMoodSettings(
                        sound, 6000, 8, 2.0f
                ));
                
                // Add particle effects for high corruption
                if (intensity > 0.8f) {
                    effectsBuilder.ambientParticle(new AmbientParticleSettings(
                            net.minecraft.core.particles.ParticleTypes.PORTAL, 0.005f
                    ));
                }
            }
            
            // Apply special effects
            builder.getSpecialEffects().setFogColor(effectsBuilder.getFogColor());
            builder.getSpecialEffects().setWaterColor(effectsBuilder.getWaterColor());
            builder.getSpecialEffects().setWaterFogColor(effectsBuilder.getWaterFogColor());
            builder.getSpecialEffects().setSkyColor(effectsBuilder.getSkyColor());
            builder.getSpecialEffects().setFoliageColorOverride(effectsBuilder.getFoliageColorOverride());
            builder.getSpecialEffects().setGrassColorOverride(effectsBuilder.getGrassColorOverride());
            
            // Add mob spawns for corrupted entities
            MobSpawnSettings.SpawnerData voidHorrorSpawn = new MobSpawnSettings.SpawnerData(
                    EntityType.ENDERMAN, 10, 1, 3); // Using enderman as placeholder
            
            builder.getMobSpawnSettings().addSpawn(MobCategory.MONSTER, voidHorrorSpawn);
            
            LOGGER.debug("Applied corruption effects to biome {}", biome.unwrapKey().get().location());
            
        } catch (Exception e) {
            LOGGER.error("Error applying biome corruption: {}", e.getMessage());
        }
    }
    
    @Override
    public KeyDispatchDataCodec<? extends BiomeModifier> codec() {
        return KeyDispatchDataCodec.of(BIOME_MODIFIER_SERIALIZERS.getHolder(CORRUPTED_BIOME_MODIFIER).get().get());
    }
}
