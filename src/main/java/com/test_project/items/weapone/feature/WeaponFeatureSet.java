package com.test_project.items.weapone.feature;


import java.util.HashSet;
import java.util.Set;

public class WeaponFeatureSet {
    private final Set<WeaponFeature> features = new HashSet<>();

    public WeaponFeatureSet add(String featureId) {
        WeaponFeature f = WeaponFeatureRegistry.get(featureId);
        if (f != null) features.add(f);
        return this;
    }

    public WeaponFeatureSet remove(String featureId) {
        features.removeIf(f -> f.getId().equals(featureId));
        return this;
    }

    public boolean has(String featureId) {
        return features.stream().anyMatch(f -> f.getId().equals(featureId));
    }

    public Set<WeaponFeature> getAll() {
        return features;
    }
}
