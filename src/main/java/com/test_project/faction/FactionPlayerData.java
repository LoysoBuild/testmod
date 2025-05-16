package com.test_project.faction;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import java.util.HashMap;
import java.util.Map;

public class FactionPlayerData {
    private final Map<String, Integer> reputation = new HashMap<>();

    public FactionPlayerData() {}

    /**
     * Получить репутацию по фракции. Если фракция не задана - вернуть 0.
     */
    public int getReputation(String factionId) {
        return reputation.getOrDefault(factionId, 0);
    }

    /**
     * Установить репутацию по фракции.
     */
    public void setReputation(String factionId, int value) {
        reputation.put(factionId, value);
    }

    /**
     * Изменить репутацию по фракции на заданное число.
     */
    public void addReputation(String factionId, int amount) {
        setReputation(factionId, getReputation(factionId) + amount);
    }

    /**
     * Получить карту всей репутации (фракция -> значение).
     */
    public Map<String, Integer> getAllReputation() {
        return reputation;
    }

    /**
     * Сбросить все значения репутации.
     */
    public void resetAll() {
        reputation.clear();
    }

    /**
     * Codec для сериализации/десериализации через Attachments API.
     */
    public static final Codec<FactionPlayerData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.unboundedMap(Codec.STRING, Codec.INT)
                    .fieldOf("reputation")
                    .forGetter(FactionPlayerData::getAllReputation)
    ).apply(instance, map -> {
        FactionPlayerData data = new FactionPlayerData();
        data.reputation.putAll(map);
        return data;
    }));
}
