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
        // Create a proper supplier to the fluid for the BucketItem constructor
        super(new FluidSupplier(fluidHolder), properties);
        this.fluidHolder = fluidHolder;
        
        EldritchVoid.LOGGER.debug("Created VoidBucketItem for fluid: {}", fluidHolder.getId());
    }

    /**
     * Returns a default ItemStack with fluid NBT data.
     */
    @Override
    public ItemStack getDefaultInstance() {
        ItemStack itemStack = new ItemStack(this);
        CompoundTag compoundTag = new CompoundTag();
        CompoundTag fluidTag = new CompoundTag();
        fluidTag.putString("id", fluidHolder.getId().toString());
        compoundTag.put("fluid", fluidTag);
        itemStack.setTag(compoundTag);
        return itemStack;
    }
    
    /**
     * A simple supplier that wraps a DeferredHolder for fluid access.
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