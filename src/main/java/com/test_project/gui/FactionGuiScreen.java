package com.test_project.gui;

import com.test_project.faction.FactionBase;
import com.test_project.faction.FactionCategory;
import com.test_project.faction.FactionRegistry;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.List;
import java.util.stream.StreamSupport;

public class FactionGuiScreen extends Screen {
    private int categoryIndex = 0;
    private int factionPage = 0;
    private List<FactionBase> currentFactions;

    public FactionGuiScreen() {
        super(Component.literal("Фракции"));
        updateFactionList();
    }

    private void updateFactionList() {
        FactionCategory currentCategory = FactionCategory.values()[categoryIndex];
        currentFactions = StreamSupport.stream(FactionRegistry.all().spliterator(), false)
                .filter(f -> f.getCategory() == currentCategory)
                .toList();
        if (factionPage >= currentFactions.size()) factionPage = 0;
    }

    @Override
    protected void init() {
        clearWidgets();
        int centerX = width / 2;
        int topY = 40;

        // Стрелки категорий
        addRenderableWidget(Button.builder(Component.literal("<"), b -> {
            categoryIndex = (categoryIndex - 1 + FactionCategory.values().length) % FactionCategory.values().length;
            updateFactionList();
            this.init();
        }).bounds(centerX - 80, topY, 20, 20).build());

        addRenderableWidget(Button.builder(Component.literal(">"), b -> {
            categoryIndex = (categoryIndex + 1) % FactionCategory.values().length;
            updateFactionList();
            this.init();
        }).bounds(centerX + 60, topY, 20, 20).build());

        // Стрелки фракций
        addRenderableWidget(Button.builder(Component.literal("<"), b -> {
            if (!currentFactions.isEmpty()) {
                factionPage = (factionPage - 1 + currentFactions.size()) % currentFactions.size();
                this.init();
            }
        }).bounds(centerX - 80, topY + 60, 20, 20).build());

        addRenderableWidget(Button.builder(Component.literal(">"), b -> {
            if (!currentFactions.isEmpty()) {
                factionPage = (factionPage + 1) % currentFactions.size();
                this.init();
            }
        }).bounds(centerX + 60, topY + 60, 20, 20).build());

   /*     // Кнопка "Дерево навыков"
        addRenderableWidget(Button.builder(Component.literal("Дерево навыков"), b -> {
            if (!currentFactions.isEmpty()) {
                minecraft.setScreen(new FactionSkillTreeScreen(currentFactions.get(factionPage)));
            }
        }).bounds(centerX - 50, topY + 180, 100, 20).build());*/

        // RenderableOnly для фона, текста и эмблемы (всегда поверх кнопок)
        addRenderableOnly(new Renderable() {
            @Override
            public void render(GuiGraphics gui, int mouseX, int mouseY, float partialTick) {
                renderForeground(gui, mouseX, mouseY, partialTick);
            }
        });
    }

    private void renderForeground(GuiGraphics gui, int mouseX, int mouseY, float partialTick) {
        // Регистрируем и рисуем фон здесь
        ResourceLocation backgroundTexture = ResourceLocation.fromNamespaceAndPath("mainmod", "textures/gui/faction_menu.png");
        int bgWidth = 152, bgHeight = 192;
        int x = (this.width - bgWidth) / 2;
        int y = (this.height - bgHeight) / 2;
        gui.blit(backgroundTexture, x, y, 0, 0, bgWidth, bgHeight, bgWidth, bgHeight);

        int centerX = width / 2;
        int topY = 40;
        FactionCategory category = FactionCategory.values()[categoryIndex];
        gui.drawCenteredString(this.font, "Категория: " + category.getDisplayName(), centerX, topY - 15, category.getColor());

        if (!currentFactions.isEmpty()) {
            FactionBase faction = currentFactions.get(factionPage);

            // Эмблема фракции
            if (faction.getEmblem() != null) {
                int emblemSize = 48;
                int emblemX = centerX - emblemSize / 2;
                int emblemY = topY + 30;
                gui.blit(faction.getEmblem(), emblemX, emblemY, 0, 0, emblemSize, emblemSize, emblemSize, emblemSize);
            }

            gui.drawCenteredString(this.font, faction.getDisplayName(), centerX, topY + 85, 0xFFFFFF);
            gui.drawCenteredString(this.font, faction.getDescription(), centerX, topY + 100, 0xAAAAAA);
        } else {
            gui.drawCenteredString(this.font, "Нет фракций в этой категории", centerX, topY + 70, 0xFF5555);
        }
    }

    @Override
    public void render(GuiGraphics gui, int mouseX, int mouseY, float partialTick) {
        // Не вызываем this.renderBackground(), чтобы не было размытости Minecraft!
        super.render(gui, mouseX, mouseY, partialTick);
        // Всё остальное (фон, текст, эмблема) рисуется в renderForeground через RenderableOnly
    }
}
