package com.test_project.worldrep;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public class WorldReputation {
    private int points = 0;

    public int getPoints() { return points; }
    public void setPoints(int value) { this.points = value; }
    public void addPoints(int amount) { this.points += amount; }
    public boolean trySpendPoints(int amount) {
        if (points >= amount) { points -= amount; return true; }
        return false;
    }

    // Codec для сериализации/десериализации
    public static final Codec<WorldReputation> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.INT.fieldOf("points").forGetter(WorldReputation::getPoints)
    ).apply(instance, points -> {
        WorldReputation rep = new WorldReputation();
        rep.setPoints(points);
        return rep;
    }));
}
