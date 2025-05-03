package com.eldritchvoid.modules.voidcultists.entity;

import com.eldritchvoid.EldritchVoid;
import com.eldritchvoid.modules.voidcultists.VoidCultistsModule;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.goal.RangedAttackGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ThrownPotion;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nullable;
import java.util.UUID;
import java.util.function.Predicate;

/**
 * Void Cultist entity.
 * An NPC that belongs to the cult of the void.
 */
public class CultistEntity extends AbstractVillager implements RangedAttackMob {
    private static final Logger LOGGER = LogManager.getLogger();
    
    // Data parameters
    private static final EntityDataAccessor<Integer> DATA_CULTIST_TYPE = 
            SynchedEntityData.defineId(CultistEntity.class, EntityDataSerializers.INT);
    
    private static final EntityDataAccessor<Boolean> DATA_IS_AGGRESSIVE = 
            SynchedEntityData.defineId(CultistEntity.class, EntityDataSerializers.BOOLEAN);
    
    // Constants
    private static final Predicate<Player> PLAYER_REPUTATION_PREDICATE = 
            player -> EldritchVoid.instance.getModuleManager().getModule("voidcultists") instanceof VoidCultistsModule module &&
                     module.shouldBeHostileTo(player.getUUID());
    
    // Fields
    private int initialReputation;
    private UUID ritualSummoner;
    private int hostilityTimer;
    
    /**
     * Constructor.
     */
    public CultistEntity(EntityType<? extends AbstractVillager> entityType, Level level) {
        super(entityType, level);
        this.setCanPickUpLoot(true);
    }
    
    /**
     * Register entity data.
     */
    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_CULTIST_TYPE, 0);
        this.entityData.define(DATA_IS_AGGRESSIVE, false);
    }
    
    /**
     * Register entity goals.
     */
    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new RangedAttackGoal(this, 1.0D, 20, 40, 10.0F));
        this.goalSelector.addGoal(2, new AvoidEntityGoal<>(this, Mob.class, 8.0F, 0.6D, 0.6D, 
                mob -> mob.getType().is(net.minecraft.tags.EntityTypeTags.RAIDERS) && !mob.isAlliedTo(this)));
        this.goalSelector.addGoal(2, new MeleeAttackGoal(this, 0.8D, false));
        this.goalSelector.addGoal(3, new RandomStrollGoal(this, 0.6D));
        this.goalSelector.addGoal(4, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(5, new RandomLookAroundGoal(this));
        
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this).setAlertOthers());
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, 10, true, false, 
                PLAYER_REPUTATION_PREDICATE));
    }
    
    /**
     * Create entity attributes.
     */
    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 20.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.35D)
                .add(Attributes.ATTACK_DAMAGE, 2.0D)
                .add(Attributes.FOLLOW_RANGE, 16.0D);
    }
    
    /**
     * Check if a cultist can spawn at the given position.
     */
    public static boolean checkCultistSpawnRules(EntityType<CultistEntity> type, LevelAccessor level,
                                               MobSpawnType spawnType, BlockPos pos, 
                                               RandomSource random) {
        // Special cases for event-based spawning
        if (spawnType == MobSpawnType.EVENT || spawnType == MobSpawnType.SPAWNER) {
            return true;
        }
        
        // For natural spawning, require darkness and proximity to obsidian
        if (level.getBrightness(pos) > 8) {
            return false;
        }
        
        // Check for obsidian nearby
        boolean nearObsidian = false;
        for (int x = -4; x <= 4 && !nearObsidian; x++) {
            for (int y = -2; y <= 2 && !nearObsidian; y++) {
                for (int z = -4; z <= 4 && !nearObsidian; z++) {
                    BlockPos checkPos = pos.offset(x, y, z);
                    if (level.getBlockState(checkPos).is(net.minecraft.world.level.block.Blocks.OBSIDIAN) ||
                        level.getBlockState(checkPos).is(net.minecraft.world.level.block.Blocks.CRYING_OBSIDIAN)) {
                        nearObsidian = true;
                        break;
                    }
                }
            }
        }
        
        return nearObsidian && AbstractVillager.checkAbstractVillagerSpawnRules(type, level, spawnType, pos, random);
    }
    
    /**
     * Handle finishing spawning.
     */
    @Override
    public void finalizeSpawn(ServerLevel level, net.minecraft.world.DifficultyInstance difficulty, 
                             MobSpawnType reason, @Nullable net.minecraft.world.entity.SpawnGroupData spawnData, 
                             @Nullable CompoundTag tag) {
        super.finalizeSpawn(level, difficulty, reason, spawnData, tag);
        
        // Assign random cultist type
        this.setCultistType(level.getRandom().nextInt(4));
        
        // Equip the cultist
        this.populateDefaultEquipmentSlots(level.getRandom(), difficulty);
    }
    
    /**
     * Populate default equipment.
     */
    @Override
    protected void populateDefaultEquipmentSlots(RandomSource random, net.minecraft.world.DifficultyInstance difficulty) {
        // Give appropriate equipment based on cultist type
        switch (this.getCultistType()) {
            case 0: // Acolyte - basic cultist
                this.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(Items.IRON_SWORD));
                this.setItemSlot(EquipmentSlot.HEAD, new ItemStack(Items.LEATHER_HELMET));
                break;
                
            case 1: // Alchemist - uses potions
                // Give splash potions
                ItemStack harmingPotion = PotionUtils.setPotion(new ItemStack(Items.SPLASH_POTION), Potions.HARMING);
                this.setItemSlot(EquipmentSlot.MAINHAND, harmingPotion);
                this.setItemSlot(EquipmentSlot.HEAD, new ItemStack(Items.LEATHER_HELMET));
                
                // Backup potions in inventory
                for (int i = 0; i < 2; i++) {
                    ItemStack potion;
                    if (random.nextBoolean()) {
                        potion = PotionUtils.setPotion(new ItemStack(Items.SPLASH_POTION), Potions.HARMING);
                    } else {
                        potion = PotionUtils.setPotion(new ItemStack(Items.SPLASH_POTION), Potions.POISON);
                    }
                    this.inventory.addItem(potion);
                }
                break;
                
            case 2: // Priest - higher rank
                this.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(Items.GOLDEN_SWORD));
                this.setItemSlot(EquipmentSlot.HEAD, new ItemStack(Items.GOLDEN_HELMET));
                this.setItemSlot(EquipmentSlot.CHEST, new ItemStack(Items.GOLDEN_CHESTPLATE));
                break;
                
            case 3: // Void Caller - highest rank
                this.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(Items.NETHERITE_SWORD));
                this.setItemSlot(EquipmentSlot.HEAD, new ItemStack(Items.NETHERITE_HELMET));
                this.setItemSlot(EquipmentSlot.CHEST, new ItemStack(Items.LEATHER_CHESTPLATE));
                break;
        }
    }
    
    /**
     * Update entity AI.
     */
    @Override
    public void aiStep() {
        super.aiStep();
        
        // Check if we should update hostility
        if (!this.level().isClientSide && this.hostilityTimer > 0) {
            this.hostilityTimer--;
            
            if (this.hostilityTimer <= 0) {
                this.setAggressive(false);
            }
        }
    }
    
    /**
     * Get the cultist type.
     */
    public int getCultistType() {
        return this.entityData.get(DATA_CULTIST_TYPE);
    }
    
    /**
     * Set the cultist type.
     */
    public void setCultistType(int type) {
        this.entityData.set(DATA_CULTIST_TYPE, Math.max(0, Math.min(3, type)));
    }
    
    /**
     * Check if the cultist is aggressive.
     */
    public boolean isAggressive() {
        return this.entityData.get(DATA_IS_AGGRESSIVE);
    }
    
    /**
     * Set the aggressive state.
     */
    public void setAggressive(boolean aggressive) {
        this.entityData.set(DATA_IS_AGGRESSIVE, aggressive);
    }
    
    /**
     * Set the initial reputation for this cultist.
     */
    public void setInitialReputation(int reputation) {
        this.initialReputation = reputation;
    }
    
    /**
     * Set the player who summoned this cultist via ritual.
     */
    public void setRitualSummoner(UUID summoner) {
        this.ritualSummoner = summoner;
    }
    
    /**
     * Handle player interaction.
     */
    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        // If aggressive, no interaction
        if (this.isAggressive()) {
            return InteractionResult.PASS;
        }
        
        // Get player reputation
        VoidCultistsModule cultistsModule = (VoidCultistsModule) EldritchVoid.instance
                .getModuleManager().getModule("voidcultists");
        
        if (cultistsModule != null) {
            int reputation = cultistsModule.getReputation(player.getUUID());
            
            // If hostile, attack instead of interact
            if (reputation < VoidCultistsModule.REPUTATION_NEUTRAL) {
                this.setTarget(player);
                this.setAggressive(true);
                this.hostilityTimer = 400; // 20 seconds
                return InteractionResult.FAIL;
            }
            
            // Different interactions based on reputation
            if (!this.level().isClientSide) {
                if (reputation >= VoidCultistsModule.REPUTATION_TRUSTED) {
                    // High reputation - give special interaction
                    player.displayClientMessage(Component.translatable(
                            "eldritchvoid.cultist.greeting.trusted", player.getName()), false);
                    
                    // Chance to offer a special item or quest
                    if (this.random.nextFloat() < 0.3f) {
                        offerSpecialItem(player);
                    }
                } else if (reputation >= VoidCultistsModule.REPUTATION_FRIENDLY) {
                    // Friendly interaction
                    player.displayClientMessage(Component.translatable(
                            "eldritchvoid.cultist.greeting.friendly", player.getName()), false);
                } else {
                    // Neutral interaction
                    player.displayClientMessage(Component.translatable(
                            "eldritchvoid.cultist.greeting.neutral"), false);
                }
                
                // Tell player their current standing
                player.displayClientMessage(Component.translatable(
                        cultistsModule.getReputationLevel(player.getUUID())), false);
            }
            
            return InteractionResult.SUCCESS;
        }
        
        return InteractionResult.PASS;
    }
    
    /**
     * Offer a special item to a trusted player.
     */
    private void offerSpecialItem(Player player) {
        // Different offerings based on cultist type
        ItemStack offering;
        
        switch (this.getCultistType()) {
            case 1: // Alchemist
                // Offer a random special potion
                Potion[] potions = {
                    Potions.STRONG_HEALING,
                    Potions.STRONG_SWIFTNESS,
                    Potions.STRONG_STRENGTH,
                    Potions.INVISIBILITY
                };
                offering = PotionUtils.setPotion(
                        new ItemStack(Items.POTION),
                        potions[this.random.nextInt(potions.length)]);
                break;
                
            case 2: // Priest
                // Offer enchanted book or ender pearl
                if (this.random.nextBoolean()) {
                    offering = new ItemStack(Items.ENDER_PEARL, 2);
                } else {
                    offering = new ItemStack(Items.ENCHANTED_BOOK);
                    // Would add enchantment here
                }
                break;
                
            case 3: // Void Caller
                // Offer rare items
                switch (this.random.nextInt(3)) {
                    case 0:
                        offering = new ItemStack(Items.DIAMOND, 2);
                        break;
                    case 1:
                        offering = new ItemStack(Items.ENDER_EYE, 2);
                        break;
                    default:
                        offering = new ItemStack(Items.GOLDEN_APPLE);
                        break;
                }
                break;
                
            default: // Acolyte
                // Basic offerings
                offering = new ItemStack(Items.OBSIDIAN, 2);
                break;
        }
        
        // Give the item to the player
        if (!player.getInventory().add(offering)) {
            // Drop item if inventory is full
            player.drop(offering, false);
        }
        
        player.displayClientMessage(Component.translatable(
                "eldritchvoid.cultist.offering.given"), false);
    }
    
    /**
     * Implement RangedAttackMob - allow cultists to throw potions.
     */
    @Override
    public void performRangedAttack(net.minecraft.world.entity.LivingEntity target, float velocity) {
        if (this.getCultistType() == 1) { // Alchemist type
            // Throw potion at target
            ItemStack potionStack = null;
            
            // Find a potion in hand or inventory
            if (this.getMainHandItem().is(Items.SPLASH_POTION)) {
                potionStack = this.getMainHandItem();
            } else {
                for (int i = 0; i < this.inventory.getContainerSize(); i++) {
                    ItemStack stack = this.inventory.getItem(i);
                    if (stack.is(Items.SPLASH_POTION)) {
                        potionStack = stack;
                        break;
                    }
                }
            }
            
            if (potionStack != null) {
                // Create and throw potion entity
                ThrownPotion thrownPotion = new ThrownPotion(this.level(), this);
                thrownPotion.setItem(potionStack.copy());
                
                // Calculate velocity and angle
                double dx = target.getX() - this.getX();
                double dy = target.getEyeY() - 1.1 - this.getY();
                double dz = target.getZ() - this.getZ();
                double dist = Math.sqrt(dx * dx + dz * dz);
                
                thrownPotion.shoot(dx, dy + dist * 0.2, dz, 0.75F, 8.0F);
                
                // Throw the potion
                this.level().addFreshEntity(thrownPotion);
                
                // Play throw sound
                this.playSound(SoundEvents.WITCH_THROW, 1.0F, 0.8F + this.random.nextFloat() * 0.4F);
                
                // Consume the potion
                if (!this.getAbilities().instabuild) {
                    potionStack.shrink(1);
                }
            }
        }
    }
    
    /**
     * Get ambient sound.
     */
    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.VILLAGER_AMBIENT;
    }
    
    /**
     * Get hurt sound.
     */
    @Override
    protected SoundEvent getHurtSound(DamageSource damageSource) {
        return SoundEvents.VILLAGER_HURT;
    }
    
    /**
     * Get death sound.
     */
    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.VILLAGER_DEATH;
    }
    
    /**
     * Handle entity death.
     */
    @Override
    public void die(DamageSource source) {
        super.die(source);
        
        // If killed by a player, reduce their reputation
        if (source.getEntity() instanceof Player player) {
            VoidCultistsModule cultistsModule = (VoidCultistsModule) EldritchVoid.instance
                    .getModuleManager().getModule("voidcultists");
            
            if (cultistsModule != null) {
                // Bigger reputation hit for killing higher-ranked cultists
                int reputationLoss = -10;
                if (this.getCultistType() >= 2) {
                    reputationLoss = -30;
                } else if (this.getCultistType() == 1) {
                    reputationLoss = -20;
                }
                
                cultistsModule.changeReputation(player.getUUID(), reputationLoss);
                
                if (!this.level().isClientSide()) {
                    player.displayClientMessage(Component.translatable(
                            "eldritchvoid.cultist.killed"), false);
                }
            }
        }
    }
    
    /**
     * Create trades for the cultist.
     * AbstractVillager implementation.
     */
    @Override
    protected void updateTrades() {
        // Not implemented - cultists don't use the standard trading system
    }
    
    /**
     * Get the trading UI title.
     * AbstractVillager implementation.
     */
    @Override
    protected Component getTypeName() {
        String typeKey;
        switch (this.getCultistType()) {
            case 1:
                typeKey = "alchemist";
                break;
            case 2:
                typeKey = "priest";
                break;
            case 3:
                typeKey = "void_caller";
                break;
            default:
                typeKey = "acolyte";
                break;
        }
        
        return Component.translatable("entity.eldritchvoid.void_cultist." + typeKey);
    }
    
    /**
     * Save entity data.
     */
    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        
        tag.putInt("CultistType", this.getCultistType());
        tag.putBoolean("IsAggressive", this.isAggressive());
        tag.putInt("InitialReputation", this.initialReputation);
        tag.putInt("HostilityTimer", this.hostilityTimer);
        
        if (this.ritualSummoner != null) {
            tag.putUUID("RitualSummoner", this.ritualSummoner);
        }
    }
    
    /**
     * Load entity data.
     */
    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        
        this.setCultistType(tag.getInt("CultistType"));
        this.setAggressive(tag.getBoolean("IsAggressive"));
        this.initialReputation = tag.getInt("InitialReputation");
        this.hostilityTimer = tag.getInt("HostilityTimer");
        
        if (tag.hasUUID("RitualSummoner")) {
            this.ritualSummoner = tag.getUUID("RitualSummoner");
        }
    }
}
