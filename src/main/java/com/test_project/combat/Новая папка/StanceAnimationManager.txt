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

    // ИСПРАВЛЕНИЕ: Добавлена правильная типизация Map
    private static final java.util.Map<java.util.UUID, ResourceLocation> currentAnimations = new java.util.HashMap<>();

    public static void playStance(Player player, StanceType stance) {
        if (!(player instanceof AbstractClientPlayer clientPlayer)) return;

        ItemStack mainHand = player.getMainHandItem();
        ResourceLocation animId = getAnimationForWeapon(mainHand, stance);

        if (animId != null) {
            // Останавливаем предыдущую анимацию
            stopCurrentAnimation(clientPlayer);

            // ИСПРАВЛЕНИЕ: Добавлен лог для отладки
            System.out.println("[CLIENT] Playing animation: " + animId + " for stance: " + stance);

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
        } else {
            System.out.println("[CLIENT] No animation found for weapon: " + mainHand.getItem().getClass().getSimpleName() + " and stance: " + stance);
        }
    }

    private static ResourceLocation getAnimationForWeapon(ItemStack stack, StanceType stance) {
        if (stack.isEmpty()) return null;

        // Проверка на конкретные типы оружия
        if (stack.getItem() instanceof ModBattleAxe) {
            return switch (stance) {
                case ATTACK -> ResourceLocation.fromNamespaceAndPath("mainmod", "sword_attack_idle");
                case DEFENSE -> ResourceLocation.fromNamespaceAndPath("mainmod", "sword_defense_idle");
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

    // Очистка данных при выходе игрока
    public static void clearPlayerData(Player player) {
        currentAnimations.remove(player.getUUID());
    }
}
