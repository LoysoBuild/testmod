package com.test_project;



import com.test_project.gui.FactionGuiScreen;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import org.lwjgl.glfw.GLFW;
import net.neoforged.neoforge.client.event.ClientTickEvent;

@EventBusSubscriber(modid = "mainmod", bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ModKeybinds {
    public static final KeyMapping OPEN_FACTION_GUI = new KeyMapping(
            "key.mainmod.open_faction_gui", // локализуемое имя
            GLFW.GLFW_KEY_G,                // по умолчанию клавиша G
            "key.categories.mainmod"        // категория в настройках управления
    );

    @SubscribeEvent
    public static void registerKeyMappings(RegisterKeyMappingsEvent event) {
        event.register(OPEN_FACTION_GUI);
    }

    // Обработка нажатия клавиши на клиенте
    @EventBusSubscriber(modid = "mainmod", value = Dist.CLIENT)
    public static class ClientEvents {

        @SubscribeEvent
        public static void onClientTick(ClientTickEvent.Post event) {
            if (Minecraft.getInstance().player != null) {
                while (OPEN_FACTION_GUI.consumeClick()) {
                    Minecraft.getInstance().setScreen(new FactionGuiScreen());
                }
            }
        }
    }
}
