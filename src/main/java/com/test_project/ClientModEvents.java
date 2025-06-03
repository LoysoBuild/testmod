package com.test_project;

import com.test_project.gui.MainMenuScreen;
import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.InputEvent;

@EventBusSubscriber(modid = "mainmod", bus = EventBusSubscriber.Bus.GAME, value = Dist.CLIENT)
public class ClientModEvents {

    @SubscribeEvent
    public static void onKeyInput(InputEvent.Key event) {
        Minecraft minecraft = Minecraft.getInstance();

        // Проверяем, что игрок в игре и не в другом GUI
        if (minecraft.screen == null && minecraft.player != null) {
            // Теперь это работает корректно
            if (KeyBindings.OPEN_MAIN_MENU.consumeClick()) {
                minecraft.setScreen(new MainMenuScreen());
            }
        }
    }
}
