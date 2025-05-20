package com.test_project.faction;

import net.minecraft.resources.ResourceLocation;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * Абстрактный класс фракции с динамическими союзниками и врагами.
 */
public abstract class FactionBase {
    public abstract String getId();
    public abstract String getDisplayName();
    public abstract String getDescription();
    public abstract List<String> getBaseRewards();
    public abstract int getStartReputation();
    public abstract FactionCategory getCategory();
    public abstract ResourceLocation getEmblem();

    // Динамические союзники и враги через FactionManager
    public List<String> getAllies() {
        return StreamSupport.stream(FactionRegistry.all().spliterator(), false)
                .map(FactionBase::getId)
                .filter(otherId -> otherId != null && !otherId.equals(getId()))
                .filter(otherId -> FactionManager.getRelation(getId(), otherId) == FactionRelation.ALLY)
                .collect(Collectors.toList());
    }

    public List<String> getEnemies() {
        return StreamSupport.stream(FactionRegistry.all().spliterator(), false)
                .map(FactionBase::getId)
                .filter(otherId -> otherId != null && !otherId.equals(getId()))
                .filter(otherId -> FactionManager.getRelation(getId(), otherId) == FactionRelation.ENEMY)
                .collect(Collectors.toList());
    }



    // Остальные методы как раньше
    public void onEvent(UUID playerId, String event, Object data) {}
    public abstract void addReputation(UUID playerId, int amount);
    public abstract int getReputation(UUID playerId);

    public boolean canAccessItem(UUID playerId, String itemId) { return true; }
    public boolean canAccessQuest(UUID playerId, String questId) { return true; }
}
