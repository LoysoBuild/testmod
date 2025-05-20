package com.test_project.faction;

import net.minecraft.world.entity.player.Player;

/**
 * Глобальная система для работы с репутацией игроков во фракциях.
 */
public class ReputationSystem {

    /**
     * Начислить или снять репутацию игроку в указанной фракции.
     */
    public static void addReputation(Player player, String factionId, int amount) {
        if (player == null) return;
        FactionPlayerData data = player.getData(FactionAttachments.FACTION_DATA.get());
        if (data == null) return;
        if (FactionRegistry.get(factionId) == null) return; // Проверка валидности фракции

        data.addReputation(factionId, amount);

        // Можно добавить уведомление игроку:
        player.sendSystemMessage(
                net.minecraft.network.chat.Component.literal(
                        "Ваша репутация у фракции " + factionId + " изменилась на " + amount +
                                ". Теперь: " + data.getReputation(factionId)
                )
        );
    }

    /**
     * Получить текущую репутацию игрока у фракции.
     */
    public static int getReputation(Player player, String factionId) {
        if (player == null) return 0;
        FactionPlayerData data = player.getData(FactionAttachments.FACTION_DATA.get());
        if (data == null) return 0;
        return data.getReputation(factionId);
    }
}
