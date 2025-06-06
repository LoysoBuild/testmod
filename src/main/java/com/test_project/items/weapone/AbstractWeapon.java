package com.test_project.items.weapone;

import com.test_project.combat.stance.StanceType;
import com.test_project.items.weapone.preset.WeaponPreset;
import com.test_project.items.weapone.preset.WeaponPresetRegistry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import com.mojang.logging.LogUtils;
import org.slf4j.Logger;

public abstract class AbstractWeapon extends SwordItem {
    private static final Logger LOGGER = LogUtils.getLogger();
    protected final String weaponPresetId;

    protected AbstractWeapon(Tier tier, ItemAttributeModifiers attributes, String presetId) {
        super(tier, new Properties()
                .stacksTo(1)
                .attributes(attributes)
        );
        this.weaponPresetId = presetId;
        LOGGER.debug("Created weapon with preset: {}", presetId);
    }

    /**
     * Получает анимацию для текущей стойки на основе пресета оружия
     */
    public ResourceLocation getStanceAnimation(StanceType stance) {
        WeaponPreset preset = WeaponPresetRegistry.getPreset(weaponPresetId);
        if (preset != null) {
            ResourceLocation animation = preset.getAnimation(stance);
            LOGGER.debug("Retrieved animation {} for stance {} with preset {}", animation, stance, weaponPresetId);
            return animation;
        }
        LOGGER.warn("No preset found for weapon: {}", weaponPresetId);
        return null;
    }

    /**
     * Получает модификатор урона для стойки из пресета
     */
    public float getStanceDamageModifier(StanceType stance) {
        WeaponPreset preset = WeaponPresetRegistry.getPreset(weaponPresetId);
        if (preset != null) {
            return preset.getDamageModifier(stance);
        }
        return 1.0f;
    }

    /**
     * Получает модификатор скорости для стойки из пресета
     */
    public float getStanceSpeedModifier(StanceType stance) {
        WeaponPreset preset = WeaponPresetRegistry.getPreset(weaponPresetId);
        if (preset != null) {
            return preset.getSpeedModifier(stance);
        }
        return 1.0f;
    }

    /**
     * Получает ID пресета оружия
     */
    public String getWeaponPresetId() {
        return weaponPresetId;
    }

    /**
     * Проверяет совместимость оружия со стойкой
     */
    public boolean isCompatibleWithStance(StanceType stance) {
        return getStanceAnimation(stance) != null;
    }

    /**
     * Создает атрибуты для оружия
     */
    protected static ItemAttributeModifiers createWeaponAttributes(
            Tier tier,
            float attackDamage,
            float attackSpeed,
            double attackRange,
            String weaponType
    ) {
        ItemAttributeModifiers.Builder builder = ItemAttributeModifiers.builder();

        // Урон
        builder.add(
                Attributes.ATTACK_DAMAGE,
                new AttributeModifier(
                        ResourceLocation.fromNamespaceAndPath("mainmod", weaponType + "_damage"),
                        attackDamage + tier.getAttackDamageBonus(),
                        AttributeModifier.Operation.ADD_VALUE
                ),
                EquipmentSlotGroup.MAINHAND
        );

        // Скорость атаки
        builder.add(
                Attributes.ATTACK_SPEED,
                new AttributeModifier(
                        ResourceLocation.fromNamespaceAndPath("mainmod", weaponType + "_speed"),
                        attackSpeed,
                        AttributeModifier.Operation.ADD_VALUE
                ),
                EquipmentSlotGroup.MAINHAND
        );

        // Дальность атаки (если отличается от стандартной)
        if (attackRange != 2.5) {
            builder.add(
                    Attributes.ENTITY_INTERACTION_RANGE,
                    new AttributeModifier(
                            ResourceLocation.fromNamespaceAndPath("mainmod", weaponType + "_range"),
                            attackRange - 2.5,
                            AttributeModifier.Operation.ADD_VALUE
                    ),
                    EquipmentSlotGroup.MAINHAND
            );
        }

        return builder.build();
    }

    @Override
    public boolean isValidRepairItem(ItemStack stack, ItemStack repairCandidate) {
        return getTier().getRepairIngredient().test(repairCandidate);
    }
}
