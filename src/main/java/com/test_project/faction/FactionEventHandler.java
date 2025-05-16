package com.test_project.faction;

import com.test_project.entity.TestMobEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.bus.api.SubscribeEvent;

/**
 * Обработчик игровых событий для фракционной системы.
 */
@EventBusSubscriber(modid = "mainmod")
public class FactionEventHandler {

    /**
     * Начислять репутацию за убийство моба определённой фракции или кастомного моба.
     */
    @SubscribeEvent
    public static void onMobKilled(LivingDeathEvent event) {
        if (!(event.getSource().getEntity() instanceof Player player)) return;
        LivingEntity killed = event.getEntity();

        // Если убит именно наш кастомный моб
        if (killed instanceof TestMobEntity) {
            ReputationSystem.addReputation(player, "gondor", 10);
            System.out.println("[Factions] Игрок " + player.getName().getString()
                    + " убил TestMobEntity. Репутация с Гондором: "
                    + ReputationSystem.getReputation(player, "gondor"));
            return; // чтобы не начислять дважды, если кастомный моб тоже "mordor"
        }

        // Пример: если убит моб, принадлежащий фракции "mordor", увеличиваем репутацию у "gondor"
        String mobFaction = getMobFaction(killed); // Реализуй свою логику определения фракции моба

        if ("mordor".equals(mobFaction)) {
            ReputationSystem.addReputation(player, "gondor", 10);
            System.out.println("[Factions] Игрок " + player.getName().getString()
                    + " убил моба фракции Mordor. Репутация с Гондором: "
                    + ReputationSystem.getReputation(player, "gondor"));
        }
        // Можно добавить другие условия для других фракций
    }

    /**
     * Определение фракции моба (заглушка - реализуй свою систему!)
     */
    private static String getMobFaction(LivingEntity mob) {
        // Пример: по типу моба
        String type = mob.getType().toString().toLowerCase();
        if (type.contains("orc") || type.contains("mordor")) return "mordor";
        if (type.contains("gondor")) return "gondor";
        // ... другие проверки
        return null;
    }
}
