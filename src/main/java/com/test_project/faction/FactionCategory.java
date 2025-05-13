package com.test_project.faction;

public enum FactionCategory {
    GOOD("Добро", 0x4CAF50),
    EVIL("Зло", 0xB71C1C),
    NEUTRAL("Нейтралитет", 0x607D8B);

    private final String displayName;
    private final int color;

    FactionCategory(String displayName, int color) {
        this.displayName = displayName;
        this.color = color;
    }

    public String getDisplayName() {
        return displayName;
    }

    public int getColor() {
        return color;
    }
}
