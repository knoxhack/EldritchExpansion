package com.eldritchvoid.core.documentation;

import java.util.HashMap;
import java.util.Map;

/**
 * Container for module documentation information.
 */
public class ModuleDocInfo {
    private final String description;
    private final String version;
    private final String author;
    private final Map<String, String> features = new HashMap<>();
    private final Map<String, String> examples = new HashMap<>();
    private final Map<String, String> integrations = new HashMap<>();
    
    /**
     * Create a new module documentation info.
     *
     * @param description The module description
     * @param version The module version
     * @param author The module author
     */
    public ModuleDocInfo(String description, String version, String author) {
        this.description = description;
        this.version = version;
        this.author = author;
    }
    
    /**
     * Add a feature to the documentation.
     *
     * @param name The feature name
     * @param description The feature description
     * @return This doc info for chaining
     */
    public ModuleDocInfo addFeature(String name, String description) {
        features.put(name, description);
        return this;
    }
    
    /**
     * Add an example to the documentation.
     *
     * @param name The example name
     * @param code The example code or description
     * @return This doc info for chaining
     */
    public ModuleDocInfo addExample(String name, String code) {
        examples.put(name, code);
        return this;
    }
    
    /**
     * Add an integration to the documentation.
     *
     * @param moduleName The name of the module to integrate with
     * @param description The integration description
     * @return This doc info for chaining
     */
    public ModuleDocInfo addIntegration(String moduleName, String description) {
        integrations.put(moduleName, description);
        return this;
    }
    
    /**
     * Get the module description.
     *
     * @return The description
     */
    public String getDescription() {
        return description;
    }
    
    /**
     * Get the module version.
     *
     * @return The version
     */
    public String getVersion() {
        return version;
    }
    
    /**
     * Get the module author.
     *
     * @return The author
     */
    public String getAuthor() {
        return author;
    }
    
    /**
     * Get all features.
     *
     * @return A map of feature names to descriptions
     */
    public Map<String, String> getFeatures() {
        return features;
    }
    
    /**
     * Get all examples.
     *
     * @return A map of example names to code or descriptions
     */
    public Map<String, String> getExamples() {
        return examples;
    }
    
    /**
     * Get all integrations.
     *
     * @return A map of module names to integration descriptions
     */
    public Map<String, String> getIntegrations() {
        return integrations;
    }
}