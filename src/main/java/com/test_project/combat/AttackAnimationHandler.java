package com.test_project.combat;

import com.test_project.combat.stance.StanceAnimationManager;
import com.test_project.items.weapone.weaponeclass.ModBattleAxe;
import com.test_project.items.weapone.weaponeclass.ModSword;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import com.mojang.logging.LogUtils;
import org.slf4j.Logger;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public final class AttackAnimationHandler {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    /**
     * Проигрывает анимацию атаки (работает даже во время движения)
     */
    public static void playAttackAnimation(Player player, ItemStack weapon) {
        ResourceLocation attackAnim = getAttackAnimationForWeapon(weapon);

        if (attackAnim != null) {
            // Анимация атаки проигрывается ВСЕГДА, даже во время бега
            StanceAnimationManager.playAttackAnimation(player, attackAnim);
            LOGGER.info("[COMBAT] Playing attack animation: {} for weapon: {}",
                    attackAnim, weapon.getItem().getClass().getSimpleName());

            // Через 1 секунду возвращаемся к idle-анимации стойки
            scheduleReturnToStance(player, 1000);
        } else {
            LOGGER.warn("[COMBAT] No attack animation found for weapon: {}", weapon.getItem());
        }
    }

    /**
     * Определяет анимацию атаки по типу оружия
     */
    private static ResourceLocation getAttackAnimationForWeapon(ItemStack weapon) {
        if (weapon.getItem() instanceof ModSword) {
            return ResourceLocation.fromNamespaceAndPath("mainmod", "swordattack");
        } else if (weapon.getItem() instanceof ModBattleAxe) {
            return ResourceLocation.fromNamespaceAndPath("mainmod", "axeattack");
        }
        // Можно добавить другие типы оружия
        return null;
    }

    /**
     * Планирует возврат к idle-анимации стойки через заданное время
     */
    private static void scheduleReturnToStance(Player player, long delayMs) {
        scheduler.schedule(() -> {
            try {
                // Возвращаемся к idle-анимации стойки только если не играется другая атака
                if (StanceAnimationManager.isPlayingAttack(player)) {
                    com.test_project.combat.PlayerCombatSettings settings =
                            com.test_project.combat.CombatEventHandler.getSettings(player);

                    if (settings != null) {
                        StanceAnimationManager.playStance(player, settings.getCurrentStance());
                        LOGGER.debug("[COMBAT] Returned to stance idle animation for player: {}",
                                player.getName().getString());
                    }
                }
            } catch (Exception e) {
                LOGGER.error("[COMBAT] Error returning to stance animation: {}", e.getMessage());
            }
        }, delayMs, TimeUnit.MILLISECONDS);
    }

    /**
     * Останавливает все запланированные задачи (вызывать при выходе из игры)
     */
    public static void shutdown() {
        scheduler.shutdown();
    }
}
