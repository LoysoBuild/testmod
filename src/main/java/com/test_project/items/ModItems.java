package com.test_project.items;

import com.test_project.MainMod;
import com.test_project.entity.ModEntities;
import com.test_project.items.weapone.feature.WeaponFeatureSet;
<<<<<<< Updated upstream
=======
<<<<<<< HEAD
import com.test_project.items.weapone.weaponeclass.ModAxe;
import com.test_project.items.weapone.weaponeclass.ModHalberd;
import com.test_project.items.weapone.weaponeclass.ModSword;
=======
>>>>>>> Stashed changes
import com.test_project.items.weapone.hammer.ModHammer;
import com.test_project.items.weapone.sword.ModSword;
>>>>>>> a8238784e08d63e14d6f55e460f4770a73f2c14d
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
    public static final DeferredItem<ModSword> GONDOR_AXE = ITEMS.registerItem(
            "gondor_axe",
            props -> new ModAxe(
                    Tiers.NETHERITE,
                    5.0F,
                    -2.4F,
                    2031,
                    5.0,
                    new WeaponFeatureSet().add("counterattack")
            ),
            new Item.Properties()
    );
    public static final DeferredItem<ModSword> GONDOR_BIG_AXE = ITEMS.registerItem(
            "gondor_big_axe",
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
    public static final DeferredItem<ModSword> GONDOR_HAMMER = ITEMS.registerItem(
            "gondor_hammer",
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
    public static final DeferredItem<ModSword> GONDOR_BIG_HAMMER = ITEMS.registerItem(
            "gondor_big_hammer",
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
    public static final DeferredItem<ModSword> GONDOR_BOW = ITEMS.registerItem(
            "gondor_bow",
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
    public static final DeferredItem<ModSword> GONDOR_BOW_LONG = ITEMS.registerItem(
            "gondor_bow_long",
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
    public static final DeferredItem<ModSword> GONDOR_CROSSBOW = ITEMS.registerItem(
            "gondor_crossbow",
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
    public static final DeferredItem<ModSword> GONDOR_DAGGER = ITEMS.registerItem(
            "gondor_dagger",
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
    public static final DeferredItem<ModSword> GONDOR_HALBERD = ITEMS.registerItem(
            "gondor_halberd",
            props -> new ModHalberd(
                    Tiers.NETHERITE,
                    5.0F,
                    -2.4F,
                    2031,
                    5.0,
                    new WeaponFeatureSet().add("counterattack")
            ),
            new Item.Properties()
    );
    public static final DeferredItem<ModSword> GONDOR_LONGSWORD = ITEMS.registerItem(
            "gondor_longsword",
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
    public static final DeferredItem<ModSword> GONDOR_WHIP = ITEMS.registerItem(
            "gondor_whip",
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
    public static final DeferredItem<ModSword> GONDOR_SPEAR = ITEMS.registerItem(
            "gondor_spear",
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

    public static final DeferredItem<ModHammer> GONDOR_HAMMER = ITEMS.registerItem(
            "gondor_hammer",
            props -> new ModHammer(
                    Tiers.NETHERITE,
                    9.0F,
                    -3.2F,
                    2031,
                    3.0,
                    new WeaponFeatureSet().add("custom_knockback")
            ),
            new Item.Properties()
    );

    public static final DeferredItem<ModHammer> GONDOR_HAMMER = ITEMS.registerItem(
            "gondor_hammer",
            props -> new ModHammer(
                    Tiers.NETHERITE,
                    9.0F,
                    -3.2F,
                    2031,
                    3.0,
                    new WeaponFeatureSet().add("custom_knockback")
            ),
            new Item.Properties()
    );


    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}