package com.test_project.combat;

import com.test_project.combat.stance.StanceType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.EnumMap;
import java.util.Map;

public class PlayerCombatSettings {
    private static final Logger LOGGER = LoggerFactory.getLogger(PlayerCombatSettings.class);

    private final Map<StanceType, String> stanceCombos = new EnumMap<>(StanceType.class);
    private StanceType currentStance = StanceType.ATTACK;
    private int stanceCooldownTicks = 0;

    // ========== Combo Management ==========
    public void setCombo(StanceType stance, String comboId) {
        LOGGER.info("Setting combo '{}' for stance {}", comboId, stance);
        stanceCombos.put(stance, comboId);
    }

    public String getCombo(StanceType stance) {
        String combo = stanceCombos.getOrDefault(stance, null);
        LOGGER.debug("Get combo for {}: {}", stance, combo != null ? combo : "none");
        return combo;
    }

    // ========== Stance Management ==========
    public synchronized void setCurrentStance(StanceType newStance) {
        if (newStance == currentStance) {
            LOGGER.warn("Trying to switch to same stance: {}", newStance);
            return;
        }

        if (isStanceCooldown()) {
            LOGGER.warn("Stance change blocked by cooldown! Remaining: {} ticks", stanceCooldownTicks);
            return;
        }

        LOGGER.info("Changing stance from {} to {}", currentStance, newStance);
        this.currentStance = newStance;
        setStanceCooldown(20); // 1 second cooldown by default
    }

    public StanceType getCurrentStance() {
        LOGGER.trace("Current stance: {}", currentStance);
        return currentStance;
    }

    // ========== Cooldown System ==========
    public synchronized void setStanceCooldown(int ticks) {
        LOGGER.debug("Setting cooldown: {} ticks", ticks);
        this.stanceCooldownTicks = Math.max(ticks, 0);
    }

    public synchronized void tickCooldown() {
        if (stanceCooldownTicks > 0) {
            stanceCooldownTicks--;
            LOGGER.trace("Cooldown tick: {} remaining", stanceCooldownTicks);
        }
    }

    public boolean isStanceCooldown() {
        boolean onCooldown = stanceCooldownTicks > 0;
        LOGGER.trace("Cooldown check: {}", onCooldown);
        return onCooldown;
    }

    public int getStanceCooldownTicks() {
        LOGGER.trace("Get cooldown ticks: {}", stanceCooldownTicks);
        return stanceCooldownTicks;
    }

    // ========== Serialization ==========
    // Добавьте методы NBT-сериализации при необходимости
}
