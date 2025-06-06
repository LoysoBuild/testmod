package com.test_project.items;

import com.test_project.combat.CombatEventHandler;
import com.test_project.combat.PlayerCombatSettings;
import com.test_project.combat.stance.StanceType;
import com.test_project.combat.stance.NetworkManager;
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

@EventBusSubscriber
public final class EquipmentEventHandler {
    private static final Logger LOGGER = LogUtils.getLogger();

    @SubscribeEvent
    public static void onEquipmentChange(LivingEquipmentChangeEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        if (event.getSlot() != EquipmentSlot.MAINHAND) return;
        if (player.level().isClientSide()) return; // Только серверная логика

        ItemStack newItem = event.getTo();
        ItemStack oldItem = event.getFrom();

        // Обработка взятия оружия (из пустых рук)
        if (oldItem.isEmpty() && !newItem.isEmpty()) {
            handleWeaponEquipped(player, newItem);
        }
        // Обработка убирания оружия (в пустые руки)
        else if (!oldItem.isEmpty() && newItem.isEmpty()) {
            handleWeaponUnequipped(player);
        }
        // ИСПРАВЛЕНО: Обработка смены оружия (оба предмета не пустые)
        else if (!oldItem.isEmpty() && !newItem.isEmpty() && !ItemStack.isSameItem(oldItem, newItem)) {
            handleWeaponChanged(player, oldItem, newItem);
        }

        LOGGER.debug("Equipment change: {} -> {} for player {}",
                oldItem.getItem(), newItem.getItem(), player.getName().getString());
    }

    private static void handleWeaponEquipped(Player player, ItemStack weaponStack) {
        PlayerCombatSettings settings = CombatEventHandler.getSettings(player);
        StanceType currentStance = settings.getCurrentStance();

        // Применяем пресет для нового оружия
        WeaponPresetManager.changeWeaponPreset(player, weaponStack, currentStance);

        // Обновляем боевое состояние
        if (settings.isInCombat()) {
            settings.enterCombat(); // Обновляем таймер боя
        }

        // Применяем кулдаун при смене оружия в бою
        if (settings.isInCombat() && !settings.isStanceCooldown()) {
            settings.setStanceCooldown(20); // 1 секунда кулдауна
        }

        LOGGER.debug("Weapon equipped: {} with preset support: {}",
                weaponStack.getItem(), WeaponPresetManager.isWeaponSupported(weaponStack));
    }

    private static void handleWeaponUnequipped(Player player) {
        PlayerCombatSettings settings = CombatEventHandler.getSettings(player);

        // Сброс в базовую стойку при убирании оружия
        if (settings.getCurrentStance() != StanceType.ATTACK) {
            settings.setCurrentStance(StanceType.ATTACK);
            WeaponPresetManager.changeWeaponPreset(player, ItemStack.EMPTY, StanceType.ATTACK);
        }

        // Отправляем пакет остановки анимации клиенту
        if (player instanceof ServerPlayer serverPlayer) {
            NetworkManager.sendStopAnimationToPlayer(serverPlayer);
            LOGGER.debug("Sent stop animation packet to player: {}", player.getName().getString());
        }

        LOGGER.debug("Weapon unequipped for player {}", player.getName().getString());
    }

    private static void handleWeaponChanged(Player player, ItemStack oldWeapon, ItemStack newWeapon) {
        PlayerCombatSettings settings = CombatEventHandler.getSettings(player);
        StanceType currentStance = settings.getCurrentStance();

        // ИСПРАВЛЕНО: Сначала останавливаем старую анимацию
        if (player instanceof ServerPlayer serverPlayer) {
            NetworkManager.sendStopAnimationToPlayer(serverPlayer);
            LOGGER.debug("Sent stop animation packet for weapon change to player: {}", player.getName().getString());
        }

        // Небольшая задержка перед применением нового пресета
        // Это позволяет клиенту корректно остановить старую анимацию
        if (player instanceof ServerPlayer serverPlayer) {
            serverPlayer.server.execute(() -> {
                // Применяем пресет для нового оружия
                WeaponPresetManager.changeWeaponPreset(player, newWeapon, currentStance);

                // Отправляем новую анимацию
                NetworkManager.sendToPlayer(new com.test_project.combat.stance.S2CPlayStanceAnimationPacket(currentStance), serverPlayer);

                LOGGER.debug("Applied new weapon preset and animation for: {}", newWeapon.getItem());
            });
        }

        // Небольшой кулдаун при смене оружия
        if (!settings.isStanceCooldown()) {
            settings.setStanceCooldown(10); // 0.5 секунды
        }

        LOGGER.debug("Weapon changed: {} -> {} for player {}",
                oldWeapon.getItem(), newWeapon.getItem(), player.getName().getString());
    }
}
