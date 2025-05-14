package com.test_project.items.weapone.pike;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.AttackEntityEvent;
import net.neoforged.bus.api.SubscribeEvent;


@EventBusSubscriber
public class PikeAttackHandler {
    @SubscribeEvent
    public static void onPlayerAttack(AttackEntityEvent event) {
        Player player = event.getEntity();
        ItemStack mainHand = player.getMainHandItem();
        if (!(mainHand.getItem() instanceof PikeItem)) return;
        if (!(event.getTarget() instanceof LivingEntity target)) return;

        if (PikeCounterHandler.tryConsumeCounterWindow(player)) {
            // Усиливаем урон и отбрасывание
            float base = 6.0F; // базовый урон пики
            float bonus = base * 1.5F; // увеличенный урон
            target.hurt(player.damageSources().playerAttack(player), bonus);
            target.knockback(1.5, player.getX() - target.getX(), player.getZ() - target.getZ());
            player.displayClientMessage(
                    net.minecraft.network.chat.Component.literal("Контратака! Мощный укол!"), true);
            event.setCanceled(true); // отменяем стандартный урон, чтобы не было двойного
        }
        // иначе - обычная атака
    }
}
