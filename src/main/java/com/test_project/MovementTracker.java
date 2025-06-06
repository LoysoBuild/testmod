package com.test_project;

import com.test_project.combat.CombatEventHandler;
import com.test_project.combat.PlayerCombatSettings;
import com.test_project.combat.stance.StanceAnimationManager;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import com.mojang.logging.LogUtils;
import org.slf4j.Logger;

@EventBusSubscriber(modid = "mainmod", bus = EventBusSubscriber.Bus.GAME, value = Dist.CLIENT)
public final class MovementTracker {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static int tickCounter = 0;

    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post event) {
        Player player = event.getEntity();

        // Проверяем только для локального игрока на клиенте
        if (player != Minecraft.getInstance().player) return;
        if (player.level().isClientSide()) {

            // Проверяем каждые 5 тиков для оптимизации
            tickCounter++;
            if (tickCounter >= 5) {
                tickCounter = 0;

                try {
                    PlayerCombatSettings settings = CombatEventHandler.getSettings(player);
                    if (settings != null) {
                        // ОБНОВЛЕНО: Обновляем только idle-анимации стоек
                        // Анимации атак не трогаем
                        if (StanceAnimationManager.isPlayingStanceIdle(player) ||
                                !StanceAnimationManager.hasActiveAnimation(player)) {
                            StanceAnimationManager.updateStanceAnimation(player, settings.getCurrentStance());
                        }
                    }
                } catch (Exception e) {
                    LOGGER.error("[CLIENT] Error in movement tracker: {}", e.getMessage());
                }
            }
        }
    }
}
