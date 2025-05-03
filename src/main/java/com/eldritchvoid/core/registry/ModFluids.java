package com.eldritchvoid.core.registry;

import com.eldritchvoid.EldritchVoid;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

/**
 * Handles registration of fluid types and fluids.
 * Using the latest NeoForge 1.21.5 fluid system.
 */
public class ModFluids {
    // Create DeferredRegisters for fluids and fluid types
    public static final DeferredRegister<FluidType> FLUID_TYPES = DeferredRegister.create(
            NeoForgeRegistries.FLUID_TYPES, EldritchVoid.MOD_ID);
    
    public static final DeferredRegister<Fluid> FLUIDS = DeferredRegister.create(
            Registries.FLUID, EldritchVoid.MOD_ID);
    
    /**
     * Register all fluids.
     */
    public static void register() {
        EldritchVoid.LOGGER.info("Registering fluids");
        
        // Get the mod event bus
        IEventBus modEventBus = net.neoforged.fml.ModLoadingContext.get().getActiveContainer().getEventBus();
        
        // Register the fluid type registry
        FLUID_TYPES.register(modEventBus);
        
        // Register the fluid registry
        FLUIDS.register(modEventBus);
    }
}