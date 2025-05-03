package com.eldritchvoid.core;

import com.eldritchvoid.EldritchVoid;
import com.eldritchvoid.core.config.ModuleConfig;
import com.eldritchvoid.core.registry.ModuleRegistry;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;

/**
 * Base class for all modules.
 * Provides common functionality and lifecycle management.
 */
public abstract class Module {
    protected final String moduleName;
    protected final ModuleConfig config;
    protected final IEventBus modBus;
    protected ModuleManager manager;
    protected boolean enabled = true;
    protected boolean clientReady = false;
    protected boolean serverReady = false;
    
    /**
     * Create a new module.
     *
     * @param moduleName The name of the module
     * @param modBus The mod event bus
     */
    protected Module(String moduleName, IEventBus modBus) {
        this.moduleName = moduleName;
        this.modBus = modBus;
        this.config = new ModuleConfig(moduleName);
        
        // Configure with default settings
        setupConfig(config);
        config.build();
        
        EldritchVoid.LOGGER.info("Created module: {}", moduleName);
    }
    
    /**
     * Log a message with the module's prefix.
     *
     * @param message The message to log
     */
    protected void log(String message) {
        EldritchVoid.LOGGER.info("[" + moduleName + "] " + message);
    }
    
    /**
     * Get the display name of the module.
     * Can be overridden by subclasses to provide a more user-friendly name.
     *
     * @return The display name
     */
    public String getDisplayName() {
        // Default to moduleNamed with first letter capitalized
        if (moduleName == null || moduleName.isEmpty()) {
            return "Unknown Module";
        }
        return moduleName.substring(0, 1).toUpperCase() + moduleName.substring(1);
    }
    
    /**
     * Set up the module's configuration.
     *
     * @param config The config to set up
     */
    protected abstract void setupConfig(ModuleConfig config);
    
    /**
     * Register the module's content.
     */
    protected abstract void registerContent();
    
    /**
     * Initialize the module during common setup.
     *
     * @param event The common setup event
     */
    protected abstract void init(FMLCommonSetupEvent event);
    
    /**
     * Initialize the module on the client side.
     *
     * @param event The client setup event
     */
    protected abstract void clientInit(FMLClientSetupEvent event);
    
    /**
     * Create a resource location for this module.
     *
     * @param path The resource path
     * @return The resource location
     */
    public ResourceLocation location(String path) {
        return ResourceLocation.of(EldritchVoid.MOD_ID, "modules/" + moduleName + "/" + path);
    }
    
    /**
     * Get the module's name.
     *
     * @return The module name
     */
    public String getModuleName() {
        return moduleName;
    }
    
    /**
     * Set the module manager.
     *
     * @param manager The module manager
     */
    void setManager(ModuleManager manager) {
        this.manager = manager;
    }
    
    /**
     * Get the module manager.
     *
     * @return The module manager
     */
    public ModuleManager getManager() {
        return manager;
    }
    
    /**
     * Register a registry with this module.
     *
     * @param registry The registry to register
     * @param <T> The registry type
     */
    public <T> void registerRegistry(ModuleRegistry<T> registry) {
        registry.register(modBus);
    }
    
    /**
     * Check if this module is enabled.
     *
     * @return True if the module is enabled
     */
    public boolean isEnabled() {
        return enabled;
    }
    
    /**
     * Enable or disable this module.
     *
     * @param enabled Whether the module should be enabled
     */
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
        
        if (enabled) {
            EldritchVoid.LOGGER.info("Enabled module: {}", moduleName);
        } else {
            EldritchVoid.LOGGER.info("Disabled module: {}", moduleName);
        }
    }
    
    /**
     * Check if this module is ready on the client side.
     *
     * @return True if the module is client-ready
     */
    public boolean isClientReady() {
        return clientReady;
    }
    
    /**
     * Check if this module is ready on the server side.
     *
     * @return True if the module is server-ready
     */
    public boolean isServerReady() {
        return serverReady;
    }
    
    /**
     * Called when content registration is complete.
     * Do not override this unless you know what you're doing.
     */
    void onRegistrationComplete() {
        // Default implementation does nothing
    }
    
    /**
     * Called when a common setup event is received.
     * Do not override this unless you know what you're doing.
     *
     * @param event The common setup event
     */
    void onCommonSetup(FMLCommonSetupEvent event) {
        if (!enabled) return;
        
        init(event);
        serverReady = true;
        EldritchVoid.LOGGER.info("Module {} initialized on common side", moduleName);
    }
    
    /**
     * Called when a client setup event is received.
     * Do not override this unless you know what you're doing.
     *
     * @param event The client setup event
     */
    void onClientSetup(FMLClientSetupEvent event) {
        if (!enabled) return;
        
        clientInit(event);
        clientReady = true;
        EldritchVoid.LOGGER.info("Module {} initialized on client side", moduleName);
    }
}