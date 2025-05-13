package com.test_project.faction;

import net.minecraft.nbt.CompoundTag;
import java.util.HashMap;
import java.util.Map;

public class FactionPlayerData {
    private final Map<String, Integer> reputation = new HashMap<>();

    public int getReputation(String factionId) {
        // Если фракция не существует - возвращаем 0
        FactionBase faction = FactionRegistry.get(factionId);
        if (faction == null) return 0;

        // Если репутация не установлена - инициализируем стартовой репутацией
        if (!reputation.containsKey(factionId)) {
            reputation.put(factionId, faction.getStartReputation());
        }
        return reputation.get(factionId);
    }

    public void setReputation(String factionId, int value) {
        if (FactionRegistry.get(factionId) != null) {
            reputation.put(factionId, value);
        }
    }

    public void addReputation(String factionId, int amount) {
        setReputation(factionId, getReputation(factionId) + amount);
    }

    public Map<String, Integer> getAllReputation() {
        return reputation;
    }

    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        reputation.forEach(tag::putInt);
        return tag;
    }

    public void deserializeNBT(CompoundTag nbt) {
        reputation.clear();
        for (String key : nbt.getAllKeys()) {
            reputation.put(key, nbt.getInt(key));
        }
    }

    // Удобный метод для сброса всех репутаций к стартовым значениям
    public void resetAll() {
        reputation.clear();
        for (FactionBase faction : FactionRegistry.all()) {
            reputation.put(faction.getId(), faction.getStartReputation());
        }
    }
}
