package com.test_project.gui;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class FactionSkillTreeScreen extends Screen {

    // Путь к вашему фоновому изображению
    private static final ResourceLocation BG_TEXTURE =
            ResourceLocation.fromNamespaceAndPath("mainmod", "textures/gui/faction_menu.png");

    public FactionSkillTreeScreen() {
        super(Component.literal("Дерево навыков"));
    }

    @Override
    public void render(GuiGraphics gui, int mouseX, int mouseY, float partialTick) {
        // НЕ вызываем this.renderBackground(), чтобы не было размытия Minecraft!

        // Размеры фонового изображения
        int bgWidth = 156;
        int bgHeight = 192;
        int x = (this.width - bgWidth) / 2;
        int y = (this.height - bgHeight) / 2;

        // Рисуем только свой фон
        gui.blit(BG_TEXTURE, x, y, 0, 0, bgWidth, bgHeight, bgWidth, bgHeight);

        // Если появятся кнопки/виджеты - их можно добавить, super.render не помешает
        super.render(gui, mouseX, mouseY, partialTick);
    }
}
