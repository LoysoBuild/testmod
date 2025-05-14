package com.test_project.gui;

import com.mojang.blaze3d.systems.RenderSystem;

import com.test_project.worldrep.WorldReputation;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class FactionGuiScreen extends Screen {
    private static final ResourceLocation BG_TEXTURE =
            ResourceLocation.fromNamespaceAndPath("mainmod", "textures/gui/faction_menu.png");
    private static final ResourceLocation CLOSE_ICON =
            ResourceLocation.fromNamespaceAndPath("mainmod", "textures/gui/close_icon.png");
    private static final ResourceLocation EMBLEM_TEXTURE =
            ResourceLocation.fromNamespaceAndPath("mainmod", "textures/gui/emblems/gondor.png"); // пример

    private static final int BG_WIDTH = 256;
    private static final int BG_HEIGHT = 256;

    private int bgX, bgY;

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

        // Кнопка "Дерево навыков"
        int skillBtnWidth = 120;
        int skillBtnHeight = 20;
        int skillBtnX = bgX + (BG_WIDTH - skillBtnWidth) / 2;
        int skillBtnY = bgY + BG_HEIGHT - skillBtnHeight - 20;

        addRenderableWidget(new TextButton(skillBtnX, skillBtnY, skillBtnWidth, skillBtnHeight,
                Component.literal("Дерево навыков"), () -> {
            // Здесь можно получить репутацию игрока из capability или другого хранилища
            // Для теста создаём временную репутацию:
            WorldReputation rep = new WorldReputation(100);
            this.minecraft.setScreen(new SkillTreeScreen(rep));
        }));
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        // 1. Кнопки и виджеты
        super.render(guiGraphics, mouseX, mouseY, partialTick);

        // 2. Фон
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, BG_TEXTURE);
        guiGraphics.blit(BG_TEXTURE, bgX, bgY, 0, 0, BG_WIDTH, BG_HEIGHT);

        // 3. Эмблема и текст
        int emblemSize = 48;
        int emblemX = bgX + (BG_WIDTH - emblemSize) / 2;
        int emblemY = bgY + 30;
        guiGraphics.blit(EMBLEM_TEXTURE, emblemX, emblemY, 0, 0, emblemSize, emblemSize, emblemSize, emblemSize);

        var font = Minecraft.getInstance().font;
        String title = "Фракции";
        int titleX = bgX + (BG_WIDTH - font.width(title)) / 2;
        int titleY = bgY + 10;
        guiGraphics.drawString(font, title, titleX, titleY, 0xFFFFFF, false);
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

    public static class TextButton extends Button {
        public TextButton(int x, int y, int width, int height, Component label, Runnable onPress) {
            super(x, y, width, height, label, btn -> onPress.run(), DEFAULT_NARRATION);
        }

        @Override
        public void renderWidget(GuiGraphics gui, int mouseX, int mouseY, float partialTick) {
            int color = isHoveredOrFocused() ? 0xFFAAAAAA : 0xFF888888;
            gui.fill(getX(), getY(), getX() + width, getY() + height, color);

            int textColor = 0xFFFFFF;
            var font = Minecraft.getInstance().font;
            int textX = getX() + (width - font.width(getMessage())) / 2;
            int textY = getY() + (height - font.lineHeight) / 2;
            gui.drawString(font, getMessage(), textX, textY, textColor, false);
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
