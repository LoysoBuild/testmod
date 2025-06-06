package com.test_project.items.weapone.preset;

import com.test_project.combat.stance.StanceType;
import com.test_project.items.weapone.AbstractWeapon;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.registries.BuiltInRegistries;
import java.util.HashMap;
import java.util.Map;
import com.mojang.logging.LogUtils;
import org.slf4j.Logger;

public final class WeaponPresetRegistry {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final Map<String, WeaponPreset> PRESETS = new HashMap<>();

    // Маппинг конкретных предметов на пресеты
    private static final Map<ResourceLocation, String> ITEM_TO_PRESET = new HashMap<>();

    public static void registerPresets() {
        LOGGER.info("Registering weapon presets...");

        // Регистрируем базовые пресеты
        registerPreset(createSwordPreset());
        registerPreset(createAxePreset());
        registerPreset(createGenericPreset());
        registerPreset(createUnarmedPreset());

        // Маппинг конкретных предметов
        mapItemsToPresets();

        LOGGER.info("Registered {} weapon presets with {} item mappings",
                PRESETS.size(), ITEM_TO_PRESET.size());
    }

    private static void mapItemsToPresets() {
        // Маппинг конкретных предметов на пресеты
        ITEM_TO_PRESET.put(
                ResourceLocation.fromNamespaceAndPath("mainmod", "gondor_sword"),
                "sword"
        );
        ITEM_TO_PRESET.put(
                ResourceLocation.fromNamespaceAndPath("mainmod", "gondor_axe"),
                "axe"
        );

        LOGGER.debug("Mapped {} items to presets", ITEM_TO_PRESET.size());
    }

    /**
     * НОВЫЙ МЕТОД: Получение анимации для конкретного ItemStack
     */
    public static ResourceLocation getAnimationForWeapon(ItemStack stack, StanceType stance) {
        if (stack.isEmpty()) {
            WeaponPreset unarmedPreset = PRESETS.get("unarmed");
            return unarmedPreset != null ? unarmedPreset.getAnimation(stance) : null;
        }

        // Сначала проверяем AbstractWeapon
        if (stack.getItem() instanceof AbstractWeapon weapon) {
            return weapon.getStanceAnimation(stance);
        }

        // Затем проверяем маппинг конкретных предметов
        ResourceLocation itemId = BuiltInRegistries.ITEM.getKey(stack.getItem());
        String presetId = ITEM_TO_PRESET.get(itemId);

        if (presetId != null) {
            WeaponPreset preset = PRESETS.get(presetId);
            if (preset != null) {
                LOGGER.debug("Found preset {} for item {}", presetId, itemId);
                return preset.getAnimation(stance);
            }
        }

        // Fallback к generic пресету
        WeaponPreset genericPreset = PRESETS.get("generic");
        if (genericPreset != null) {
            LOGGER.debug("Using generic preset for item {}", itemId);
            return genericPreset.getAnimation(stance);
        }

        LOGGER.warn("No preset found for item {}", itemId);
        return null;
    }

    /**
     * НОВЫЙ МЕТОД: Проверка поддержки оружия
     */
    public static boolean hasPresetForWeapon(ItemStack stack) {
        if (stack.isEmpty()) return true; // unarmed всегда поддерживается

        if (stack.getItem() instanceof AbstractWeapon) return true;

        ResourceLocation itemId = BuiltInRegistries.ITEM.getKey(stack.getItem());
        return ITEM_TO_PRESET.containsKey(itemId) || PRESETS.containsKey("generic");
    }

    /**
     * НОВЫЙ МЕТОД: Получение модификаторов для ItemStack
     */
    public static float getDamageModifierForWeapon(ItemStack stack, StanceType stance) {
        if (stack.getItem() instanceof AbstractWeapon weapon) {
            return weapon.getStanceDamageModifier(stance);
        }

        ResourceLocation itemId = BuiltInRegistries.ITEM.getKey(stack.getItem());
        String presetId = ITEM_TO_PRESET.getOrDefault(itemId, "generic");
        WeaponPreset preset = PRESETS.get(presetId);

        return preset != null ? preset.getDamageModifier(stance) : 1.0f;
    }

    public static float getSpeedModifierForWeapon(ItemStack stack, StanceType stance) {
        if (stack.getItem() instanceof AbstractWeapon weapon) {
            return weapon.getStanceSpeedModifier(stance);
        }

        ResourceLocation itemId = BuiltInRegistries.ITEM.getKey(stack.getItem());
        String presetId = ITEM_TO_PRESET.getOrDefault(itemId, "generic");
        WeaponPreset preset = PRESETS.get(presetId);

        return preset != null ? preset.getSpeedModifier(stance) : 1.0f;
    }

    private static WeaponPreset createSwordPreset() {
        return new WeaponPreset("sword")
                .setAnimation(StanceType.ATTACK, ResourceLocation.fromNamespaceAndPath("mainmod", "swordattackidle"))
                .setAnimation(StanceType.DEFENSE, ResourceLocation.fromNamespaceAndPath("mainmod", "sworddefenseidle"))
                .setDamageModifier(StanceType.ATTACK, 1.1f)   // +10% урона в атаке
                .setDamageModifier(StanceType.DEFENSE, 0.95f) // -5% урона в защите
                .setSpeedModifier(StanceType.ATTACK, 1.0f)    // Полная скорость в атаке
                .setSpeedModifier(StanceType.DEFENSE, 0.9f);  // -10% скорости в защите
    }

    private static WeaponPreset createAxePreset() {
        return new WeaponPreset("axe")
                .setAnimation(StanceType.ATTACK, ResourceLocation.fromNamespaceAndPath("mainmod", "axeattackidle"))
                .setAnimation(StanceType.DEFENSE, ResourceLocation.fromNamespaceAndPath("mainmod", "axedefenseidle"))
                .setDamageModifier(StanceType.ATTACK, 1.15f)  // +15% урона в атаке (топоры мощнее)
                .setDamageModifier(StanceType.DEFENSE, 0.9f)  // -10% урона в защите (тяжелое оружие)
                .setSpeedModifier(StanceType.ATTACK, 0.95f)   // -5% скорости в атаке (вес)
                .setSpeedModifier(StanceType.DEFENSE, 0.85f); // -15% скорости в защите (очень медленно)
    }

    private static WeaponPreset createGenericPreset() {
        return new WeaponPreset("generic")
                .setAnimation(StanceType.ATTACK, ResourceLocation.fromNamespaceAndPath("mainmod", "genericweaponattackidle"))
                .setAnimation(StanceType.DEFENSE, ResourceLocation.fromNamespaceAndPath("mainmod", "genericweapondefenseidle"))
                .setDamageModifier(StanceType.ATTACK, 1.05f)  // +5% урона в атаке
                .setDamageModifier(StanceType.DEFENSE, 0.98f) // -2% урона в защите
                .setSpeedModifier(StanceType.ATTACK, 1.0f)    // Нормальная скорость
                .setSpeedModifier(StanceType.DEFENSE, 0.95f); // -5% скорости в защите
    }

    private static WeaponPreset createUnarmedPreset() {
        return new WeaponPreset("unarmed")
                .setAnimation(StanceType.ATTACK, ResourceLocation.fromNamespaceAndPath("mainmod", "unarmedattackidle"))
                .setAnimation(StanceType.DEFENSE, ResourceLocation.fromNamespaceAndPath("mainmod", "unarmeddefenseidle"))
                .setDamageModifier(StanceType.ATTACK, 1.0f)   // Без модификаторов
                .setDamageModifier(StanceType.DEFENSE, 1.0f)  // Без модификаторов
                .setSpeedModifier(StanceType.ATTACK, 1.0f)    // Без модификаторов
                .setSpeedModifier(StanceType.DEFENSE, 1.0f);  // Без модификаторов
    }

    private static void registerPreset(WeaponPreset preset) {
        PRESETS.put(preset.getPresetId(), preset);
        LOGGER.debug("Registered weapon preset: {}", preset.getPresetId());
    }

    public static WeaponPreset getPreset(String presetId) {
        WeaponPreset preset = PRESETS.get(presetId);
        if (preset == null) {
            LOGGER.warn("Requested unknown weapon preset: {}", presetId);
        }
        return preset;
    }

    public static boolean hasPreset(String presetId) {
        return PRESETS.containsKey(presetId);
    }

    public static int getPresetCount() {
        return PRESETS.size();
    }

    /**
     * Получение всех зарегистрированных пресетов (для отладки)
     */
    public static Map<String, WeaponPreset> getAllPresets() {
        return new HashMap<>(PRESETS);
    }
}
