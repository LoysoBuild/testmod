package com.test_project.items.weapone.sword;


import com.test_project.items.weapone.feature.WeaponFeatureSet;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.Item;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public class ModSword extends SwordItem {
    private final WeaponFeatureSet featureSet;

    public ModSword(Tier tier, float attackDamage, float attackSpeed, int durability, double attackRange, WeaponFeatureSet features) {
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
    }

    public WeaponFeatureSet getFeatureSet() {
        return featureSet;
    }

    // Пример вызова особенностей при атаке
    public void triggerAttackFeatures(LivingEntity attacker, LivingEntity target, ItemStack stack) {
        for (var f : featureSet.getAll()) {
            f.onAttack(attacker, target, stack);
        }
    }

    // В вашем классе ModSword
    public void triggerCounterAttack(net.minecraft.world.entity.LivingEntity defender, net.minecraft.world.entity.LivingEntity attacker, net.minecraft.world.item.ItemStack stack) {
        if (featureSet != null) {
            for (var f : featureSet.getAll()) {
                f.onCounterAttack(defender, attacker, stack);
            }
        }
    }


    // Аналогично можно добавить методы для парирования, оглушения и т.д.
}
