package com.test_project.combat.stance;

import com.test_project.items.weapone.preset.WeaponPresetRegistry;
import com.zigythebird.playeranimatorapi.data.PlayerAnimationData;
import com.zigythebird.playeranimatorapi.data.PlayerParts;
import com.zigythebird.playeranimatorapi.playeranims.PlayerAnimations;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import com.mojang.logging.LogUtils;
import org.slf4j.Logger;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public final class StanceAnimationManager {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final Map<UUID, ResourceLocation> currentAnimations = new HashMap<>();
    private static final Map<UUID, CompletableFuture<Void>> pendingAnimations = new HashMap<>();

    public static void playStance(Player player, StanceType stance) {
        if (!(player instanceof AbstractClientPlayer clientPlayer)) {
            LOGGER.warn("[CLIENT] Cannot play stance animation - player is not AbstractClientPlayer");
            return;
        }

        // ИСПРАВЛЕНО: Отменяем любые ожидающие анимации
        UUID playerId = player.getUUID();
        CompletableFuture<Void> pending = pendingAnimations.get(playerId);
        if (pending != null && !pending.isDone()) {
            pending.cancel(true);
            LOGGER.debug("[CLIENT] Cancelled pending animation for player: {}", player.getName().getString());
        }

        ItemStack mainHand = player.getMainHandItem();
        ResourceLocation animId = getAnimationForWeapon(mainHand, stance);

        if (animId != null) {
            // ИСПРАВЛЕНО: Асинхронная обработка с гарантированным порядком
            CompletableFuture<Void> animationTask = CompletableFuture.runAsync(() -> {
                try {
                    // Останавливаем текущую анимацию
                    ResourceLocation currentAnim = currentAnimations.get(playerId);
                    if (currentAnim != null) {
                        PlayerAnimations.stopAnimation(playerId, currentAnim);
                        LOGGER.debug("[CLIENT] Stopped animation: {} for stance change to: {}", currentAnim, stance);

                        // Небольшая задержка для корректной остановки
                        Thread.sleep(100);
                    }

                    // Запускаем новую анимацию
                    PlayerParts parts = new PlayerParts();
                    PlayerAnimationData data = new PlayerAnimationData(
                            playerId, animId, parts, null, 0, 0, 0, 0
                    );

                    PlayerAnimations.playAnimation(clientPlayer, data);
                    currentAnimations.put(playerId, animId);

                    LOGGER.info("[CLIENT] Successfully played animation: {} for stance: {} with weapon: {}",
                            animId, stance, getWeaponInfo(mainHand));

                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    LOGGER.warn("[CLIENT] Animation task interrupted for player: {}", player.getName().getString());
                } catch (Exception e) {
                    LOGGER.error("[CLIENT] Failed to play stance animation: {}", e.getMessage(), e);
                }
            });

            pendingAnimations.put(playerId, animationTask);

        } else {
            LOGGER.warn("[CLIENT] No animation found for stance: {} with weapon: {}", stance, getWeaponInfo(mainHand));
            // Останавливаем текущую анимацию если нет новой
            stopCurrentAnimation(clientPlayer);
        }
    }

    private static ResourceLocation getAnimationForWeapon(ItemStack stack, StanceType stance) {
        return WeaponPresetRegistry.getAnimationForWeapon(stack, stance);
    }

    private static String getWeaponInfo(ItemStack stack) {
        if (stack.isEmpty()) return "empty_hand";
        return stack.getItem().getClass().getSimpleName();
    }

    public static void stopAnimation(Player player) {
        if (player instanceof AbstractClientPlayer clientPlayer) {
            stopCurrentAnimation(clientPlayer);
            LOGGER.info("[CLIENT] Stopped stance animation for player: {}", player.getName().getString());
        }
    }

    public static void stopCurrentAnimation(AbstractClientPlayer player) {
        UUID playerId = player.getUUID();

        // Отменяем ожидающие анимации
        CompletableFuture<Void> pending = pendingAnimations.get(playerId);
        if (pending != null && !pending.isDone()) {
            pending.cancel(true);
            pendingAnimations.remove(playerId);
        }

        ResourceLocation currentAnim = currentAnimations.get(playerId);
        if (currentAnim != null) {
            try {
                PlayerAnimations.stopAnimation(playerId, currentAnim);
                LOGGER.debug("[CLIENT] Stopped animation: {} for player: {}",
                        currentAnim, player.getName().getString());
            } catch (Exception e) {
                LOGGER.error("[CLIENT] Failed to stop animation for player {}: {}",
                        player.getName().getString(), e.getMessage());
            }
            currentAnimations.remove(playerId);
        }
    }

    public static void clearPlayerData(Player player) {
        UUID playerId = player.getUUID();
        currentAnimations.remove(playerId);

        // Очищаем ожидающие анимации
        CompletableFuture<Void> pending = pendingAnimations.remove(playerId);
        if (pending != null && !pending.isDone()) {
            pending.cancel(true);
        }

        LOGGER.debug("[CLIENT] Cleared animation data for player: {}", player.getName().getString());
    }

    public static boolean hasActiveAnimation(Player player) {
        return currentAnimations.containsKey(player.getUUID());
    }

    public static ResourceLocation getCurrentAnimation(Player player) {
        return currentAnimations.get(player.getUUID());
    }

    public static int getActiveAnimationCount() {
        return currentAnimations.size();
    }

    public static void clearAllAnimations() {
        // Отменяем все ожидающие анимации
        for (CompletableFuture<Void> pending : pendingAnimations.values()) {
            if (!pending.isDone()) {
                pending.cancel(true);
            }
        }
        pendingAnimations.clear();

        // Останавливаем все текущие анимации
        for (Map.Entry<UUID, ResourceLocation> entry : currentAnimations.entrySet()) {
            try {
                PlayerAnimations.stopAnimation(entry.getKey(), entry.getValue());
            } catch (Exception e) {
                LOGGER.warn("[CLIENT] Failed to stop animation {} for UUID {}: {}",
                        entry.getValue(), entry.getKey(), e.getMessage());
            }
        }
        currentAnimations.clear();
        LOGGER.info("[CLIENT] Cleared all stance animations");
    }
}
