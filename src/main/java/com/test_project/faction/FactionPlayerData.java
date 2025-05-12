package com.test_project.faction;

import net.minecraft.nbt.CompoundTag;

import java.util.HashMap;
import java.util.Map;

public class FactionPlayerData {
    private final Map<String, Integer> reputation = new HashMap<>();

    public int getReputation(String factionId) {
        return reputation.getOrDefault(factionId, 0);
    }

    public void setReputation(String factionId, int value) {
        reputation.put(factionId, value);
    }

    public void addReputation(String factionId, int amount) {
        setReputation(factionId, getReputation(factionId) + amount);
    }

    public Map<String, Integer> getAllReputation() {
        return reputation;
    }

    // Для ручной сериализации (если потребуется)
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
}
