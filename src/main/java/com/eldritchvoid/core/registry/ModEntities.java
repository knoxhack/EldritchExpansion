package com.eldritchvoid.core.registry;

import com.eldritchvoid.EldritchVoid;
import com.eldritchvoid.modules.eldritchbestiary.entity.EldritchEntity;
import com.eldritchvoid.modules.obsidianconstructs.entity.ConstructEntity;
import com.eldritchvoid.modules.voidcultists.entity.CultistEntity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.neoforge.registries.DeferredHolder;

/**
 * Registers all entities for the Eldritch Void mod.
 */
public class ModEntities {
    // Obsidian Constructs Module
    public static final DeferredHolder<EntityType<?>, EntityType<ConstructEntity>> OBSIDIAN_GOLEM = 
            RegistryHandler.ENTITIES.register("obsidian_golem", 
                    () -> EntityType.Builder.<ConstructEntity>of(ConstructEntity::new, MobCategory.MISC)
                            .sized(1.4F, 2.7F)
                            .clientTrackingRange(10)
                            .build(RegistryHandler.modLoc("obsidian_golem").toString()));
    
    // Eldritch Bestiary Module
    public static final DeferredHolder<EntityType<?>, EntityType<EldritchEntity>> VOID_HORROR = 
            RegistryHandler.ENTITIES.register("void_horror", 
                    () -> EntityType.Builder.<EldritchEntity>of(EldritchEntity::new, MobCategory.MONSTER)
                            .sized(0.6F, 1.95F)
                            .clientTrackingRange(8)
                            .build(RegistryHandler.modLoc("void_horror").toString()));
    
    // Void Cultists Module
    public static final DeferredHolder<EntityType<?>, EntityType<CultistEntity>> VOID_CULTIST = 
            RegistryHandler.ENTITIES.register("void_cultist", 
                    () -> EntityType.Builder.<CultistEntity>of(CultistEntity::new, MobCategory.CREATURE)
                            .sized(0.6F, 1.95F)
                            .clientTrackingRange(8)
                            .build(RegistryHandler.modLoc("void_cultist").toString()));

    /**
     * Register all entities.
     */
    public static void register() {
        EldritchVoid.LOGGER.info("Registering Eldritch Void entities");
    }
}
