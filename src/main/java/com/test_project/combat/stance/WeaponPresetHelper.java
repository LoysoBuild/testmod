package com.test_project.combat.stance;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerPlayer;
import java.util.Map;

public class WeaponPresetHelper {

    public record WeaponPresets(ResourceLocation attackPreset, ResourceLocation defensePreset) {}

    // ИСПРАВЛЕНИЕ: Добавлена правильная типизация Map
    private static final Map<ResourceLocation, WeaponPresets> WEAPON_PRESETS = Map.of(
            ResourceLocation.fromNamespaceAndPath("mainmod", "gondor_sword"), new WeaponPresets(
                    ResourceLocation.fromNamespaceAndPath("mainmod", "sword_attack_preset"),
                    ResourceLocation.fromNamespaceAndPath("mainmod", "sword_defense_preset")
            ),
            ResourceLocation.fromNamespaceAndPath("mainmod", "gondor_axe"), new WeaponPresets(
                    ResourceLocation.fromNamespaceAndPath("mainmod", "battle_axe_attack_preset"),
                    ResourceLocation.fromNamespaceAndPath("mainmod", "battle_axe_defense_preset")
            )
    );

    public static void setPresetForItem(ItemStack stack, StanceType stance, ServerPlayer player) {
        if (stack.isEmpty()) return;

        ResourceLocation itemId = BuiltInRegistries.ITEM.getKey(stack.getItem());
        WeaponPresets presets = WEAPON_PRESETS.get(itemId);
        if (presets == null) return;

        try {
            Registry<DataComponentType<?>> registry = player.registryAccess().registryOrThrow(Registries.DATA_COMPONENT_TYPE);
            @SuppressWarnings("unchecked")
            DataComponentType<ResourceLocation> presetComponent =
                    (DataComponentType<ResourceLocation>) registry.get(ResourceLocation.fromNamespaceAndPath("bettercombat", "preset_id"));

            if (presetComponent != null) {
                ResourceLocation presetId = stance == StanceType.ATTACK
                        ? presets.attackPreset()
                        : presets.defensePreset();
                stack.set(presetComponent, presetId);

                System.out.println("[SERVER] Set weapon preset: " + presetId + " for stance: " + stance);
            }
        } catch (Exception e) {
            // Логирование ошибки, если Better Combat не установлен
            System.err.println("Failed to set weapon preset: " + e.getMessage());
        }
    }

    // Проверка, поддерживается ли оружие
    public static boolean isWeaponSupported(ItemStack stack) {
        if (stack.isEmpty()) return false;
        ResourceLocation itemId = BuiltInRegistries.ITEM.getKey(stack.getItem());
        return WEAPON_PRESETS.containsKey(itemId);
    }
}
