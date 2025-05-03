package com.eldritchvoid.core.network;

import com.eldritchvoid.EldritchVoid;
import com.eldritchvoid.core.registry.Registration;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.fml.LogicalSide;
import net.neoforged.neoforge.network.NetworkEvent;
import net.neoforged.neoforge.network.NetworkRegistry;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.simple.SimpleChannel;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * Centralized networking system for modules.
 * Provides a unified way to register and send packets between server and client.
 */
public class ModuleNetwork {
    private static final String PROTOCOL_VERSION = "1.0";
    private static SimpleChannel CHANNEL;
    private static int ID = 0;
    
    private static final Map<String, SimpleChannel> MODULE_CHANNELS = new HashMap<>();
    
    /**
     * Initialize the network system.
     */
    public static void init() {
        // Create the main channel
        CHANNEL = NetworkRegistry.newSimpleChannel(
            Registration.location("main"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals
        );
        
        EldritchVoid.LOGGER.info("Initialized module network system");
    }
    
    /**
     * Register a packet.
     *
     * @param messageType The message class
     * @param encoder The encoder for the message
     * @param decoder The decoder for the message
     * @param handler The message handler
     * @param <T> The message type
     */
    public static <T> void registerPacket(Class<T> messageType, BiConsumer<T, FriendlyByteBuf> encoder, 
                                       Function<FriendlyByteBuf, T> decoder, 
                                       BiConsumer<T, Supplier<NetworkEvent.Context>> handler) {
        CHANNEL.registerMessage(ID++, messageType, encoder, decoder, handler);
        EldritchVoid.LOGGER.debug("Registered packet: {}", messageType.getSimpleName());
    }
    
    /**
     * Register a packet with a side check.
     *
     * @param messageType The message class
     * @param encoder The encoder for the message
     * @param decoder The decoder for the message
     * @param handler The message handler
     * @param sideCheck The side check predicate
     * @param <T> The message type
     */
    public static <T> void registerPacket(Class<T> messageType, BiConsumer<T, FriendlyByteBuf> encoder, 
                                       Function<FriendlyByteBuf, T> decoder, 
                                       BiConsumer<T, Supplier<NetworkEvent.Context>> handler,
                                       Predicate<Supplier<NetworkEvent.Context>> sideCheck) {
        BiConsumer<T, Supplier<NetworkEvent.Context>> wrappedHandler = (msg, contextSupplier) -> {
            NetworkEvent.Context context = contextSupplier.get();
            if (sideCheck.test(contextSupplier)) {
                handler.accept(msg, contextSupplier);
            }
            context.setPacketHandled(true);
        };
        
        CHANNEL.registerMessage(ID++, messageType, encoder, decoder, wrappedHandler);
        EldritchVoid.LOGGER.debug("Registered packet with side check: {}", messageType.getSimpleName());
    }
    
    /**
     * Create a side check predicate for the given side.
     *
     * @param side The side to check for
     * @return The side check predicate
     */
    public static Predicate<Supplier<NetworkEvent.Context>> sideCheck(LogicalSide side) {
        return contextSupplier -> contextSupplier.get().getDirection().getReceptionSide() == side;
    }
    
    /**
     * Send a packet to the server.
     *
     * @param message The message to send
     * @param <T> The message type
     */
    public static <T> void sendToServer(T message) {
        CHANNEL.sendToServer(message);
    }
    
    /**
     * Send a packet to a specific player.
     *
     * @param message The message to send
     * @param player The player to send to
     * @param <T> The message type
     */
    public static <T> void sendToPlayer(T message, ServerPlayer player) {
        CHANNEL.send(PacketDistributor.PLAYER.with(() -> player), message);
    }
    
    /**
     * Send a packet to all players.
     *
     * @param message The message to send
     * @param <T> The message type
     */
    public static <T> void sendToAll(T message) {
        CHANNEL.send(PacketDistributor.ALL.noArg(), message);
    }
    
    /**
     * Send a packet to all players near a position.
     *
     * @param message The message to send
     * @param packetTarget The target position and range
     * @param <T> The message type
     */
    public static <T> void sendToAllAround(T message, PacketDistributor.TargetPoint packetTarget) {
        CHANNEL.send(PacketDistributor.NEAR.with(() -> packetTarget), message);
    }
    
    /**
     * Send a packet to all players in a dimension.
     *
     * @param message The message to send
     * @param dimensionType The dimension to send to
     * @param <T> The message type
     */
    public static <T> void sendToDimension(T message, net.minecraft.resources.ResourceKey<net.minecraft.world.level.Level> dimensionType) {
        CHANNEL.send(PacketDistributor.DIMENSION.with(() -> dimensionType), message);
    }
    
    /**
     * Get a channel for a specific module.
     *
     * @param moduleName The module name
     * @return The module's channel
     */
    public static SimpleChannel getModuleChannel(String moduleName) {
        if (MODULE_CHANNELS.containsKey(moduleName)) {
            return MODULE_CHANNELS.get(moduleName);
        }
        
        // Create a new channel for the module
        ResourceLocation channelId = Registration.location(moduleName + "_channel");
        SimpleChannel channel = NetworkRegistry.newSimpleChannel(
            channelId,
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals
        );
        
        MODULE_CHANNELS.put(moduleName, channel);
        EldritchVoid.LOGGER.info("Created network channel for module: {}", moduleName);
        
        return channel;
    }
}