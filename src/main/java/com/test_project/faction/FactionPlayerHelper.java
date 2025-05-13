package com.test_project.faction;

import net.minecraft.world.entity.player.Player;

public class FactionPlayerHelper {
    // Начислить репутацию игроку
    public static void addReputation(Player player, String factionId, int amount) {
        FactionPlayerData data = player.getData(FactionAttachments.FACTION_DATA.get());
        if (data != null) {
            data.addReputation(factionId, amount);
        }
    }

    // Получить репутацию игрока
    public static int getReputation(Player player, String factionId) {
        FactionPlayerData data = player.getData(FactionAttachments.FACTION_DATA.get());
        return data != null ? data.getReputation(factionId) : 0;
    }

    // Сбросить все репутации к стартовым значениям
    public static void resetAll(Player player) {
        FactionPlayerData data = player.getData(FactionAttachments.FACTION_DATA.get());
        if (data != null) {
            data.resetAll();
        }
    }
}
