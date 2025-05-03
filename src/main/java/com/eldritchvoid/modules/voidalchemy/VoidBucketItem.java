package com.eldritchvoid.modules.voidalchemy;

import com.eldritchvoid.EldritchVoid;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.ICapabilityProvider;
import net.neoforged.neoforge.common.capabilities.Capability;
import net.neoforged.neoforge.common.util.LazyOptional;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandlerItem;
import net.neoforged.neoforge.fluids.capability.IFluidHandler.FluidAction;
import net.neoforged.neoforge.registries.DeferredHolder;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.function.Consumer;

/**
 * A specialized bucket item for Void Alchemy fluids, compatible with NeoForge 1.21.5.
 * This implementation uses a custom approach rather than extending BucketItem
 * to avoid API compatibility issues with the fluid registration system.
 */
public class VoidBucketItem extends Item {
    private final DeferredHolder<Fluid, Fluid> fluidHolder;
    private static final int BUCKET_VOLUME = 1000;

    /**
     * Create a new void bucket item.
     *
     * @param fluidHolder The holder for the fluid
     * @param properties  The item properties
     */
    public VoidBucketItem(DeferredHolder<Fluid, Fluid> fluidHolder, Item.Properties properties) {
        super(properties.stacksTo(1).craftRemainder(Items.BUCKET));
        this.fluidHolder = fluidHolder;
        
        EldritchVoid.LOGGER.debug("Created VoidBucketItem for fluid: {}", fluidHolder.getId());
    }

    /**
     * Returns the fluid holder associated with this bucket
     */
    public DeferredHolder<Fluid, Fluid> getFluidHolder() {
        return fluidHolder;
    }
    
    /**
     * Get the fluid contained in this bucket
     */
    public Fluid getFluid() {
        return fluidHolder.get();
    }

    /**
     * Returns a default ItemStack with fluid NBT data.
     */
    @Override
    public ItemStack getDefaultInstance() {
        ItemStack stack = new ItemStack(this);
        
        try {
            // Create a basic tag structure
            CompoundTag tag = new CompoundTag();
            tag.putString("fluid", fluidHolder.getId().toString());
            
            // Store in the item's tag
            stack.getOrCreateTag().put("FluidData", tag);
        } catch (Exception e) {
            EldritchVoid.LOGGER.error("Failed to create default instance for VoidBucketItem: {}", e.getMessage());
        }
        
        return stack;
    }
    
    /**
     * Create a new FluidStack from this bucket.
     * 
     * @param stack The bucket item stack
     * @return A fluid stack containing this bucket's fluid
     */
    public FluidStack getFluidStack(ItemStack stack) {
        return new FluidStack(getFluid(), BUCKET_VOLUME);
    }
    
    /**
     * Checks if this bucket contains the specified fluid
     */
    public boolean containsFluid(ItemStack stack, Fluid fluid) {
        return getFluid() == fluid;
    }
    
    /**
     * Create capability providers for this bucket.
     */
    @Override
    public @Nullable ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundTag nbt) {
        return new VoidBucketFluidHandler(stack, this);
    }
    
    /**
     * A fluid handler implementation for the void bucket item.
     */
    private static class VoidBucketFluidHandler implements IFluidHandlerItem, ICapabilityProvider {
        private final ItemStack container;
        private final VoidBucketItem bucketItem;
        private final LazyOptional<IFluidHandlerItem> holder = LazyOptional.of(() -> this);
        
        public VoidBucketFluidHandler(ItemStack container, VoidBucketItem bucketItem) {
            this.container = container;
            this.bucketItem = bucketItem;
        }
        
        @Override
        public ItemStack getContainer() {
            return container;
        }
        
        @Override
        public int getTanks() {
            return 1;
        }
        
        @Nonnull
        @Override
        public FluidStack getFluidInTank(int tank) {
            return tank == 0 ? bucketItem.getFluidStack(container) : FluidStack.EMPTY;
        }
        
        @Override
        public int getTankCapacity(int tank) {
            return tank == 0 ? BUCKET_VOLUME : 0;
        }
        
        @Override
        public boolean isFluidValid(int tank, @Nonnull FluidStack stack) {
            return tank == 0 && stack.getFluid() == bucketItem.getFluid();
        }
        
        @Override
        public int fill(FluidStack resource, FluidAction action) {
            // Buckets can't be filled, they're pre-filled
            return 0;
        }
        
        @Nonnull
        @Override
        public FluidStack drain(FluidStack resource, FluidAction action) {
            if (resource.isEmpty() || !isFluidValid(0, resource) || resource.getAmount() < BUCKET_VOLUME) {
                return FluidStack.EMPTY;
            }
            return drain(BUCKET_VOLUME, action);
        }
        
        @Nonnull
        @Override
        public FluidStack drain(int maxDrain, FluidAction action) {
            if (maxDrain < BUCKET_VOLUME) {
                return FluidStack.EMPTY;
            }
            
            FluidStack fluidStack = bucketItem.getFluidStack(container);
            if (action.execute()) {
                container.shrink(1);
                ItemStack emptyBucket = new ItemStack(Items.BUCKET);
                if (container.isEmpty()) {
                    container = emptyBucket;
                } else if (container.getCount() == 0) {
                    container = emptyBucket;
                }
            }
            
            return fluidStack;
        }
        
        @Override
        public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
            if (cap == Capabilities.FluidHandler.ITEM) {
                return holder.cast();
            }
            return LazyOptional.empty();
        }
    }
}