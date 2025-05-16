package com.test_project.faction.factions_list;

import com.test_project.faction.FactionBase;
import com.test_project.faction.FactionCategory;
import net.minecraft.resources.ResourceLocation;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.HashMap;
import java.util.Map;

public class BanditFaction extends FactionBase {
    // Храним индивидуальную репутацию игроков (для примера)
    private final Map<UUID, Integer> reputation = new HashMap<>();

    @Override
    public String getId() {
        return "bandits";
    }

    @Override
    public String getDisplayName() {
        return "Бандиты";
    }

    @Override
    public String getDescription() {
        return "Группировка разбойников, живущих вне закона и нападающих на путников.";
    }

    @Override
    public FactionCategory getCategory() {
        return FactionCategory.NEUTRAL;
    }

    @Override
    public List<String> getAllies() {
        return Arrays.asList("mordor"); // Например, дружат с Мордором
    }

    @Override
    public List<String> getEnemies() {
        return Arrays.asList("gondor", "rohan", "elfs"); // Враждуют с добрыми фракциями
    }

    @Override
    public List<String> getBaseRewards() {
        return Arrays.asList("bandit_knife", "bandit_mask");
    }

    @Override
    public int getStartReputation() {
        return 0;
    }

    @Override
    public void addReputation(UUID playerId, int amount) {
        reputation.put(playerId, getReputation(playerId) + amount);
    }

    @Override
    public int getReputation(UUID playerId) {
        return reputation.getOrDefault(playerId, getStartReputation());
    }
    @Override
    public ResourceLocation getEmblem() {
        return ResourceLocation.fromNamespaceAndPath("mainmod", "textures/gui/emblems/gondor.png");
    }
}
