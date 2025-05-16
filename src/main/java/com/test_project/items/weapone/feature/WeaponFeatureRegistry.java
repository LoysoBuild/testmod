package com.test_project.items.weapone.feature;

import java.util.HashMap;
import java.util.Map;

public class WeaponFeatureRegistry {
    private static final Map<String, WeaponFeature> FEATURES = new HashMap<>();

    public static void register(WeaponFeature feature) {
        FEATURES.put(feature.getId(), feature);
    }

    public static WeaponFeature get(String id) {
        return FEATURES.get(id);
    }

    // Статическая инициализация (добавьте все нужные особенности)
    static {
        register(new CounterAttackFeature());

        // register(new ParryFeature());
        // register(new StunFeature());
    }
}
