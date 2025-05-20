package com.test_project.combat.combo;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Реестр всех доступных комбо для боевой системы.
 * Позволяет регистрировать и получать комбо по идентификатору.
 */
public final class ComboRegistry {
    // Используем неизменяемую карту для внешнего доступа (best practice)
    private static final Map<String, Combo> COMBOS = new HashMap<>();

    private ComboRegistry() {
        // Приватный конструктор - утилитарный класс не должен инстанцироваться
    }

    /**
     * Регистрирует новое комбо.
     * @param combo объект Combo для регистрации
     */
    public static void register(Combo combo) {
        if (combo == null || combo.getId() == null) {
            throw new IllegalArgumentException("Combo и его id не могут быть null");
        }
        COMBOS.put(combo.getId(), combo);
    }

    /**
     * Получает комбо по идентификатору.
     * @param id идентификатор комбо
     * @return Combo или null, если не найдено
     */
    public static Combo get(String id) {
        return COMBOS.get(id);
    }

    /**
     * Возвращает неизменяемую карту всех зарегистрированных комбо.
     */
    public static Map<String, Combo> getAll() {
        return Collections.unmodifiableMap(COMBOS);
    }
    static {
        register(new Combo(
                "sword_smash_combo",
                List.of(
                        "yourmodid:sword_slash_1_hit",
                        "yourmodid:sword_slash_2_hit",
                        "yourmodid:sword_smash_ended_hit"
                ),
                "stun", // finisherAbility
                null // requiredAdvancement
        ));
    }
}
