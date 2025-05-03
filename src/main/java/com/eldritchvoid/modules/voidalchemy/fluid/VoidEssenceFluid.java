package com.eldritchvoid.modules.voidalchemy.fluid;

import com.eldritchvoid.EldritchVoid;
import com.eldritchvoid.core.registry.ModFluids;
import net.minecraft.client.Camera;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.common.SoundActions;
import net.neoforged.neoforge.event.entity.living.LivingEvent;

import net.neoforged.neoforge.fluids.BaseFlowingFluid;
import net.neoforged.neoforge.fluids.FluidType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.joml.Vector3f;

import java.util.function.Consumer;

/**
 * The Void Essence fluid.
 */
public class VoidEssenceFluid extends BaseFlowingFluid {
    private static final Logger LOGGER = LogManager.getLogger();
    
    /**
     * Constructor for source fluid.
     */
    protected VoidEssenceFluid(Properties properties) {
        super(properties);
    }
    
    /**
     * Register the fluid with the event bus.
     * 
     * @param eventBus The event bus to register with
     */
    public static void register(IEventBus eventBus) {
        // Register event handlers
        NeoForge.EVENT_BUS.addListener(VoidEssenceFluid::onEntityInVoidEssence);
    }
    
    /**
     * Create the fluid type for Void Essence.
     * 
     * @return The fluid type
     */
    public static FluidType createFluidType() {
        return new FluidType(FluidType.Properties.create()
                .descriptionId("fluid.eldritchvoid.void_essence")
                .density(1100)
                .viscosity(1500)
                .temperature(280)
                .sound(SoundActions.BUCKET_FILL, SoundEvents.BUCKET_FILL)
                .sound(SoundActions.BUCKET_EMPTY, SoundEvents.BUCKET_EMPTY)) {
            
            @Override
            public void initializeClient(Consumer<IClientFluidTypeExtensions> consumer) {
                consumer.accept(new IClientFluidTypeExtensions() {
                    private static final ResourceLocation STILL_TEXTURE = 
                            new ResourceLocation(EldritchVoid.MOD_ID, "block/fluid/void_essence_still");
                    private static final ResourceLocation FLOWING_TEXTURE = 
                            new ResourceLocation(EldritchVoid.MOD_ID, "block/fluid/void_essence_flow");
                    
                    @Override
                    public ResourceLocation getStillTexture() {
                        return STILL_TEXTURE;
                    }
                    
                    @Override
                    public ResourceLocation getFlowingTexture() {
                        return FLOWING_TEXTURE;
                    }
                    
                    @Override
                    public int getTintColor() {
                        return 0x990099FF; // Dark purple with alpha
                    }
                    
                    @Override
                    public Vector3f modifyFogColor(Camera camera, float partialTick, Level level, 
                                                 int renderDistance, float darkenWorldAmount, 
                                                 Vector3f fluidFogColor) {
                        // Modify fog color when player is in void essence
                        return new Vector3f(0.1f, 0.0f, 0.2f); // Dark purple fog
                    }
                });
            }
        };
    }
    
    /**
     * Handle entities that are in void essence.
     * 
     * @param event The living tick event
     */
    private static void onEntityInVoidEssence(LivingEvent event) {
        LivingEntity entity = event.getEntity();
        Level level = entity.level();
        
        if (entity.isInFluidType(ModFluids.VOID_ESSENCE_FLUID_TYPE.get())) {
            // Apply void corruption effect
            if (entity.tickCount % 20 == 0) { // Once per second
                // Add corruption or other effects
                entity.addEffect(new net.minecraft.world.effect.MobEffectInstance(
                        net.minecraft.world.effect.MobEffects.BLINDNESS, 40, 0));
                
                entity.addEffect(new net.minecraft.world.effect.MobEffectInstance(
                        net.minecraft.world.effect.MobEffects.MOVEMENT_SLOWDOWN, 40, 1));
                
                // Teleport effect - 5% chance per second
                if (level.random.nextFloat() < 0.05f) {
                    double offsetX = level.random.nextDouble() * 10 - 5;
                    double offsetY = level.random.nextDouble() * 4 - 2;
                    double offsetZ = level.random.nextDouble() * 10 - 5;
                    
                    if (entity.randomTeleport(
                            entity.getX() + offsetX,
                            entity.getY() + offsetY,
                            entity.getZ() + offsetZ,
                            true)) {
                        level.playSound(null, entity.getX(), entity.getY(), entity.getZ(), 
                                SoundEvents.ENDERMAN_TELEPORT, entity.getSoundSource(), 
                                1.0F, 1.0F);
                    }
                }
            }
        }
    }
    
    // Required implementations for FlowingFluid
    
    @Override
    public Fluid getFlowing() {
        return ModFluids.FLOWING_VOID_ESSENCE_FLUID.get();
    }
    
    @Override
    public Fluid getSource() {
        return ModFluids.VOID_ESSENCE_FLUID.get();
    }
    
    @Override
    protected boolean canConvertToSource(Level level) {
        return false; // Void essence cannot form source blocks naturally
    }
    
    @Override
    protected void beforeDestroyingBlock(Level level, BlockPos pos, BlockState state) {
        // Special effects when void essence destroys a block
    }
    
    @Override
    protected int getSlopeFindDistance(Level level) {
        return 4;
    }
    
    @Override
    protected int getDropOff(Level level) {
        return 2; // Void essence flows slower than water
    }
    
    @Override
    public boolean canBeReplacedWith(FluidState fluidState, Level level, BlockPos pos, 
                                    Fluid fluid, Direction direction) {
        return false; // Void essence is "heavier" than other fluids and cannot be replaced
    }
    
    @Override
    public int getTickDelay(Level level) {
        return 20; // Tick delay for updates, higher = slower flowing
    }
    
    @Override
    protected float getExplosionResistance() {
        return 100.0F; // Very resistant to explosions
    }
    
    /**
     * The flowing version of Void Essence fluid.
     */
    public static class Flowing extends VoidEssenceFluid {
        public Flowing(Properties properties) {
            super(properties);
            registerDefaultState(getStateDefinition().any().setValue(LEVEL, 7));
        }
        
        @Override
        protected void createFluidStateDefinition(net.minecraft.world.level.block.state.StateDefinition.Builder<Fluid, FluidState> builder) {
            super.createFluidStateDefinition(builder);
            builder.add(LEVEL);
        }
        
        @Override
        public int getAmount(FluidState state) {
            return state.getValue(LEVEL);
        }
        
        @Override
        public boolean isSource(FluidState state) {
            return false;
        }
    }
    
    /**
     * The source version of Void Essence fluid.
     */
    public static class Source extends VoidEssenceFluid {
        public Source(Properties properties) {
            super(properties);
        }
        
        @Override
        public int getAmount(FluidState state) {
            return 8; // Full fluid block
        }
        
        @Override
        public boolean isSource(FluidState state) {
            return true;
        }
    }
}
