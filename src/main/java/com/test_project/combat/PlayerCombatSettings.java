package com.test_project.combat;

import com.test_project.combat.stance.StanceType;
import java.util.EnumMap;
import java.util.Map;

public class PlayerCombatSettings {
    private final Map<StanceType, String> stanceCombos = new EnumMap<>(StanceType.class);
    private StanceType currentStance = StanceType.ATTACK;

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

    // Для Capability/Network синхронизации реализуйте сериализацию при необходимости
}
