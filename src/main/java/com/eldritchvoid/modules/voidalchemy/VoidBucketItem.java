package com.eldritchvoid.modules.voidalchemy;

import com.eldritchvoid.EldritchVoid;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.registries.DeferredHolder;

import java.util.function.Supplier;

/**
 * A specialized bucket item that works with NeoForge 1.21.5's registry system.
 * This handles the delayed registration pattern needed for fluids.
 */
public class VoidBucketItem extends BucketItem {
    private final DeferredHolder<Fluid, Fluid> fluidHolder;

    /**
     * Create a new void bucket item.
     *
     * @param fluidHolder The holder for the fluid
     * @param properties  The item properties
     */
    public VoidBucketItem(DeferredHolder<Fluid, Fluid> fluidHolder, Item.Properties properties) {
        // Create a supplier that will provide the fluid when needed
        super(new FluidSupplier(fluidHolder), properties);
        this.fluidHolder = fluidHolder;
        
        EldritchVoid.LOGGER.debug("Created VoidBucketItem for fluid: {}", fluidHolder.getId());
    }

    /**
     * Returns a default ItemStack with fluid NBT data.
     */
    @Override
    public ItemStack getDefaultInstance() {
        // Create a basic ItemStack for this bucket
        ItemStack itemStack = super.getDefaultInstance();
        
        try {
            // Add our own custom NBT data for the fluid
            CompoundTag compoundTag = itemStack.getOrCreateTagElement("fluid");
            compoundTag.putString("id", fluidHolder.getId().toString());
        } catch (Exception e) {
            EldritchVoid.LOGGER.error("Failed to set tag on bucket item: {}", e.getMessage());
        }
        
        return itemStack;
    }
    
    /**
     * A proper implementation of a fluid supplier for the BucketItem constructor.
     */
    private static class FluidSupplier implements Supplier<Fluid> {
        private final DeferredHolder<Fluid, Fluid> holder;
        
        public FluidSupplier(DeferredHolder<Fluid, Fluid> holder) {
            this.holder = holder;
        }
        
        @Override
        public Fluid get() {
            return holder.get();
        }
    }
}