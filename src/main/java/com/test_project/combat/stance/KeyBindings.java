package com.test_project.combat.stance;

import net.minecraft.client.KeyMapping;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import org.lwjgl.glfw.GLFW;

public class KeyBindings {
    public static final KeyMapping SWITCH_STANCE = new KeyMapping(
            "key.mainmod.switch_stance",
            GLFW.GLFW_KEY_V,
            "key.categories.mainmod"
    );

    // Регистрировать через modEventBus.addListener(KeyBindings::onRegisterKeys)
    public static void onRegisterKeys(RegisterKeyMappingsEvent event) {
        event.register(SWITCH_STANCE);
    }

    // Регистрировать через NeoForge.EVENT_BUS.addListener(KeyBindings::onClientTick)
    public static void onClientTick(ClientTickEvent.Post event) {
        while (SWITCH_STANCE.consumeClick()) {
            PacketDistributor.sendToServer(new C2SToggleStancePacket());
        }
    }
}
