package com.eldritchvoid.modules.voidalchemy.fluid;

import com.eldritchvoid.core.registry.Registration;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.fluids.FluidType;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;

import java.util.function.Consumer;

/**
 * Definition for the Void Essence fluid.
 * A dark, viscous fluid that emanates a subtle purple glow and emanates with void energy.
 */
public class VoidEssenceFluid {
    
    /**
     * The Void Essence fluid type.
     */
    public static class Type extends FluidType {
        public Type() {
            super(FluidType.Properties.create()
                    .descriptionId("fluid.eldritchvoid.void_essence")
                    .density(3000)
                    .viscosity(6000)
                    .temperature(240)
                    .lightLevel(2)
                    .sound(net.neoforged.neoforge.common.SoundActions.BUCKET_FILL, 
                           net.minecraft.sounds.SoundEvents.BUCKET_FILL)
                    .sound(net.neoforged.neoforge.common.SoundActions.BUCKET_EMPTY, 
                           net.minecraft.sounds.SoundEvents.BUCKET_EMPTY));
        }
        
        /**
         * Set client-side properties for the fluid type.
         */
        public void initializeClient(Consumer<IClientFluidTypeExtensions> consumer) {
            consumer.accept(new IClientFluidTypeExtensions() {
                private static final ResourceLocation STILL = Registration.location("block/fluid/void_essence_still");
                private static final ResourceLocation FLOWING = Registration.location("block/fluid/void_essence_flow");
                private static final ResourceLocation OVERLAY = Registration.location("block/fluid/void_essence_overlay");
                
                public int getTintColor() {
                    return 0x3C1F5A;  // Dark purple tint
                }
                
                public @NotNull ResourceLocation getStillTexture() {
                    return STILL;
                }
                
                public @NotNull ResourceLocation getFlowingTexture() {
                    return FLOWING;
                }
                
                public @NotNull ResourceLocation getOverlayTexture() {
                    return OVERLAY;
                }
                
                public @NotNull Vector3f modifyFogColor(IClientFluidTypeExtensions extensions, 
                                                      @NotNull org.joml.Vector3f fogColor, 
                                                      float playerEyeHeight) {
                    return new Vector3f(0.1f, 0.0f, 0.2f);  // Dark purple fog when submersed
                }
            });
        }
    }
}