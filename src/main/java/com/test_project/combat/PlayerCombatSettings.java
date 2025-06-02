package com.test_project.combat;

import com.test_project.combat.stance.StanceType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import java.util.EnumMap;
import java.util.Map;

public class PlayerCombatSettings {
    private final Map<StanceType, String> stanceCombos = new EnumMap<>(StanceType.class);
    private StanceType currentStance = StanceType.ATTACK;
    private int stanceCooldownTicks = 0; // Кулдаун в тиках (20 = 1 секунда)

    // Дополнительные поля для расширенной боевой системы
    private boolean isInCombat = false;
    private int combatTimer = 0; // Время с последней атаки/получения урона
    private String lastUsedWeaponType = "";
    private int comboStep = 0; // Текущий шаг в комбо
    private long lastAttackTime = 0; // Время последней атаки для сброса комбо

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
        if (combatTimer > 0) combatTimer--;

        // Выход из боя через 5 секунд (100 тиков) без активности
        if (combatTimer <= 0 && isInCombat) {
            isInCombat = false;
            resetCombo();
        }
    }

    /** true, если кулдаун еще не прошёл (стойку переключать нельзя) */
    public boolean isStanceCooldown() {
        return stanceCooldownTicks > 0;
    }

    /** Сколько тиков осталось до конца кулдауна */
    public int getStanceCooldownTicks() {
        return stanceCooldownTicks;
    }

    // --- Дополнительные методы для боевой системы ---

    /** Вход в бой */
    public void enterCombat() {
        this.isInCombat = true;
        this.combatTimer = 100; // 5 секунд
    }

    /** Проверка, находится ли игрок в бою */
    public boolean isInCombat() {
        return isInCombat;
    }

    /** Установить тип последнего использованного оружия */
    public void setLastUsedWeaponType(String weaponType) {
        // Если сменилось оружие - сбросить комбо
        if (!this.lastUsedWeaponType.equals(weaponType)) {
            resetCombo();
        }
        this.lastUsedWeaponType = weaponType;
    }

    public String getLastUsedWeaponType() {
        return lastUsedWeaponType;
    }

    /** Продвинуть комбо на следующий шаг */
    public void advanceCombo() {
        this.comboStep++;
        this.lastAttackTime = System.currentTimeMillis();
        enterCombat();
    }

    /** Сбросить комбо */
    public void resetCombo() {
        this.comboStep = 0;
    }

    /** Получить текущий шаг комбо */
    public int getComboStep() {
        // Сброс комбо через 2 секунды бездействия
        if (System.currentTimeMillis() - lastAttackTime > 2000) {
            resetCombo();
        }
        return comboStep;
    }

    /** Проверить, можно ли выполнить следующий шаг комбо */
    public boolean canContinueCombo() {
        return System.currentTimeMillis() - lastAttackTime < 2000;
    }

    // --- Сериализация для сохранения данных ---

    /** Сохранить настройки в NBT */
    public CompoundTag saveToNBT() {
        CompoundTag tag = new CompoundTag();
        tag.putString("currentStance", currentStance.name());
        tag.putInt("stanceCooldownTicks", stanceCooldownTicks);
        tag.putBoolean("isInCombat", isInCombat);
        tag.putInt("combatTimer", combatTimer);
        tag.putString("lastUsedWeaponType", lastUsedWeaponType);
        tag.putInt("comboStep", comboStep);
        tag.putLong("lastAttackTime", lastAttackTime);

        // Сохранение комбо для каждой стойки
        CompoundTag combosTag = new CompoundTag();
        for (Map.Entry<StanceType, String> entry : stanceCombos.entrySet()) {
            if (entry.getValue() != null) {
                combosTag.putString(entry.getKey().name(), entry.getValue());
            }
        }
        tag.put("stanceCombos", combosTag);

        return tag;
    }

    /** Загрузить настройки из NBT */
    public void loadFromNBT(CompoundTag tag) {
        if (tag.contains("currentStance")) {
            try {
                this.currentStance = StanceType.valueOf(tag.getString("currentStance"));
            } catch (IllegalArgumentException e) {
                this.currentStance = StanceType.ATTACK; // Дефолт при ошибке
            }
        }

        this.stanceCooldownTicks = tag.getInt("stanceCooldownTicks");
        this.isInCombat = tag.getBoolean("isInCombat");
        this.combatTimer = tag.getInt("combatTimer");
        this.lastUsedWeaponType = tag.getString("lastUsedWeaponType");
        this.comboStep = tag.getInt("comboStep");
        this.lastAttackTime = tag.getLong("lastAttackTime");

        // Загрузка комбо для каждой стойки
        if (tag.contains("stanceCombos")) {
            CompoundTag combosTag = tag.getCompound("stanceCombos");
            stanceCombos.clear();
            for (StanceType stance : StanceType.values()) {
                if (combosTag.contains(stance.name())) {
                    stanceCombos.put(stance, combosTag.getString(stance.name()));
                }
            }
        }
    }

    /** Создать копию настроек для синхронизации с клиентом */
    public PlayerCombatSettings copy() {
        PlayerCombatSettings copy = new PlayerCombatSettings();
        copy.currentStance = this.currentStance;
        copy.stanceCooldownTicks = this.stanceCooldownTicks;
        copy.isInCombat = this.isInCombat;
        copy.combatTimer = this.combatTimer;
        copy.lastUsedWeaponType = this.lastUsedWeaponType;
        copy.comboStep = this.comboStep;
        copy.lastAttackTime = this.lastAttackTime;
        copy.stanceCombos.putAll(this.stanceCombos);
        return copy;
    }

    /** Применить настройки из другого объекта (для синхронизации) */
    public void applyFrom(PlayerCombatSettings other) {
        this.currentStance = other.currentStance;
        this.stanceCooldownTicks = other.stanceCooldownTicks;
        this.isInCombat = other.isInCombat;
        this.combatTimer = other.combatTimer;
        this.lastUsedWeaponType = other.lastUsedWeaponType;
        this.comboStep = other.comboStep;
        this.lastAttackTime = other.lastAttackTime;
        this.stanceCombos.clear();
        this.stanceCombos.putAll(other.stanceCombos);
    }

    // Для Capability/Network синхронизации реализуйте сериализацию при необходимости
}
