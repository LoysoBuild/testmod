package com.test_project.items;


import com.test_project.MainMod;
import com.test_project.entity.ModEntities;
import com.test_project.items.weapone.weaponeclass.ModBattleAxe;
import com.test_project.items.weapone.weaponeclass.ModSword;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.item.Tiers;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.bus.api.IEventBus;
import com.mojang.logging.LogUtils;
import org.slf4j.Logger;

public final class ModItems {
    private static final Logger LOGGER = LogUtils.getLogger();

    public static final DeferredRegister.Items ITEMS =
            DeferredRegister.createItems(MainMod.MOD_ID);

    // Спавн-яйца для тестирования
    public static final DeferredHolder<Item, SpawnEggItem> TEST_MOB_SPAWN_EGG =
            ITEMS.register("test_mob_spawn_egg", () ->
                    new SpawnEggItem(
                            ModEntities.TEST_MOB.get(),
                            0xA0A0A0, 0x505050,
                            new Item.Properties()
                    )
            );

    public static final DeferredHolder<Item, SpawnEggItem> MIRROR_PLAYER_SPAWN_EGG =
            ITEMS.register("mirror_player_spawn_egg", () ->
                    new SpawnEggItem(
                            ModEntities.MIRROR_PLAYER.get(),
                            0x4A4A4A, 0x8B4513,
                            new Item.Properties()
                    )
            );

    // Материалы
    public static final DeferredHolder<Item, Item> STEEL =
            ITEMS.register("steel", () -> new Item(new Item.Properties()));

    public static final DeferredHolder<Item, Item> ORC_STEEL =
            ITEMS.register("orc_steel", () -> new Item(new Item.Properties()));

    // Оружие с упрощенными конструкторами
    public static final DeferredHolder<Item, ModSword> GONDOR_SWORD =
            ITEMS.register("gondor_sword", () ->
                    new ModSword(
                            Tiers.NETHERITE,
                            5.0F,    // урон
                            -2.4F,   // скорость атаки
                            2031,    // прочность
                            3.0      // дальность атаки
                    )
            );

    public static final DeferredHolder<Item, ModBattleAxe> GONDOR_AXE =
            ITEMS.register("gondor_axe", () ->
                    new ModBattleAxe(
                            Tiers.NETHERITE,
                            6.0F,    // урон (топор больше урона)
                            -3.0F,   // скорость атаки (топор медленнее)
                            2031,    // прочность
                            3.5      // дальность атаки (топор больше дальность)
                    )
            );

    /**
     * Регистрирует все предметы на шине событий мода.
     * Вызывается из конструктора MainMod.
     */
    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
        LOGGER.info("Registered {} items for {}", ITEMS.getEntries().size(), MainMod.MOD_ID);
    }
}
