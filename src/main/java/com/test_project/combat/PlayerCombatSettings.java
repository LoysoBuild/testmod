package com.test_project.combat;

import com.test_project.combat.stance.StanceType;
import java.util.EnumMap;
import java.util.Map;

public class PlayerCombatSettings {
    private final Map<StanceType, String> stanceCombos = new EnumMap<>(StanceType.class);
    private StanceType currentStance = StanceType.ATTACK;
    private int stanceCooldownTicks = 0; // Кулдаун в тиках (20 = 1 секунда)

    public void setCombo(StanceType stance, String comboId) {
        stanceCombos.put(stance, comboId);
    }

    public String getCombo(StanceType stance) {
        return stanceCombos.getOrDefault(stance, null);
    }

    public void setCurrentStance(StanceType stance) {
        this.currentStance = stance;
    }

    public StanceType getCurrentStance() {
        return currentStance;
    }

    // --- Кулдаун между переключениями стоек ---

    /** Установить кулдаун (в тиках) после переключения стойки */
    public void setStanceCooldown(int ticks) {
        this.stanceCooldownTicks = ticks;
    }

    /** Уменьшить кулдаун на 1 тик (вызывать каждый серверный тик) */
    public void tickCooldown() {
        if (stanceCooldownTicks > 0) stanceCooldownTicks--;
    }

    /** true, если кулдаун еще не прошёл (стойку переключать нельзя) */
    public boolean isStanceCooldown() {
        return stanceCooldownTicks > 0;
    }

    /** Сколько тиков осталось до конца кулдауна */
    public int getStanceCooldownTicks() {
        return stanceCooldownTicks;
    }

    // Для Capability/Network синхронизации реализуйте сериализацию при необходимости
}
