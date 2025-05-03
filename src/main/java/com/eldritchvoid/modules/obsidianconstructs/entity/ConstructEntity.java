package com.eldritchvoid.modules.obsidianconstructs.entity;

import com.eldritchvoid.EldritchVoid;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.TimeUtil;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.NeutralMob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.goal.target.ResetUniversalAngerTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nullable;
import java.util.UUID;

/**
 * Obsidian Golem entity.
 * An automatable construct that can perform tasks.
 */
public class ConstructEntity extends Monster implements NeutralMob {
    private static final Logger LOGGER = LogManager.getLogger();
    
    // Data parameters
    private static final EntityDataAccessor<Integer> DATA_REMAINING_ANGER_TIME = 
            SynchedEntityData.defineId(ConstructEntity.class, EntityDataSerializers.INT);
    
    private static final EntityDataAccessor<String> DATA_TASK = 
            SynchedEntityData.defineId(ConstructEntity.class, EntityDataSerializers.STRING);
    
    private static final EntityDataAccessor<String> DATA_OWNER = 
            SynchedEntityData.defineId(ConstructEntity.class, EntityDataSerializers.STRING);
    
    // Constants
    private static final UniformInt ANGER_TIME_RANGE = TimeUtil.rangeOfSeconds(20, 39);
    
    // Fields
    private UUID owner;
    private BlockPos workTarget;
    private int workProgress;
    
    @Nullable
    private UUID persistentAngerTarget;
    
    /**
     * Constructor.
     */
    public ConstructEntity(EntityType<? extends Monster> entityType, Level level) {
        super(entityType, level);
        this.setPathfindingMalus(BlockPathTypes.LAVA, 8.0F);
        this.setPathfindingMalus(BlockPathTypes.DANGER_FIRE, 0.0F);
        this.setPathfindingMalus(BlockPathTypes.DAMAGE_FIRE, 0.0F);
        this.xpReward = 5;
    }
    
    /**
     * Register entity data.
     */
    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_REMAINING_ANGER_TIME, 0);
        builder.define(DATA_TASK, "idle");
        builder.define(DATA_OWNER, "");
    }
    
    /**
     * Register entity goals.
     */
    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new FloatGoal(this));
        this.goalSelector.addGoal(2, new MeleeAttackGoal(this, 1.0D, false));
        this.goalSelector.addGoal(3, new WaterAvoidingRandomStrollGoal(this, 0.8D));
        this.goalSelector.addGoal(4, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(5, new RandomLookAroundGoal(this));
        
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Monster.class, 5, false, false, 
                (entity) -> !(entity instanceof ConstructEntity)));
        this.targetSelector.addGoal(3, new ResetUniversalAngerTargetGoal<>(this, false));
    }
    
    /**
     * Create entity attributes.
     */
    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 80.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.25D)
                .add(Attributes.ATTACK_DAMAGE, 7.0D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.8D)
                .add(Attributes.ARMOR, 8.0D);
    }
    
    /**
     * Check if a construct can spawn at the given position.
     */
    public static boolean checkConstructSpawnRules(EntityType<ConstructEntity> type, LevelAccessor level,
                                               net.minecraft.world.entity.MobSpawnType spawnType, BlockPos pos, 
                                               net.minecraft.util.RandomSource random) {
        return Monster.checkMonsterSpawnRules(type, level, spawnType, pos, random);
    }
    
    /**
     * Handle player interaction.
     */
    @Override
    public InteractionResult interactAt(Player player, Vec3 hitVec, InteractionHand hand) {
        ItemStack itemstack = player.getItemInHand(hand);
        
        // Check if player is the owner
        if (isOwnedBy(player)) {
            // If holding a wrench or similar tool, open configuration GUI
            if (itemstack.is(net.minecraft.tags.ItemTags.create(new net.minecraft.resources.ResourceLocation("neoforge", "tools/wrench")))) {
                if (!level().isClientSide) {
                    // Open configuration GUI
                    // This would be implemented with a packet to open the GUI
                    player.displayClientMessage(Component.literal("Configuring Obsidian Golem..."), true);
                }
                return InteractionResult.SUCCESS;
            }
            
            // Set task to follow player
            if (this.getTask().equals("idle")) {
                this.setTask("follow");
                if (!level().isClientSide) {
                    player.displayClientMessage(Component.literal("Obsidian Golem is now following you."), true);
                }
                return InteractionResult.SUCCESS;
            } else if (this.getTask().equals("follow")) {
                this.setTask("idle");
                if (!level().isClientSide) {
                    player.displayClientMessage(Component.literal("Obsidian Golem is now idle."), true);
                }
                return InteractionResult.SUCCESS;
            }
        } 
        // If no owner, player can claim construct
        else if (this.getOwnerUUID() == null) {
            if (!level().isClientSide) {
                this.setOwner(player.getUUID());
                player.displayClientMessage(Component.literal("You have claimed this Obsidian Golem."), true);
            }
            return InteractionResult.SUCCESS;
        }
        
        return InteractionResult.PASS;
    }
    
    /**
     * Get the current task.
     */
    public String getTask() {
        return this.entityData.get(DATA_TASK);
    }
    
    /**
     * Set the current task.
     */
    public void setTask(String task) {
        this.entityData.set(DATA_TASK, task);
    }
    
    /**
     * Set the owner UUID.
     */
    public void setOwner(UUID uuid) {
        this.owner = uuid;
        this.entityData.set(DATA_OWNER, uuid.toString());
    }
    
    /**
     * Get the owner UUID.
     */
    @Nullable
    public UUID getOwnerUUID() {
        if (this.owner == null && !this.entityData.get(DATA_OWNER).isEmpty()) {
            try {
                this.owner = UUID.fromString(this.entityData.get(DATA_OWNER));
            } catch (IllegalArgumentException e) {
                LOGGER.error("Invalid owner UUID for Obsidian Golem: {}", e.getMessage());
            }
        }
        return this.owner;
    }
    
    /**
     * Check if this construct is owned by the given entity.
     */
    public boolean isOwnedBy(Entity entity) {
        return entity.getUUID().equals(this.getOwnerUUID());
    }
    
    /**
     * Update entity AI.
     */
    @Override
    public void aiStep() {
        super.aiStep();
        
        if (!this.level().isClientSide) {
            this.updatePersistentAnger((ServerLevel)this.level(), true);
            
            // Handle tasks
            String task = getTask();
            if (task.equals("follow")) {
                // Follow owner
                UUID ownerUUID = getOwnerUUID();
                if (ownerUUID != null) {
                    Player owner = level().getPlayerByUUID(ownerUUID);
                    if (owner != null && this.distanceToSqr(owner) > 100.0D) {
                        // Teleport to owner if too far
                        this.teleportToEntity(owner);
                    }
                }
            } else if (task.equals("work") && workTarget != null) {
                // Perform work task
                if (this.blockPosition().distSqr(workTarget) <= 4.0D) {
                    // Close enough to work
                    workProgress++;
                    
                    // Finished work
                    if (workProgress >= 100) {
                        completeWork();
                    }
                }
            }
        }
    }
    
    /**
     * Complete the current work task.
     */
    private void completeWork() {
        // Task completed
        this.setTask("idle");
        this.workTarget = null;
        this.workProgress = 0;
    }
    
    /**
     * Teleport to an entity.
     */
    private void teleportToEntity(Entity entity) {
        BlockPos blockpos = entity.blockPosition();
        
        for(int i = 0; i < 10; i++) {
            double d0 = this.random.nextDouble() * 2.0D - 1.0D;
            double d1 = this.random.nextDouble() * 2.0D - 1.0D;
            
            int j = this.random.nextInt(2) * 2 - 1;
            int k = this.random.nextInt(2) * 2 - 1;
            
            double d2 = blockpos.getX() + 0.5D + 0.25D * j;
            double d3 = blockpos.getY() + this.random.nextInt(3) - 1;
            double d4 = blockpos.getZ() + 0.5D + 0.25D * k;
            
            if (this.randomTeleport(d2, d3, d4, true)) {
                this.playSound(SoundEvents.ENDERMAN_TELEPORT, 1.0F, 1.0F);
                return;
            }
        }
    }
    
    /**
     * Get step sound.
     */
    @Override
    protected SoundEvent getStepSound() {
        return SoundEvents.IRON_GOLEM_STEP;
    }
    
    /**
     * Get hurt sound.
     */
    @Override
    protected SoundEvent getHurtSound(DamageSource damageSource) {
        return SoundEvents.IRON_GOLEM_HURT;
    }
    
    /**
     * Get death sound.
     */
    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.IRON_GOLEM_DEATH;
    }
    
    /**
     * Save entity data.
     */
    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        this.addPersistentAngerSaveData(tag);
        
        tag.putString("Task", this.getTask());
        
        if (this.getOwnerUUID() != null) {
            tag.putString("Owner", this.getOwnerUUID().toString());
        }
        
        if (this.workTarget != null) {
            tag.putInt("WorkX", this.workTarget.getX());
            tag.putInt("WorkY", this.workTarget.getY());
            tag.putInt("WorkZ", this.workTarget.getZ());
            tag.putInt("WorkProgress", this.workProgress);
        }
    }
    
    /**
     * Load entity data.
     */
    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        this.readPersistentAngerSaveData(this.level(), tag);
        
        if (tag.contains("Task")) {
            this.setTask(tag.getString("Task").orElse("idle"));
        }
        
        if (tag.contains("Owner")) {
            try {
                this.setOwner(UUID.fromString(tag.getString("Owner").orElse("")));
            } catch (IllegalArgumentException e) {
                // Log invalid UUID format
            }
        }
        
        if (tag.contains("WorkX")) {
            this.workTarget = new BlockPos(
                    tag.getInt("WorkX").orElse(0),
                    tag.getInt("WorkY").orElse(0),
                    tag.getInt("WorkZ").orElse(0)
            );
            this.workProgress = tag.getInt("WorkProgress").orElse(0);
        }
    }
    
    // NeutralMob implementation
    
    @Override
    public void startPersistentAngerTimer() {
        this.setRemainingPersistentAngerTime(ANGER_TIME_RANGE.sample(this.random));
    }
    
    @Override
    public void setRemainingPersistentAngerTime(int time) {
        this.entityData.set(DATA_REMAINING_ANGER_TIME, time);
    }
    
    @Override
    public int getRemainingPersistentAngerTime() {
        return this.entityData.get(DATA_REMAINING_ANGER_TIME);
    }
    
    @Override
    public void setPersistentAngerTarget(@Nullable UUID uuid) {
        this.persistentAngerTarget = uuid;
    }
    
    @Override
    @Nullable
    public UUID getPersistentAngerTarget() {
        return this.persistentAngerTarget;
    }
    
    /**
     * Check if the construct can remain at a position.
     */
    @Override
    public float getWalkTargetValue(BlockPos pos, LevelReader level) {
        // Prefer to stay on obsidian
        return level.getBlockState(pos.below()).is(net.minecraft.world.level.block.Blocks.OBSIDIAN) ? 10.0F : super.getWalkTargetValue(pos, level);
    }
    
    /**
     * Handle block break.
     */
    @Override
    public boolean canBreakDoors() {
        return true;
    }
    
    /**
     * Check if an entity should be targeted.
     */
    @Override
    public boolean canAttack(LivingEntity target) {
        // Don't attack owner
        if (target instanceof Player && this.isOwnedBy(target)) {
            return false;
        }
        
        return super.canAttack(target);
    }
}
