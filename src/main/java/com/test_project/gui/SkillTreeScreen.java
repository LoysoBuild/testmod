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

public class SkillTreeScreen extends Screen {
    private static final ResourceLocation BG_TEXTURE =
            ResourceLocation.fromNamespaceAndPath("mainmod", "textures/gui/skilltree_bg.png");
    private static final ResourceLocation CLOSE_ICON =
            ResourceLocation.fromNamespaceAndPath("mainmod", "textures/gui/close_icon.png");

    private static final int BG_WIDTH = 256;
    private static final int BG_HEIGHT = 192;

    private int bgX, bgY;
    private WorldReputation reputation;

    public SkillTreeScreen(WorldReputation reputation) {
        super(Component.literal("Дерево навыков"));
        this.reputation = reputation;
    }

    @Override
    protected void init() {
        bgX = (this.width - BG_WIDTH) / 2;
        bgY = (this.height - BG_HEIGHT) / 2;

        int closeBtnSize = 20;
        int closeBtnX = bgX + BG_WIDTH - closeBtnSize - 6;
        int closeBtnY = bgY + 6;

        addRenderableWidget(new ImageButton(closeBtnX, closeBtnY, closeBtnSize, closeBtnSize, CLOSE_ICON, this::onClose));

        // Пример кнопки-навыка (иконка)
        int skillBtnSize = 32;
        int skillBtnX = bgX + 40;
        int skillBtnY = bgY + 60;
        addRenderableWidget(new SkillButton(skillBtnX, skillBtnY, skillBtnSize, skillBtnSize,
                Component.literal("Навык: Быстрое плавание"),
                10, // стоимость
                () -> {
                    if (reputation.trySpendPoints(10)) {
                        // Изучить навык, выдать игроку эффект и т.п.
                        Minecraft.getInstance().player.sendSystemMessage(Component.literal("Навык изучен!"));
                    } else {
                        Minecraft.getInstance().player.sendSystemMessage(Component.literal("Недостаточно репутации!"));
                    }
                }
        ));
    }

    @Override
    public void render(GuiGraphics gui, int mouseX, int mouseY, float partialTick) {
        // Фон
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, BG_TEXTURE);
        gui.blit(BG_TEXTURE, bgX, bgY, 0, 0, BG_WIDTH, BG_HEIGHT);

        // Текущая репутация
        var font = Minecraft.getInstance().font;
        String repText = "Мировая репутация: " + reputation.getPoints();
        int repX = bgX + 16;
        int repY = bgY + 16;
        gui.drawString(font, repText, repX, repY, 0xFFFFAA, false);

        super.render(gui, mouseX, mouseY, partialTick);
    }

    // Кнопка-навык
    public static class SkillButton extends Button {
        private final int cost;
        public SkillButton(int x, int y, int width, int height, Component label, int cost, Runnable onPress) {
            super(x, y, width, height, label, btn -> onPress.run(), DEFAULT_NARRATION);
            this.cost = cost;
        }

        @Override
        public void renderWidget(GuiGraphics gui, int mouseX, int mouseY, float partialTick) {
            int color = isHoveredOrFocused() ? 0xFF66FF99 : 0xFF228888;
            gui.fill(getX(), getY(), getX() + width, getY() + height, color);

            var font = Minecraft.getInstance().font;
            int textColor = 0xFFFFFF;
            int textX = getX() + (width - font.width(getMessage())) / 2;
            int textY = getY() + (height - font.lineHeight) / 2;
            gui.drawString(font, getMessage(), textX, textY, textColor, false);

            // Всплывающая подсказка
            if (isHoveredOrFocused()) {
                gui.renderTooltip(font, Component.literal("Стоимость: " + cost), mouseX, mouseY);
            }
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
}
