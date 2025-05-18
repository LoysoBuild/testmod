package com.test_project;

import com.test_project.gui.FactionGuiScreen;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import org.lwjgl.glfw.GLFW;

public class ModKeybinds {
    public static final KeyMapping OPEN_FACTION_GUI = new KeyMapping(
            "key.mainmod.open_faction_gui",
            GLFW.GLFW_KEY_G,
            "key.categories.mainmod"
    );

    // Регистрировать через modEventBus.addListener(ModKeybinds::registerKeyMappings)
    public static void registerKeyMappings(RegisterKeyMappingsEvent event) {
        event.register(OPEN_FACTION_GUI);
    }

    // Регистрировать через NeoForge.EVENT_BUS.addListener(ModKeybinds::onClientTick)
    public static void onClientTick(ClientTickEvent.Post event) {
        if (Minecraft.getInstance().player != null) {
            while (OPEN_FACTION_GUI.consumeClick()) {
                Minecraft.getInstance().setScreen(new FactionGuiScreen());
            }
        }
    }
}
