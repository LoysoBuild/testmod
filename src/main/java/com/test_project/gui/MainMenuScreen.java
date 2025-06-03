package com.test_project.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import com.test_project.MainMod;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class MainMenuScreen extends Screen {

    // Константы для размеров и позиций
    private static final ResourceLocation BACKGROUND_TEXTURE =
            ResourceLocation.fromNamespaceAndPath(MainMod.MOD_ID, "textures/gui/menu/menu_main.png");

    private static final int MENU_WIDTH = 256;
    private static final int MENU_HEIGHT = 200;

    // Позиция меню на экране
    private int guiLeft, guiTop;

    public MainMenuScreen() {
        super(Component.translatable("gui.mainmod.main_menu.title"));
    }

    @Override
    protected void init() {
        super.init();

        // Центрируем меню на экране
        this.guiLeft = (this.width - MENU_WIDTH) / 2;
        this.guiTop = (this.height - MENU_HEIGHT) / 2;

        // Кнопка закрытия (X в правом верхнем углу)
        this.addRenderableWidget(Button.builder(
                Component.literal("✕"),
                button -> this.onClose()
        ).bounds(guiLeft + MENU_WIDTH - 25, guiTop + 5, 20, 20).build());

        // Здесь добавляйте свои кнопки и элементы
        this.initCustomElements();
    }

    /**
     * Переопределите этот метод для добавления своих элементов
     */
    protected void initCustomElements() {
        // Пример кнопки - замените на свои
        this.addRenderableWidget(Button.builder(
                Component.literal("Пример кнопки"),
                button -> {
                    // Ваша логика здесь
                    this.minecraft.player.sendSystemMessage(Component.literal("Кнопка нажата!"));
                }
        ).bounds(guiLeft + 20, guiTop + 40, 200, 20).build());
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        // УБРАНО: Не создаём затемнённый фон
        // this.renderCustomBackground(guiGraphics);

        // Рендерим только изображение пергамента без затемнения
        this.renderSharpBackground(guiGraphics);

        // Заголовок меню
        this.renderTitle(guiGraphics);

        // Рендер всех кнопок и элементов
        super.render(guiGraphics, mouseX, mouseY, partialTick);

        // Дополнительные элементы поверх всего
        this.renderCustomOverlay(guiGraphics, mouseX, mouseY, partialTick);
    }

    /**
     * Рендер четкого изображения пергамента БЕЗ затемнения
     */
    private void renderSharpBackground(GuiGraphics guiGraphics) {
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();

        // Рендерим изображение пергамента с точными размерами
        guiGraphics.blit(BACKGROUND_TEXTURE,
                guiLeft, guiTop,           // позиция на экране
                0, 0,                      // позиция в текстуре
                MENU_WIDTH, MENU_HEIGHT,   // размер на экране
                MENU_WIDTH, MENU_HEIGHT    // размер текстуры
        );

        RenderSystem.disableBlend();
    }

    /**
     * ПОЛНОСТЬЮ переопределяем метод фона - НЕ добавляем затемнение
     */
    @Override
    public void renderBackground(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        // НИЧЕГО НЕ ДЕЛАЕМ - полностью прозрачный фон
        // Это убирает стандартное затемнение Minecraft
    }

    /**
     * Рендер заголовка - переопределите для кастомизации
     */
    protected void renderTitle(GuiGraphics guiGraphics) {
        guiGraphics.drawCenteredString(this.font, this.title,
                guiLeft + MENU_WIDTH / 2, guiTop + 15, 0x8B4513);
    }

    /**
     * Переопределите для добавления кастомных элементов поверх всего
     */
    protected void renderCustomOverlay(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        // Ваши кастомные элементы здесь
    }

    @Override
    public boolean isPauseScreen() {
        return false; // Не ставим игру на паузу
    }

    // Утилитарные методы для удобства

    /**
     * Проверяет, находится ли курсор в указанной области
     */
    protected boolean isMouseOver(int mouseX, int mouseY, int x, int y, int width, int height) {
        return mouseX >= x && mouseX < x + width && mouseY >= y && mouseY < y + height;
    }

    /**
     * Возвращает X координату относительно левого края меню
     */
    protected int getRelativeX(int offset) {
        return guiLeft + offset;
    }

    /**
     * Возвращает Y координату относительно верхнего края меню
     */
    protected int getRelativeY(int offset) {
        return guiTop + offset;
    }

    /**
     * Добавляет кнопку с относительными координатами
     */
    protected Button addRelativeButton(int x, int y, int width, int height,
                                       Component text, Button.OnPress onPress) {
        return this.addRenderableWidget(Button.builder(text, onPress)
                .bounds(getRelativeX(x), getRelativeY(y), width, height).build());
    }
}
