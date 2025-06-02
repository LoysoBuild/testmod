package com.test_project.combat.stance;

public enum StanceType {
    ATTACK,
    DEFENSE;

    public static final java.util.function.IntFunction<StanceType> BY_ID =
            id -> {
                var values = values();
                return id >= 0 && id < values.length ? values[id] : values[0];
            };
}
