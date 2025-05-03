package com.eldritchvoid.modules.voidalchemy;

import com.eldritchvoid.EldritchVoid;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler.FluidAction;
import net.neoforged.neoforge.registries.DeferredHolder;

import java.util.Optional;

/**
 * A specialized bucket item for Void Alchemy fluids, compatible with NeoForge 1.21.5.
 * This implementation uses a simpler approach than the standard BucketItem
 * to avoid API compatibility issues with NeoForge 1.21.5's fluid system.
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
            // Create a basic tag structure for the fluid data
            CompoundTag fluidTag = new CompoundTag();
            fluidTag.putString("id", fluidHolder.getId().toString());
            
            // Add the fluid data to the main tag
            CompoundTag tag = new CompoundTag();
            tag.put("FluidData", fluidTag);
            
            // Set the tag on the stack - NeoForge 1.21.5 compatible
            if (stack.hasTag()) {
                // Merge with existing tag
                CompoundTag existingTag = stack.getTag();
                if (existingTag != null) {
                    existingTag.merge(tag);
                }
            } else {
                // Create new tag using Java reflection since API has changed
                try {
                    java.lang.reflect.Method setTagMethod = ItemStack.class.getDeclaredMethod("setTag", CompoundTag.class);
                    setTagMethod.setAccessible(true);
                    setTagMethod.invoke(stack, tag);
                } catch (Exception e) {
                    EldritchVoid.LOGGER.error("Failed to set tag using reflection: {}", e.getMessage());
                }
            }
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
     * Get a tag from the ItemStack with NeoForge 1.21.5 compatibility
     * 
     * @param stack The item stack
     * @return Optional of CompoundTag
     */
    private Optional<CompoundTag> getSafeTag(ItemStack stack) {
        return Optional.ofNullable(stack.getTag());
    }
}