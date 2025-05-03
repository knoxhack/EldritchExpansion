package com.eldritchvoid.core.registry;

import com.eldritchvoid.EldritchVoid;
import com.eldritchvoid.modules.voidalchemy.fluid.VoidEssenceFluid;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.fluids.BaseFlowingFluid;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

/**
 * Registers all fluids for the Eldritch Void mod.
 */
public class ModFluids {
    public static final DeferredRegister<FluidType> FLUID_TYPES = 
            DeferredRegister.create(NeoForgeRegistries.Keys.FLUID_TYPES, EldritchVoid.MOD_ID);
    
    public static final DeferredRegister<Fluid> FLUIDS = 
            DeferredRegister.create(Registries.FLUID, EldritchVoid.MOD_ID);
    
    // Void Alchemy Module - Void Essence Fluid
    public static final DeferredHolder<FluidType, FluidType> VOID_ESSENCE_FLUID_TYPE = FLUID_TYPES.register("void_essence",
            () -> VoidEssenceFluid.createFluidType());
    
    public static final DeferredHolder<Fluid, FlowingFluid> VOID_ESSENCE_FLUID = FLUIDS.register("void_essence",
            () -> new BaseFlowingFluid.Source(ModFluids.voidEssenceProperties()));
    
    public static final DeferredHolder<Fluid, FlowingFluid> FLOWING_VOID_ESSENCE_FLUID = FLUIDS.register("flowing_void_essence",
            () -> new BaseFlowingFluid.Flowing(ModFluids.voidEssenceProperties()));
    
    public static final DeferredHolder<Item, Item> VOID_ESSENCE_BUCKET = RegistryHandler.ITEMS.register("void_essence_bucket",
            () -> new BucketItem(VOID_ESSENCE_FLUID, new Item.Properties().craftRemainder(Items.BUCKET).stacksTo(1)));
    
    public static final DeferredHolder<LiquidBlock, LiquidBlock> VOID_ESSENCE_BLOCK = RegistryHandler.BLOCKS.register("void_essence",
            () -> new LiquidBlock(VOID_ESSENCE_FLUID, BlockBehaviour.Properties.of().noOcclusion().replaceable().noCollission().liquid()));
    
    /**
     * Properties for the Void Essence fluid.
     */
    private static BaseFlowingFluid.Properties voidEssenceProperties() {
        return new BaseFlowingFluid.Properties(
                VOID_ESSENCE_FLUID_TYPE,
                VOID_ESSENCE_FLUID,
                FLOWING_VOID_ESSENCE_FLUID)
                .bucket(VOID_ESSENCE_BUCKET)
                .block(VOID_ESSENCE_BLOCK)
                .slopeFindDistance(4)
                .levelDecreasePerBlock(2);
    }

    /**
     * Register all fluids.
     */
    public static void register() {
        EldritchVoid.LOGGER.info("Registering Eldritch Void fluids");
        // Fluid registrations are now handled by RegistryHandler.init()
        // The registers are already attached to the event bus
    }
}
