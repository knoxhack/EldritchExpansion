package com.eldritchvoid.modules.voidalchemy;

import com.eldritchvoid.EldritchVoid;
import com.eldritchvoid.core.registry.ModuleRegistry;
import com.eldritchvoid.core.registry.Registration;
import com.eldritchvoid.modules.voidalchemy.FluidPropertiesHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FlowingFluid;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.common.SoundActions;
// Using our custom BaseFlowingFluid implementation instead of NeoForge's
import net.neoforged.neoforge.fluids.BaseFlowingFluid;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.registries.DeferredHolder;

import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Fluids for the Void Alchemy module.
 */
public class VoidAlchemyFluids {
    private final String moduleName;
    private final ModuleRegistry<Fluid> fluidRegistry;
    private final ModuleRegistry<Item> itemRegistry;
    
    // In NeoForge 1.21.5, the deferred holder typing has changed
    // Void Essence fluid
    public final DeferredHolder<Fluid, Fluid> VOID_ESSENCE_SOURCE;
    public final DeferredHolder<Fluid, Fluid> VOID_ESSENCE_FLOWING;
    public final DeferredHolder<Item, Item> VOID_ESSENCE_BUCKET;
    
    // Void Pee fluid (humorous alchemical reagent)
    public final DeferredHolder<Fluid, Fluid> VOID_PEE_SOURCE;
    public final DeferredHolder<Fluid, Fluid> VOID_PEE_FLOWING;
    public final DeferredHolder<Item, Item> VOID_PEE_BUCKET;
    
    /**
     * Create a new fluids component for the Void Alchemy module.
     *
     * @param moduleName The name of the module
     */
    public VoidAlchemyFluids(String moduleName) {
        this.moduleName = moduleName;
        // In NeoForge 1.21.5, registry keys have changed
        this.fluidRegistry = Registration.getOrCreateModuleRegistry(moduleName, "Fluids", net.minecraft.core.registries.BuiltInRegistries.FLUID);
        this.itemRegistry = Registration.getItemRegistry(moduleName);
        
        // In NeoForge 1.21.5, fluid registration patterns have changed
        
        // ===== VOID ESSENCE FLUID =====
        // First create the fluid type
        final FluidType voidEssenceType = createVoidEssenceFluidType();
        
        // Then create the properties (initially with null references)
        // Properties need to be final for lambda usage
        final BaseFlowingFluid.Properties initialVoidEssenceProperties = new BaseFlowingFluid.Properties(
            () -> voidEssenceType,
            () -> null, // These will be updated later
            () -> null
        );
        
        // Register the source and flowing fluids using the initial properties
        VOID_ESSENCE_SOURCE = fluidRegistry.register(moduleName, "void_essence", 
            () -> new BaseFlowingFluid.Source(initialVoidEssenceProperties));
        
        VOID_ESSENCE_FLOWING = fluidRegistry.register(moduleName, "flowing_void_essence", 
            () -> new BaseFlowingFluid.Flowing(initialVoidEssenceProperties));
        
        // Create and register the bucket item
        // In NeoForge 1.21.5, BucketItem constructor has changed
        java.util.function.Supplier<Item> voidEssenceBucketSupplier = () -> {
            // BucketItem needs the actual fluid, not a supplier
            // But we need to delay getting the fluid until registration is complete
            return new VoidBucketItem(
                VOID_ESSENCE_SOURCE,
                new Item.Properties().craftRemainder(Items.BUCKET).stacksTo(1)
            );
        };
        
        VOID_ESSENCE_BUCKET = itemRegistry.register(moduleName, "void_essence_bucket", 
            voidEssenceBucketSupplier);
        
        // Create the final properties configuration 
        // Create a new instance rather than modifying the initial properties
        final BaseFlowingFluid.Properties finalVoidEssenceProperties = new BaseFlowingFluid.Properties(
            () -> voidEssenceType,
            () -> VOID_ESSENCE_SOURCE.get(),
            () -> VOID_ESSENCE_FLOWING.get()
        )
        .bucket(() -> VOID_ESSENCE_BUCKET.get());
        
        // Update the fluid references with the final properties using our helper
        FluidPropertiesHelper.updateFluidProperties((FlowingFluid)VOID_ESSENCE_SOURCE.get(), finalVoidEssenceProperties);
        FluidPropertiesHelper.updateFluidProperties((FlowingFluid)VOID_ESSENCE_FLOWING.get(), finalVoidEssenceProperties);
        
        // ===== VOID PEE FLUID =====
        // Create a fluid type for the humorous alchemical reagent
        final FluidType voidPeeType = createVoidPeeFluidType();
        
        // Create the properties (initially with null references)
        // Properties need to be final for lambda usage
        final BaseFlowingFluid.Properties initialVoidPeeProperties = new BaseFlowingFluid.Properties(
            () -> voidPeeType,
            () -> null,
            () -> null
        );
        
        // Register the source and flowing fluids using the initial properties
        VOID_PEE_SOURCE = fluidRegistry.register(moduleName, "void_pee", 
            () -> new BaseFlowingFluid.Source(initialVoidPeeProperties));
        
        VOID_PEE_FLOWING = fluidRegistry.register(moduleName, "flowing_void_pee", 
            () -> new BaseFlowingFluid.Flowing(initialVoidPeeProperties));
        
        // Create and register the bucket item
        // Same pattern for Void Pee bucket item
        java.util.function.Supplier<Item> voidPeeBucketSupplier = () -> {
            // BucketItem needs the actual fluid, not a supplier
            // But we need to delay getting the fluid until registration is complete
            return new VoidBucketItem(
                VOID_PEE_SOURCE,
                new Item.Properties().craftRemainder(Items.BUCKET).stacksTo(1)
            );
        };
        
        VOID_PEE_BUCKET = itemRegistry.register(moduleName, "void_pee_bucket", 
            voidPeeBucketSupplier);
        
        // Create the final properties configuration
        // Create a new instance rather than modifying the initial properties
        final BaseFlowingFluid.Properties finalVoidPeeProperties = new BaseFlowingFluid.Properties(
            () -> voidPeeType,
            () -> VOID_PEE_SOURCE.get(),
            () -> VOID_PEE_FLOWING.get()
        )
        .bucket(() -> VOID_PEE_BUCKET.get());
        
        // Update the fluid references with the final properties using our helper
        FluidPropertiesHelper.updateFluidProperties((FlowingFluid)VOID_PEE_SOURCE.get(), finalVoidPeeProperties);
        FluidPropertiesHelper.updateFluidProperties((FlowingFluid)VOID_PEE_FLOWING.get(), finalVoidPeeProperties);
        
        EldritchVoid.LOGGER.info("Registered Void Alchemy fluids");
    }
    
    /**
     * Create a fluid type for Void Essence.
     *
     * @return The fluid type
     */
    private FluidType createVoidEssenceFluidType() {
        // In NeoForge 1.21.5, FluidType.Properties.create() has been replaced
        // For compatibility, let's create our fluid type with the new pattern
        FluidType.Properties properties = FluidType.Properties.create();
        properties.descriptionId("fluid.eldritchvoid.void_essence");
        properties.canSwim(false);
        properties.canDrown(true);
        properties.canPushEntity(true);
        properties.supportsBoating(false);
        properties.sound(SoundActions.BUCKET_FILL, SoundEvents.BUCKET_FILL);
        properties.sound(SoundActions.BUCKET_EMPTY, SoundEvents.BUCKET_EMPTY);
        
        return new FluidType(properties) {
            // NeoForge 1.21.5: initializeClient is not an override
            public void initializeClient(Consumer<IClientFluidTypeExtensions> consumer) {
                consumer.accept(new IClientFluidTypeExtensions() {
                    @Override
                    public int getTintColor() {
                        return 0xFF000033; // Very dark purple-blue
                    }
                    
                    @Override
                    public ResourceLocation getStillTexture() {
                        return new ResourceLocation("minecraft:block/water_still");
                    }
                    
                    @Override
                    public ResourceLocation getFlowingTexture() {
                        return new ResourceLocation("minecraft:block/water_flow");
                    }
                });
            }
        };
    }
    
    /**
     * Create a fluid type for Void Pee.
     *
     * @return The fluid type
     */
    private FluidType createVoidPeeFluidType() {
        // Same pattern as void essence fluid type for NeoForge 1.21.5
        FluidType.Properties properties = FluidType.Properties.create();
        properties.descriptionId("fluid.eldritchvoid.void_pee");
        properties.canSwim(false);
        properties.canDrown(true);
        properties.canPushEntity(true);
        properties.supportsBoating(false);
        properties.sound(SoundActions.BUCKET_FILL, SoundEvents.BUCKET_FILL);
        properties.sound(SoundActions.BUCKET_EMPTY, SoundEvents.BUCKET_EMPTY);
        
        return new FluidType(properties) {
            // NeoForge 1.21.5: initializeClient is not an override
            public void initializeClient(Consumer<IClientFluidTypeExtensions> consumer) {
                consumer.accept(new IClientFluidTypeExtensions() {
                    @Override
                    public int getTintColor() {
                        return 0xFFFFBB00; // Yellow-gold color
                    }
                    
                    @Override
                    public ResourceLocation getStillTexture() {
                        return new ResourceLocation("minecraft:block/water_still");
                    }
                    
                    @Override
                    public ResourceLocation getFlowingTexture() {
                        return new ResourceLocation("minecraft:block/water_flow");
                    }
                });
            }
        };
    }
    
    /**
     * Register fluid renderers.
     */
    public void registerRenderers() {
        // In a real implementation, this would register custom renderers for the fluids
        EldritchVoid.LOGGER.info("Registered Void Alchemy fluid renderers");
    }
    
    /**
     * Get the fluid registry.
     *
     * @return The fluid registry
     */
    public ModuleRegistry<Fluid> getFluidRegistry() {
        return fluidRegistry;
    }
}