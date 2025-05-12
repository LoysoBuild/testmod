package com.test_project.factions;

import com.test_project.faction.FactionBase;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.HashMap;
import java.util.Map;

public class GondorFaction extends FactionBase {
    private final Map<UUID, Integer> reputation = new HashMap<>();

    @Override
    public String getId() { return "gondor"; }

    @Override
    public String getDisplayName() { return "Гондор"; }

    @Override
    public String getDescription() { return "Великая фракция людей юга."; }

    @Override
    public List<String> getAllies() { return Arrays.asList("rohan"); }

    @Override
    public List<String> getEnemies() { return Arrays.asList("gundabad", "mordor"); }

    @Override
    public List<String> getBaseRewards() { return Arrays.asList("gondor_sword", "gondor_shield"); }

    @Override
    public int getStartReputation() { return 0; }

    @Override
    public void addReputation(UUID playerId, int amount) {
        reputation.put(playerId, getReputation(playerId) + amount);
    }

    @Override
    public int getReputation(UUID playerId) {
        return reputation.getOrDefault(playerId, getStartReputation());
    }
}
