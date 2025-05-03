package com.eldritchvoid.core.config;

import com.eldritchvoid.EldritchVoid;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.ModConfigSpec;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

/**
 * Configuration system for modules.
 * Provides a unified way to handle configuration across modules.
 */
public class ModuleConfig {
    private static final Map<String, ModuleConfig> CONFIGS = new HashMap<>();
    private static final Map<String, Map<String, ConfigValue<?>>> CONFIG_VALUES = new HashMap<>();
    
    private final String moduleName;
    private final ModConfigSpec.Builder builder;
    private final Map<String, ConfigValue<?>> values = new HashMap<>();
    private ModConfigSpec spec;
    
    /**
     * Create a new module configuration.
     *
     * @param moduleName The name of the module
     */
    public ModuleConfig(String moduleName) {
        this.moduleName = moduleName;
        this.builder = new ModConfigSpec.Builder();
        
        // Start with a comment describing the module
        builder.comment("Configuration for the " + moduleName + " module");
        
        CONFIGS.put(moduleName, this);
        CONFIG_VALUES.put(moduleName, values);
    }
    
    /**
     * Push a category to the builder.
     *
     * @param category The category name
     * @return This config for chaining
     */
    public ModuleConfig push(String category) {
        builder.push(category);
        return this;
    }
    
    /**
     * Pop a category from the builder.
     *
     * @return This config for chaining
     */
    public ModuleConfig pop() {
        builder.pop();
        return this;
    }
    
    /**
     * Add a comment to the builder.
     *
     * @param comment The comment to add
     * @return This config for chaining
     */
    public ModuleConfig comment(String comment) {
        builder.comment(comment);
        return this;
    }
    
    /**
     * Define an integer configuration value.
     *
     * @param path The path for the value
     * @param defaultValue The default value
     * @param min The minimum value
     * @param max The maximum value
     * @param comment The comment for the value
     * @return The config value
     */
    public ConfigValue<Integer> defineInt(String path, int defaultValue, int min, int max, String comment) {
        builder.comment(comment);
        ModConfigSpec.IntValue value = builder.defineInRange(path, defaultValue, min, max);
        ConfigValue<Integer> configValue = new ConfigValue<>(path, value::get, value::set);
        values.put(path, configValue);
        return configValue;
    }
    
    /**
     * Define an integer configuration value with no bounds.
     *
     * @param path The path for the value
     * @param defaultValue The default value
     * @param comment The comment for the value
     * @return The config value
     */
    public ConfigValue<Integer> defineInt(String path, int defaultValue, String comment) {
        return defineInt(path, defaultValue, Integer.MIN_VALUE, Integer.MAX_VALUE, comment);
    }
    
    /**
     * Define a boolean configuration value.
     *
     * @param path The path for the value
     * @param defaultValue The default value
     * @param comment The comment for the value
     * @return The config value
     */
    public ConfigValue<Boolean> defineBool(String path, boolean defaultValue, String comment) {
        builder.comment(comment);
        ModConfigSpec.BooleanValue value = builder.define(path, defaultValue);
        ConfigValue<Boolean> configValue = new ConfigValue<>(path, value::get, value::set);
        values.put(path, configValue);
        return configValue;
    }
    
    /**
     * Define a double configuration value.
     *
     * @param path The path for the value
     * @param defaultValue The default value
     * @param min The minimum value
     * @param max The maximum value
     * @param comment The comment for the value
     * @return The config value
     */
    public ConfigValue<Double> defineDouble(String path, double defaultValue, double min, double max, String comment) {
        builder.comment(comment);
        ModConfigSpec.DoubleValue value = builder.defineInRange(path, defaultValue, min, max);
        ConfigValue<Double> configValue = new ConfigValue<>(path, value::get, value::set);
        values.put(path, configValue);
        return configValue;
    }
    
    /**
     * Define a string configuration value.
     *
     * @param path The path for the value
     * @param defaultValue The default value
     * @param comment The comment for the value
     * @return The config value
     */
    public ConfigValue<String> defineString(String path, String defaultValue, String comment) {
        builder.comment(comment);
        ModConfigSpec.ConfigValue<String> value = builder.define(path, defaultValue);
        ConfigValue<String> configValue = new ConfigValue<>(path, value::get, value::set);
        values.put(path, configValue);
        return configValue;
    }
    
    /**
     * Define a string list configuration value.
     *
     * @param path The path for the value
     * @param defaultValue The default value
     * @param comment The comment for the value
     * @return The config value
     */
    public ConfigValue<List<String>> defineStringList(String path, List<String> defaultValue, String comment) {
        builder.comment(comment);
        ModConfigSpec.ConfigValue<List<? extends String>> value = builder.defineList(path, defaultValue, s -> true);
        ConfigValue<List<String>> configValue = new ConfigValue<>(path, () -> new ArrayList<>(value.get()), newValue -> value.set(newValue));
        values.put(path, configValue);
        return configValue;
    }
    
    /**
     * Add a boolean parameter to the configuration.
     * Simplified method for the new module API.
     *
     * @param path The parameter path
     * @param defaultValue The default value
     * @param comment The comment for this parameter
     * @return The config value
     */
    public ConfigValue<Boolean> addBooleanParameter(String path, boolean defaultValue, String comment) {
        return defineBool(path, defaultValue, comment);
    }
    
    /**
     * Add an integer parameter to the configuration.
     * Simplified method for the new module API.
     *
     * @param path The parameter path
     * @param defaultValue The default value
     * @param comment The comment for this parameter
     * @return The config value
     */
    public ConfigValue<Integer> addIntParameter(String path, int defaultValue, String comment) {
        return defineInt(path, defaultValue, comment);
    }
    
    /**
     * Add a double parameter to the configuration.
     * Simplified method for the new module API.
     *
     * @param path The parameter path
     * @param defaultValue The default value
     * @param comment The comment for this parameter
     * @return The config value
     */
    public ConfigValue<Double> addDoubleParameter(String path, double defaultValue, String comment) {
        return defineDouble(path, defaultValue, Double.MIN_VALUE, Double.MAX_VALUE, comment);
    }
    
    /**
     * Add a string parameter to the configuration.
     * Simplified method for the new module API.
     *
     * @param path The parameter path
     * @param defaultValue The default value
     * @param comment The comment for this parameter
     * @return The config value
     */
    public ConfigValue<String> addStringParameter(String path, String defaultValue, String comment) {
        return defineString(path, defaultValue, comment);
    }
    
    /**
     * Build the configuration specification and register it with NeoForge.
     */
    public void build() {
        spec = builder.build();
        
        // In NeoForge 1.21.5, we need to create a ModConfig instance directly
        // and then register it with the ConfigTracker
        String fileName = EldritchVoid.MOD_ID + "-" + moduleName + ".toml";
        ModConfig modConfig = new ModConfig(ModConfig.Type.COMMON, spec, fileName, EldritchVoid.MOD_ID);
        net.neoforged.fml.config.ConfigTracker.INSTANCE.trackConfig(modConfig);
        
        EldritchVoid.LOGGER.info("Registered configuration for module: {}", moduleName);
    }
    
    /**
     * Check if a module has a specific configuration value.
     *
     * @param moduleName The name of the module
     * @param path The path of the value
     * @return True if the module has the config value
     */
    public static boolean hasConfig(String moduleName, String path) {
        Map<String, ConfigValue<?>> moduleValues = CONFIG_VALUES.get(moduleName);
        return moduleValues != null && moduleValues.containsKey(path);
    }
    
    /**
     * Get a configuration value from a module.
     *
     * @param moduleName The name of the module
     * @param path The path of the value
     * @param <T> The value type
     * @return The config value, or null if not found
     */
    @SuppressWarnings("unchecked")
    public static <T> ConfigValue<T> getConfig(String moduleName, String path) {
        Map<String, ConfigValue<?>> moduleValues = CONFIG_VALUES.get(moduleName);
        if (moduleValues == null) {
            return null;
        }
        return (ConfigValue<T>) moduleValues.get(path);
    }
    
    /**
     * Get the module name.
     *
     * @return The module name
     */
    public String getModuleName() {
        return moduleName;
    }
    
    /**
     * Get the configuration specification.
     *
     * @return The config spec
     */
    public ModConfigSpec getSpec() {
        return spec;
    }
    
    /**
     * Get all configuration values for this module.
     *
     * @return The config values
     */
    public Map<String, ConfigValue<?>> getValues() {
        return values;
    }
    
    /**
     * A wrapper for configuration values.
     *
     * @param <T> The value type
     */
    public static class ConfigValue<T> {
        private final String path;
        private final Supplier<T> getter;
        private final java.util.function.Consumer<T> setter;
        
        /**
         * Create a new config value.
         *
         * @param path The path for the value
         * @param getter The getter for the value
         * @param setter The setter for the value
         */
        public ConfigValue(String path, Supplier<T> getter, java.util.function.Consumer<T> setter) {
            this.path = path;
            this.getter = getter;
            this.setter = setter;
        }
        
        /**
         * Get the value.
         *
         * @return The value
         */
        public T get() {
            return getter.get();
        }
        
        /**
         * Set the value.
         *
         * @param value The new value
         */
        public void set(T value) {
            setter.accept(value);
        }
        
        /**
         * Get the path for this value.
         *
         * @return The path
         */
        public String getPath() {
            return path;
        }
    }
}