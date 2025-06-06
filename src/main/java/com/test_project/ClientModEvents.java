package com.test_project;

import com.test_project.gui.MainMenuScreen;
import com.test_project.combat.stance.C2SToggleStancePacket;
import com.test_project.combat.stance.NetworkManager;
import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.InputEvent;
import com.mojang.logging.LogUtils;
import org.slf4j.Logger;

@EventBusSubscriber(modid = "mainmod", bus = EventBusSubscriber.Bus.GAME, value = Dist.CLIENT)
public final class ClientModEvents {
    private static final Logger LOGGER = LogUtils.getLogger();

    @SubscribeEvent
    public static void onKeyInput(InputEvent.Key event) {
        Minecraft minecraft = Minecraft.getInstance();

        if (minecraft.screen == null && minecraft.player != null) {
            if (KeyBindings.OPEN_MAIN_MENU.consumeClick()) {
                minecraft.setScreen(new MainMenuScreen());
                LOGGER.debug("[CLIENT] Opening main menu");
            }

            if (KeyBindings.TOGGLE_STANCE.consumeClick()) {
                LOGGER.info("[CLIENT] Stance toggle key pressed - sending packet");
                try {
                    NetworkManager.sendToServer(new C2SToggleStancePacket());
                    LOGGER.info("[CLIENT] Stance toggle packet sent successfully");
                } catch (Exception e) {
                    LOGGER.error("[CLIENT] Failed to send stance toggle packet: {}", e.getMessage());
                }
            }
        }
    }
}
