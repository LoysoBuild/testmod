package com.test_project.faction;

import java.util.HashMap;
import java.util.Map;

public class FactionRegistry {
    private static final Map<String, FactionBase> FACTIONS = new HashMap<>();

    public static void register(FactionBase faction) {
        FACTIONS.put(faction.getId(), faction);
    }

    public static FactionBase get(String id) {
        return FACTIONS.get(id);
    }

    public static Iterable<FactionBase> all() {
        return FACTIONS.values();
    }
}
