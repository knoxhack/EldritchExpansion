package com.eldritchvoid.core.network;

import com.eldritchvoid.EldritchVoid;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.NetworkEvent;
import net.neoforged.neoforge.network.NetworkRegistry;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.simple.SimpleChannel;

import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Handles networking for the Eldritch Void mod.
 */
public class PacketHandler {
    private static final String PROTOCOL_VERSION = "1";
    
    public static final SimpleChannel INSTANCE = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(EldritchVoid.MOD_ID, "main"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals
    );
    
    private static int messageId = 0;
    
    /**
     * Initialize the packet handler.
     */
    public static void init() {
        EldritchVoid.LOGGER.info("Initializing packet handler");
        
        // Register packets
        registerResearchSyncPacket();
        registerVoidCorruptionPacket();
        registerModuleStatusPacket();
    }
    
    /**
     * Register a packet with the network channel.
     * 
     * @param messageSupplier Supplier for creating new message instances
     * @param encoder Function for encoding the message
     * @param decoder Function for decoding the message
     * @param handler Function for handling the message
     * @param <MSG> The message type
     */
    public static <MSG> void registerMessage(Supplier<MSG> messageSupplier, 
                                            BiConsumer<MSG, FriendlyByteBuf> encoder,
                                            Function<FriendlyByteBuf, MSG> decoder,
                                            BiConsumer<MSG, Supplier<NetworkEvent.Context>> handler) {
        INSTANCE.registerMessage(messageId++, messageSupplier.get().getClass(), encoder, decoder, handler);
    }
    
    /**
     * Send a packet to a specific player.
     * 
     * @param message The packet to send
     * @param player The player to send to
     * @param <MSG> The message type
     */
    public static <MSG> void sendToPlayer(MSG message, ServerPlayer player) {
        INSTANCE.send(PacketDistributor.PLAYER.with(() -> player), message);
    }
    
    /**
     * Send a packet to all players.
     * 
     * @param message The packet to send
     * @param <MSG> The message type
     */
    public static <MSG> void sendToAll(MSG message) {
        INSTANCE.send(PacketDistributor.ALL.noArg(), message);
    }
    
    /**
     * Send a packet to all players near a point.
     * 
     * @param message The packet to send
     * @param pointDistributor The point distributor
     * @param <MSG> The message type
     */
    public static <MSG> void sendToAllAround(MSG message, PacketDistributor.TargetPoint pointDistributor) {
        INSTANCE.send(PacketDistributor.NEAR.with(() -> pointDistributor), message);
    }
    
    /**
     * Send a packet to the server.
     * 
     * @param message The packet to send
     * @param <MSG> The message type
     */
    public static <MSG> void sendToServer(MSG message) {
        INSTANCE.sendToServer(message);
    }
    
    /**
     * Register the research sync packet.
     */
    private static void registerResearchSyncPacket() {
        // Register packet for syncing research data between client and server
        // Will be implemented in the Eldritch Arcana module
    }
    
    /**
     * Register the void corruption packet.
     */
    private static void registerVoidCorruptionPacket() {
        // Register packet for syncing void corruption data between client and server
        // Will be implemented in the Void Corruption module
    }
    
    /**
     * Register the module status packet.
     */
    private static void registerModuleStatusPacket() {
        // Register packet for syncing module status between client and server
    }
}
