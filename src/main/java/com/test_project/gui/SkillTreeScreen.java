package com.test_project.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import com.test_project.skills.Skill;
import com.test_project.skills.SkillTree;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SkillTreeScreen extends Screen {
    private static final ResourceLocation BG_TEXTURE =
            ResourceLocation.fromNamespaceAndPath("mainmod", "textures/gui/menu/skill_tree.png");

    private static final int BG_WIDTH = 256;
    private static final int BG_HEIGHT = 256;

    private int bgX, bgY;

    // Кастомные кнопки-навыки
    private final List<OverlayButton> skillButtons = new ArrayList<>();
    private final SkillTree skillTree;

    public SkillTreeScreen(SkillTree skillTree) {
        super(Component.literal("Дерево навыков"));
        this.skillTree = skillTree;
    }

    @Override
    protected void init() {
        bgX = (this.width - BG_WIDTH) / 2;
        bgY = (this.height - BG_HEIGHT) / 2;
        skillButtons.clear();

        // Пример вертикального расположения кнопок-навыков
        int btnWidth = 140;
        int btnHeight = 20;
        int startY = bgY + 60;
        int x = bgX + (BG_WIDTH - btnWidth) / 2;
        int y = startY;

        for (Map.Entry<String, Skill> entry : skillTree.getAllSkills().entrySet()) {
            Skill skill = entry.getValue();
            OverlayButton btn = new OverlayButton(x, y, btnWidth, btnHeight,
                    Component.literal(skill.getName() + " (" + skill.getCost() + ")"),
                    () -> {
                        Minecraft.getInstance().player.sendSystemMessage(
                                Component.literal("Прокачка навыка: " + skill.getName())
                        );
                        // Здесь добавь свою логику изучения навыка!
                    }
            );
            skillButtons.add(btn);
            y += btnHeight + 8;
        }
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

        // 3. Заголовок
        var font = Minecraft.getInstance().font;
        String title = "Дерево навыков";
        int titleX = bgX + (BG_WIDTH - font.width(title)) / 2;
        int titleY = bgY + 10;
        guiGraphics.drawString(font, title, titleX, titleY, 0xFFFFFF, false);

        // 4. Кастомные кнопки-навыки
        for (OverlayButton btn : skillButtons) {
            btn.render(guiGraphics, mouseX, mouseY, partialTick);
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        for (OverlayButton btn : skillButtons) {
            if (btn.mouseClicked(mouseX, mouseY, button)) {
                return true;
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    // Кастомная кнопка, как в FactionGuiScreen
    public static class OverlayButton extends Button {
        public OverlayButton(int x, int y, int width, int height, Component label, Runnable onPress) {
            super(x, y, width, height, label, btn -> onPress.run(), DEFAULT_NARRATION);
        }

        @Override
        public void renderWidget(GuiGraphics gui, int mouseX, int mouseY, float partialTick) {
            int color = isHoveredOrFocused() ? 0xFF66AAFF : 0xFF225588;
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
