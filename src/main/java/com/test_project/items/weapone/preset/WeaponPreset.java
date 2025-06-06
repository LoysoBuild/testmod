package com.test_project.items.weapone.preset;

import com.test_project.combat.stance.StanceType;
import net.minecraft.resources.ResourceLocation;
import java.util.Map;
import java.util.HashMap;

public class WeaponPreset {
    private final String presetId;
    private final Map<StanceType, ResourceLocation> stanceAnimations;
    private final Map<StanceType, Float> stanceDamageModifiers;
    private final Map<StanceType, Float> stanceSpeedModifiers;

    public WeaponPreset(String presetId) {
        this.presetId = presetId;
        this.stanceAnimations = new HashMap<>();
        this.stanceDamageModifiers = new HashMap<>();
        this.stanceSpeedModifiers = new HashMap<>();
    }

    public WeaponPreset setAnimation(StanceType stance, ResourceLocation animation) {
        this.stanceAnimations.put(stance, animation);
        return this;
    }

    public WeaponPreset setDamageModifier(StanceType stance, float modifier) {
        this.stanceDamageModifiers.put(stance, modifier);
        return this;
    }

    public WeaponPreset setSpeedModifier(StanceType stance, float modifier) {
        this.stanceSpeedModifiers.put(stance, modifier);
        return this;
    }

    public ResourceLocation getAnimation(StanceType stance) {
        return stanceAnimations.get(stance);
    }

    public float getDamageModifier(StanceType stance) {
        return stanceDamageModifiers.getOrDefault(stance, 1.0f);
    }

    public float getSpeedModifier(StanceType stance) {
        return stanceSpeedModifiers.getOrDefault(stance, 1.0f);
    }

    public String getPresetId() {
        return presetId;
    }

    public boolean hasAnimation(StanceType stance) {
        return stanceAnimations.containsKey(stance);
    }
}
