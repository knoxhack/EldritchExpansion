package com.eldritchvoid.modules.voidalchemy;

import com.eldritchvoid.EldritchVoid;
import com.eldritchvoid.core.registry.Registration;
import com.eldritchvoid.modules.voidalchemy.fluid.VoidEssenceFluid;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.fluids.BaseFlowingFluid;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

/**
 * Registers all fluids for the Void Alchemy module.
 */
public class VoidAlchemyFluids {
    // Create a DeferredRegister for FluidTypes
    public static final DeferredRegister<FluidType> FLUID_TYPES = DeferredRegister.create(
            NeoForgeRegistries.FLUID_TYPES, EldritchVoid.MOD_ID);
    
    // Create a DeferredRegister for Fluids
    public static final DeferredRegister<Fluid> FLUIDS = DeferredRegister.create(
            net.minecraft.core.registries.Registries.FLUID, EldritchVoid.MOD_ID);
    
    // Void Essence Fluid Type
    public static final DeferredHolder<FluidType, FluidType> VOID_ESSENCE_TYPE = FLUID_TYPES.register(
            "void_essence", VoidEssenceFluid.Type::new);
    
    // Void Essence Fluids (Source and Flowing)
    public static final DeferredHolder<Fluid, FlowingFluid> VOID_ESSENCE = FLUIDS.register(
            "void_essence", () -> new BaseFlowingFluid.Source(VoidAlchemyFluids.voidEssenceProperties()));
    
    public static final DeferredHolder<Fluid, FlowingFluid> FLOWING_VOID_ESSENCE = FLUIDS.register(
            "flowing_void_essence", () -> new BaseFlowingFluid.Flowing(VoidAlchemyFluids.voidEssenceProperties()));
    
    // Void Essence Bucket - compatible with NeoForge 1.21.5
    public static final DeferredHolder<Item, Item> VOID_ESSENCE_BUCKET = Registration.ITEMS.register(
            "void_essence_bucket", () -> {
                // Create a supplier that provides the fluid
                java.util.function.Supplier<? extends net.minecraft.world.level.material.Fluid> fluidSupplier = 
                    () -> VOID_ESSENCE.get();
                
                // Create the bucket with the supplier
                return new BucketItem(
                    fluidSupplier,
                    new Item.Properties()
                        .craftRemainder(Items.BUCKET)
                        .stacksTo(1)
                );
            });
    
    /**
     * Properties for the Void Essence fluid.
     */
    private static BaseFlowingFluid.Properties voidEssenceProperties() {
        return new BaseFlowingFluid.Properties(
                VOID_ESSENCE_TYPE,
                VOID_ESSENCE,
                FLOWING_VOID_ESSENCE)
                .bucket(VOID_ESSENCE_BUCKET);
    }
    
    /**
     * Register all fluids.
     */
    public static void register() {
        EldritchVoid.LOGGER.info("Registering Void Alchemy fluids");
        
        // Get the mod event bus
        IEventBus modEventBus = net.neoforged.fml.ModLoadingContext.get().getActiveContainer().getEventBus();
        
        // Register the fluid types and fluids
        FLUID_TYPES.register(modEventBus);
        FLUIDS.register(modEventBus);
    }
}