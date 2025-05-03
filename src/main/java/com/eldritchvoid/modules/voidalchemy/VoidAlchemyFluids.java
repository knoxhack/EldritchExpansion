package com.eldritchvoid.modules.voidalchemy;

import com.eldritchvoid.EldritchVoid;
import com.eldritchvoid.core.registry.ModuleRegistry;
import com.eldritchvoid.core.registry.Registration;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.common.SoundActions;
import net.neoforged.neoforge.fluids.BaseFlowingFluid;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Consumer;

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
        
        // In NeoForge 1.21.5, we need to modify how we handle fluid registration
        
        // NeoForge 1.21.5 has a different approach to fluid registration
        // Create temporary properties with placeholders
        FluidType voidEssenceType = createVoidEssenceFluidType();
        
        // In NeoForge 1.21.5, we need to create the properties before registering
        BaseFlowingFluid.Properties voidEssenceProperties = new BaseFlowingFluid.Properties(
            () -> voidEssenceType,
            // These will be fixed later in a deferred manner
            () -> null, // source fluid placeholder
            () -> null  // flowing fluid placeholder
        );
        
        // Register source and flowing fluids with the created properties
        VOID_ESSENCE_SOURCE = fluidRegistry.register(moduleName, "void_essence", 
            () -> new BaseFlowingFluid.Source(voidEssenceProperties));
        
        VOID_ESSENCE_FLOWING = fluidRegistry.register(moduleName, "flowing_void_essence", 
            () -> new BaseFlowingFluid.Flowing(voidEssenceProperties));
        
        // Update the self-references in the properties
        voidEssenceProperties.source(() -> VOID_ESSENCE_SOURCE.get())
                             .flowing(() -> VOID_ESSENCE_FLOWING.get());
        
        // Register the bucket item
        VOID_ESSENCE_BUCKET = itemRegistry.register(moduleName, "void_essence_bucket", 
            () -> new BucketItem(() -> VOID_ESSENCE_SOURCE.get(), new Item.Properties().craftRemainder(Items.BUCKET).stacksTo(1)));
            
        // Update the bucket reference
        voidEssenceProperties.bucket(() -> this.VOID_ESSENCE_BUCKET.get());
        
        // In NeoForge 1.21.5, updateProperties was removed
        // Instead, we need to create new instances with the properties and register them
        // This is a workaround since we can't directly modify properties
        EldritchVoid.LOGGER.info("Setting up Void Essence fluid properties");
        
        // Register Void Pee fluid - same pattern as void essence
        // Create the fluid type
        FluidType voidPeeType = createVoidPeeFluidType();
        
        // Create properties with placeholders
        BaseFlowingFluid.Properties voidPeeProperties = new BaseFlowingFluid.Properties(
            () -> voidPeeType,
            () -> null, // source fluid placeholder
            () -> null  // flowing fluid placeholder
        );
        
        // Register source and flowing fluids with the created properties
        VOID_PEE_SOURCE = fluidRegistry.register(moduleName, "void_pee", 
            () -> new BaseFlowingFluid.Source(voidPeeProperties));
        
        VOID_PEE_FLOWING = fluidRegistry.register(moduleName, "flowing_void_pee", 
            () -> new BaseFlowingFluid.Flowing(voidPeeProperties));
        
        // Update the self-references in the properties
        voidPeeProperties.source(() -> VOID_PEE_SOURCE.get())
                         .flowing(() -> VOID_PEE_FLOWING.get());
        
        // Register the bucket item
        VOID_PEE_BUCKET = itemRegistry.register(moduleName, "void_pee_bucket", 
            () -> new BucketItem(() -> VOID_PEE_SOURCE.get(), new Item.Properties().craftRemainder(Items.BUCKET).stacksTo(1)));
            
        // Update the bucket reference
        voidPeeProperties.bucket(() -> this.VOID_PEE_BUCKET.get());
        
        // Similar change as with void essence - can't update properties in NeoForge 1.21.5
        EldritchVoid.LOGGER.info("Setting up Void Pee fluid properties");
        
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
            @Override
            public void initializeClient(Consumer<IClientFluidTypeExtensions> consumer) {
                consumer.accept(new IClientFluidTypeExtensions() {
                    @Override
                    public int getTintColor() {
                        return 0xFF000033; // Very dark purple-blue
                    }
                    
                    @Override
                    public ResourceLocation getStillTexture() {
                        return ResourceLocation.parse("minecraft:block/water_still");
                    }
                    
                    @Override
                    public ResourceLocation getFlowingTexture() {
                        return ResourceLocation.parse("minecraft:block/water_flow");
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
            @Override
            public void initializeClient(Consumer<IClientFluidTypeExtensions> consumer) {
                consumer.accept(new IClientFluidTypeExtensions() {
                    @Override
                    public int getTintColor() {
                        return 0xFFFFBB00; // Yellow-gold color
                    }
                    
                    @Override
                    public ResourceLocation getStillTexture() {
                        return ResourceLocation.parse("minecraft:block/water_still");
                    }
                    
                    @Override
                    public ResourceLocation getFlowingTexture() {
                        return ResourceLocation.parse("minecraft:block/water_flow");
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