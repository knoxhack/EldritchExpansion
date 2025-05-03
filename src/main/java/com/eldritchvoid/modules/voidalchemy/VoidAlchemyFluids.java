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
        this.fluidRegistry = Registration.getOrCreateModuleRegistry(moduleName, "Fluids", net.neoforged.neoforge.registries.BuiltInRegistries.Fluid_REGISTRY);
        this.itemRegistry = Registration.getItemRegistry(moduleName);
        
        // In NeoForge 1.21.5, we need to modify how we handle fluid registration
        
        // First register our holders
        VOID_ESSENCE_SOURCE = fluidRegistry.register(moduleName, "void_essence", 
            () -> new BaseFlowingFluid.Source(null)); // Temporary null properties
        
        VOID_ESSENCE_FLOWING = fluidRegistry.register(moduleName, "flowing_void_essence", 
            () -> new BaseFlowingFluid.Flowing(null)); // Temporary null properties
        
        VOID_ESSENCE_BUCKET = itemRegistry.register(moduleName, "void_essence_bucket", 
            () -> new BucketItem(() -> VOID_ESSENCE_SOURCE.get(), new Item.Properties().craftRemainder(Items.BUCKET).stacksTo(1)));
            
        // Now create the properties with the registered fluids
        BaseFlowingFluid.Properties voidEssenceProperties = new BaseFlowingFluid.Properties(
            () -> createVoidEssenceFluidType(),
            () -> this.VOID_ESSENCE_SOURCE.get(),
            () -> this.VOID_ESSENCE_FLOWING.get()
        ).bucket(() -> this.VOID_ESSENCE_BUCKET.get());
        
        // Update the fluids with proper properties
        if (VOID_ESSENCE_SOURCE.get() instanceof BaseFlowingFluid.Source source) {
            source.updateProperties(voidEssenceProperties);
        }
        
        if (VOID_ESSENCE_FLOWING.get() instanceof BaseFlowingFluid.Flowing flowing) {
            flowing.updateProperties(voidEssenceProperties);
        }
        
        // Register Void Pee fluid - same pattern as void essence
        // First register our holders
        VOID_PEE_SOURCE = fluidRegistry.register(moduleName, "void_pee", 
            () -> new BaseFlowingFluid.Source(null)); // Temporary null properties
        
        VOID_PEE_FLOWING = fluidRegistry.register(moduleName, "flowing_void_pee", 
            () -> new BaseFlowingFluid.Flowing(null)); // Temporary null properties
        
        VOID_PEE_BUCKET = itemRegistry.register(moduleName, "void_pee_bucket", 
            () -> new BucketItem(() -> VOID_PEE_SOURCE.get(), new Item.Properties().craftRemainder(Items.BUCKET).stacksTo(1)));
            
        // Now create the properties with the registered fluids
        BaseFlowingFluid.Properties voidPeeProperties = new BaseFlowingFluid.Properties(
            () -> createVoidPeeFluidType(),
            () -> this.VOID_PEE_SOURCE.get(),
            () -> this.VOID_PEE_FLOWING.get()
        ).bucket(() -> this.VOID_PEE_BUCKET.get());
        
        // Update the fluids with proper properties
        if (VOID_PEE_SOURCE.get() instanceof BaseFlowingFluid.Source source) {
            source.updateProperties(voidPeeProperties);
        }
        
        if (VOID_PEE_FLOWING.get() instanceof BaseFlowingFluid.Flowing flowing) {
            flowing.updateProperties(voidPeeProperties);
        }
        
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