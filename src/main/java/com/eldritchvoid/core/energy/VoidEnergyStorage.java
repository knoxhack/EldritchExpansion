package com.eldritchvoid.core.energy;

/**
 * Interface for void energy storage.
 * This is the base interface for all void energy storage implementations.
 * Used by modules that need to store or transfer void energy.
 */
public interface VoidEnergyStorage {
    /**
     * Get the amount of void energy stored.
     *
     * @return The amount of void energy stored
     */
    long getStoredEnergy();
    
    /**
     * Get the maximum capacity of void energy that can be stored.
     *
     * @return The maximum capacity
     */
    long getMaxCapacity();
    
    /**
     * Extract void energy from storage.
     *
     * @param amount The amount to extract
     * @param simulate If true, the extraction is only simulated
     * @return The amount of void energy that was (or would have been) extracted
     */
    long extractEnergy(long amount, boolean simulate);
    
    /**
     * Receive void energy into storage.
     *
     * @param amount The amount to receive
     * @param simulate If true, the receive operation is only simulated
     * @return The amount of void energy that was (or would have been) received
     */
    long receiveEnergy(long amount, boolean simulate);
    
    /**
     * Check if this storage can extract void energy.
     *
     * @return True if this storage can extract energy
     */
    default boolean canExtract() {
        return true;
    }
    
    /**
     * Check if this storage can receive void energy.
     *
     * @return True if this storage can receive energy
     */
    default boolean canReceive() {
        return true;
    }
    
    /**
     * Get the percentage of void energy stored (0.0 to 1.0).
     *
     * @return The percentage of void energy stored
     */
    default double getStoredPercentage() {
        long max = getMaxCapacity();
        return max > 0 ? (double) getStoredEnergy() / max : 0.0;
    }
}