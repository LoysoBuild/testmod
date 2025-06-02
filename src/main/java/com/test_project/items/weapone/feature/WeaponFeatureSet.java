package com.test_project.items.weapone.feature;

import java.util.ArrayList;
import java.util.List;

public class WeaponFeatureSet {
    private final List<WeaponFeature> features = new ArrayList<>();

    public void addFeature(WeaponFeature feature) {
        features.add(feature);
    }

    public List<WeaponFeature> getAll() {
        return features;
    }

    /**
     * Добавляет особенность по строковому ID (создаёт базовую реализацию).
     */
    public WeaponFeatureSet add(String featureId) {
        WeaponFeature feature = createFeatureById(featureId);
        if (feature != null) {
            addFeature(feature);
        }
        return this; // Для цепочки вызовов
    }

    /**
     * Создаёт особенность по ID.
     */
    private WeaponFeature createFeatureById(String featureId) {
        return switch (featureId.toLowerCase()) {
            case "counterattack", "counter_attack" -> new CounterAttackFeature();
            case "lifesteal", "life_steal" -> new LifeStealFeature();
            case "lifesteal_minor" -> LifeStealFeature.createMinor();
            case "lifesteal_major" -> LifeStealFeature.createMajor();
            case "lifesteal_pvp" -> LifeStealFeature.createPvPOnly();
            default -> {
                System.err.println("Unknown weapon feature: " + featureId);
                yield null;
            }
        };
    }


    /**
     * Проверяет, есть ли особенность с данным ID.
     */
    public boolean has(String featureId) {
        return features.stream()
                .anyMatch(feature -> feature.getId().equals(featureId));
    }

    /**
     * Получает особенность по ID.
     */
    public WeaponFeature get(String featureId) {
        return features.stream()
                .filter(feature -> feature.getId().equals(featureId))
                .findFirst()
                .orElse(null);
    }

    /**
     * Удаляет особенность по ID.
     */
    public boolean remove(String featureId) {
        return features.removeIf(feature -> feature.getId().equals(featureId));
    }

    /**
     * Проверяет, пуст ли набор особенностей.
     */
    public boolean isEmpty() {
        return features.isEmpty();
    }

    /**
     * Возвращает количество особенностей.
     */
    public int size() {
        return features.size();
    }
}
