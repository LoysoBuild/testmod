package com.test_project.items.weapone.feature;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

/**
 * Базовый интерфейс для особенностей оружия.
 * Особенности реализуют нужные методы для реакции на игровые события (например, контратака, парирование, оглушение).
 */
public interface WeaponFeature {
    /**
     * Уникальный идентификатор особенности (например, "counterattack").
     */
    String getId();

    /**
     * Вызывается при атаке этим оружием.
     */
    default void onAttack(LivingEntity attacker, LivingEntity target, ItemStack stack) {}

    /**
     * Вызывается при успешном парировании.
     */
    default void onParry(LivingEntity defender, LivingEntity attacker, ItemStack stack) {}

    /**
     * Вызывается при активации контратаки.
     */
    default void onCounterAttack(LivingEntity defender, LivingEntity attacker, ItemStack stack) {}

    /**
     * Вызывается при оглушении цели.
     */
    default void onStun(LivingEntity attacker, LivingEntity target, ItemStack stack) {}
}
