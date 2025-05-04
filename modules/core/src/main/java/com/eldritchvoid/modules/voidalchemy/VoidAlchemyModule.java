package com.eldritchvoid.modules.voidalchemy;

import com.eldritchvoid.core.Module;
import com.eldritchvoid.core.config.ModuleConfig;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;

/**
 * The Void Alchemy module.
 * Handles alchemy recipes, fluid systems, and void essence extraction and processing.
 */
public class VoidAlchemyModule extends Module {

    /**
     * Create the Void Alchemy module.
     *
     * @param modBus The mod event bus
     */
    public VoidAlchemyModule(IEventBus modBus) {
        super("voidalchemy", modBus);
    }

    @Override
    protected void setupConfig(ModuleConfig config) {
        // Add Void Alchemy specific configuration
        config.addBooleanParameter("enableVoidPee", true, "Enable the 'Void Pee' alchemical reagent");
        config.addIntParameter("voidEssenceYield", 5, 1, 20, "Base yield of void essence per extraction");
    }

    @Override
    protected void registerContent() {
        log("Registering Void Alchemy content");
        // Void Alchemy content will be registered here
    }

    @Override
    protected void init(FMLCommonSetupEvent event) {
        log("Initializing Void Alchemy");
        // Common setup for Void Alchemy will be done here
    }

    @Override
    protected void clientInit(FMLClientSetupEvent event) {
        log("Initializing Void Alchemy client");
        // Client setup for Void Alchemy will be done here
    }
}