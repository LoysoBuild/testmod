package com.test_project;

import com.test_project.blocks.ModBlocks;
import com.test_project.entity.ModEntities;
import com.test_project.entity.TestMobEntity;
import com.test_project.faction.FactionAttachments;
import com.test_project.faction.FactionRegistry;
import com.test_project.items.ModItems;
import com.test_project.world.biome.ModBiomes;
import net.minecraft.world.item.CreativeModeTabs;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import org.slf4j.Logger;
import com.mojang.logging.LogUtils;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;

@Mod(MainMod.MOD_ID)
public class MainMod {
    public static final String MOD_ID = "mainmod";
    private static final Logger LOGGER = LogUtils.getLogger();

    public MainMod(IEventBus modEventBus, ModContainer modContainer) {
        LOGGER.info("Загрузка MainMod...");
        ModBiomes.register(modEventBus);
        modEventBus.addListener(this::registerAttributes);
        ModEntities.register(modEventBus);
        modEventBus.addListener(this::commonSetup);
        NeoForge.EVENT_BUS.register(this);
        ModItems.register(modEventBus);
        ModBlocks.register(modEventBus);

        FactionAttachments.register(modEventBus);

        modEventBus.addListener(this::addCreative);
        FactionRegistry.register(new com.test_project.factions.GondorFaction());
        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);

        LOGGER.info("MainMod успешно загружен!");
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        LOGGER.debug("Выполняется commonSetup MainMod");
    }

    private void addCreative(BuildCreativeModeTabContentsEvent event) {
        if(event.getTabKey() == CreativeModeTabs.INGREDIENTS) {
            event.accept(ModItems.STEEL);
            event.accept(ModItems.ORC_STEEL);
            event.accept(ModItems.TEST_MOB_SPAWN_EGG.get() );
        }
        if(event.getTabKey() == CreativeModeTabs.BUILDING_BLOCKS) {
            event.accept(ModBlocks.STEEL_BLOCK);
            event.accept(ModBlocks.STEEL_ORE);
        }
    }

    private void registerAttributes(EntityAttributeCreationEvent event) {
        event.put(ModEntities.TEST_MOB.get(), TestMobEntity.createAttributes().build());
    }

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        LOGGER.debug("Сервер стартует с MainMod");
    }

    @EventBusSubscriber(modid = MOD_ID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {
            // Клиентская инициализация
        }
    }
}
