package com.eldritchvoid.modules.voidtech.energy;

import net.neoforged.neoforge.energy.EnergyStorage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Custom energy storage implementation for void energy.
 * Extends the standard NeoForge EnergyStorage with additional void-specific functionality.
 */
public class VoidEnergyStorage extends EnergyStorage {
    private static final Logger LOGGER = LogManager.getLogger();
    
    private boolean isVoidPowered = false;
    private int voidEfficiency = 100; // Percentage (0-100)
    private Runnable changeListener;
    
    /**
     * Constructor for void energy storage.
     * 
     * @param capacity The maximum energy capacity
     */
    public VoidEnergyStorage(int capacity) {
        super(capacity);
    }
    
    /**
     * Constructor for void energy storage with transfer limits.
     * 
     * @param capacity The maximum energy capacity
     * @param maxTransfer The maximum transfer rate
     */
    public VoidEnergyStorage(int capacity, int maxTransfer) {
        super(capacity, maxTransfer);
    }
    
    /**
     * Constructor for void energy storage with separate input/output transfer limits.
     * 
     * @param capacity The maximum energy capacity
     * @param maxReceive The maximum receive rate
     * @param maxExtract The maximum extract rate
     */
    public VoidEnergyStorage(int capacity, int maxReceive, int maxExtract) {
        super(capacity, maxReceive, maxExtract);
    }
    
    /**
     * Full constructor for void energy storage.
     * 
     * @param capacity The maximum energy capacity
     * @param maxReceive The maximum receive rate
     * @param maxExtract The maximum extract rate
     * @param energy The initial energy
     */
    public VoidEnergyStorage(int capacity, int maxReceive, int maxExtract, int energy) {
        super(capacity, maxReceive, maxExtract, energy);
    }
    
    /**
     * Set whether this storage is void-powered (more efficient).
     * 
     * @param voidPowered True if void-powered
     */
    public void setVoidPowered(boolean voidPowered) {
        this.isVoidPowered = voidPowered;
    }
    
    /**
     * Check if this storage is void-powered.
     * 
     * @return True if void-powered
     */
    public boolean isVoidPowered() {
        return this.isVoidPowered;
    }
    
    /**
     * Set the void efficiency.
     * 
     * @param efficiency The efficiency percentage (0-100)
     */
    public void setVoidEfficiency(int efficiency) {
        this.voidEfficiency = Math.max(0, Math.min(100, efficiency));
    }
    
    /**
     * Get the void efficiency.
     * 
     * @return The efficiency percentage
     */
    public int getVoidEfficiency() {
        return this.voidEfficiency;
    }
    
    /**
     * Set the listener to call when energy changes.
     * 
     * @param listener The listener to call
     */
    public void setChangeListener(Runnable listener) {
        this.changeListener = listener;
    }
    
    /**
     * Get the effective energy capacity, considering void power.
     * 
     * @return The effective capacity
     */
    public int getEffectiveCapacity() {
        return isVoidPowered ? (int)(capacity * (1.0f + voidEfficiency / 100.0f)) : capacity;
    }
    
    @Override
    public int receiveEnergy(int maxReceive, boolean simulate) {
        int received = super.receiveEnergy(maxReceive, simulate);
        
        if (received > 0 && !simulate && changeListener != null) {
            changeListener.run();
        }
        
        return received;
    }
    
    @Override
    public int extractEnergy(int maxExtract, boolean simulate) {
        // Apply efficiency bonus if void-powered
        int effectiveExtract = maxExtract;
        if (isVoidPowered && !simulate) {
            // When void-powered, we can extract more for the same energy cost
            effectiveExtract = (int)(maxExtract * (1.0f + voidEfficiency / 100.0f));
        }
        
        int extracted = super.extractEnergy(effectiveExtract, simulate);
        
        if (extracted > 0 && !simulate && changeListener != null) {
            changeListener.run();
        }
        
        return extracted;
    }
    
    /**
     * Set the energy stored directly (for admin/cheat purposes or loading from NBT).
     * 
     * @param energy The energy to set
     */
    public void setEnergyStored(int energy) {
        this.energy = Math.max(0, Math.min(getEffectiveCapacity(), energy));
        
        if (changeListener != null) {
            changeListener.run();
        }
    }
    
    /**
     * Add energy without any limits.
     * 
     * @param energy The energy to add
     * @param simulate Whether to simulate the operation
     * @return The amount of energy that was added
     */
    public int addEnergyRaw(int energy, boolean simulate) {
        int energyReceived = Math.min(getEffectiveCapacity() - this.energy, energy);
        
        if (!simulate) {
            this.energy += energyReceived;
            
            if (changeListener != null) {
                changeListener.run();
            }
        }
        
        return energyReceived;
    }
    
    /**
     * Remove energy without any limits.
     * 
     * @param energy The energy to remove
     * @param simulate Whether to simulate the operation
     * @return The amount of energy that was removed
     */
    public int removeEnergyRaw(int energy, boolean simulate) {
        int energyExtracted = Math.min(this.energy, energy);
        
        if (!simulate) {
            this.energy -= energyExtracted;
            
            if (changeListener != null) {
                changeListener.run();
            }
        }
        
        return energyExtracted;
    }
}
