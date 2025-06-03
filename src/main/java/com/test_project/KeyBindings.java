package com.test_project;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.settings.KeyConflictContext;
import net.neoforged.neoforge.client.settings.KeyModifier;

@EventBusSubscriber(modid = "mainmod", bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class KeyBindings {

    public static final String KEY_CATEGORY = "key.categories.mainmod";

    public static final KeyMapping TOGGLE_STANCE = new KeyMapping(
            "key.mainmod.toggle_stance",
            KeyConflictContext.IN_GAME,
            KeyModifier.NONE,
            InputConstants.getKey(InputConstants.KEY_R, -1),
            KEY_CATEGORY
    );

    public static final KeyMapping OPEN_MAIN_MENU = new KeyMapping(
            "key.mainmod.open_main_menu",
            KeyConflictContext.IN_GAME,
            KeyModifier.NONE,
            InputConstants.getKey(InputConstants.KEY_M, -1),
            KEY_CATEGORY
    );

    @SubscribeEvent
    public static void register(RegisterKeyMappingsEvent event) {
        event.register(TOGGLE_STANCE);
        event.register(OPEN_MAIN_MENU);
        System.out.println("[KeyBindings] Registered key mappings successfully");
    }
}
