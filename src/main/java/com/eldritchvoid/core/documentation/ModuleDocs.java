package com.eldritchvoid.core.documentation;

import com.eldritchvoid.EldritchVoid;
import com.eldritchvoid.core.Module;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * Documentation system for modules.
 * Provides in-code documentation and JSON generation for modules.
 */
public class ModuleDocs {
    private static final Map<String, ModuleDocInfo> MODULE_DOCS = new HashMap<>();
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final String DOCS_DIR = "documentation";
    
    /**
     * Register documentation for a module.
     *
     * @param module The module to document
     * @param docInfo The documentation info
     */
    public static void registerModuleDocs(Module module, ModuleDocInfo docInfo) {
        MODULE_DOCS.put(module.getModuleName(), docInfo);
        EldritchVoid.LOGGER.info("Registered documentation for module: {}", module.getModuleName());
    }
    
    /**
     * Get documentation for a module.
     *
     * @param moduleName The name of the module
     * @return The module documentation info, or null if not found
     */
    public static ModuleDocInfo getModuleDocs(String moduleName) {
        return MODULE_DOCS.get(moduleName);
    }
    
    /**
     * Get all module documentation.
     *
     * @return A map of module names to documentation info
     */
    public static Map<String, ModuleDocInfo> getAllModuleDocs() {
        return MODULE_DOCS;
    }
    
    /**
     * Generate documentation for all modules.
     */
    public static void generateAllDocs() {
        File docsDir = new File(DOCS_DIR);
        if (!docsDir.exists() && !docsDir.mkdirs()) {
            EldritchVoid.LOGGER.error("Failed to create documentation directory");
            return;
        }
        
        for (Map.Entry<String, ModuleDocInfo> entry : MODULE_DOCS.entrySet()) {
            String moduleName = entry.getKey();
            ModuleDocInfo docInfo = entry.getValue();
            
            JsonObject rootJson = new JsonObject();
            rootJson.addProperty("moduleName", moduleName);
            rootJson.addProperty("version", docInfo.getVersion());
            rootJson.addProperty("description", docInfo.getDescription());
            rootJson.addProperty("author", docInfo.getAuthor());
            
            // Add features
            JsonObject featuresJson = new JsonObject();
            for (Map.Entry<String, String> feature : docInfo.getFeatures().entrySet()) {
                featuresJson.addProperty(feature.getKey(), feature.getValue());
            }
            rootJson.add("features", featuresJson);
            
            // Add usage examples
            JsonObject examplesJson = new JsonObject();
            for (Map.Entry<String, String> example : docInfo.getExamples().entrySet()) {
                examplesJson.addProperty(example.getKey(), example.getValue());
            }
            rootJson.add("examples", examplesJson);
            
            // Add integration info
            JsonObject integrationJson = new JsonObject();
            for (Map.Entry<String, String> integration : docInfo.getIntegrations().entrySet()) {
                integrationJson.addProperty(integration.getKey(), integration.getValue());
            }
            rootJson.add("integrations", integrationJson);
            
            // Write to file
            String filename = moduleName + "_docs.json";
            File outputFile = new File(docsDir, filename);
            
            try (FileWriter writer = new FileWriter(outputFile)) {
                GSON.toJson(rootJson, writer);
                EldritchVoid.LOGGER.info("Generated documentation for module {} at {}", moduleName, outputFile.getAbsolutePath());
            } catch (IOException e) {
                EldritchVoid.LOGGER.error("Failed to write documentation for module {}: {}", moduleName, e.getMessage());
            }
        }
    }
    
    /**
     * Load documentation from disk.
     */
    public static void loadDocs() {
        File docsDir = new File(DOCS_DIR);
        if (!docsDir.exists()) {
            EldritchVoid.LOGGER.warn("Documentation directory does not exist, skipping loading");
            return;
        }
        
        File[] files = docsDir.listFiles((dir, name) -> name.endsWith("_docs.json"));
        if (files == null) {
            EldritchVoid.LOGGER.warn("No documentation files found");
            return;
        }
        
        for (File file : files) {
            try (FileReader reader = new FileReader(file)) {
                JsonObject rootJson = GSON.fromJson(reader, JsonObject.class);
                
                String moduleName = rootJson.get("moduleName").getAsString();
                String version = rootJson.get("version").getAsString();
                String description = rootJson.get("description").getAsString();
                String author = rootJson.get("author").getAsString();
                
                ModuleDocInfo docInfo = new ModuleDocInfo(description, version, author);
                
                // Load features
                JsonObject featuresJson = rootJson.getAsJsonObject("features");
                for (String featureKey : featuresJson.keySet()) {
                    docInfo.addFeature(featureKey, featuresJson.get(featureKey).getAsString());
                }
                
                // Load examples
                JsonObject examplesJson = rootJson.getAsJsonObject("examples");
                for (String exampleKey : examplesJson.keySet()) {
                    docInfo.addExample(exampleKey, examplesJson.get(exampleKey).getAsString());
                }
                
                // Load integrations
                JsonObject integrationsJson = rootJson.getAsJsonObject("integrations");
                for (String integrationKey : integrationsJson.keySet()) {
                    docInfo.addIntegration(integrationKey, integrationsJson.get(integrationKey).getAsString());
                }
                
                MODULE_DOCS.put(moduleName, docInfo);
                EldritchVoid.LOGGER.info("Loaded documentation for module {}", moduleName);
            } catch (IOException e) {
                EldritchVoid.LOGGER.error("Failed to read documentation file {}: {}", file.getName(), e.getMessage());
            }
        }
    }
    
    /**
     * Create documentation for a class by reflecting on its documented annotations.
     *
     * @param clazz The class to document
     * @param moduleName The name of the module
     * @return The module documentation info
     */
    public static ModuleDocInfo documentClass(Class<?> clazz, String moduleName) {
        Document classDoc = clazz.getAnnotation(Document.class);
        if (classDoc == null) {
            EldritchVoid.LOGGER.warn("Class {} is not documented, returning empty docs", clazz.getName());
            return new ModuleDocInfo("No description available", "unknown", "unknown");
        }
        
        ModuleDocInfo docInfo = new ModuleDocInfo(classDoc.description(), classDoc.version(), classDoc.author());
        
        // Process features
        for (Feature feature : classDoc.features()) {
            docInfo.addFeature(feature.name(), feature.description());
        }
        
        // Add examples from method annotations
        for (Method method : clazz.getDeclaredMethods()) {
            Example example = method.getAnnotation(Example.class);
            if (example != null) {
                docInfo.addExample(method.getName(), example.value());
            }
        }
        
        // Register the docs
        registerModuleDocs(null, docInfo);
        
        return docInfo;
    }
    
    /**
     * Create a resource location for documentation assets.
     *
     * @param moduleName The module name
     * @param path The asset path
     * @return The resource location
     */
    public static ResourceLocation getDocResourceLocation(String moduleName, String path) {
        String fullPath = "documentation/" + moduleName + "/" + path;
        // Use ResourceLocation.parse as recommended for NeoForge 1.21.5
        return ResourceLocation.parse(EldritchVoid.MOD_ID + ":" + fullPath);
    }
    
    /**
     * Documentation annotation for classes.
     */
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.TYPE)
    public @interface Document {
        /**
         * The description of the documented element.
         *
         * @return The description
         */
        String description();
        
        /**
         * The version of the documented element.
         *
         * @return The version
         */
        String version() default "1.0";
        
        /**
         * The author of the documented element.
         *
         * @return The author
         */
        String author() default "EldritchVoid Team";
        
        /**
         * The features of the documented element.
         *
         * @return The features
         */
        Feature[] features() default {};
    }
    
    /**
     * Feature annotation for documenting class features.
     */
    @Retention(RetentionPolicy.RUNTIME)
    @Target({})
    public @interface Feature {
        /**
         * The name of the feature.
         *
         * @return The feature name
         */
        String name();
        
        /**
         * The description of the feature.
         *
         * @return The feature description
         */
        String description();
    }
    
    /**
     * Example annotation for documenting methods.
     */
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.METHOD)
    public @interface Example {
        /**
         * The example code or description.
         *
         * @return The example
         */
        String value();
    }
}