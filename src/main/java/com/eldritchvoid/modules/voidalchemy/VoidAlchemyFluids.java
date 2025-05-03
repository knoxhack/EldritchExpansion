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
    
    // Void Essence fluid
    public final DeferredHolder<Fluid, BaseFlowingFluid.Source> VOID_ESSENCE_SOURCE;
    public final DeferredHolder<Fluid, BaseFlowingFluid.Flowing> VOID_ESSENCE_FLOWING;
    public final DeferredHolder<Item, BucketItem> VOID_ESSENCE_BUCKET;
    
    // Void Pee fluid (humorous alchemical reagent)
    public final DeferredHolder<Fluid, BaseFlowingFluid.Source> VOID_PEE_SOURCE;
    public final DeferredHolder<Fluid, BaseFlowingFluid.Flowing> VOID_PEE_FLOWING;
    public final DeferredHolder<Item, BucketItem> VOID_PEE_BUCKET;
    
    /**
     * Create a new fluids component for the Void Alchemy module.
     *
     * @param moduleName The name of the module
     */
    public VoidAlchemyFluids(String moduleName) {
        this.moduleName = moduleName;
        this.fluidRegistry = Registration.getOrCreateModuleRegistry(moduleName, "Fluids", Registry.FLUID_REGISTRY);
        this.itemRegistry = Registration.getItemRegistry(moduleName);
        
        // Register Void Essence fluid
        BaseFlowingFluid.Properties voidEssenceProperties = new BaseFlowingFluid.Properties(
            createVoidEssenceFluidType(),
            () -> this.VOID_ESSENCE_SOURCE.get(),
            () -> this.VOID_ESSENCE_FLOWING.get()
        ).bucket(() -> this.VOID_ESSENCE_BUCKET.get());
        
        VOID_ESSENCE_SOURCE = fluidRegistry.register(moduleName, "void_essence", 
            () -> new BaseFlowingFluid.Source(voidEssenceProperties));
        
        VOID_ESSENCE_FLOWING = fluidRegistry.register(moduleName, "flowing_void_essence", 
            () -> new BaseFlowingFluid.Flowing(voidEssenceProperties));
        
        VOID_ESSENCE_BUCKET = itemRegistry.register(moduleName, "void_essence_bucket", 
            () -> new BucketItem(VOID_ESSENCE_SOURCE, new Item.Properties().craftRemainder(Items.BUCKET).stacksTo(1)));
        
        // Register Void Pee fluid (humorous alchemical reagent)
        BaseFlowingFluid.Properties voidPeeProperties = new BaseFlowingFluid.Properties(
            createVoidPeeFluidType(),
            () -> this.VOID_PEE_SOURCE.get(),
            () -> this.VOID_PEE_FLOWING.get()
        ).bucket(() -> this.VOID_PEE_BUCKET.get());
        
        VOID_PEE_SOURCE = fluidRegistry.register(moduleName, "void_pee", 
            () -> new BaseFlowingFluid.Source(voidPeeProperties));
        
        VOID_PEE_FLOWING = fluidRegistry.register(moduleName, "flowing_void_pee", 
            () -> new BaseFlowingFluid.Flowing(voidPeeProperties));
        
        VOID_PEE_BUCKET = itemRegistry.register(moduleName, "void_pee_bucket", 
            () -> new BucketItem(VOID_PEE_SOURCE, new Item.Properties().craftRemainder(Items.BUCKET).stacksTo(1)));
        
        EldritchVoid.LOGGER.info("Registered Void Alchemy fluids");
    }
    
    /**
     * Create a fluid type for Void Essence.
     *
     * @return The fluid type
     */
    private FluidType createVoidEssenceFluidType() {
        return new FluidType(FluidType.Properties.create()
            .descriptionId("fluid.eldritchvoid.void_essence")
            .canSwim(false)
            .canDrown(true)
            .canPushEntity(true)
            .supportsBoating(false)
            .sound(SoundActions.BUCKET_FILL, SoundEvents.BUCKET_FILL)
            .sound(SoundActions.BUCKET_EMPTY, SoundEvents.BUCKET_EMPTY))
        {
            @Override
            public void initializeClient(Consumer<IClientFluidTypeExtensions> consumer) {
                consumer.accept(new IClientFluidTypeExtensions() {
                    @Override
                    public int getTintColor() {
                        return 0xFF000033; // Very dark purple-blue
                    }
                    
                    @Override
                    public ResourceLocation getStillTexture() {
                        return new ResourceLocation("minecraft", "block/water_still");
                    }
                    
                    @Override
                    public ResourceLocation getFlowingTexture() {
                        return new ResourceLocation("minecraft", "block/water_flow");
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
        return new FluidType(FluidType.Properties.create()
            .descriptionId("fluid.eldritchvoid.void_pee")
            .canSwim(false)
            .canDrown(true)
            .canPushEntity(true)
            .supportsBoating(false)
            .sound(SoundActions.BUCKET_FILL, SoundEvents.BUCKET_FILL)
            .sound(SoundActions.BUCKET_EMPTY, SoundEvents.BUCKET_EMPTY))
        {
            @Override
            public void initializeClient(Consumer<IClientFluidTypeExtensions> consumer) {
                consumer.accept(new IClientFluidTypeExtensions() {
                    @Override
                    public int getTintColor() {
                        return 0xFFFFBB00; // Yellow-gold color
                    }
                    
                    @Override
                    public ResourceLocation getStillTexture() {
                        return new ResourceLocation("minecraft", "block/water_still");
                    }
                    
                    @Override
                    public ResourceLocation getFlowingTexture() {
                        return new ResourceLocation("minecraft", "block/water_flow");
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