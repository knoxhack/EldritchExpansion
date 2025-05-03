package com.eldritchvoid.modules.eldritchdimensions;

import com.eldritchvoid.EldritchVoid;
import com.eldritchvoid.core.IEldritchModule;
import com.eldritchvoid.core.registry.ModBlocks;
import com.eldritchvoid.modules.eldritchdimensions.dimension.VoidDimension;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.event.lifecycle.InterModProcessEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The Eldritch Dimensions module for Eldritch Void.
 * Handles custom dimension generation and teleportation.
 */
public class EldritchDimensionsModule implements IEldritchModule {
    private static final Logger LOGGER = LogManager.getLogger();
    private final IEventBus modEventBus;
    
    // Cached portal locations for each player
    private final Map<ResourceKey<Level>, Map<String, BlockPos>> portalLocations = new HashMap<>();
    
    public EldritchDimensionsModule(IEventBus modEventBus) {
        this.modEventBus = modEventBus;
    }
    
    @Override
    public String getModuleId() {
        return "eldritchdimensions";
    }
    
    @Override
    public String getDisplayName() {
        return "Eldritch Dimensions";
    }
    
    @Override
    public String getDescription() {
        return "Travel to otherworldly dimensions filled with strange wonders";
    }
    
    @Override
    public List<String> getDependencies() {
        return Collections.emptyList(); // No dependencies
    }
    
    @Override
    public void initialize() {
        LOGGER.info("Initializing Eldritch Dimensions Module");
        
        // Register event handlers
        modEventBus.addListener(this::onCommonSetup);
        modEventBus.addListener(this::onClientSetup);
        
        // Register world events
        NeoForge.EVENT_BUS.addListener(this::onBlockRightClick);
        NeoForge.EVENT_BUS.addListener(this::onItemUse);
        
        // Initialize Void Dimension
        VoidDimension.register(modEventBus);
    }
    
    @Override
    public void onCommonSetup() {
        LOGGER.info("Eldritch Dimensions: Common Setup");
        
        // Initialize dimension types and biomes
    }
    
    @Override
    public void onClientSetup() {
        LOGGER.info("Eldritch Dimensions: Client Setup");
        
        // Register dimension effects
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
     * Handle right-clicking on a void portal block.
     */
    private void onBlockRightClick(PlayerInteractEvent.RightClickBlock event) {
        Block block = event.getLevel().getBlockState(event.getPos()).getBlock();
        
        if (block == ModBlocks.VOID_PORTAL.get()) {
            Player player = event.getEntity();
            
            if (!player.level().isClientSide() && player instanceof ServerPlayer serverPlayer) {
                // Attempt to teleport the player
                teleportPlayer(serverPlayer, event.getPos());
                event.setCanceled(true);
            }
        }
    }
    
    /**
     * Handle using a void key item.
     */
    private void onItemUse(PlayerInteractEvent.RightClickItem event) {
        ItemStack itemStack = event.getItemStack();
        
        if (itemStack.is(net.minecraft.world.item.Items.ENDER_EYE)) { // Replace with VOID_KEY when available
            Player player = event.getEntity();
            
            if (!player.level().isClientSide() && player instanceof ServerPlayer serverPlayer) {
                // Check if player is near obsidian structure
                if (isNearVoidPortalFrame(player)) {
                    // Create or activate portal
                    createVoidPortal(serverPlayer);
                    
                    // Consume item if not in creative mode
                    if (!player.getAbilities().instabuild) {
                        itemStack.shrink(1);
                    }
                    
                    event.setCanceled(true);
                }
            }
        }
    }
    
    /**
     * Check if a player is near a valid void portal frame.
     */
    private boolean isNearVoidPortalFrame(Player player) {
        // Check for valid portal frame structure
        // This would be a proper implementation with frame detection
        // For now, we'll use a placeholder check
        
        BlockPos playerPos = player.blockPosition();
        
        // Check for obsidian blocks in a portal-like formation
        int obsidianCount = 0;
        for (int x = -2; x <= 2; x++) {
            for (int y = -1; y <= 3; y++) {
                for (int z = -2; z <= 2; z++) {
                    BlockPos checkPos = playerPos.offset(x, y, z);
                    if (player.level().getBlockState(checkPos).is(net.minecraft.world.level.block.Blocks.OBSIDIAN)) {
                        obsidianCount++;
                    }
                }
            }
        }
        
        // Require at least 14 obsidian blocks in the vicinity
        return obsidianCount >= 14;
    }
    
    /**
     * Create a void portal at the player's location.
     */
    private void createVoidPortal(ServerPlayer player) {
        BlockPos playerPos = player.blockPosition();
        Level level = player.level();
        
        // Find suitable location for portal
        BlockPos portalPos = null;
        for (int y = -1; y <= 2; y++) {
            for (int x = -2; x <= 2; x++) {
                for (int z = -2; z <= 2; z++) {
                    BlockPos checkPos = playerPos.offset(x, y, z);
                    
                    // If there's air with obsidian below
                    if (level.getBlockState(checkPos).isAir() && 
                            level.getBlockState(checkPos.below()).is(net.minecraft.world.level.block.Blocks.OBSIDIAN)) {
                        portalPos = checkPos;
                        break;
                    }
                }
                if (portalPos != null) break;
            }
            if (portalPos != null) break;
        }
        
        if (portalPos != null) {
            // Create portal block
            level.setBlock(portalPos, ModBlocks.VOID_PORTAL.get().defaultBlockState(), 3);
            
            // Play portal creation sound
            level.playSound(null, portalPos, SoundEvents.END_PORTAL_SPAWN, 
                    player.getSoundSource(), 1.0F, 1.0F);
            
            // Store portal location
            ResourceKey<Level> dimensionKey = level.dimension();
            
            portalLocations.computeIfAbsent(dimensionKey, k -> new HashMap<>())
                    .put(player.getStringUUID(), portalPos);
            
            LOGGER.debug("Created void portal at {} for player {}", portalPos, player.getName().getString());
        }
    }
    
    /**
     * Teleport a player to/from the Void Dimension.
     */
    private void teleportPlayer(ServerPlayer player, BlockPos portalPos) {
        ResourceKey<Level> currentDimension = player.level().dimension();
        
        // Determine target dimension
        ResourceKey<Level> targetDimension;
        if (currentDimension.equals(VoidDimension.VOID_REALM_KEY)) {
            // Going from void realm to overworld
            targetDimension = Level.OVERWORLD;
        } else {
            // Going from any dimension to void realm
            targetDimension = VoidDimension.VOID_REALM_KEY;
        }
        
        // Get server instance
        net.minecraft.server.MinecraftServer server = player.getServer();
        if (server == null) {
            LOGGER.error("Failed to get server instance for teleportation");
            return;
        }
        
        // Get target level
        ServerLevel targetLevel = server.getLevel(targetDimension);
        if (targetLevel == null) {
            LOGGER.error("Target dimension does not exist: {}", targetDimension.location());
            return;
        }
        
        // Find or create target portal
        BlockPos targetPos = findOrCreateTargetPortal(player, portalPos, currentDimension, targetDimension, targetLevel);
        
        // Teleport player
        player.teleportTo(targetLevel, 
                targetPos.getX() + 0.5, 
                targetPos.getY() + 1.0, 
                targetPos.getZ() + 0.5, 
                player.getYRot(), player.getXRot());
        
        // Play teleport sound
        player.playSound(SoundEvents.ENDERMAN_TELEPORT, 1.0F, 1.0F);
        
        LOGGER.debug("Teleported player {} to dimension {} at {}", 
                player.getName().getString(), targetDimension.location(), targetPos);
    }
    
    /**
     * Find or create a target portal for teleportation.
     */
    private BlockPos findOrCreateTargetPortal(ServerPlayer player, BlockPos sourcePortal, 
                                             ResourceKey<Level> sourceDimension, 
                                             ResourceKey<Level> targetDimension,
                                             ServerLevel targetLevel) {
        
        // Check if we have a cached portal location
        Map<String, BlockPos> dimensionPortals = portalLocations.computeIfAbsent(targetDimension, k -> new HashMap<>());
        String playerUuid = player.getStringUUID();
        
        if (dimensionPortals.containsKey(playerUuid)) {
            BlockPos targetPos = dimensionPortals.get(playerUuid);
            
            // Verify portal still exists
            if (targetLevel.getBlockState(targetPos).is(ModBlocks.VOID_PORTAL.get())) {
                return targetPos;
            }
        }
        
        // No existing portal, create a new one
        BlockPos targetPos;
        
        if (targetDimension.equals(VoidDimension.VOID_REALM_KEY)) {
            // Going to void dimension - find a safe platform
            targetPos = VoidDimension.findSafeSpawnLocation(targetLevel, 0, 100, 0);
            
            // Create obsidian platform
            for (int x = -1; x <= 1; x++) {
                for (int z = -1; z <= 1; z++) {
                    targetLevel.setBlock(targetPos.offset(x, -1, z), 
                            net.minecraft.world.level.block.Blocks.OBSIDIAN.defaultBlockState(), 3);
                    
                    // Clear space above platform
                    for (int y = 0; y <= 2; y++) {
                        targetLevel.setBlock(targetPos.offset(x, y, z), 
                                net.minecraft.world.level.block.Blocks.AIR.defaultBlockState(), 3);
                    }
                }
            }
            
            // Create portal block in center
            targetLevel.setBlock(targetPos, ModBlocks.VOID_PORTAL.get().defaultBlockState(), 3);
            
        } else {
            // Going to overworld or other dimension
            // Try to find a safe location near spawn
            targetPos = targetLevel.getSharedSpawnPos();
            targetPos = targetLevel.getHeightmapPos(net.minecraft.world.level.levelgen.Heightmap.Types.MOTION_BLOCKING, targetPos);
            
            // Create obsidian platform
            for (int x = -1; x <= 1; x++) {
                for (int z = -1; z <= 1; z++) {
                    targetLevel.setBlock(targetPos.offset(x, -1, z), 
                            net.minecraft.world.level.block.Blocks.OBSIDIAN.defaultBlockState(), 3);
                    
                    // Clear space above platform
                    for (int y = 0; y <= 2; y++) {
                        targetLevel.setBlock(targetPos.offset(x, y, z), 
                                net.minecraft.world.level.block.Blocks.AIR.defaultBlockState(), 3);
                    }
                }
            }
            
            // Create portal block in center
            targetLevel.setBlock(targetPos, ModBlocks.VOID_PORTAL.get().defaultBlockState(), 3);
        }
        
        // Cache portal location
        dimensionPortals.put(playerUuid, targetPos);
        
        return targetPos;
    }
}
