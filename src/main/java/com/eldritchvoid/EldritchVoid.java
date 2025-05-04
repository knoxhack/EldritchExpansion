package com.eldritchvoid;

import com.eldritchvoid.core.ModuleRegistry;
import com.eldritchvoid.core.module.*;
import com.eldritchvoid.init.ItemInit;
import com.eldritchvoid.init.BlockInit;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Main mod class for Eldritch Void
 * Manages modules and initialization
 */
@Mod(EldritchVoid.MODID)
public class EldritchVoid {
    public static final String MODID = "eldritchvoid";
    public static final Logger LOGGER = LoggerFactory.getLogger("EldritchVoid");
    
    // Creative tab registry
    public static final DeferredRegister<CreativeModeTab> CREATIVE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MODID);
    
    // Create a creative tab for all mod items
    public static final RegistryObject<CreativeModeTab> ELDRITCH_TAB = CREATIVE_TABS.register("eldritch_tab",
            () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.eldritchvoid"))
                    .icon(() -> ItemInit.VOID_ESSENCE.get().getDefaultInstance())
                    .displayItems((parameters, output) -> {
                        // Add core items
                        output.accept(ItemInit.VOID_ESSENCE.get());
                        output.accept(ItemInit.ELDRITCH_CRYSTAL.get());
                        output.accept(ItemInit.OBSIDIAN_SHARD.get());
                        
                        // Add blocks
                        output.accept(BlockInit.VOID_ALTAR.get());
                        output.accept(BlockInit.ELDRITCH_PEDESTAL.get());
                        output.accept(BlockInit.OBSIDIAN_FORGE.get());
                    })
                    .build());
    
    // Module registry to manage all modules
    private static ModuleRegistry moduleRegistry;
    
    public EldritchVoid() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        
        // Register deferred registries
        CREATIVE_TABS.register(modEventBus);
        ItemInit.ITEMS.register(modEventBus);
        BlockInit.BLOCKS.register(modEventBus);
        
        // Initialize module registry
        moduleRegistry = new ModuleRegistry();
        
        // Register modules
        moduleRegistry.registerModule(new CoreModule());
        moduleRegistry.registerModule(new VoidAlchemyModule());
        moduleRegistry.registerModule(new EldritchArtifactsModule());
        moduleRegistry.registerModule(new ObsidianForgemasterModule());
        moduleRegistry.registerModule(new VoidCorruptionModule());
        moduleRegistry.registerModule(new EldritchArcanaModule());
        moduleRegistry.registerModule(new ObsidianConstructsModule());
        
        // Register event listeners
        modEventBus.addListener(this::commonSetup);
        modEventBus.addListener(this::addCreative);
        
        // Register ourselves for server and other game events
        MinecraftForge.EVENT_BUS.register(this);
        
        LOGGER.info("Eldritch Void initialized");
    }
    
    private void commonSetup(final FMLCommonSetupEvent event) {
        // Common setup code
        LOGGER.info("Common setup complete");
        
        // Initialize all modules
        moduleRegistry.initializeAll(event);
    }
    
    private void addCreative(BuildCreativeModeTabContentsEvent event) {
        // Add items to vanilla tabs if needed
        if (event.getTabKey() == CreativeModeTabs.INGREDIENTS) {
            event.accept(ItemInit.VOID_ESSENCE);
            event.accept(ItemInit.ELDRITCH_CRYSTAL);
            event.accept(ItemInit.OBSIDIAN_SHARD);
        }
    }
    
    // You can use SubscribeEvent and let the Event Bus discover methods to call
    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        // Server starting code
        LOGGER.info("Server starting");
    }
    
    // You can use EventBusSubscriber to automatically register all static methods in the class
    @Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {
            // Client setup code
            LOGGER.info("Client setup complete");
        }
    }
    
    /**
     * Get the module registry
     * @return The module registry
     */
    public static ModuleRegistry getModuleRegistry() {
        return moduleRegistry;
    }
}