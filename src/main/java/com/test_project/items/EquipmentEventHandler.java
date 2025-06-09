package com.test_project.items;

import com.test_project.combat.CombatEventHandler;
import com.test_project.combat.PlayerCombatSettings;
import com.test_project.combat.stance.StanceType;
import com.test_project.combat.stance.NetworkManager;
import com.test_project.combat.stance.S2CPlayStanceAnimationPacket;
import com.test_project.items.weapone.preset.WeaponPresetManager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.event.entity.living.LivingEquipmentChangeEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import com.mojang.logging.LogUtils;
import org.slf4j.Logger;

/**
 * Обработчик событий смены экипировки.
 * Управляет анимациями и пресетами при смене оружия в главной руке.
 */
@EventBusSubscriber
public final class EquipmentEventHandler {
    private static final Logger LOGGER = LogUtils.getLogger();

    @SubscribeEvent
    public static void onEquipmentChange(LivingEquipmentChangeEvent event) {
        // Фильтрация: только игроки, только главная рука, только сервер
        if (!(event.getEntity() instanceof Player player)) return;
        if (event.getSlot() != EquipmentSlot.MAINHAND) return;
        if (player.level().isClientSide()) return;

        ItemStack newItem = event.getTo();
        ItemStack oldItem = event.getFrom();

        // Логика обработки различных сценариев смены экипировки
        if (oldItem.isEmpty() && !newItem.isEmpty()) {
            // Сценарий 1: Взятие оружия из пустых рук
            handleWeaponEquipped(player, newItem);
        }
        else if (!oldItem.isEmpty() && newItem.isEmpty()) {
            // Сценарий 2: Убирание оружия в пустые руки
            handleWeaponUnequipped(player);
        }
        else if (!oldItem.isEmpty() && !newItem.isEmpty() && !ItemStack.isSameItem(oldItem, newItem)) {
            // ИСПРАВЛЕНО: Используем newItem вместо newWeapon
            handleWeaponChanged(player, oldItem, newItem);
        }

        LOGGER.debug("[SERVER] Equipment change: {} -> {} for player {}",
                oldItem.getItem(), newItem.getItem(), player.getName().getString());
    }


    /**
     * Обработка взятия оружия в руки
     */
    private static void handleWeaponEquipped(Player player, ItemStack weaponStack) {
        PlayerCombatSettings settings = CombatEventHandler.getSettings(player);
        StanceType currentStance = settings.getCurrentStance();

        // Применяем пресет для нового оружия
        WeaponPresetManager.changeWeaponPreset(player, weaponStack, currentStance);

        // Обновляем боевое состояние
        if (settings.isInCombat()) {
            settings.enterCombat();
        }

        // Кулдаун при смене оружия в бою
        if (settings.isInCombat() && !settings.isStanceCooldown()) {
            settings.setStanceCooldown(20); // 1 секунда
        }

        LOGGER.debug("[SERVER] Weapon equipped: {} with preset support: {}",
                weaponStack.getItem(), WeaponPresetManager.isWeaponSupported(weaponStack));
    }

    /**
     * Обработка убирания оружия из рук
     */
    private static void handleWeaponUnequipped(Player player) {
        PlayerCombatSettings settings = CombatEventHandler.getSettings(player);

        // Сброс в базовую стойку при убирании оружия
        if (settings.getCurrentStance() != StanceType.ATTACK) {
            settings.setCurrentStance(StanceType.ATTACK);
            WeaponPresetManager.changeWeaponPreset(player, ItemStack.EMPTY, StanceType.ATTACK);
        }

        // Останавливаем анимацию
        if (player instanceof ServerPlayer serverPlayer) {
            NetworkManager.sendStopAnimationToPlayer(serverPlayer);
            LOGGER.debug("[SERVER] Sent stop animation packet to player: {}", player.getName().getString());
        }

        LOGGER.debug("[SERVER] Weapon unequipped for player {}", player.getName().getString());
    }

    /**
     * ИСПРАВЛЕННЫЙ МЕТОД: Обработка смены одного оружия на другое
     */
    private static void handleWeaponChanged(Player player, ItemStack oldWeapon, ItemStack newWeapon) {
        PlayerCombatSettings settings = CombatEventHandler.getSettings(player);
        StanceType currentStance = settings.getCurrentStance();

        // Останавливаем старую анимацию
        if (player instanceof ServerPlayer serverPlayer) {
            NetworkManager.sendStopAnimationToPlayer(serverPlayer);
            LOGGER.debug("[SERVER] Sent stop animation packet for weapon change to player: {}",
                    player.getName().getString());
        }

        // Асинхронное применение нового пресета с небольшой задержкой
        if (player instanceof ServerPlayer serverPlayer) {
            serverPlayer.server.execute(() -> {
                // Применяем пресет для нового оружия
                WeaponPresetManager.changeWeaponPreset(player, newWeapon, currentStance);

                // ИСПРАВЛЕНО: Отправляем анимацию с двумя параметрами
                // Используем одинаковые стойки для плавного перехода к новому оружию
                NetworkManager.sendToPlayer(
                        new S2CPlayStanceAnimationPacket(currentStance, currentStance),
                        serverPlayer
                );

                LOGGER.debug("[SERVER] Applied new weapon preset and animation for: {}",
                        newWeapon.getItem());
            });
        }

        // Небольшой кулдаун при смене оружия
        if (!settings.isStanceCooldown()) {
            settings.setStanceCooldown(10); // 0.5 секунды
        }

        LOGGER.debug("[SERVER] Weapon changed: {} -> {} for player {}",
                oldWeapon.getItem(), newWeapon.getItem(), player.getName().getString());
    }
}
