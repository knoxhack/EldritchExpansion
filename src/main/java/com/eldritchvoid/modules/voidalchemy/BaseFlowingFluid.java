package com.eldritchvoid.modules.voidalchemy;

import com.eldritchvoid.EldritchVoid;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.core.Direction;
import net.minecraft.world.item.Item;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.material.FluidType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.material.Fluids;

import javax.annotation.Nullable;
import java.util.function.Supplier;

/**
 * A custom BaseFlowingFluid class that extends FlowingFluid to provide
 * compatibility with NeoForge 1.21.5 and our registration pattern.
 * This allows properties to be set after registration.
 */
public abstract class BaseFlowingFluid extends FlowingFluid {
    private Properties properties;

    /**
     * Base constructor that takes properties.
     * 
     * @param properties The fluid properties
     */
    protected BaseFlowingFluid(Properties properties) {
        this.properties = properties;
    }

    /**
     * Get the fluid type defined in properties.
     */
    @Override
    public FluidType getFluidType() {
        return properties.fluidTypeSupplier.get();
    }

    /**
     * Check if the fluid can create a source.
     */
    @Override
    protected boolean canConvertToSource(Level level) {
        return properties.canConvertToSource;
    }

    /**
     * Get how quickly this fluid flows.
     */
    @Override
    protected int getSlopeFindDistance(LevelReader levelReader) {
        return properties.slopeFindDistance;
    }

    /**
     * Get the drop off per block.
     */
    @Override
    protected int getDropOff(LevelReader levelReader) {
        return properties.levelDecreasePerBlock;
    }

    /**
     * Get the bucket item for this fluid.
     */
    @Override
    public Item getBucket() {
        return properties.bucket.get();
    }

    /**
     * Get the block state for fluid placement.
     */
    @Override
    protected BlockState createLegacyBlock(FluidState fluidState) {
        if (properties.block != null) {
            return properties.block.get().defaultBlockState().setValue(LiquidBlock.LEVEL, 
                    Integer.valueOf(getLegacyLevel(fluidState)));
        }
        return null;
    }

    /**
     * Check if fluids are the same.
     */
    @Override
    public boolean isSame(Fluid fluid) {
        return fluid == this || fluid == getSource() || fluid == getFlowing();
    }

    /**
     * Get the flowing variant of this fluid.
     */
    @Override
    public abstract Fluid getFlowing();

    /**
     * Get the source variant of this fluid.
     */
    @Override
    public abstract Fluid getSource();

    /**
     * Can this fluid flow into the passed in state.
     */
    @Override
    protected boolean canBeReplacedWith(FluidState fluidState, LevelReader level, BlockPos pos, Fluid fluid, Direction direction) {
        // By default, allow replacement by all non-source blocks
        return direction == Direction.DOWN || 
               (direction == Direction.UP && !isSource(fluidState)) ||
               fluidState.getHeight(level, pos) < 0.4D;
    }

    /**
     * Get the explosion resistance.
     */
    @Override
    protected float getExplosionResistance() {
        return properties.explosionResistance;
    }

    /**
     * Gets tick rate.
     */
    @Override
    protected int getTickDelay(LevelReader level) {
        return properties.tickDelay;
    }

    /**
     * Find falling height.
     */
    @Override
    protected boolean isRandomlyTicking() {
        return false;
    }

    /**
     * After a block is destroyed, this can handle special fluid placement.
     */
    @Override
    protected void beforeDestroyingBlock(LevelAccessor level, BlockPos pos, BlockState state) {
        BlockEntity blockEntity = state.hasBlockEntity() ? level.getBlockEntity(pos) : null;
        net.minecraft.world.level.block.Blocks.dropResources(state, level, pos, blockEntity);
    }

    /**
     * Method to update fluid properties after registration.
     * This is needed because of NeoForge 1.21.5's registration pattern.
     * 
     * @param properties The new properties to use
     */
    public void setProperties(Properties properties) {
        this.properties = properties;
        EldritchVoid.LOGGER.debug("Updated fluid properties for {}", this.getClass().getSimpleName());
    }

    /**
     * Source variant of the fluid.
     */
    public static class Source extends BaseFlowingFluid {
        public Source(Properties properties) {
            super(properties);
        }

        @Override
        public boolean isSource(FluidState state) {
            return true;
        }

        @Override
        public int getAmount(FluidState state) {
            return 8;
        }

        @Override
        public Fluid getFlowing() {
            return properties.flowingSupplier.get();
        }

        @Override
        public Fluid getSource() {
            return properties.sourceSupplier.get();
        }
    }

    /**
     * Flowing variant of the fluid.
     */
    public static class Flowing extends BaseFlowingFluid {
        public Flowing(Properties properties) {
            super(properties);
        }

        @Override
        protected void createFluidStateDefinition(StateDefinition.Builder<Fluid, FluidState> builder) {
            super.createFluidStateDefinition(builder);
            builder.add(LEVEL);
        }

        @Override
        public boolean isSource(FluidState state) {
            return false;
        }

        @Override
        public int getAmount(FluidState state) {
            return state.getValue(LEVEL);
        }

        @Override
        public Fluid getFlowing() {
            return properties.flowingSupplier.get();
        }

        @Override
        public Fluid getSource() {
            return properties.sourceSupplier.get();
        }
    }

    /**
     * Properties for the fluid.
     */
    public static class Properties {
        private Supplier<FluidType> fluidTypeSupplier;
        private Supplier<? extends Fluid> sourceSupplier;
        private Supplier<? extends Fluid> flowingSupplier;
        private Supplier<? extends Item> bucket = () -> null;
        private Supplier<? extends LiquidBlock> block = () -> null;
        private int levelDecreasePerBlock = 1;
        private float explosionResistance = 1;
        private int tickDelay = 5;
        private int slopeFindDistance = 4;
        private boolean canConvertToSource = false;

        /**
         * Create properties with required suppliers.
         *
         * @param fluidTypeSupplier The fluid type supplier
         * @param sourceSupplier The source fluid supplier
         * @param flowingSupplier The flowing fluid supplier
         */
        public Properties(Supplier<FluidType> fluidTypeSupplier, 
                         Supplier<? extends Fluid> sourceSupplier, 
                         Supplier<? extends Fluid> flowingSupplier) {
            this.fluidTypeSupplier = fluidTypeSupplier;
            this.sourceSupplier = sourceSupplier;
            this.flowingSupplier = flowingSupplier;
        }

        /**
         * Set the bucket item.
         */
        public Properties bucket(Supplier<? extends Item> bucket) {
            this.bucket = bucket;
            return this;
        }

        /**
         * Set the block for this fluid.
         */
        public Properties block(Supplier<? extends LiquidBlock> block) {
            this.block = block;
            return this;
        }

        /**
         * Set explosion resistance.
         */
        public Properties explosionResistance(float explosionResistance) {
            this.explosionResistance = explosionResistance;
            return this;
        }

        /**
         * Set tick delay.
         */
        public Properties tickDelay(int tickDelay) {
            this.tickDelay = tickDelay;
            return this;
        }

        /**
         * Set level decrease per block.
         */
        public Properties levelDecreasePerBlock(int levelDecreasePerBlock) {
            this.levelDecreasePerBlock = levelDecreasePerBlock;
            return this;
        }

        /**
         * Set slope find distance.
         */
        public Properties slopeFindDistance(int slopeFindDistance) {
            this.slopeFindDistance = slopeFindDistance;
            return this;
        }

        /**
         * Set if can create source blocks.
         */
        public Properties canConvertToSource() {
            this.canConvertToSource = true;
            return this;
        }
    }
}