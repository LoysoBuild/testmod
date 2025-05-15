package com.test_project.items;

import com.test_project.MainMod;
import com.test_project.entity.ModEntities;
import com.test_project.items.weapone.feature.WeaponFeatureSet;
import com.test_project.items.weapone.sword.ModSword;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.item.Tiers;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(MainMod.MOD_ID);

    public static final DeferredHolder<Item, SpawnEggItem> TEST_MOB_SPAWN_EGG =
            ITEMS.register("test_mob_spawn_egg", () ->
                    new SpawnEggItem(
                            ModEntities.TEST_MOB.get(), // должен быть не null!
                            0xA0A0A0, 0x505050,
                            new Item.Properties()
                    ));


    public static final DeferredItem<Item> STEEL = ITEMS.register("steel",
            () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> ORC_STEEL = ITEMS.register("orc_steel",
            () -> new Item(new Item.Properties()));


    public static final DeferredItem<ModSword> GONDOR_SWORD = ITEMS.registerItem(
            "gondor_sword",
            props -> new ModSword(
                    Tiers.NETHERITE,
                    5.0F,
                    -2.4F,
                    2031,
                    5.0,
                    new WeaponFeatureSet().add("counterattack")
            ),
            new Item.Properties()
    );


    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}