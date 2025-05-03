package com.eldritchvoid.modules.voidalchemy;

import com.eldritchvoid.EldritchVoid;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.registries.DeferredHolder;

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
        // Pass the actual fluid to the parent constructor by resolving it from the holder
        super(fluidHolder::get, properties);
        this.fluidHolder = fluidHolder;
        
        EldritchVoid.LOGGER.debug("Created VoidBucketItem for fluid: {}", fluidHolder.getId());
    }

    @Override
    public CompoundTag getDefaultInstance() {
        // Override this method to ensure we're using the proper fluid even if it changes
        CompoundTag compoundTag = super.getDefaultInstance();
        // Ensure the fluid tag references our current fluid
        CompoundTag fluidTag = new CompoundTag();
        // In NeoForge 1.21.5, we need to handle the fluid tag differently
        fluidTag.putString("id", fluidHolder.getId().toString());
        compoundTag.put("fluid", fluidTag);
        return compoundTag;
    }
}