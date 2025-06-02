package com.test_project;

import com.test_project.blocks.ModBlocks;
import com.test_project.combat.combo.CombatEventHandler;
import com.test_project.combat.combo.KeyBindings;
import com.test_project.combat.stance.C2SToggleStancePacket;
import com.test_project.entity.ModEntities;
import com.test_project.entity.TestMobEntity;
import com.test_project.faction.FactionAttachments;
import com.test_project.faction.FactionCommands;
import com.test_project.faction.FactionRegistry;
import com.test_project.faction.factions_list.BanditFaction;
import com.test_project.faction.factions_list.GondorFaction;
import com.test_project.faction.factions_list.MordorFaction;
import com.test_project.items.EquipmentEventHandler;
import com.test_project.items.ModItems;
import com.test_project.items.weapone.AttackRangeAttributes;
import com.test_project.items.weapone.feature.CounterAttackEventHandler;
import com.test_project.world.biome.ModBiomes;
import com.test_project.worldrep.ModAttachments;
import com.test_project.worldrep.WorldReputationCommands;
import net.minecraft.world.item.CreativeModeTabs;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import org.slf4j.Logger;
import com.mojang.logging.LogUtils;


@Mod(MainMod.MOD_ID)
public class MainMod {
    public static final String MOD_ID = "mainmod";
    private static final Logger LOGGER = LogUtils.getLogger();

    public MainMod(IEventBus modEventBus, ModContainer modContainer) {
        LOGGER.info("Загрузка MainMod...");

        // Регистрация биомов, предметов, блоков, сущностей
        ModBiomes.register(modEventBus);
        ModItems.register(modEventBus);
        ModBlocks.register(modEventBus);
        ModEntities.register(modEventBus);

        // Регистрация фракций и Data Attachments
        FactionRegistry.register(new GondorFaction());
        FactionRegistry.register(new MordorFaction());
        FactionRegistry.register(new BanditFaction());
        FactionAttachments.register(modEventBus);

        // Регистрация мировой репутации (AttachmentType)
        ModAttachments.register(modEventBus);

        // Регистрация атрибутов сущностей
        AttackRangeAttributes.ATTRIBUTES.register(modEventBus);
        modEventBus.addListener(this::registerAttributes);

        // Регистрация креативных вкладок
        modEventBus.addListener(this::addCreative);

        // Регистрация KeyBindings и клиентских событий
        modEventBus.addListener(KeyBindings::onRegisterKeys); // только для RegisterKeyMappingsEvent!
        NeoForge.EVENT_BUS.addListener(KeyBindings::onClientTick); // только для ClientTickEvent!

        // Регистрация клиентских мод-ивентов (например, FMLClientSetupEvent)
        modEventBus.addListener(ClientModEvents::onClientSetup);

        // Регистрация конфигов (если есть)
        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);

        // Common setup
        modEventBus.addListener(this::commonSetup);

        // Регистрация игровых событий (game events)
        NeoForge.EVENT_BUS.register(CounterAttackEventHandler.class);
        NeoForge.EVENT_BUS.register(CombatEventHandler.class);
        NeoForge.EVENT_BUS.register(EquipmentEventHandler.class);
        modEventBus.addListener(MainMod::registerPayloads);
    }

    // --- Регистрация пользовательских пакетов ---
    @SubscribeEvent
    public static void registerPayloads(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar("mainmod"); // modid должен совпадать!
        registrar.playToServer(
                C2SToggleStancePacket.TYPE,
                C2SToggleStancePacket.STREAM_CODEC,
                C2SToggleStancePacket::handle
        );
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        LOGGER.debug("Выполняется commonSetup MainMod");
    }

    private void addCreative(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.INGREDIENTS) {
            event.accept(ModItems.STEEL);
            event.accept(ModItems.GONDOR_SWORD);
            event.accept(ModItems.GONDOR_AXE);
            event.accept(ModItems.ORC_STEEL);
            event.accept(ModItems.TEST_MOB_SPAWN_EGG.get());
        }
        if (event.getTabKey() == CreativeModeTabs.BUILDING_BLOCKS) {
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

    // --- Регистрация команд ---
    @SubscribeEvent
    public static void onRegisterCommands(RegisterCommandsEvent event) {
        FactionCommands.register(event.getDispatcher());
        WorldReputationCommands.register(event.getDispatcher());
    }
}
