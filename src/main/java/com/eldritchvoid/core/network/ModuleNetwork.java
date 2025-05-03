package com.eldritchvoid.core.network;

import com.eldritchvoid.EldritchVoid;
import com.eldritchvoid.core.registry.Registration;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.fml.LogicalSide;
import net.neoforged.neoforge.network.handling.PlayNetworkContext;
import net.neoforged.neoforge.network.handling.PlayPayloadContext;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlerEvent;
import net.neoforged.neoforge.network.payload.BasePayload;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * Centralized networking system for modules based on NeoForge 1.21.5's network API.
 * Provides a unified way to register and send packets between server and client.
 */
public class ModuleNetwork {
    private static final String PROTOCOL_VERSION = "1.0";
    private static PlayNetworkContext CONTEXT;
    
    private static final Map<String, PlayNetworkContext> MODULE_CONTEXTS = new HashMap<>();
    
    /**
     * Initialize the network system. This should be called during mod initialization.
     */
    public static void init() {
        // In NeoForge 1.21.5, networking is initialized through events
        EldritchVoid.LOGGER.info("Network system initialized. Register handlers in RegisterPayloadHandlerEvent");
    }
    
    /**
     * Register network payloads for all modules.
     * This should be called when the RegisterPayloadHandlerEvent is fired.
     *
     * @param event The RegisterPayloadHandlerEvent
     */
    public static void registerPayloads(RegisterPayloadHandlerEvent event) {
        // Create a network context for the main mod channel
        CONTEXT = event.registrar(EldritchVoid.MOD_ID);
        
        // This is where specific payloads would be registered
        
        EldritchVoid.LOGGER.info("Registered network payloads");
    }
    
    /**
     * Register a packet payload with the given ID.
     * 
     * @param <T> The payload type 
     * @param context The network context
     * @param id The payload ID
     * @param payloadClass The payload class
     * @param decoder Function to decode the payload from a buffer
     */
    public static <T extends BasePayload> void registerPayload(PlayNetworkContext context, 
                                               String id, 
                                               Class<T> payloadClass, 
                                               Function<FriendlyByteBuf, T> decoder) {
        context.registrar().register(ResourceLocation.parse(EldritchVoid.MOD_ID + ":" + id), payloadClass, decoder);
        EldritchVoid.LOGGER.debug("Registered payload: {}", id);
    }
    
    /**
     * Register a payload handler for client-to-server packets.
     *
     * @param <T> The payload type
     * @param context The network context  
     * @param payloadClass The payload class
     * @param handler The consumer to handle the payload and context
     */
    public static <T extends BasePayload> void registerClientboundHandler(PlayNetworkContext context, 
                                                      Class<T> payloadClass, 
                                                      java.util.function.BiConsumer<T, PlayPayloadContext> handler) {
        context.registrar().addClientboundHandler(payloadClass, handler);
        EldritchVoid.LOGGER.debug("Registered clientbound handler for: {}", payloadClass.getSimpleName());
    }
    
    /**
     * Register a payload handler for server-to-client packets.
     *
     * @param <T> The payload type
     * @param context The network context
     * @param payloadClass The payload class
     * @param handler The consumer to handle the payload and context
     */
    public static <T extends BasePayload> void registerServerboundHandler(PlayNetworkContext context, 
                                                      Class<T> payloadClass, 
                                                      java.util.function.BiConsumer<T, PlayPayloadContext> handler) {
        context.registrar().addServerboundHandler(payloadClass, handler);
        EldritchVoid.LOGGER.debug("Registered serverbound handler for: {}", payloadClass.getSimpleName());
    }
    
    /**
     * Send a packet to the server.
     *
     * @param payload The payload to send
     */
    public static void sendToServer(BasePayload payload) {
        PacketDistributor.SERVER.noArg().send(payload);
        EldritchVoid.LOGGER.debug("Sent payload to server: {}", payload.getClass().getSimpleName());
    }
    
    /**
     * Send a packet to a specific player.
     *
     * @param payload The payload to send
     * @param player The player to send to
     */
    public static void sendToPlayer(BasePayload payload, ServerPlayer player) {
        PacketDistributor.PLAYER.with(player).send(payload);
        EldritchVoid.LOGGER.debug("Sent payload to player: {}", payload.getClass().getSimpleName());
    }
    
    /**
     * Send a packet to all players.
     *
     * @param payload The payload to send
     */
    public static void sendToAll(BasePayload payload) {
        PacketDistributor.ALL.noArg().send(payload);
        EldritchVoid.LOGGER.debug("Sent payload to all players: {}", payload.getClass().getSimpleName());
    }
    
    /**
     * Get a network context for a specific module.
     *
     * @param moduleName The module name
     * @return The module's network context
     */
    public static PlayNetworkContext getModuleContext(String moduleName) {
        return MODULE_CONTEXTS.computeIfAbsent(moduleName, name -> {
            EldritchVoid.LOGGER.info("Created network context for module: {}", name);
            return CONTEXT;  // For now, share the main context
        });
    }
}