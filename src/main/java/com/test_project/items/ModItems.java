package com.test_project.items;

import com.test_project.MainMod;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(MainMod.MOD_ID);

    public static final DeferredItem<Item> STEEL = ITEMS.register("steel",
            () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> ORC_STEEL = ITEMS.register("orc_steel",
            () -> new Item(new Item.Properties()));


    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}