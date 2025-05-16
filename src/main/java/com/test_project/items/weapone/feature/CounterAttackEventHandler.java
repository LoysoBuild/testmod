package com.test_project.items.weapone.feature;

import com.test_project.items.weapone.sword.ModSword;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.living.LivingShieldBlockEvent;

public class CounterAttackEventHandler {

    @SubscribeEvent
    public static void onLivingShieldBlock(LivingShieldBlockEvent event) {
        LivingEntity defender = event.getEntity();
        ItemStack mainHand = defender.getMainHandItem();

        if (mainHand.getItem() instanceof ModSword sword && sword.getFeatureSet().has("counterattack")) {
            if (event.getDamageSource().getEntity() instanceof LivingEntity attacker && attacker.isAlive()) {
                sword.triggerCounterAttack(defender, attacker, mainHand);

                // Для теста
                if (defender instanceof Player player) {
                    player.sendSystemMessage(net.minecraft.network.chat.Component.literal("Контратака сработала!"));
                }
            }
        }
    }
}
