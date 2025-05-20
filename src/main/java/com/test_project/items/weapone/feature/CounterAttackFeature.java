package com.test_project.items.weapone.feature;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

/**
 * Особенность "Контратака" для оружия.
 * При вызове onCounterAttack наносит атакующему фиксированный урон от имени защитника.
 */
public class CounterAttackFeature implements WeaponFeature {
    @Override
    public String getId() {
        return "counterattack";
    }

    @Override
    public void onCounterAttack(LivingEntity defender, LivingEntity attacker, ItemStack stack) {
        if (attacker != null && attacker != defender && attacker.isAlive()) {
            if (defender instanceof net.minecraft.world.entity.player.Player player) {
                attacker.hurt(defender.damageSources().playerAttack(player), 4.0F);
            } else {
                attacker.hurt(defender.damageSources().mobAttack(defender), 4.0F);
            }
            // Здесь можно добавить визуальные или звуковые эффекты
        }
    }
}