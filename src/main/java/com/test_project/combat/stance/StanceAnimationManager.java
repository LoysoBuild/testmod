package com.test_project.combat.stance;


import com.test_project.items.weapone.AbstractWeapon;
import com.test_project.items.weapone.weaponeclass.ModBattleAxe;
import com.test_project.items.weapone.weaponeclass.ModSword;
import com.zigythebird.playeranimatorapi.data.PlayerAnimationData;
import com.zigythebird.playeranimatorapi.data.PlayerParts;
import com.zigythebird.playeranimatorapi.playeranims.PlayerAnimations;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class StanceAnimationManager {

    // Хранение текущих анимаций для каждого игрока
    private static final java.util.Map<java.util.UUID, ResourceLocation> currentAnimations = new java.util.HashMap<>();

    public static void playStance(Player player, StanceType stance) {
        if (!(player instanceof AbstractClientPlayer clientPlayer)) return;

        ItemStack mainHand = player.getMainHandItem();
        ResourceLocation animId = getAnimationForWeapon(mainHand, stance);

        if (animId != null) {
            // Останавливаем предыдущую анимацию, если она была
            stopCurrentAnimation(clientPlayer);

            // Проигрываем новую анимацию
            PlayerAnimationData data = new PlayerAnimationData(
                    player.getUUID(),
                    animId,
                    PlayerParts.allEnabled,
                    null,
                    0,
                    0,
                    0,
                    0
            );
            PlayerAnimations.playAnimation(clientPlayer, data);

            // Сохраняем текущую анимацию
            currentAnimations.put(player.getUUID(), animId);
        }
    }

    private static ResourceLocation getAnimationForWeapon(ItemStack stack, StanceType stance) {
        if (stack.isEmpty()) return null;

        // Проверка на конкретные типы оружия
        if (stack.getItem() instanceof ModBattleAxe) {
            return switch (stance) {
                case ATTACK -> ResourceLocation.fromNamespaceAndPath("mainmod", "battle_axe_attack_idle");
                case DEFENSE -> ResourceLocation.fromNamespaceAndPath("mainmod", "battle_axe_defense_idle");
            };
        } else if (stack.getItem() instanceof ModSword) {
            return switch (stance) {
                case ATTACK -> ResourceLocation.fromNamespaceAndPath("mainmod", "sword_attack_idle");
                case DEFENSE -> ResourceLocation.fromNamespaceAndPath("mainmod", "sword_defense_idle");
            };
        } else if (stack.getItem() instanceof AbstractWeapon) {
            // Общие анимации для любого AbstractWeapon
            return switch (stance) {
                case ATTACK -> ResourceLocation.fromNamespaceAndPath("mainmod", "generic_weapon_attack_idle");
                case DEFENSE -> ResourceLocation.fromNamespaceAndPath("mainmod", "generic_weapon_defense_idle");
            };
        }

        return null; // Нет анимации для данного предмета
    }

    // Метод для остановки текущей анимации игрока
    public static void stopCurrentAnimation(AbstractClientPlayer player) {
        ResourceLocation currentAnim = currentAnimations.get(player.getUUID());
        if (currentAnim != null) {
            try {
                // Останавливаем конкретную анимацию
                PlayerAnimations.stopAnimation(player.getUUID(), currentAnim);
            } catch (Exception e) {
                // Если не удалось остановить анимацию, логируем ошибку
                System.err.println("Failed to stop animation for player " + player.getName().getString() + ": " + e.getMessage());
            }
            currentAnimations.remove(player.getUUID());
        }
    }

    // Метод для остановки анимации (публичный)
    public static void stopAnimation(Player player) {
        if (player instanceof AbstractClientPlayer clientPlayer) {
            stopCurrentAnimation(clientPlayer);
        }
    }

    // Метод для остановки всех анимаций игрока (альтернативный подход)
    public static void stopAllAnimations(AbstractClientPlayer player) {
        // Останавливаем все возможные анимации стоек
        ResourceLocation[] possibleAnimations = {
                ResourceLocation.fromNamespaceAndPath("mainmod", "battle_axe_attack_idle"),
                ResourceLocation.fromNamespaceAndPath("mainmod", "battle_axe_defense_idle"),
                ResourceLocation.fromNamespaceAndPath("mainmod", "sword_attack_idle"),
                ResourceLocation.fromNamespaceAndPath("mainmod", "sword_defense_idle"),
                ResourceLocation.fromNamespaceAndPath("mainmod", "generic_weapon_attack_idle"),
                ResourceLocation.fromNamespaceAndPath("mainmod", "generic_weapon_defense_idle")
        };

        for (ResourceLocation animId : possibleAnimations) {
            try {
                PlayerAnimations.stopAnimation(player.getUUID(), animId);
            } catch (Exception e) {
                // Игнорируем ошибки - анимация может не проигрываться
            }
        }

        currentAnimations.remove(player.getUUID());
    }

    // Метод для проверки, проигрывается ли анимация
    public static boolean isAnimationPlaying(Player player) {
        return currentAnimations.containsKey(player.getUUID());
    }

    // Метод для получения текущей анимации
    public static ResourceLocation getCurrentAnimation(Player player) {
        return currentAnimations.get(player.getUUID());
    }

    // Очистка данных при выходе игрока
    public static void clearPlayerData(Player player) {
        currentAnimations.remove(player.getUUID());
    }
}
