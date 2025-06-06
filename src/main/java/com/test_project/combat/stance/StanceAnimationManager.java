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

public final class StanceAnimationManager {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final Map<UUID, ResourceLocation> currentAnimations = new HashMap<>();
    private static final Map<UUID, Boolean> playerMovementState = new HashMap<>();
    private static final Map<UUID, StanceType> pausedStances = new HashMap<>();
    private static final Map<UUID, AnimationType> currentAnimationType = new HashMap<>();

    // НОВЫЙ ENUM: Типы анимаций
    public enum AnimationType {
        STANCE_IDLE,    // Анимации стоек (должны останавливаться при движении)
        ATTACK,         // Анимации атак (должны проигрываться всегда)
        BLOCK,          // Анимации блока (должны проигрываться всегда)
        SPECIAL         // Специальные анимации (должны проигрываться всегда)
    }

    /**
     * ОБНОВЛЕННЫЙ МЕТОД: Проигрывание анимации стойки (idle)
     */
    public static void playStance(Player player, StanceType stance) {
        playAnimation(player, stance, AnimationType.STANCE_IDLE);
    }

    /**
     * НОВЫЙ МЕТОД: Проигрывание анимации атаки
     */
    public static void playAttackAnimation(Player player, ResourceLocation attackAnimId) {
        if (!(player instanceof AbstractClientPlayer clientPlayer)) {
            LOGGER.warn("[CLIENT] Cannot play attack animation - player is not AbstractClientPlayer");
            return;
        }

        UUID playerId = player.getUUID();

        // Анимации атак ВСЕГДА проигрываются, независимо от движения
        LOGGER.info("[CLIENT] Playing attack animation: {} (ignoring movement state)", attackAnimId);

        try {
            // Останавливаем текущую анимацию
            stopCurrentAnimation(clientPlayer);

            PlayerParts parts = new PlayerParts();
            PlayerAnimationData data = new PlayerAnimationData(
                    playerId, attackAnimId, parts, null, 0, 0, 0, 0
            );

            PlayerAnimations.playAnimation(clientPlayer, data);
            currentAnimations.put(playerId, attackAnimId);
            currentAnimationType.put(playerId, AnimationType.ATTACK);

            LOGGER.info("[CLIENT] Successfully played attack animation: {}", attackAnimId);
        } catch (Exception e) {
            LOGGER.error("[CLIENT] Failed to play attack animation: {}", e.getMessage(), e);
        }
    }

    /**
     * УНИВЕРСАЛЬНЫЙ МЕТОД: Проигрывание анимации с типом
     */
    private static void playAnimation(Player player, StanceType stance, AnimationType animType) {
        if (!(player instanceof AbstractClientPlayer clientPlayer)) {
            LOGGER.warn("[CLIENT] Cannot play animation - player is not AbstractClientPlayer");
            return;
        }

        UUID playerId = player.getUUID();

        // КЛЮЧЕВАЯ ЛОГИКА: Проверяем движение только для STANCE_IDLE анимаций
        if (animType == AnimationType.STANCE_IDLE && isPlayerMoving(player)) {
            LOGGER.debug("[CLIENT] Player is moving, pausing stance idle animation: {}", stance);
            pausedStances.put(playerId, stance);
            pauseStanceAnimation(player);
            return;
        }

        // Убираем из паузы, если была
        if (animType == AnimationType.STANCE_IDLE) {
            pausedStances.remove(playerId);
        }

        ItemStack mainHand = player.getMainHandItem();
        ResourceLocation animId = getAnimationForWeapon(mainHand, stance);

        if (animId != null) {
            ResourceLocation currentAnim = currentAnimations.get(playerId);

            // Для атак всегда перезапускаем, для стоек проверяем дублирование
            if (animType == AnimationType.STANCE_IDLE && animId.equals(currentAnim)) {
                LOGGER.debug("[CLIENT] Stance animation {} already playing", animId);
                return;
            }

            if (currentAnim != null) {
                stopCurrentAnimation(clientPlayer);
            }

            try {
                PlayerParts parts = new PlayerParts();
                PlayerAnimationData data = new PlayerAnimationData(
                        playerId, animId, parts, null, 0, 0, 0, 0
                );

                PlayerAnimations.playAnimation(clientPlayer, data);
                currentAnimations.put(playerId, animId);
                currentAnimationType.put(playerId, animType);

                LOGGER.info("[CLIENT] Playing {} animation: {} for stance: {}",
                        animType, animId, stance);
            } catch (Exception e) {
                LOGGER.error("[CLIENT] Failed to play {} animation: {}", animType, e.getMessage(), e);
            }
        } else {
            LOGGER.warn("[CLIENT] No animation found for stance: {} type: {}", stance, animType);
        }
    }

    /**
     * ОБНОВЛЕННЫЙ МЕТОД: Проверка движения игрока
     */
    private static boolean isPlayerMoving(Player player) {
        double horizontalSpeed = Math.sqrt(
                player.getDeltaMovement().x * player.getDeltaMovement().x +
                        player.getDeltaMovement().z * player.getDeltaMovement().z
        );

        boolean isWalking = horizontalSpeed > 0.01;
        boolean isJumping = !player.onGround() && player.getDeltaMovement().y != 0;
        boolean isSneaking = player.isCrouching();

        boolean isMoving = isWalking || isJumping || (isSneaking && horizontalSpeed > 0.005);

        if (isMoving) {
            LOGGER.debug("[CLIENT] Player movement detected - speed: {}, walking: {}, jumping: {}, sneaking: {}",
                    horizontalSpeed, isWalking, isJumping, isSneaking);
        }

        return isMoving;
    }

    /**
     * ОБНОВЛЕННЫЙ МЕТОД: Приостановка только idle-анимаций стоек
     */
    private static void pauseStanceAnimation(Player player) {
        UUID playerId = player.getUUID();
        AnimationType currentType = currentAnimationType.get(playerId);

        // Приостанавливаем только idle-анимации стоек
        if (currentType == AnimationType.STANCE_IDLE) {
            Boolean wasMoving = playerMovementState.get(playerId);

            if (wasMoving == null || !wasMoving) {
                if (player instanceof AbstractClientPlayer clientPlayer) {
                    stopCurrentAnimation(clientPlayer);
                    LOGGER.debug("[CLIENT] Paused stance idle animation due to movement");
                }
                playerMovementState.put(playerId, true);
            }
        } else {
            LOGGER.debug("[CLIENT] Not pausing {} animation during movement", currentType);
        }
    }

    /**
     * ОБНОВЛЕННЫЙ МЕТОД: Возобновление только idle-анимаций
     */
    public static void resumeStanceAnimationIfNeeded(Player player) {
        UUID playerId = player.getUUID();
        Boolean wasMoving = playerMovementState.get(playerId);
        StanceType pausedStance = pausedStances.get(playerId);

        if (wasMoving != null && wasMoving && !isPlayerMoving(player) && pausedStance != null) {
            // Возобновляем только idle-анимации стоек
            playerMovementState.put(playerId, false);
            LOGGER.debug("[CLIENT] Resuming stance idle animation after movement stopped: {}", pausedStance);
            playStance(player, pausedStance);
        } else if (!isPlayerMoving(player)) {
            playerMovementState.put(playerId, false);
        }
    }

    /**
     * ОБНОВЛЕННЫЙ МЕТОД: Обновление только idle-анимаций
     */
    public static void updateStanceAnimation(Player player, StanceType currentStance) {
        UUID playerId = player.getUUID();
        AnimationType currentType = currentAnimationType.get(playerId);

        // Обновляем только idle-анимации стоек
        if (currentType == null || currentType == AnimationType.STANCE_IDLE) {
            if (isPlayerMoving(player)) {
                pausedStances.put(playerId, currentStance);
                pauseStanceAnimation(player);
            } else {
                resumeStanceAnimationIfNeeded(player);
            }
        }
        // Анимации атак и другие типы не трогаем
    }

    /**
     * НОВЫЙ МЕТОД: Проверка типа текущей анимации
     */
    public static boolean isPlayingStanceIdle(Player player) {
        AnimationType type = currentAnimationType.get(player.getUUID());
        return type == AnimationType.STANCE_IDLE;
    }

    /**
     * НОВЫЙ МЕТОД: Проверка проигрывания атаки
     */
    public static boolean isPlayingAttack(Player player) {
        AnimationType type = currentAnimationType.get(player.getUUID());
        return type == AnimationType.ATTACK;
    }

    private static ResourceLocation getAnimationForWeapon(ItemStack stack, StanceType stance) {
        return WeaponPresetRegistry.getAnimationForWeapon(stack, stance);
    }

    public static void stopAnimation(Player player) {
        if (player instanceof AbstractClientPlayer clientPlayer) {
            stopCurrentAnimation(clientPlayer);
            LOGGER.info("[CLIENT] Stopped animation for player: {}", player.getName().getString());
        }
    }

    public static void stopCurrentAnimation(AbstractClientPlayer player) {
        UUID playerId = player.getUUID();
        ResourceLocation currentAnim = currentAnimations.get(playerId);

        if (currentAnim != null) {
            try {
                PlayerAnimations.stopAnimation(playerId, currentAnim);
                LOGGER.debug("[CLIENT] Stopped animation: {}", currentAnim);
            } catch (Exception e) {
                LOGGER.error("[CLIENT] Failed to stop animation: {}", e.getMessage());
            }
            currentAnimations.remove(playerId);
            currentAnimationType.remove(playerId);
        }
    }

    public static void clearPlayerData(Player player) {
        UUID playerId = player.getUUID();
        currentAnimations.remove(playerId);
        playerMovementState.remove(playerId);
        pausedStances.remove(playerId);
        currentAnimationType.remove(playerId);
        LOGGER.debug("[CLIENT] Cleared animation data for player: {}", player.getName().getString());
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

    public static int getActiveAnimationCount() {
        return currentAnimations.size();
    }

    public static void clearAllAnimations() {
        for (Map.Entry<UUID, ResourceLocation> entry : currentAnimations.entrySet()) {
            try {
                PlayerAnimations.stopAnimation(entry.getKey(), entry.getValue());
            } catch (Exception e) {
                LOGGER.warn("[CLIENT] Failed to stop animation {} for UUID {}: {}",
                        entry.getValue(), entry.getKey(), e.getMessage());
            }
        }
        currentAnimations.clear();
        playerMovementState.clear();
        pausedStances.clear();
        currentAnimationType.clear();
        LOGGER.info("[CLIENT] Cleared all animations");
    }
}
