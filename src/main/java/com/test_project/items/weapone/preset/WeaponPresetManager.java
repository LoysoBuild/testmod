package com.test_project.items.weapone.preset;

import com.test_project.combat.stance.StanceType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import com.mojang.logging.LogUtils;
import org.slf4j.Logger;

import java.util.Map;
import java.util.HashMap;
import java.util.Optional;

public final class WeaponPresetManager {
    private static final Logger LOGGER = LogUtils.getLogger();

    private static final Map<ResourceLocation, BetterCombatPresets> BETTER_COMBAT_PRESETS = new HashMap<>();
    private static boolean betterCombatAvailable = false;

    public record BetterCombatPresets(
            ResourceLocation attackPreset,
            ResourceLocation defensePreset
    ) {}

    static {
        initializeBetterCombatPresets();
        checkBetterCombatAvailability();
    }

    private static void initializeBetterCombatPresets() {
        BETTER_COMBAT_PRESETS.put(
                ResourceLocation.fromNamespaceAndPath("mainmod", "gondor_sword"),
                new BetterCombatPresets(
                        ResourceLocation.fromNamespaceAndPath("mainmod", "sword_attack_preset"),
                        ResourceLocation.fromNamespaceAndPath("mainmod", "sword_defense_preset")
                )
        );

        BETTER_COMBAT_PRESETS.put(
                ResourceLocation.fromNamespaceAndPath("mainmod", "gondor_axe"),
                new BetterCombatPresets(
                        ResourceLocation.fromNamespaceAndPath("mainmod", "battle_axe_attack_preset"),
                        ResourceLocation.fromNamespaceAndPath("mainmod", "battle_axe_defense_preset")
                )
        );

        LOGGER.info("Initialized {} Better Combat preset mappings", BETTER_COMBAT_PRESETS.size());
    }

    private static void checkBetterCombatAvailability() {
        try {
            Class.forName("net.bettercombat.BetterCombat");
            betterCombatAvailable = true;
            LOGGER.info("Better Combat detected - enabling integration");
        } catch (ClassNotFoundException e) {
            betterCombatAvailable = false;
            LOGGER.info("Better Combat not found - using internal preset system");
        }
    }

    /**
     * ИСПРАВЛЕНО: Главный метод смены пресета (только для Better Combat)
     */
    public static void changeWeaponPreset(Player player, ItemStack weapon, StanceType newStance) {
        if (weapon.isEmpty()) {
            LOGGER.debug("No weapon to change preset for");
            return;
        }

        // ИСПРАВЛЕНО: Только Better Combat интеграция, без вмешательства в анимации
        if (betterCombatAvailable && player instanceof ServerPlayer serverPlayer) {
            if (setBetterCombatPreset(weapon, newStance, serverPlayer)) {
                LOGGER.debug("Applied Better Combat preset for stance: {}", newStance);
            }
        }

        // УБРАНО: applyInternalPreset() - анимации обрабатываются отдельно
        LOGGER.debug("Weapon preset change completed for stance: {}", newStance);
    }

    private static boolean setBetterCombatPreset(ItemStack stack, StanceType stance, ServerPlayer player) {
        ResourceLocation itemId = BuiltInRegistries.ITEM.getKey(stack.getItem());
        BetterCombatPresets presets = BETTER_COMBAT_PRESETS.get(itemId);

        if (presets == null) {
            LOGGER.debug("No Better Combat presets found for item: {}", itemId);
            return false;
        }

        try {
            Registry<DataComponentType<?>> registry = player.registryAccess()
                    .registryOrThrow(Registries.DATA_COMPONENT_TYPE);

            @SuppressWarnings("unchecked")
            DataComponentType<ResourceLocation> presetComponent =
                    (DataComponentType<ResourceLocation>) registry.get(
                            ResourceLocation.fromNamespaceAndPath("bettercombat", "preset_id")
                    );

            if (presetComponent != null) {
                ResourceLocation presetId = stance == StanceType.ATTACK
                        ? presets.attackPreset()
                        : presets.defensePreset();

                stack.set(presetComponent, presetId);
                LOGGER.info("Applied Better Combat preset: {} for stance: {}", presetId, stance);
                return true;
            }
        } catch (Exception e) {
            LOGGER.warn("Failed to apply Better Combat preset: {}", e.getMessage());
        }

        return false;
    }

    public static boolean isWeaponSupported(ItemStack stack) {
        if (stack.isEmpty()) return false;

        if (betterCombatAvailable) {
            ResourceLocation itemId = BuiltInRegistries.ITEM.getKey(stack.getItem());
            if (BETTER_COMBAT_PRESETS.containsKey(itemId)) {
                return true;
            }
        }

        return WeaponPresetRegistry.hasPresetForWeapon(stack);
    }

    public static Optional<String> getCurrentPreset(ItemStack stack, StanceType stance) {
        if (betterCombatAvailable) {
            ResourceLocation itemId = BuiltInRegistries.ITEM.getKey(stack.getItem());
            BetterCombatPresets presets = BETTER_COMBAT_PRESETS.get(itemId);
            if (presets != null) {
                ResourceLocation presetId = stance == StanceType.ATTACK
                        ? presets.attackPreset()
                        : presets.defensePreset();
                return Optional.of(presetId.toString());
            }
        }

        ResourceLocation animation = WeaponPresetRegistry.getAnimationForWeapon(stack, stance);
        return animation != null ? Optional.of(animation.toString()) : Optional.empty();
    }

    public static boolean isBetterCombatAvailable() {
        return betterCombatAvailable;
    }

    public static String getDebugInfo() {
        return String.format("WeaponPresetManager: Better Combat %s, %d BC presets, %d internal presets",
                betterCombatAvailable ? "available" : "not available",
                BETTER_COMBAT_PRESETS.size(),
                WeaponPresetRegistry.getPresetCount()
        );
    }
}
