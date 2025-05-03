package com.eldritchvoid.core.energy;

import net.minecraft.nbt.CompoundTag;

/**
 * Base implementation of the VoidEnergyStorage interface.
 * This provides a standard implementation that modules can extend or use directly.
 */
public class BaseVoidEnergyStorage implements VoidEnergyStorage {
    protected long energy;
    protected long capacity;
    protected long maxReceive;
    protected long maxExtract;
    protected boolean canReceive;
    protected boolean canExtract;
    
    /**
     * Create a new base void energy storage with the given parameters.
     *
     * @param capacity The maximum capacity
     * @param maxReceive The maximum amount that can be received per operation
     * @param maxExtract The maximum amount that can be extracted per operation
     * @param energy The initial amount of energy
     */
    public BaseVoidEnergyStorage(long capacity, long maxReceive, long maxExtract, long energy) {
        this.capacity = capacity;
        this.maxReceive = maxReceive;
        this.maxExtract = maxExtract;
        this.energy = Math.max(0, Math.min(capacity, energy));
        this.canReceive = maxReceive > 0;
        this.canExtract = maxExtract > 0;
    }
    
    /**
     * Create a new base void energy storage with the given capacity.
     *
     * @param capacity The maximum capacity
     */
    public BaseVoidEnergyStorage(long capacity) {
        this(capacity, capacity, capacity, 0);
    }
    
    @Override
    public long getStoredEnergy() {
        return energy;
    }
    
    @Override
    public long getMaxCapacity() {
        return capacity;
    }
    
    @Override
    public long extractEnergy(long amount, boolean simulate) {
        if (!canExtract) {
            return 0;
        }
        
        long extractAmount = Math.min(Math.min(energy, amount), maxExtract);
        
        if (!simulate) {
            energy -= extractAmount;
            onEnergyChanged();
        }
        
        return extractAmount;
    }
    
    @Override
    public long receiveEnergy(long amount, boolean simulate) {
        if (!canReceive) {
            return 0;
        }
        
        long receiveAmount = Math.min(Math.min(capacity - energy, amount), maxReceive);
        
        if (!simulate) {
            energy += receiveAmount;
            onEnergyChanged();
        }
        
        return receiveAmount;
    }
    
    @Override
    public boolean canExtract() {
        return canExtract;
    }
    
    @Override
    public boolean canReceive() {
        return canReceive;
    }
    
    /**
     * Read energy storage data from NBT.
     *
     * @param nbt The NBT tag to read from
     */
    public void readFromNBT(CompoundTag nbt) {
        // In NeoForge 1.21.5, NBT methods return Optional values
        // We need to handle this properly
        energy = nbt.getLong("Energy").orElse(0L);
        
        if (energy > capacity) {
            energy = capacity;
        }
    }
    
    /**
     * Write energy storage data to NBT.
     *
     * @param nbt The NBT tag to write to
     * @return The NBT tag with energy data
     */
    public CompoundTag writeToNBT(CompoundTag nbt) {
        nbt.putLong("Energy", energy);
        return nbt;
    }
    
    /**
     * Set the amount of energy stored.
     *
     * @param energy The new amount of energy
     */
    public void setEnergy(long energy) {
        this.energy = Math.max(0, Math.min(capacity, energy));
        onEnergyChanged();
    }
    
    /**
     * Set the maximum capacity.
     *
     * @param capacity The new capacity
     */
    public void setCapacity(long capacity) {
        this.capacity = capacity;
        
        if (energy > capacity) {
            energy = capacity;
        }
        
        onEnergyChanged();
    }
    
    /**
     * Set the maximum amount that can be received per operation.
     *
     * @param maxReceive The new maximum receive amount
     */
    public void setMaxReceive(long maxReceive) {
        this.maxReceive = maxReceive;
        this.canReceive = maxReceive > 0;
    }
    
    /**
     * Set the maximum amount that can be extracted per operation.
     *
     * @param maxExtract The new maximum extract amount
     */
    public void setMaxExtract(long maxExtract) {
        this.maxExtract = maxExtract;
        this.canExtract = maxExtract > 0;
    }
    
    /**
     * Called when the energy value changes.
     * Override this to perform additional actions when energy changes.
     */
    protected void onEnergyChanged() {
        // Override this to handle energy changes
    }
}