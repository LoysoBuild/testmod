package com.test_project.combat.parry;


import com.test_project.KeyBindings;
import com.test_project.combat.stance.NetworkManager;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.client.event.InputEvent;
import net.neoforged.api.distmarker.Dist;
import net.minecraft.client.Minecraft;

@EventBusSubscriber(modid = "mainmod", bus = EventBusSubscriber.Bus.GAME)
public class ParryEventHandler {

    // Клиентская обработка нажатия клавиши парирования
    @EventBusSubscriber(modid = "mainmod", bus = EventBusSubscriber.Bus.GAME, value = Dist.CLIENT)
    public static class ClientEvents {
        @SubscribeEvent
        public static void onKeyInput(InputEvent.Key event) {
            Minecraft minecraft = Minecraft.getInstance();

            if (minecraft.screen == null && minecraft.player != null) {
                if (KeyBindings.isParryPressed()) {
                    // Отправляем пакет на сервер для активации парирования
                    NetworkManager.sendToServer(new C2SActivateParryPacket());
                }
            }
        }
    }

    // ИСПРАВЛЕНО: Обработка парирования в LivingIncomingDamageEvent
    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void onLivingIncomingDamage(LivingIncomingDamageEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer defender)) return;
        if (!(event.getSource().getEntity() instanceof Player attacker)) return;

        // Попытка парирования
        if (ParrySystem.attemptParry(defender, attacker, event.getAmount())) {
            // Парирование успешно - отменяем урон
            event.setCanceled(true);
            System.out.println("[PARRY] Damage blocked by parry: " + event.getAmount());
        }
    }

    // ИСПРАВЛЕНО: Используем LivingDamageEvent.Post для контратак
    @SubscribeEvent(priority = EventPriority.NORMAL)
    public static void onLivingDamagePost(LivingDamageEvent.Post event) {
        if (!(event.getSource().getEntity() instanceof ServerPlayer attacker)) return;
        if (!(event.getEntity() instanceof Player target)) return;

        // Проверка контратаки после нанесения урона
        if (ParrySystem.attemptCounterAttack(attacker, target)) {
            System.out.println("[COUNTER] Counter attack executed after damage");
        }
    }
}
