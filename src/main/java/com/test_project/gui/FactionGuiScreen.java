package com.test_project.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class FactionGuiScreen extends Screen {
    private static final ResourceLocation BG_TEXTURE =
            ResourceLocation.fromNamespaceAndPath("mainmod", "textures/gui/faction_menu1.png");
    private static final ResourceLocation CLOSE_ICON =
            ResourceLocation.fromNamespaceAndPath("mainmod", "textures/gui/close_icon.png");
    private static final ResourceLocation EMBLEM_TEXTURE =
            ResourceLocation.fromNamespaceAndPath("mainmod", "textures/gui/emblems/gondor.png"); // пример

    private static final int BG_WIDTH = 256;
    private static final int BG_HEIGHT = 256;

    private int bgX, bgY;

    // Кастомная "поверхностная" кнопка
    private OverlayButton overlayButton;

    public FactionGuiScreen() {
        super(Component.literal("Фракции"));
    }

    @Override
    protected void init() {
        bgX = (this.width - BG_WIDTH) / 2;
        bgY = (this.height - BG_HEIGHT) / 2;

        int closeBtnSize = 20;
        int closeBtnX = bgX + BG_WIDTH - closeBtnSize - 6;
        int closeBtnY = bgY + 6;

        addRenderableWidget(new ImageButton(closeBtnX, closeBtnY, closeBtnSize, closeBtnSize, CLOSE_ICON, this::onClose));

        // Создаём кастомную кнопку, которая будет рисоваться вручную поверх всего
        int overlayBtnWidth = 120;
        int overlayBtnHeight = 20;
        int overlayBtnX = bgX + (BG_WIDTH - overlayBtnWidth) / 2;
        int overlayBtnY = bgY + BG_HEIGHT - overlayBtnHeight - 20;
        overlayButton = new OverlayButton(overlayBtnX, overlayBtnY, overlayBtnWidth, overlayBtnHeight,
                Component.literal("Супер кнопка"), () -> this.minecraft.player.sendSystemMessage(
                Component.literal("Нажата супер кнопка!")));
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        // 1. Кнопки и виджеты (самый нижний слой)
        super.render(guiGraphics, mouseX, mouseY, partialTick);

        // 2. Фон поверх кнопок (но под кастомными элементами)
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, BG_TEXTURE);
        guiGraphics.blit(BG_TEXTURE, bgX, bgY, 0, 0, BG_WIDTH, BG_HEIGHT);

        // 3. Эмблема и текст поверх фона
        int emblemSize = 48;
        int emblemX = bgX + (BG_WIDTH - emblemSize) / 2;
        int emblemY = bgY + 30;
        guiGraphics.blit(EMBLEM_TEXTURE, emblemX, emblemY, 0, 0, emblemSize, emblemSize, emblemSize, emblemSize);

        var font = Minecraft.getInstance().font;
        String title = "Фракции";
        int titleX = bgX + (BG_WIDTH - font.width(title)) / 2;
        int titleY = bgY + 10;
        guiGraphics.drawString(font, title, titleX, titleY, 0xFFFFFF, false);

        // 4. Кастомная кнопка - рисуем поверх всего
        overlayButton.render(guiGraphics, mouseX, mouseY, partialTick);
    }

    // Обработка клика для overlayButton
    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (overlayButton.mouseClicked(mouseX, mouseY, button)) {
            return true;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    // Кастомная кнопка, не добавляется в addRenderableWidget, а рисуется вручную
    public static class OverlayButton extends Button {
        public OverlayButton(int x, int y, int width, int height, Component label, Runnable onPress) {
            super(x, y, width, height, label, btn -> onPress.run(), DEFAULT_NARRATION);
        }

        @Override
        public void renderWidget(GuiGraphics gui, int mouseX, int mouseY, float partialTick) {
            int color = isHoveredOrFocused() ? 0xFF66FF66 : 0xFF228822;
            gui.fill(getX(), getY(), getX() + width, getY() + height, color);

            int textColor = 0xFFFFFF;
            var font = Minecraft.getInstance().font;
            int textX = getX() + (width - font.width(getMessage())) / 2;
            int textY = getY() + (height - font.lineHeight) / 2;
            gui.drawString(font, getMessage(), textX, textY, textColor, false);
        }
    }

    public static class ImageButton extends Button {
        private final ResourceLocation icon;

        public ImageButton(int x, int y, int width, int height, ResourceLocation icon, Runnable onPress) {
            super(x, y, width, height, Component.empty(), btn -> onPress.run(), DEFAULT_NARRATION);
            this.icon = icon;
        }

        @Override
        public void renderWidget(GuiGraphics gui, int mouseX, int mouseY, float partialTick) {
            int color = isHoveredOrFocused() ? 0xFFAAAAAA : 0xFF888888;
            gui.fill(getX(), getY(), getX() + width, getY() + height, color);

            int iconSize = Math.min(width, height) - 4;
            int iconX = getX() + (width - iconSize) / 2;
            int iconY = getY() + (height - iconSize) / 2;
            gui.blit(icon, iconX, iconY, 0, 0, iconSize, iconSize, iconSize, iconSize);
        }
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == 256) { // ESC
            this.onClose();
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }
}
