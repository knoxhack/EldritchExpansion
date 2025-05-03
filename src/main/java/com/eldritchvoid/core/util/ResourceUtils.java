package com.eldritchvoid.core.util;

import net.minecraft.resources.ResourceLocation;

/**
 * Utility methods for creating ResourceLocation objects
 * with NeoForge 1.21.5 compatibility.
 */
public class ResourceUtils {
    
    /**
     * Create a ResourceLocation with namespace and path.
     * This method works with NeoForge 1.21.5.
     *
     * @param namespace The namespace
     * @param path The path
     * @return A new ResourceLocation
     */
    public static ResourceLocation createResourceLocation(String namespace, String path) {
        // In NeoForge 1.21.5, we should use ResourceLocation.withDefaultNamespace or create from string
        return ResourceLocation.parse(namespace + ":" + path);
    }
    
    /**
     * Create a ResourceLocation from a string.
     * This method works with NeoForge 1.21.5.
     *
     * @param path The path with optional namespace
     * @return A new ResourceLocation
     */
    public static ResourceLocation createResourceLocation(String path) {
        // In NeoForge 1.21.5, we should use ResourceLocation.parse
        return ResourceLocation.parse(path);
    }
}