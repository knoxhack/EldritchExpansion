package com.eldritchvoid.modules.voidalchemy;

import com.eldritchvoid.EldritchVoid;
import com.eldritchvoid.core.Module;
import com.eldritchvoid.core.config.ModuleConfig;
import com.eldritchvoid.core.documentation.ModuleDocs;
import com.eldritchvoid.core.documentation.ModuleDocs.Document;
import com.eldritchvoid.core.documentation.ModuleDocs.Feature;
import com.eldritchvoid.core.documentation.ModuleDocInfo;
import com.eldritchvoid.core.registry.Registration;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;

/**
 * The Void Alchemy module for the Eldritch Void mod.
 * Adds various alchemical elements, fluids, and recipes related to void magic.
 */
@Document(
    description = "A module that adds void-based alchemy, including strange fluids and transmutation recipes.",
    version = "1.0.0",
    author = "EldritchVoid Team",
    features = {
        @Feature(name = "Void Essences", description = "Various essences extracted from the void, used in crafting and brewing."),
        @Feature(name = "Alchemical Apparatus", description = "Special equipment for performing void alchemy."),
        @Feature(name = "Transmutation Recipes", description = "Convert items using void energy and essences.")
    }
)
public class VoidAlchemyModule extends Module {
    public static final String MODULE_NAME = "voidalchemy";
    
    private VoidAlchemyFluids fluids;
    private boolean enableVoidEssence;
    private int voidPeeAmount;
    
    /**
     * Create a new Void Alchemy module.
     *
     * @param modBus The mod event bus
     */
    public VoidAlchemyModule(IEventBus modBus) {
        super(MODULE_NAME, modBus);
        
        // Register documentation
        ModuleDocInfo docInfo = ModuleDocs.documentClass(VoidAlchemyModule.class, MODULE_NAME);
        docInfo.addExample("Creating Void Pee", "Mix 1 bucket of water with 2 units of void essence in the Void Alchemical Cauldron.");
        docInfo.addIntegration("obsidianforgemaster", "The Obsidian Forgemaster module can use Void Pee as a quenching agent for special tool effects.");
        
        EldritchVoid.LOGGER.info("Void Alchemy module created");
    }
    
    @Override
    protected void setupConfig(ModuleConfig config) {
        config.push("general");
        
        // General settings
        config.defineBool("enableVoidEssence", true, "Whether to enable Void Essence fluid");
        config.defineInt("voidPeeAmount", 250, 10, 1000, "Amount of Void Pee produced per operation");
        
        config.pop();
        
        EldritchVoid.LOGGER.info("Void Alchemy config set up");
    }
    
    @Override
    protected void registerContent() {
        // Initialize sub-components
        fluids = new VoidAlchemyFluids(MODULE_NAME);
        
        // In NeoForge 1.21.5, config handling has changed
        // Config values are returned in a different way, using ConfigValue objects
        var enableVoidEssenceConfig = ModuleConfig.getConfig(MODULE_NAME, "enableVoidEssence");
        enableVoidEssence = enableVoidEssenceConfig != null ? 
            Boolean.TRUE.equals(enableVoidEssenceConfig.get()) : true;
        
        var voidPeeAmountConfig = ModuleConfig.getConfig(MODULE_NAME, "voidPeeAmount");
        voidPeeAmount = voidPeeAmountConfig != null ? 
            (voidPeeAmountConfig.get() instanceof Integer ? (Integer)voidPeeAmountConfig.get() : 250) : 250;
        
        // Register fluids
        if (enableVoidEssence) {
            EldritchVoid.LOGGER.info("Registering Void Alchemy fluids");
            registerRegistry(fluids.getFluidRegistry());
        }
        
        EldritchVoid.LOGGER.info("Void Alchemy content registered");
    }
    
    @Override
    protected void init(FMLCommonSetupEvent event) {
        EldritchVoid.LOGGER.info("Initializing Void Alchemy module");
        
        // Register recipes and other common setup
        
        EldritchVoid.LOGGER.info("Void Alchemy module initialized");
    }
    
    @Override
    protected void clientInit(FMLClientSetupEvent event) {
        EldritchVoid.LOGGER.info("Initializing Void Alchemy client");
        
        // Register fluid renderers
        if (enableVoidEssence) {
            fluids.registerRenderers();
        }
        
        EldritchVoid.LOGGER.info("Void Alchemy client initialized");
    }
    
    /**
     * Get the fluids component.
     *
     * @return The fluids component
     */
    public VoidAlchemyFluids getFluids() {
        return fluids;
    }
    
    /**
     * Get the configured Void Pee amount.
     *
     * @return The Void Pee amount
     */
    public int getVoidPeeAmount() {
        return voidPeeAmount;
    }
}