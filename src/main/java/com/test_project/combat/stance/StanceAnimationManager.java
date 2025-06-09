package com.test_project.combat.stance;

import com.test_project.items.weapone.preset.WeaponPresetRegistry;
import com.test_project.items.weapone.weaponeclass.ModBattleAxe;
import com.test_project.items.weapone.weaponeclass.ModSword;
import com.zigythebird.playeranimatorapi.data.PlayerAnimationData;
import com.zigythebird.playeranimatorapi.data.PlayerParts;
import com.zigythebird.playeranimatorapi.playeranims.PlayerAnimations;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import com.mojang.logging.LogUtils;
import org.slf4j.Logger;

import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

/**
 * ИНТЕГРИРОВАННЫЙ менеджер с исправленной логикой переходов
 */
public final class StanceAnimationManager {
    private static final Logger LOGGER = LogUtils.getLogger();

    private static final Map<UUID, ResourceLocation> currentAnimations = new ConcurrentHashMap<>();
    private static final Map<UUID, Boolean> playerMovementState = new ConcurrentHashMap<>();
    private static final Map<UUID, StanceType> pausedStances = new ConcurrentHashMap<>();
    private static final Map<UUID, AnimationType> currentAnimationType = new ConcurrentHashMap<>();
    private static final Map<UUID, CompletableFuture<Void>> pendingTransitions = new ConcurrentHashMap<>();

    public enum AnimationType {
        STANCE_IDLE,
        STANCE_TRANSITION,
        ATTACK,
        BLOCK,
        SPECIAL
    }

    /**
     * ИСПРАВЛЕННЫЙ МЕТОД: Плавный переход между стойками без дергания
     */
    public static void playStanceTransition(Player player, StanceType fromStance, StanceType toStance) {
        LOGGER.info("[CLIENT] ===== PRECISE STANCE TRANSITION =====");
        LOGGER.info("[CLIENT] Transition: {} -> {}", fromStance, toStance);

        if (!(player instanceof AbstractClientPlayer clientPlayer)) {
            LOGGER.error("[CLIENT] ❌ Player is not AbstractClientPlayer!");
            return;
        }

        UUID playerId = player.getUUID();

        // Отменяем предыдущие переходы
        cancelPendingTransition(playerId);

        ItemStack mainHand = player.getMainHandItem();
        ResourceLocation transitionAnim = getWeaponSpecificTransitionAnimation(mainHand, fromStance, toStance);

        if (transitionAnim != null) {
            LOGGER.info("[CLIENT] Playing transition animation: {}", transitionAnim);

            try {
                // КЛЮЧЕВОЕ ИСПРАВЛЕНИЕ: Используем точную синхронизацию
                if (playTransitionWithPreciseSync(clientPlayer, playerId, transitionAnim, toStance, mainHand)) {
                    LOGGER.info("[CLIENT] ✅ Precise transition started successfully");
                } else {
                    // Fallback к прямому переходу
                    playStanceDirectly(player, toStance);
                }

            } catch (Exception e) {
                LOGGER.error("[CLIENT] ❌ Transition failed: {}", e.getMessage(), e);
                playStanceDirectly(player, toStance);
            }
        } else {
            LOGGER.warn("[CLIENT] ⚠️ No transition animation, direct switch");
            playStanceDirectly(player, toStance);
        }

        LOGGER.info("[CLIENT] ===== PRECISE STANCE TRANSITION END =====");
    }

    /**
     * НОВЫЙ МЕТОД: Точная синхронизация transition и idle анимаций
     */
    private static boolean playTransitionWithPreciseSync(AbstractClientPlayer player, UUID playerId,
                                                         ResourceLocation transitionAnim, StanceType targetStance, ItemStack weapon) {
        try {
            // Быстрая остановка текущей анимации
            ResourceLocation currentAnim = currentAnimations.get(playerId);
            if (currentAnim != null) {
                PlayerAnimations.stopAnimation(playerId, currentAnim);
                LOGGER.debug("[CLIENT] Stopped current animation: {}", currentAnim);
            }

            // Запуск transition анимации БЕЗ blend для точности
            PlayerParts parts = new PlayerParts();
            PlayerAnimationData transitionData = new PlayerAnimationData(
                    playerId, transitionAnim, parts, null,
                    0, // priority
                    0, // offset
                    0, // speed
                    0  // БЕЗ blend для точного перехода
            );

            PlayerAnimations.playAnimation(player, transitionData);
            currentAnimations.put(playerId, transitionAnim);
            currentAnimationType.put(playerId, AnimationType.STANCE_TRANSITION);

            LOGGER.info("[CLIENT] ✅ Transition animation started: {}", transitionAnim);

            // Планируем точный переход к idle
            schedulePreciseIdleTransition(player, targetStance, weapon, parts);
            return true;

        } catch (Exception e) {
            LOGGER.error("[CLIENT] ❌ Precise sync failed: {}", e.getMessage());
            return false;
        }
    }

    /**
     * НОВЫЙ МЕТОД: Точное планирование перехода к idle
     */
    private static void schedulePreciseIdleTransition(Player player, StanceType targetStance, ItemStack weapon, PlayerParts parts) {
        UUID playerId = player.getUUID();
        long duration = getTransitionDuration(weapon);

        LOGGER.info("[CLIENT] ⏰ Scheduling precise idle transition to {} in {}ms", targetStance, duration);

        CompletableFuture<Void> transition = CompletableFuture.runAsync(() -> {
            try {
                TimeUnit.MILLISECONDS.sleep(duration);

                AnimationType currentType = currentAnimationType.get(playerId);
                if (currentType == AnimationType.STANCE_TRANSITION) {
                    LOGGER.info("[CLIENT] 🎯 Executing precise idle transition: {}", targetStance);

                    // КЛЮЧЕВОЕ ИСПРАВЛЕНИЕ: НЕ останавливаем transition, сразу запускаем idle
                    ResourceLocation idleAnim = WeaponPresetRegistry.getAnimationForWeapon(weapon, targetStance);
                    if (idleAnim != null && player instanceof AbstractClientPlayer clientPlayer) {

                        PlayerAnimationData idleData = new PlayerAnimationData(
                                playerId, idleAnim, parts, null,
                                0, // priority
                                0, // offset
                                0, // speed
                                0  // БЕЗ blend для точного перехода
                        );

                        PlayerAnimations.playAnimation(clientPlayer, idleData);
                        currentAnimations.put(playerId, idleAnim);
                        currentAnimationType.put(playerId, AnimationType.STANCE_IDLE);

                        LOGGER.info("[CLIENT] ✅ Precise idle animation started: {}", idleAnim);
                    }
                }

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                LOGGER.debug("[CLIENT] Precise transition interrupted");
            } catch (Exception e) {
                LOGGER.error("[CLIENT] ❌ Precise idle transition error: {}", e.getMessage());
            } finally {
                pendingTransitions.remove(playerId);
            }
        });

        pendingTransitions.put(playerId, transition);
    }

    /**
     * ИСПРАВЛЕННЫЙ МЕТОД: Прямое проигрывание стойки
     */
    private static void playStanceDirectly(Player player, StanceType stance) {
        if (!(player instanceof AbstractClientPlayer clientPlayer)) return;

        UUID playerId = player.getUUID();
        ItemStack mainHand = player.getMainHandItem();
        ResourceLocation animId = WeaponPresetRegistry.getAnimationForWeapon(mainHand, stance);

        if (animId != null) {
            // Быстрая остановка и запуск
            ResourceLocation currentAnim = currentAnimations.get(playerId);
            if (currentAnim != null) {
                PlayerAnimations.stopAnimation(playerId, currentAnim);
            }

            PlayerParts parts = new PlayerParts();
            PlayerAnimationData data = new PlayerAnimationData(
                    playerId, animId, parts, null, 0, 0, 0, 0 // БЕЗ blend
            );

            PlayerAnimations.playAnimation(clientPlayer, data);
            currentAnimations.put(playerId, animId);
            currentAnimationType.put(playerId, AnimationType.STANCE_IDLE);

            LOGGER.info("[CLIENT] ✅ Direct stance animation: {}", animId);
        }
    }

    /**
     * Получение transition анимации по типу оружия
     */
    private static ResourceLocation getWeaponSpecificTransitionAnimation(ItemStack weapon, StanceType fromStance, StanceType toStance) {
        if (weapon.isEmpty()) {
            return switch (toStance) {
                case ATTACK -> ResourceLocation.fromNamespaceAndPath("mainmod", "unarmed_switch_to_attack");
                case DEFENSE -> ResourceLocation.fromNamespaceAndPath("mainmod", "unarmed_switch_to_defense");
            };
        }

        if (weapon.getItem() instanceof ModSword) {
            return switch (toStance) {
                case ATTACK -> ResourceLocation.fromNamespaceAndPath("mainmod", "sword_switch_to_attack");
                case DEFENSE -> ResourceLocation.fromNamespaceAndPath("mainmod", "sword_switch_to_defense");
            };
        } else if (weapon.getItem() instanceof ModBattleAxe) {
            return switch (toStance) {
                case ATTACK -> ResourceLocation.fromNamespaceAndPath("mainmod", "axe_switch_to_attack");
                case DEFENSE -> ResourceLocation.fromNamespaceAndPath("mainmod", "axe_switch_to_defense");
            };
        }

        return switch (toStance) {
            case ATTACK -> ResourceLocation.fromNamespaceAndPath("mainmod", "switch_to_attack");
            case DEFENSE -> ResourceLocation.fromNamespaceAndPath("mainmod", "switch_to_defense");
        };
    }

    /**
     * Длительность transition анимации
     */
    private static long getTransitionDuration(ItemStack weapon) {
        if (weapon.getItem() instanceof ModSword) {
            return 800L;
        } else if (weapon.getItem() instanceof ModBattleAxe) {
            return 708L; // Точно соответствует вашим JSON файлам
        }
        return 600L;
    }

    private static void cancelPendingTransition(UUID playerId) {
        CompletableFuture<Void> pending = pendingTransitions.remove(playerId);
        if (pending != null && !pending.isDone()) {
            pending.cancel(true);
        }
    }

    // Остальные методы без изменений
    public static void playStance(Player player, StanceType stance) {
        playStanceDirectly(player, stance);
    }

    public static void playAttackAnimation(Player player, ResourceLocation attackAnimId) {
        if (!(player instanceof AbstractClientPlayer clientPlayer)) return;

        UUID playerId = player.getUUID();
        cancelPendingTransition(playerId);

        try {
            // Быстрая остановка текущей анимации
            ResourceLocation currentAnim = currentAnimations.get(playerId);
            if (currentAnim != null) {
                PlayerAnimations.stopAnimation(playerId, currentAnim);
            }

            PlayerParts parts = new PlayerParts();
            PlayerAnimationData data = new PlayerAnimationData(
                    playerId, attackAnimId, parts, null, 0, 0, 0, 0
            );

            PlayerAnimations.playAnimation(clientPlayer, data);
            currentAnimations.put(playerId, attackAnimId);
            currentAnimationType.put(playerId, AnimationType.ATTACK);

            LOGGER.info("[CLIENT] ✅ Attack animation started: {}", attackAnimId);

        } catch (Exception e) {
            LOGGER.error("[CLIENT] ❌ Attack animation failed: {}", e.getMessage());
        }
    }

    private static boolean isPlayerMoving(Player player) {
        double deltaX = player.getDeltaMovement().x;
        double deltaZ = player.getDeltaMovement().z;
        double horizontalSpeed = deltaX * deltaX + deltaZ * deltaZ;

        return horizontalSpeed > 0.0001 || !player.onGround();
    }

    public static void updateStanceAnimation(Player player, StanceType currentStance) {
        UUID playerId = player.getUUID();
        AnimationType currentType = currentAnimationType.get(playerId);

        if (currentType == null || currentType == AnimationType.STANCE_IDLE) {
            if (isPlayerMoving(player)) {
                pausedStances.put(playerId, currentStance);
                playerMovementState.put(playerId, true);
                stopAnimation(player);
            } else if (playerMovementState.get(playerId) != null && playerMovementState.get(playerId)) {
                playerMovementState.put(playerId, false);
                StanceType pausedStance = pausedStances.get(playerId);
                if (pausedStance != null) {
                    playStanceDirectly(player, pausedStance);
                }
            }
        }
    }

    // Методы проверки состояния
    public static boolean isPlayingStanceIdle(Player player) {
        return currentAnimationType.get(player.getUUID()) == AnimationType.STANCE_IDLE;
    }

    public static boolean isPlayingTransition(Player player) {
        return currentAnimationType.get(player.getUUID()) == AnimationType.STANCE_TRANSITION;
    }

    public static boolean isPlayingAttack(Player player) {
        return currentAnimationType.get(player.getUUID()) == AnimationType.ATTACK;
    }

    public static boolean hasActiveAnimation(Player player) {
        return currentAnimations.containsKey(player.getUUID());
    }

    public static ResourceLocation getCurrentAnimation(Player player) {
        return currentAnimations.get(player.getUUID());
    }

    public static AnimationType getCurrentAnimationType(Player player) {
        return currentAnimationType.get(player.getUUID());
    }

    public static void stopAnimation(Player player) {
        if (!(player instanceof AbstractClientPlayer)) return;

        UUID playerId = player.getUUID();
        ResourceLocation currentAnim = currentAnimations.remove(playerId);
        currentAnimationType.remove(playerId);

        if (currentAnim != null) {
            try {
                PlayerAnimations.stopAnimation(playerId, currentAnim);
                LOGGER.debug("[CLIENT] Stopped animation: {}", currentAnim);
            } catch (Exception e) {
                LOGGER.error("[CLIENT] Failed to stop animation: {}", e.getMessage());
            }
        }
    }

    public static void clearPlayerData(Player player) {
        UUID playerId = player.getUUID();
        stopAnimation(player);
        currentAnimations.remove(playerId);
        playerMovementState.remove(playerId);
        pausedStances.remove(playerId);
        currentAnimationType.remove(playerId);
        cancelPendingTransition(playerId);
    }

    public static void clearAllAnimations() {
        pendingTransitions.values().forEach(future -> {
            if (!future.isDone()) {
                future.cancel(true);
            }
        });

        currentAnimations.forEach((playerId, animId) -> {
            try {
                PlayerAnimations.stopAnimation(playerId, animId);
            } catch (Exception e) {
                LOGGER.warn("[CLIENT] Failed to stop animation {}: {}", animId, e.getMessage());
            }
        });

        currentAnimations.clear();
        playerMovementState.clear();
        pausedStances.clear();
        currentAnimationType.clear();
        pendingTransitions.clear();
    }
}
