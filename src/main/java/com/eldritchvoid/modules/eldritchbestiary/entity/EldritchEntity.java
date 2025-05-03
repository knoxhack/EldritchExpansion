package com.eldritchvoid.modules.eldritchbestiary.entity;

import com.eldritchvoid.EldritchVoid;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.Vec3;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Random;

/**
 * Base class for Eldritch entities.
 * Implements shared behavior for void-influenced monsters.
 */
public class EldritchEntity extends Monster {
    private static final Logger LOGGER = LogManager.getLogger();
    
    // Data parameters
    private static final EntityDataAccessor<Boolean> DATA_IS_POWERED = 
            SynchedEntityData.defineId(EldritchEntity.class, EntityDataSerializers.BOOLEAN);
    
    private static final EntityDataAccessor<Integer> DATA_CORRUPTION_LEVEL = 
            SynchedEntityData.defineId(EldritchEntity.class, EntityDataSerializers.INT);
    
    // Fields
    private int teleportCooldown = 0;
    private final Random random = new Random();
    
    /**
     * Constructor.
     */
    public EldritchEntity(EntityType<? extends Monster> entityType, Level level) {
        super(entityType, level);
        this.xpReward = 10;
    }
    
    /**
     * Register entity data.
     */
    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_IS_POWERED, false);
        this.entityData.define(DATA_CORRUPTION_LEVEL, 0);
    }
    
    /**
     * Register entity goals.
     */
    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new FloatGoal(this));
        this.goalSelector.addGoal(2, new MeleeAttackGoal(this, 1.0D, false));
        this.goalSelector.addGoal(3, new RandomStrollGoal(this, 0.8D));
        this.goalSelector.addGoal(4, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(5, new RandomLookAroundGoal(this));
        
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
    }
    
    /**
     * Create entity attributes.
     */
    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 40.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.3D)
                .add(Attributes.ATTACK_DAMAGE, 6.0D)
                .add(Attributes.FOLLOW_RANGE, 32.0D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.6D);
    }
    
    /**
     * Check if a void horror can spawn at the given position.
     */
    public static boolean checkVoidHorrorSpawnRules(EntityType<EldritchEntity> type, LevelAccessor level,
                                               MobSpawnType spawnType, BlockPos pos, 
                                               net.minecraft.util.RandomSource random) {
        // Only spawn in darkness
        if (level.getBrightness(pos) > 4) {
            return false;
        }
        
        // Check for special conditions based on spawn type
        if (spawnType == MobSpawnType.SPAWNER) {
            return true;
        }
        
        // Chance to spawn near obsidian or in the void realm dimension
        boolean nearObsidian = false;
        for (int x = -4; x <= 4 && !nearObsidian; x++) {
            for (int y = -4; y <= 4 && !nearObsidian; y++) {
                for (int z = -4; z <= 4 && !nearObsidian; z++) {
                    BlockPos checkPos = pos.offset(x, y, z);
                    if (level.getBlockState(checkPos).is(net.minecraft.world.level.block.Blocks.OBSIDIAN)) {
                        nearObsidian = true;
                        break;
                    }
                }
            }
        }
        
        // Check for standard monster spawn conditions or near obsidian
        return nearObsidian || Monster.checkMonsterSpawnRules(type, level, spawnType, pos, random);
    }
    
    /**
     * Update entity AI.
     */
    @Override
    public void aiStep() {
        super.aiStep();
        
        // Handle teleportation cooldown
        if (this.teleportCooldown > 0) {
            this.teleportCooldown--;
        }
        
        // Chance to teleport when damaged
        if (this.getHealth() < this.getMaxHealth() * 0.5 && this.random.nextInt(40) == 0 && this.teleportCooldown <= 0) {
            this.teleport();
        }
        
        // Apply corruption effect to nearby entities
        if (this.isPowered() && this.tickCount % 20 == 0) {
            int corruptionLevel = this.getCorruptionLevel();
            
            for (LivingEntity entity : this.level().getEntitiesOfClass(
                    LivingEntity.class, 
                    this.getBoundingBox().inflate(5.0D), 
                    e -> e != this && !(e instanceof EldritchEntity))) {
                
                // Apply negative effects
                if (corruptionLevel > 1) {
                    entity.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 60, 0));
                }
                
                if (corruptionLevel > 2) {
                    entity.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 100, 0));
                }
                
                if (corruptionLevel > 3) {
                    entity.addEffect(new MobEffectInstance(MobEffects.WITHER, 40, 0));
                }
            }
        }
        
        // Emit particles when powered
        if (this.isPowered() && this.level().isClientSide()) {
            for (int i = 0; i < 2; i++) {
                this.level().addParticle(
                        net.minecraft.core.particles.ParticleTypes.PORTAL,
                        this.getRandomX(0.5D),
                        this.getRandomY(),
                        this.getRandomZ(0.5D),
                        (this.random.nextDouble() - 0.5D) * 2.0D,
                        -this.random.nextDouble(),
                        (this.random.nextDouble() - 0.5D) * 2.0D);
            }
        }
    }
    
    /**
     * Handle entity being hit.
     */
    @Override
    public boolean hurt(DamageSource source, float amount) {
        // Chance to teleport when hit
        if (!this.level().isClientSide() && source.getEntity() != null && this.random.nextInt(10) == 0 && this.teleportCooldown <= 0) {
            this.teleport();
        }
        
        return super.hurt(source, amount);
    }
    
    /**
     * Handle entity teleportation.
     */
    private void teleport() {
        if (this.level().isClientSide() || !this.isAlive()) {
            return;
        }
        
        double range = isPowered() ? 32.0D : 16.0D;
        
        double x = this.getX() + (this.random.nextDouble() - 0.5D) * range;
        double y = this.getY() + (this.random.nextDouble() - 0.5D) * range / 2;
        double z = this.getZ() + (this.random.nextDouble() - 0.5D) * range;
        
        this.teleportCooldown = 20;
        
        for (int attempts = 0; attempts < 10; attempts++) {
            BlockPos pos = new BlockPos((int)x, (int)y, (int)z);
            
            if (this.level().getBlockState(pos).isAir() && this.level().getBlockState(pos.below()).isSolid()) {
                this.teleportTo(x, y, z);
                this.level().playSound(null, this.xo, this.yo, this.zo, SoundEvents.ENDERMAN_TELEPORT, this.getSoundSource(), 1.0F, 1.0F);
                this.playSound(SoundEvents.ENDERMAN_TELEPORT, 1.0F, 1.0F);
                return;
            }
            
            x = this.getX() + (this.random.nextDouble() - 0.5D) * range;
            y = this.getY() + (this.random.nextDouble() - 0.5D) * range / 2;
            z = this.getZ() + (this.random.nextDouble() - 0.5D) * range;
        }
    }
    
    /**
     * Check if the entity is powered.
     */
    public boolean isPowered() {
        return this.entityData.get(DATA_IS_POWERED);
    }
    
    /**
     * Set the powered state.
     */
    public void setPoweredState(boolean powered) {
        this.entityData.set(DATA_IS_POWERED, powered);
    }
    
    /**
     * Get the corruption level.
     */
    public int getCorruptionLevel() {
        return this.entityData.get(DATA_CORRUPTION_LEVEL);
    }
    
    /**
     * Set the corruption level.
     */
    public void setCorruptionLevel(int level) {
        this.entityData.set(DATA_CORRUPTION_LEVEL, Math.max(0, Math.min(5, level)));
    }
    
    /**
     * Handle attacking another entity.
     */
    @Override
    public boolean doHurtTarget(Entity target) {
        boolean success = super.doHurtTarget(target);
        
        if (success && target instanceof LivingEntity livingTarget) {
            // Apply special effects on hit
            int corruptionLevel = this.getCorruptionLevel();
            
            if (corruptionLevel > 0) {
                // Base corruption effect
                livingTarget.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 100, 0));
            }
            
            if (isPowered()) {
                // Stronger effects when powered
                int duration = 80 + this.random.nextInt(40);
                livingTarget.addEffect(new MobEffectInstance(MobEffects.CONFUSION, duration, 0));
                
                if (corruptionLevel > 2) {
                    livingTarget.addEffect(new MobEffectInstance(MobEffects.WITHER, 60, 0));
                }
            }
        }
        
        return success;
    }
    
    /**
     * Get step sound.
     */
    @Override
    protected SoundEvent getStepSound() {
        return SoundEvents.ENDERMAN_SCREAM;
    }
    
    /**
     * Get hurt sound.
     */
    @Override
    protected SoundEvent getHurtSound(DamageSource damageSource) {
        return SoundEvents.ENDERMAN_HURT;
    }
    
    /**
     * Get death sound.
     */
    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.ENDERMAN_DEATH;
    }
    
    /**
     * Get ambient sound.
     */
    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.ENDERMAN_AMBIENT;
    }
    
    /**
     * Save entity data.
     */
    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        
        tag.putBoolean("Powered", this.isPowered());
        tag.putInt("CorruptionLevel", this.getCorruptionLevel());
        tag.putInt("TeleportCooldown", this.teleportCooldown);
    }
    
    /**
     * Load entity data.
     */
    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        
        this.setPoweredState(tag.getBoolean("Powered"));
        this.setCorruptionLevel(tag.getInt("CorruptionLevel"));
        this.teleportCooldown = tag.getInt("TeleportCooldown");
    }
    
    /**
     * Check if the entity can remain at a position.
     */
    @Override
    public float getWalkTargetValue(BlockPos pos, LevelReader level) {
        // Prefer to stay on obsidian or in darker areas
        if (level.getBlockState(pos.below()).is(net.minecraft.world.level.block.Blocks.OBSIDIAN)) {
            return 10.0F;
        }
        
        // Prefer darkness
        return 10.0F - level.getBrightness(pos);
    }
    
    /**
     * Handle entity swimming.
     */
    @Override
    public boolean isInWaterOrBubble() {
        // Void horrors are damaged by water but can float on it
        if (super.isInWaterOrBubble()) {
            if (this.tickCount % 10 == 0) {
                // Damage when in water
                this.hurt(this.damageSources().drown(), 1.0F);
            }
            return false; // Allow to float on water
        }
        return false;
    }
    
    /**
     * Calculate fall damage.
     */
    @Override
    protected int calculateFallDamage(float distance, float damageMultiplier) {
        // Eldritch entities take less fall damage
        return super.calculateFallDamage(distance, damageMultiplier) / 2;
    }
    
    /**
     * Handle entity death.
     */
    @Override
    public void die(DamageSource source) {
        super.die(source);
        
        // Chance to spawn corruption when dying
        if (!this.level().isClientSide() && this.random.nextInt(10) == 0) {
            BlockPos pos = this.blockPosition();
            
            // Spread corruption in a small area
            for (int x = -1; x <= 1; x++) {
                for (int y = -1; y <= 1; y++) {
                    for (int z = -1; z <= 1; z++) {
                        BlockPos corruptPos = pos.offset(x, y, z);
                        BlockState state = this.level().getBlockState(corruptPos);
                        
                        // Convert certain blocks to corrupted versions
                        if (state.is(net.minecraft.world.level.block.Blocks.STONE)) {
                            this.level().setBlock(corruptPos, net.minecraft.world.level.block.Blocks.OBSIDIAN.defaultBlockState(), 3);
                        } else if (state.is(net.minecraft.world.level.block.Blocks.DIRT) || 
                                  state.is(net.minecraft.world.level.block.Blocks.GRASS_BLOCK)) {
                            this.level().setBlock(corruptPos, net.minecraft.world.level.block.Blocks.COARSE_DIRT.defaultBlockState(), 3);
                        }
                    }
                }
            }
        }
    }
}
