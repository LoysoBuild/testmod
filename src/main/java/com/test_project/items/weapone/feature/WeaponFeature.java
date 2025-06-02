package com.test_project.items.weapone.feature;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.component.CustomData;

/**
 * Базовый интерфейс для особенностей оружия.
 * Особенности реализуют нужные методы для реакции на игровые события.
 */
public interface WeaponFeature {

    /**
     * Уникальный идентификатор особенности (например, "counterattack", "lifesteal", "fire_aspect").
     */
    String getId();

    /**
     * Отображаемое имя особенности для UI и описаний.
     */
    default String getDisplayName() {
        return getId();
    }

    /**
     * Описание особенности для подсказок.
     */
    default String getDescription() {
        return "Особенность оружия: " + getDisplayName();
    }

    /**
     * Приоритет выполнения особенности (чем выше, тем раньше выполняется).
     */
    default int getPriority() {
        return 100;
    }

    /**
     * Проверяет, активна ли особенность в данный момент.
     */
    default boolean isActive(LivingEntity entity, ItemStack stack) {
        return true;
    }

    /**
     * Проверяет, совместима ли эта особенность с другой.
     */
    default boolean isCompatibleWith(WeaponFeature other) {
        return !this.getId().equals(other.getId());
    }

    // === ОСНОВНЫЕ СОБЫТИЯ ===

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

    // === ДОПОЛНИТЕЛЬНЫЕ СОБЫТИЯ ===

    /**
     * Вызывается при критическом ударе.
     */
    default void onCriticalHit(LivingEntity attacker, LivingEntity target, ItemStack stack, float damage) {}

    /**
     * Вызывается при убийстве цели.
     */
    default void onKill(LivingEntity attacker, LivingEntity target, ItemStack stack) {}

    /**
     * Вызывается при правом клике оружием (активация способности).
     */
    default InteractionResult onRightClick(Level level, Player player, InteractionHand hand, ItemStack stack) {
        return InteractionResult.PASS;
    }

    /**
     * Вызывается при экипировке оружия.
     */
    default void onEquip(LivingEntity entity, ItemStack stack) {}

    /**
     * Вызывается при снятии оружия.
     */
    default void onUnequip(LivingEntity entity, ItemStack stack) {}

    // === СИСТЕМА КУЛДАУНОВ ===

    /**
     * Возвращает кулдаун особенности в тиках.
     */
    default int getCooldown() {
        return 0;
    }

    /**
     * Получает данные из CustomData компонента.
     */
    default CompoundTag getCustomData(ItemStack stack) {
        CustomData customData = stack.get(DataComponents.CUSTOM_DATA);
        return customData != null ? customData.copyTag() : new CompoundTag();
    }

    /**
     * Устанавливает данные в CustomData компонент.
     */
    default void setCustomData(ItemStack stack, CompoundTag tag) {
        stack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
    }

    /**
     * Проверяет, находится ли особенность на кулдауне.
     */
    default boolean isOnCooldown(LivingEntity entity, ItemStack stack) {
        if (getCooldown() <= 0) return false;

        CompoundTag tag = getCustomData(stack);
        String cooldownKey = "cooldown_" + getId();
        long lastUsed = tag.getLong(cooldownKey);
        long currentTime = entity.level().getGameTime();

        return (currentTime - lastUsed) < getCooldown();
    }

    /**
     * Устанавливает кулдаун особенности.
     */
    default void setCooldown(LivingEntity entity, ItemStack stack) {
        if (getCooldown() <= 0) return;

        CompoundTag tag = getCustomData(stack);
        String cooldownKey = "cooldown_" + getId();
        tag.putLong(cooldownKey, entity.level().getGameTime());
        setCustomData(stack, tag);
    }

    // === УТИЛИТАРНЫЕ МЕТОДЫ ===

    /**
     * Создаёт копию особенности с теми же параметрами.
     */
    default WeaponFeature copy() {
        return this;
    }

    // УДАЛЕНЫ equals() и hashCode() - они запрещены в default методах интерфейсов!
    // Реализующие классы должны сами переопределить эти методы при необходимости
}
