package com.eldritchvoid.modules.voidcultists;

import com.eldritchvoid.EldritchVoid;
import com.eldritchvoid.core.IEldritchModule;
import com.eldritchvoid.core.registry.ModEntities;
import com.eldritchvoid.modules.voidcultists.entity.CultistEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.MobSpawnType.Type;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.village.poi.PoiManager;
import net.minecraft.world.entity.ai.village.poi.PoiTypes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.levelgen.Heightmap;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.event.lifecycle.InterModProcessEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.level.BlockEvent;
import net.neoforged.neoforge.event.village.VillagerTradesEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

/**
 * The Void Cultists module for Eldritch Void.
 * Handles NPC systems and interactions.
 */
public class VoidCultistsModule implements IEldritchModule {
    private static final Logger LOGGER = LogManager.getLogger();
    private final IEventBus modEventBus;
    private final Random random = new Random();
    
    // Reputation system - tracks player standing with the cult
    private final Map<UUID, Integer> cultistReputation = new HashMap<>();
    
    // Reputation thresholds
    public static final int REPUTATION_HOSTILE = -100;
    public static final int REPUTATION_NEUTRAL = 0;
    public static final int REPUTATION_FRIENDLY = 50;
    public static final int REPUTATION_TRUSTED = 100;
    public static final int REPUTATION_DEVOTED = 200;
    
    public VoidCultistsModule(IEventBus modEventBus) {
        this.modEventBus = modEventBus;
    }
    
    @Override
    public String getModuleId() {
        return "voidcultists";
    }
    
    @Override
    public String getDisplayName() {
        return "Void Cultists";
    }
    
    @Override
    public String getDescription() {
        return "Interact with NPCs who worship the void";
    }
    
    @Override
    public List<String> getDependencies() {
        return Collections.emptyList(); // No dependencies
    }
    
    @Override
    public void initialize() {
        LOGGER.info("Initializing Void Cultists Module");
        
        // Register event handlers
        modEventBus.addListener(this::onCommonSetup);
        modEventBus.addListener(this::onClientSetup);
        modEventBus.addListener(this::onEntityAttributeCreation);
        modEventBus.addListener(this::onRegisterRenderers);
        
        // Register interaction events
        NeoForge.EVENT_BUS.addListener(this::onPlayerInteract);
        NeoForge.EVENT_BUS.addListener(this::onEntityInteract);
        NeoForge.EVENT_BUS.addListener(this::onBlockPlace);
        NeoForge.EVENT_BUS.addListener(this::onVillagerTrades);
    }
    
    @Override
    public void onCommonSetup() {
        LOGGER.info("Void Cultists: Common Setup");
        
        // Register spawn placements
        SpawnPlacements.register(
                ModEntities.VOID_CULTIST.get(),
                SpawnPlacements.Type.ON_GROUND,
                Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                CultistEntity::checkCultistSpawnRules
        );
    }
    
    @Override
    public void onClientSetup() {
        LOGGER.info("Void Cultists: Client Setup");
        
        // Client setup will be handled in onRegisterRenderers
    }
    
    /**
     * Register entity attributes.
     */
    private void onEntityAttributeCreation(EntityAttributeCreationEvent event) {
        event.put(ModEntities.VOID_CULTIST.get(), CultistEntity.createAttributes()
                .add(Attributes.MAX_HEALTH, 20.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.35D)
                .add(Attributes.ATTACK_DAMAGE, 2.0D)
                .add(Attributes.FOLLOW_RANGE, 16.0D)
                .build());
    }
    
    /**
     * Register entity renderers.
     */
    private void onRegisterRenderers(EntityRenderersEvent.RegisterRenderers event) {
        // Register entity renderer
        // event.registerEntityRenderer(ModEntities.VOID_CULTIST.get(), CultistRenderer::new);
        
        // This would be a proper implementation with a real renderer
        // For now, we'll use a placeholder
    }
    
    /**
     * Handle player right-click interaction.
     */
    private void onPlayerInteract(PlayerInteractEvent.RightClickBlock event) {
        // Check for ritual altar activation
        if (event.getLevel().getBlockState(event.getPos()).is(net.minecraft.world.level.block.Blocks.OBSIDIAN) &&
                event.getItemStack().is(net.minecraft.world.level.block.Blocks.OBSIDIAN.asItem())) {
            
            // Check for obsidian pattern that could form a ritual altar
            if (isValidRitualPattern(event.getLevel(), event.getPos())) {
                if (!event.getLevel().isClientSide()) {
                    // Spawn cultist if player reputation is not hostile
                    Player player = event.getEntity();
                    int reputation = getReputation(player.getUUID());
                    
                    if (reputation > REPUTATION_HOSTILE) {
                        // Try to spawn a cultist nearby
                        trySpawnCultist((ServerLevel)event.getLevel(), event.getPos(), player);
                        
                        // Increase reputation slightly for performing the ritual
                        changeReputation(player.getUUID(), 5);
                        
                        player.displayClientMessage(net.minecraft.network.chat.Component.translatable(
                                "eldritchvoid.cultist.ritual_success"), false);
                    } else {
                        // Player is hostile, cultists won't respond
                        player.displayClientMessage(net.minecraft.network.chat.Component.translatable(
                                "eldritchvoid.cultist.ritual_ignored"), false);
                    }
                }
                
                // Play sound effect
                event.getLevel().playSound(null, event.getPos(), 
                        net.minecraft.sounds.SoundEvents.ENDERMAN_TELEPORT, 
                        event.getEntity().getSoundSource(), 1.0F, 0.8F);
                
                // Consume the item if not in creative mode
                if (!event.getEntity().getAbilities().instabuild) {
                    event.getItemStack().shrink(1);
                }
                
                event.setCanceled(true);
            }
        }
    }
    
    /**
     * Check if a block pattern forms a valid ritual altar.
     */
    private boolean isValidRitualPattern(net.minecraft.world.level.Level level, BlockPos pos) {
        // Check for a 3x3 platform of obsidian
        for (int x = -1; x <= 1; x++) {
            for (int z = -1; z <= 1; z++) {
                if (!level.getBlockState(pos.offset(x, 0, z)).is(net.minecraft.world.level.block.Blocks.OBSIDIAN)) {
                    return false;
                }
            }
        }
        
        // Check for at least 2 candles or similar blocks around
        int decorations = 0;
        for (int x = -2; x <= 2; x++) {
            for (int z = -2; z <= 2; z++) {
                // Skip the central 3x3
                if (Math.abs(x) <= 1 && Math.abs(z) <= 1) continue;
                
                BlockPos checkPos = pos.offset(x, 0, z);
                if (level.getBlockState(checkPos).is(net.minecraft.world.level.block.Blocks.CANDLE) ||
                    level.getBlockState(checkPos).is(net.minecraft.world.level.block.Blocks.SOUL_FIRE) ||
                    level.getBlockState(checkPos).is(net.minecraft.world.level.block.Blocks.CRYING_OBSIDIAN)) {
                    decorations++;
                }
            }
        }
        
        return decorations >= 2;
    }
    
    /**
     * Try to spawn a cultist near the ritual altar.
     */
    private void trySpawnCultist(ServerLevel level, BlockPos pos, Player player) {
        // Find a suitable spawn position
        for (int attempts = 0; attempts < 10; attempts++) {
            int x = pos.getX() + random.nextInt(7) - 3;
            int z = pos.getZ() + random.nextInt(7) - 3;
            BlockPos spawnPos = new BlockPos(x, pos.getY(), z);
            
            // Ensure there's enough space and it's on solid ground
            if (level.getBlockState(spawnPos).isAir() && 
                level.getBlockState(spawnPos.above()).isAir() &&
                level.getBlockState(spawnPos.below()).isSolid()) {
                
                // Spawn the cultist
                CultistEntity cultist = ModEntities.VOID_CULTIST.get().spawn(
                        level, spawnPos, Type.EVENT);
                
                if (cultist != null) {
                    cultist.setInitialReputation(getReputation(player.getUUID()));
                    cultist.setRitualSummoner(player.getUUID());
                    
                    // Make cultist look at player
                    cultist.getLookControl().setLookAt(player, 30.0F, 30.0F);
                    
                    LOGGER.debug("Spawned cultist at {} in response to ritual", spawnPos);
                    return;
                }
            }
        }
        
        LOGGER.debug("Failed to find suitable location to spawn cultist");
    }
    
    /**
     * Handle entity interaction.
     */
    private void onEntityInteract(PlayerInteractEvent.EntityInteract event) {
        if (event.getTarget() instanceof CultistEntity cultist) {
            Player player = event.getEntity();
            
            // Check if player is holding an offering
            if (!player.getItemInHand(event.getHand()).isEmpty()) {
                handleOffering(player, cultist, player.getItemInHand(event.getHand()));
            }
        }
    }
    
    /**
     * Handle a player offering an item to a cultist.
     */
    private void handleOffering(Player player, CultistEntity cultist, net.minecraft.world.item.ItemStack offering) {
        // Calculate the value of the offering
        int value = calculateOfferingValue(offering);
        
        if (value > 0) {
            // Accept the offering
            if (!player.level().isClientSide()) {
                // Increase reputation based on value
                changeReputation(player.getUUID(), value);
                
                // Cultist's reaction depends on the value
                if (value >= 20) {
                    // Valuable offering
                    player.displayClientMessage(net.minecraft.network.chat.Component.translatable(
                            "eldritchvoid.cultist.offering_valuable"), false);
                    
                    // High-value offerings might get a reward
                    if (random.nextFloat() < 0.3f) {
                        giveReward(player, cultist);
                    }
                } else {
                    // Standard offering
                    player.displayClientMessage(net.minecraft.network.chat.Component.translatable(
                            "eldritchvoid.cultist.offering_accepted"), false);
                }
                
                // Consume the item
                offering.shrink(1);
            }
            
            // Play acceptance sound
            player.level().playSound(null, player.blockPosition(), 
                    net.minecraft.sounds.SoundEvents.VILLAGER_YES, 
                    player.getSoundSource(), 0.7F, 0.9F);
        } else {
            // Reject the offering
            if (!player.level().isClientSide()) {
                player.displayClientMessage(net.minecraft.network.chat.Component.translatable(
                        "eldritchvoid.cultist.offering_rejected"), false);
            }
            
            // Play rejection sound
            player.level().playSound(null, player.blockPosition(), 
                    net.minecraft.sounds.SoundEvents.VILLAGER_NO, 
                    player.getSoundSource(), 0.7F, 0.9F);
        }
    }
    
    /**
     * Calculate the value of an offering.
     */
    private int calculateOfferingValue(net.minecraft.world.item.ItemStack offering) {
        // Base values for common offerings
        if (offering.is(net.minecraft.world.level.block.Blocks.OBSIDIAN.asItem())) {
            return 5 * offering.getCount();
        }
        
        if (offering.is(net.minecraft.world.level.block.Blocks.CRYING_OBSIDIAN.asItem())) {
            return 10 * offering.getCount();
        }
        
        if (offering.is(net.minecraft.world.item.Items.ENDER_PEARL)) {
            return 15 * offering.getCount();
        }
        
        if (offering.is(net.minecraft.world.item.Items.ENDER_EYE)) {
            return 25 * offering.getCount();
        }
        
        if (offering.is(net.minecraft.world.item.Items.DRAGON_BREATH)) {
            return 40 * offering.getCount();
        }
        
        if (offering.is(net.minecraft.world.item.Items.DRAGON_EGG)) {
            return 100;
        }
        
        // Enchanted items have value
        if (offering.isEnchanted()) {
            return 20;
        }
        
        // Most other items have no value
        return 0;
    }
    
    /**
     * Give a reward to a player based on reputation.
     */
    private void giveReward(Player player, CultistEntity cultist) {
        int reputation = getReputation(player.getUUID());
        net.minecraft.world.item.ItemStack reward;
        
        if (reputation >= REPUTATION_DEVOTED) {
            // High-tier rewards for devoted followers
            switch (random.nextInt(4)) {
                case 0:
                    reward = new net.minecraft.world.item.ItemStack(net.minecraft.world.item.Items.NETHERITE_INGOT);
                    break;
                case 1:
                    reward = new net.minecraft.world.item.ItemStack(net.minecraft.world.item.Items.TOTEM_OF_UNDYING);
                    break;
                case 2:
                    reward = new net.minecraft.world.item.ItemStack(net.minecraft.world.item.Items.ENCHANTED_GOLDEN_APPLE);
                    break;
                default:
                    reward = new net.minecraft.world.item.ItemStack(net.minecraft.world.item.Items.WITHER_SKELETON_SKULL);
                    break;
            }
        } else if (reputation >= REPUTATION_TRUSTED) {
            // Mid-tier rewards for trusted members
            switch (random.nextInt(4)) {
                case 0:
                    reward = new net.minecraft.world.item.ItemStack(net.minecraft.world.item.Items.DIAMOND, 2);
                    break;
                case 1:
                    reward = new net.minecraft.world.item.ItemStack(net.minecraft.world.item.Items.GOLDEN_APPLE, 2);
                    break;
                case 2:
                    reward = new net.minecraft.world.item.ItemStack(net.minecraft.world.item.Items.ENDER_EYE, 4);
                    break;
                default:
                    // Enchanted book
                    reward = net.minecraft.world.item.enchantment.EnchantmentHelper.enchantItem(
                            random, 
                            new net.minecraft.world.item.ItemStack(net.minecraft.world.item.Items.BOOK), 
                            15 + random.nextInt(15), 
                            true);
                    break;
            }
        } else {
            // Basic rewards for newer members
            switch (random.nextInt(4)) {
                case 0:
                    reward = new net.minecraft.world.item.ItemStack(net.minecraft.world.item.Items.ENDER_PEARL, 2);
                    break;
                case 1:
                    reward = new net.minecraft.world.item.ItemStack(net.minecraft.world.level.block.Blocks.OBSIDIAN.asItem(), 4);
                    break;
                case 2:
                    reward = new net.minecraft.world.item.ItemStack(net.minecraft.world.item.Items.GOLD_INGOT, 3);
                    break;
                default:
                    reward = new net.minecraft.world.item.ItemStack(net.minecraft.world.item.Items.IRON_INGOT, 5);
                    break;
            }
        }
        
        // Give the reward to the player
        if (!player.getInventory().add(reward)) {
            // Drop the item if inventory is full
            net.minecraft.world.entity.item.ItemEntity itemEntity = new net.minecraft.world.entity.item.ItemEntity(
                    player.level(), 
                    cultist.getX(), 
                    cultist.getY() + 1.0, 
                    cultist.getZ(), 
                    reward);
            player.level().addFreshEntity(itemEntity);
        }
        
        player.displayClientMessage(net.minecraft.network.chat.Component.translatable(
                "eldritchvoid.cultist.reward_given"), false);
    }
    
    /**
     * Handle block placement for cult-related structures.
     */
    private void onBlockPlace(BlockEvent.EntityPlaceEvent event) {
        if (event.getEntity() instanceof Player player && 
                event.getPlacedBlock().is(net.minecraft.world.level.block.Blocks.OBSIDIAN)) {
            
            // Check if this completes a cult shrine structure
            if (isCompleteCultShrine(event.getLevel(), event.getPos())) {
                // Reward player for building a shrine
                if (!event.getLevel().isClientSide()) {
                    changeReputation(player.getUUID(), 10);
                    player.displayClientMessage(net.minecraft.network.chat.Component.translatable(
                            "eldritchvoid.cultist.shrine_built"), false);
                }
            }
        }
    }
    
    /**
     * Check if a block is part of a complete cult shrine.
     */
    private boolean isCompleteCultShrine(net.minecraft.world.level.LevelAccessor level, BlockPos pos) {
        // Count obsidian blocks in the area
        int obsidianCount = 0;
        boolean hasAltarBlock = false;
        
        for (int x = -2; x <= 2; x++) {
            for (int y = -2; y <= 2; y++) {
                for (int z = -2; z <= 2; z++) {
                    BlockPos checkPos = pos.offset(x, y, z);
                    if (level.getBlockState(checkPos).is(net.minecraft.world.level.block.Blocks.OBSIDIAN)) {
                        obsidianCount++;
                    } else if (level.getBlockState(checkPos).is(net.minecraft.world.level.block.Blocks.CRYING_OBSIDIAN)) {
                        hasAltarBlock = true;
                    }
                }
            }
        }
        
        // A shrine should have at least 10 obsidian blocks and an altar block
        return obsidianCount >= 10 && hasAltarBlock;
    }
    
    /**
     * Handle integration with village trades.
     */
    private void onVillagerTrades(VillagerTradesEvent event) {
        // Add cultist-related items to clerics
        if (event.getType() == net.minecraft.world.entity.npc.VillagerProfession.CLERIC) {
            event.getTrades().get(3).add(new net.minecraft.world.entity.npc.VillagerTrades.ItemsForEmeralds(
                    net.minecraft.world.level.block.Blocks.CRYING_OBSIDIAN.asItem(), 5, 1, 5, 10));
            
            event.getTrades().get(5).add(new net.minecraft.world.entity.npc.VillagerTrades.ItemsForEmeralds(
                    net.minecraft.world.item.Items.ENDER_EYE, 7, 1, 3, 15));
        }
    }
    
    @Override
    public void onInterModEnqueue() {
        // Send IMC messages to other mods
    }
    
    @Override
    public void onInterModProcess(InterModProcessEvent event) {
        // Process IMC messages from other mods
    }
    
    /**
     * Get a player's reputation with the cult.
     * 
     * @param playerId The player UUID
     * @return The reputation value
     */
    public int getReputation(UUID playerId) {
        return cultistReputation.getOrDefault(playerId, REPUTATION_NEUTRAL);
    }
    
    /**
     * Change a player's reputation with the cult.
     * 
     * @param playerId The player UUID
     * @param change The amount to change (positive or negative)
     * @return The new reputation value
     */
    public int changeReputation(UUID playerId, int change) {
        int currentRep = getReputation(playerId);
        int newRep = currentRep + change;
        
        cultistReputation.put(playerId, newRep);
        LOGGER.debug("Changed player {} reputation from {} to {}", playerId, currentRep, newRep);
        
        return newRep;
    }
    
    /**
     * Get a player's reputation level as a string.
     * 
     * @param playerId The player UUID
     * @return The reputation level string
     */
    public String getReputationLevel(UUID playerId) {
        int rep = getReputation(playerId);
        
        if (rep < REPUTATION_HOSTILE) {
            return "eldritchvoid.cultist.reputation.hated";
        } else if (rep < REPUTATION_NEUTRAL) {
            return "eldritchvoid.cultist.reputation.hostile";
        } else if (rep < REPUTATION_FRIENDLY) {
            return "eldritchvoid.cultist.reputation.neutral";
        } else if (rep < REPUTATION_TRUSTED) {
            return "eldritchvoid.cultist.reputation.friendly";
        } else if (rep < REPUTATION_DEVOTED) {
            return "eldritchvoid.cultist.reputation.trusted";
        } else {
            return "eldritchvoid.cultist.reputation.devoted";
        }
    }
    
    /**
     * Check if cultists should be hostile to a player.
     * 
     * @param playerId The player UUID
     * @return True if cultists should be hostile
     */
    public boolean shouldBeHostileTo(UUID playerId) {
        return getReputation(playerId) < REPUTATION_NEUTRAL;
    }
}
