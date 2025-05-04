package com.eldritchvoid.modules.voidalchemy;

import com.eldritchvoid.EldritchVoid;
import net.minecraft.world.level.material.FlowingFluid;
import net.neoforged.neoforge.fluids.BaseFlowingFluid;

import java.lang.reflect.Field;

/**
 * A helper class to update fluid properties after registration.
 * This is a workaround for NeoForge 1.21.5's fluid registration system.
 */
public class FluidPropertiesHelper {

    /**
     * Update the properties of a BaseFlowingFluid using reflection.
     * This is necessary because Forge's BaseFlowingFluid doesn't provide a setProperties method.
     *
     * @param fluid The fluid to update
     * @param properties The new properties to set
     * @return true if successful, false otherwise
     */
    public static boolean updateFluidProperties(FlowingFluid fluid, BaseFlowingFluid.Properties properties) {
        if (!(fluid instanceof BaseFlowingFluid)) {
            EldritchVoid.LOGGER.error("Cannot update properties of non-BaseFlowingFluid");
            return false;
        }
        
        try {
            // Get the properties field using reflection
            Field propField = BaseFlowingFluid.class.getDeclaredField("properties");
            propField.setAccessible(true);
            
            // Set the new properties
            propField.set(fluid, properties);
            
            EldritchVoid.LOGGER.debug("Successfully updated properties for fluid: {}", fluid.getClass().getSimpleName());
            return true;
        } catch (NoSuchFieldException | IllegalAccessException e) {
            EldritchVoid.LOGGER.error("Failed to update fluid properties", e);
            return false;
        }
    }
}