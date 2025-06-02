package com.test_project.items.weapone;

import com.test_project.items.weapone.feature.WeaponFeatureSet;
import com.test_project.items.weapone.feature.WeaponFeature;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.component.ItemAttributeModifiers;

public abstract class AbstractWeapon extends SwordItem {
    protected final WeaponFeatureSet featureSet;
    protected final String defaultComboId;

    public AbstractWeapon(
            Tier tier,
            float attackDamage,
            float attackSpeed,
            int durability,
            double attackRange,
            WeaponFeatureSet features,
            String defaultComboId
    ) {
        super(tier, new Properties()
                .stacksTo(1)
                .durability(durability)
                .attributes(
                        ItemAttributeModifiers.builder()
                                .add(
                                        Attributes.ATTACK_DAMAGE,
                                        new AttributeModifier(
                                                ResourceLocation.fromNamespaceAndPath("mainmod", "weapon_attack_damage"),
                                                attackDamage,
                                                AttributeModifier.Operation.ADD_VALUE
                                        ),
                                        EquipmentSlotGroup.MAINHAND
                                )
                                .add(
                                        Attributes.ATTACK_SPEED,
                                        new AttributeModifier(
                                                ResourceLocation.fromNamespaceAndPath("mainmod", "weapon_attack_speed"),
                                                attackSpeed,
                                                AttributeModifier.Operation.ADD_VALUE
                                        ),
                                        EquipmentSlotGroup.MAINHAND
                                )
                                .build()
                )
        );
        this.featureSet = features;
        this.defaultComboId = defaultComboId;
    }

    public WeaponFeatureSet getFeatureSet() {
        return featureSet;
    }

    public String getDefaultComboId() {
        return defaultComboId;
    }

    // === МЕТОДЫ ДЛЯ ОСОБЕННОСТЕЙ ОРУЖИЯ ===

    /**
     * Вызывает все особенности onAttack у данного оружия.
     */
    public void triggerOnAttack(LivingEntity attacker, LivingEntity target, ItemStack stack) {
        if (featureSet != null) {
            for (WeaponFeature feature : featureSet.getAll()) {
                try {
                    feature.onAttack(attacker, target, stack);
                } catch (Exception e) {
                    System.err.println("Error triggering onAttack for feature " + feature.getId() + ": " + e.getMessage());
                }
            }
        }
    }

    /**
     * Вызывает все особенности onCounterAttack у данного оружия.
     */
    public void triggerCounterAttack(LivingEntity defender, LivingEntity attacker, ItemStack stack) {
        if (featureSet != null) {
            for (WeaponFeature feature : featureSet.getAll()) {
                try {
                    feature.onCounterAttack(defender, attacker, stack);
                } catch (Exception e) {
                    System.err.println("Error triggering onCounterAttack for feature " + feature.getId() + ": " + e.getMessage());
                }
            }
        }
    }

    /**
     * Вызывает все особенности onParry у данного оружия.
     */
    public void triggerParry(LivingEntity defender, LivingEntity attacker, ItemStack stack) {
        if (featureSet != null) {
            for (WeaponFeature feature : featureSet.getAll()) {
                try {
                    feature.onParry(defender, attacker, stack);
                } catch (Exception e) {
                    System.err.println("Error triggering onParry for feature " + feature.getId() + ": " + e.getMessage());
                }
            }
        }
    }

    /**
     * Вызывает все особенности onStun у данного оружия.
     */
    public void triggerStun(LivingEntity attacker, LivingEntity target, ItemStack stack) {
        if (featureSet != null) {
            for (WeaponFeature feature : featureSet.getAll()) {
                try {
                    feature.onStun(attacker, target, stack);
                } catch (Exception e) {
                    System.err.println("Error triggering onStun for feature " + feature.getId() + ": " + e.getMessage());
                }
            }
        }
    }

    /**
     * Вызывает все особенности onCriticalHit у данного оружия.
     */
    public void triggerCriticalHit(LivingEntity attacker, LivingEntity target, ItemStack stack, float damage) {
        if (featureSet != null) {
            for (WeaponFeature feature : featureSet.getAll()) {
                try {
                    feature.onCriticalHit(attacker, target, stack, damage);
                } catch (Exception e) {
                    System.err.println("Error triggering onCriticalHit for feature " + feature.getId() + ": " + e.getMessage());
                }
            }
        }
    }

    /**
     * Вызывает все особенности onKill у данного оружия.
     */
    public void triggerKill(LivingEntity attacker, LivingEntity target, ItemStack stack) {
        if (featureSet != null) {
            for (WeaponFeature feature : featureSet.getAll()) {
                try {
                    feature.onKill(attacker, target, stack);
                } catch (Exception e) {
                    System.err.println("Error triggering onKill for feature " + feature.getId() + ": " + e.getMessage());
                }
            }
        }
    }

    /**
     * Переопределяем hurtEnemy для автоматического вызова особенностей при атаке.
     */
    @Override
    public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        // Вызываем особенности при атаке
        triggerOnAttack(attacker, target, stack);

        // Проверяем, был ли это критический удар (примерная логика)
        if (attacker.getRandom().nextFloat() < 0.1f) { // 10% шанс крита
            triggerCriticalHit(attacker, target, stack, 0); // Урон можно получить из других источников
        }

        // Проверяем, умерла ли цель
        if (!target.isAlive()) {
            triggerKill(attacker, target, stack);
        }

        return super.hurtEnemy(stack, target, attacker);
    }

    // === УТИЛИТАРНЫЕ МЕТОДЫ ===

    /**
     * Проверяет, есть ли у оружия определённая особенность.
     */
    public boolean hasFeature(String featureId) {
        if (featureSet == null) return false;
        return featureSet.getAll().stream()
                .anyMatch(feature -> feature.getId().equals(featureId));
    }

    /**
     * Получает особенность по ID.
     */
    public WeaponFeature getFeature(String featureId) {
        if (featureSet == null) return null;
        return featureSet.getAll().stream()
                .filter(feature -> feature.getId().equals(featureId))
                .findFirst()
                .orElse(null);
    }
}
