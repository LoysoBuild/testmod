package com.test_project.combat.stance;

import net.minecraft.util.ByIdMap;
import java.util.function.IntFunction;

public enum StanceType {
    ATTACK(0),
    DEFENSE(1);

    private final int id;

    StanceType(int id) {
        this.id = id;
    }

    public int getId() {
        return this.id;
    }

    // ИСПРАВЛЕНИЕ: Используем ByIdMap.continuous для создания IntFunction
    public static final IntFunction<StanceType> BY_ID = ByIdMap.continuous(
            StanceType::getId,
            StanceType.values(),
            ByIdMap.OutOfBoundsStrategy.ZERO
    );
}
