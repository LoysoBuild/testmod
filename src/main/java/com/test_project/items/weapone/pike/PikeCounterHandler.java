package com.test_project.items.weapone.pike;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.core.component.DataComponents;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@EventBusSubscriber
public class PikeCounterHandler {
    private static final Map<UUID, Integer> counterWindows = new HashMap<>();

    @SubscribeEvent
    public static void onPlayerAttacked(LivingIncomingDamageEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        ItemStack mainHand = player.getMainHandItem();
        if (!(mainHand.getItem() instanceof PikeItem)) return;
        DamageSource src = event.getSource();
        if (src.getDirectEntity() instanceof LivingEntity) {
            counterWindows.put(player.getUUID(), PikeItem.COUNTERATTACK_WINDOW);
            player.displayClientMessage(
                    net.minecraft.network.chat.Component.literal("Контратака доступна!"), true);
        }
    }

    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post event) {
        Player player = event.getEntity();
        UUID uuid = player.getUUID();
        boolean counterActive = counterWindows.containsKey(uuid);

        ItemStack mainHand = player.getMainHandItem();
        if (mainHand.getItem() instanceof PikeItem) {
            if (counterActive) {
                // Добавляем пустой компонент ENCHANTMENTS для свечения
                var ench = mainHand.get(DataComponents.ENCHANTMENTS);
                if (ench == null || ench.isEmpty()) {
                    mainHand.set(DataComponents.ENCHANTMENTS, ItemEnchantments.EMPTY);
                }
            } else {
                // Убираем компонент свечения, если он пустой
                var ench = mainHand.get(DataComponents.ENCHANTMENTS);
                if (ench != null && ench.isEmpty()) {
                    mainHand.remove(DataComponents.ENCHANTMENTS);
                }
            }
        }

        // Обработка таймера окна контратаки
        if (counterActive) {
            int ticks = counterWindows.get(uuid) - 1;
            if (ticks <= 0) {
                counterWindows.remove(uuid);
            } else {
                counterWindows.put(uuid, ticks);
            }
        }
    }

    public static boolean tryConsumeCounterWindow(Player player) {
        UUID uuid = player.getUUID();
        if (counterWindows.containsKey(uuid)) {
            counterWindows.remove(uuid);
            return true;
        }
        return false;
    }
}
