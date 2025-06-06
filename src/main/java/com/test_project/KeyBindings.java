package com.test_project;

import net.minecraft.client.KeyMapping;
import org.lwjgl.glfw.GLFW;

public final class KeyBindings {
    public static final KeyMapping TOGGLE_STANCE = new KeyMapping(
            "key.mainmod.toggle_stance",
            GLFW.GLFW_KEY_R,
            "key.categories.mainmod"
    );

    public static final KeyMapping OPEN_MAIN_MENU = new KeyMapping(
            "key.mainmod.open_main_menu",
            GLFW.GLFW_KEY_M,
            "key.categories.mainmod"
    );

    // ИСПРАВЛЕНО: Удален устаревший метод register()
    // Регистрация происходит через ClientSetup
}
