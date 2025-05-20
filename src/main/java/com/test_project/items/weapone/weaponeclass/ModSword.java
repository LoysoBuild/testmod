package com.test_project.items.weapone.weaponeclass;

import com.test_project.combat.PlayerCombatSettings;
import com.test_project.combat.combo.CombatEventHandler;
import com.test_project.combat.stance.StanceType;
import com.test_project.items.weapone.feature.WeaponFeatureSet;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlotGroup;

public class ModSword extends SwordItem {
    private final WeaponFeatureSet featureSet;
    private final String defaultComboId;

    public ModSword(Tier tier,
                    float attackDamage,
                    float attackSpeed,
                    int durability,
                    double attackRange,
                    WeaponFeatureSet features,
                    String defaultComboId) {
        super(tier, new Item.Properties()
                .stacksTo(1)
                .durability(durability)
                .attributes(
                        ItemAttributeModifiers.builder()
                                .add(
                                        Attributes.ATTACK_DAMAGE,
                                        new AttributeModifier(
                                                ResourceLocation.fromNamespaceAndPath("yourmodid", "weapon_attack_damage"),
                                                attackDamage,
                                                AttributeModifier.Operation.ADD_VALUE
                                        ),
                                        EquipmentSlotGroup.MAINHAND
                                )
                                .add(
                                        Attributes.ATTACK_SPEED,
                                        new AttributeModifier(
                                                ResourceLocation.fromNamespaceAndPath("yourmodid", "weapon_attack_speed"),
                                                attackSpeed,
                                                AttributeModifier.Operation.ADD_VALUE
                                        ),
                                        EquipmentSlotGroup.MAINHAND
                                )
                                .add(
                                        Attributes.ENTITY_INTERACTION_RANGE,
                                        new AttributeModifier(
                                                ResourceLocation.fromNamespaceAndPath("yourmodid", "weapon_entity_interaction_range"),
                                                attackRange - 3.0,
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

    /** Получить сет особенностей */
    public WeaponFeatureSet getFeatureSet() {
        return featureSet;
    }

    /** ID комбо, привязанного по-умолчанию к этому мечу */
    public String getDefaultComboId() {
        return defaultComboId;
    }

    /**
     * Вызывается при смене оружия в руке.
     * Устанавливает в PlayerCombatSettings дефолтное комбо,
     * если у игрока ещё не выбраны свои.
     */
    public static void handleEquip(Player player, ItemStack newStack) {
        if (newStack.getItem() instanceof ModSword sword) {
            PlayerCombatSettings settings = CombatEventHandler.getSettings(player);
            if (settings.getCombo(StanceType.ATTACK) == null) {
                settings.setCombo(StanceType.ATTACK, sword.getDefaultComboId());
            }
            if (settings.getCombo(StanceType.DEFENSE) == null) {
                settings.setCombo(StanceType.DEFENSE, sword.getDefaultComboId());
            }
        }
    }

    /**
     * Переопределяем hurtEnemy, чтобы при каждом нанесении урона
     * автоматически вызывать все onAttack-особенности.
     */
    @Override
    public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        triggerOnAttack(attacker, target, stack);
        return super.hurtEnemy(stack, target, attacker);
    }

    /** Вызывает onAttack у всех особенностей */
    public void triggerOnAttack(LivingEntity attacker, LivingEntity target, ItemStack stack) {
        for (var f : featureSet.getAll()) {
            f.onAttack(attacker, target, stack);
        }
    }

    /** Вызывает onCounterAttack у всех особенностей */
    public void triggerCounterAttack(LivingEntity defender, LivingEntity attacker, ItemStack stack) {
        for (var f : featureSet.getAll()) {
            f.onCounterAttack(defender, attacker, stack);
        }
    }

    // При необходимости добавьте аналогичные методы для onParry, onStun и т.д.
}
