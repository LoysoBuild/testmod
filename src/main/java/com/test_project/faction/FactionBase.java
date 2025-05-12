package com.test_project.faction;

import java.util.List;
import java.util.UUID;

public abstract class FactionBase {
    // Уникальный идентификатор фракции
    public abstract String getId();

    // Отображаемое имя
    public abstract String getDisplayName();

    // Описание фракции
    public abstract String getDescription();

    // Союзники и враги (id фракций)
    public abstract List<String> getAllies();
    public abstract List<String> getEnemies();

    // Базовые награды (можно расширить)
    public abstract List<String> getBaseRewards();

    // Стартовая репутация для нового игрока
    public abstract int getStartReputation();

    // Обработка событий (можно расширить под нужды)
    public void onEvent(UUID playerId, String event, Object data) {}

    // Начисление/снятие репутации
    public abstract void addReputation(UUID playerId, int amount);
    public abstract int getReputation(UUID playerId);

    // Проверка доступа к предметам/квестам
    public boolean canAccessItem(UUID playerId, String itemId) { return true; }
    public boolean canAccessQuest(UUID playerId, String questId) { return true; }
}
