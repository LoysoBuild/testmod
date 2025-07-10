package com.test_project.world.biomes;

public record BiomeGenerationData(
        float biomeWeight,
        float noiseScale,
        float heightModifier,
        float temperatureModifier,
        float humidityModifier,
        float erosionWeight,
        float continentalWeight,
        float ridgeWeight
) {
    public static final BiomeGenerationData DEFAULT = new BiomeGenerationData(
            1.0f, 1.0f, 0.0f, 0.0f, 0.0f, 1.0f, 1.0f, 1.0f
    );

    // Предустановленные конфигурации
    public static final BiomeGenerationData MOUNTAIN = new BiomeGenerationData(
            0.8f, 0.6f, 2.0f, -0.2f, 0.0f, 0.3f, 1.2f, 1.5f
    );

    public static final BiomeGenerationData OCEAN = new BiomeGenerationData(
            1.2f, 1.0f, -1.8f, 0.0f, 0.3f, 1.0f, 0.8f, 0.2f
    );

    public static final BiomeGenerationData PLAINS = new BiomeGenerationData(
            1.0f, 0.8f, 0.0f, 0.1f, 0.0f, 1.0f, 1.0f, 0.8f
    );

    public static final BiomeGenerationData DESERT = new BiomeGenerationData(
            0.7f, 0.9f, 0.1f, 0.8f, -0.5f, 1.2f, 1.0f, 0.9f
    );

    public static final BiomeGenerationData FOREST = new BiomeGenerationData(
            0.9f, 0.7f, 0.2f, 0.0f, 0.4f, 0.8f, 1.0f, 0.7f
    );

    // Валидация в компактном конструкторе
    public BiomeGenerationData {
        validateRange(biomeWeight, 0.0f, 2.0f, "biomeWeight");
        validateRange(noiseScale, 0.1f, 2.0f, "noiseScale");
        validateRange(heightModifier, -3.0f, 3.0f, "heightModifier");
        validateRange(temperatureModifier, -1.0f, 1.0f, "temperatureModifier");
        validateRange(humidityModifier, -1.0f, 1.0f, "humidityModifier");
        validateRange(erosionWeight, 0.0f, 2.0f, "erosionWeight");
        validateRange(continentalWeight, 0.0f, 2.0f, "continentalWeight");
        validateRange(ridgeWeight, 0.0f, 2.0f, "ridgeWeight");
    }

    /**
     * Создает новый BiomeGenerationData с измененным весом биома
     */
    public BiomeGenerationData withWeight(float weight) {
        return new BiomeGenerationData(weight, noiseScale, heightModifier, temperatureModifier,
                humidityModifier, erosionWeight, continentalWeight, ridgeWeight);
    }

    /**
     * Создает новый BiomeGenerationData с измененным модификатором высоты
     */
    public BiomeGenerationData withHeightModifier(float height) {
        return new BiomeGenerationData(biomeWeight, noiseScale, height, temperatureModifier,
                humidityModifier, erosionWeight, continentalWeight, ridgeWeight);
    }

    /**
     * Создает новый BiomeGenerationData с измененным масштабом шума
     */
    public BiomeGenerationData withNoiseScale(float scale) {
        return new BiomeGenerationData(biomeWeight, scale, heightModifier, temperatureModifier,
                humidityModifier, erosionWeight, continentalWeight, ridgeWeight);
    }

    /**
     * Создает новый BiomeGenerationData с измененными модификаторами климата
     */
    public BiomeGenerationData withClimateModifiers(float temperature, float humidity) {
        return new BiomeGenerationData(biomeWeight, noiseScale, heightModifier, temperature,
                humidity, erosionWeight, continentalWeight, ridgeWeight);
    }

    /**
     * Создает новый BiomeGenerationData с измененными весами ландшафта
     */
    public BiomeGenerationData withLandscapeWeights(float erosion, float continental, float ridge) {
        return new BiomeGenerationData(biomeWeight, noiseScale, heightModifier, temperatureModifier,
                humidityModifier, erosion, continental, ridge);
    }

    /**
     * Применяет модификатор ко всем весам
     */
    public BiomeGenerationData multiplyWeights(float multiplier) {
        return new BiomeGenerationData(
                biomeWeight * multiplier,
                noiseScale,
                heightModifier,
                temperatureModifier,
                humidityModifier,
                erosionWeight * multiplier,
                continentalWeight * multiplier,
                ridgeWeight * multiplier
        );
    }

    /**
     * Проверяет, является ли биом горным
     */
    public boolean isMountainous() {
        return heightModifier > 1.0f && ridgeWeight > 1.0f;
    }

    /**
     * Проверяет, является ли биом водным
     */
    public boolean isAquatic() {
        return heightModifier < -1.0f;
    }

    /**
     * Проверяет, является ли биом равнинным
     */
    public boolean isFlat() {
        return Math.abs(heightModifier) < 0.5f && erosionWeight > 0.8f;
    }

    /**
     * Получает категорию ландшафта
     */
    public LandscapeCategory getLandscapeCategory() {
        if (heightModifier > 1.5f) return LandscapeCategory.MOUNTAIN;
        if (heightModifier < -1.0f) return LandscapeCategory.OCEAN;
        if (heightModifier < -0.5f) return LandscapeCategory.RIVER;
        if (Math.abs(heightModifier) < 0.3f) return LandscapeCategory.PLAINS;
        return LandscapeCategory.HILLS;
    }

    /**
     * Получает общий вес влияния на генерацию
     */
    public float getTotalInfluence() {
        return (biomeWeight + erosionWeight + continentalWeight + ridgeWeight) / 4.0f;
    }

    /**
     * Валидация диапазона значений
     */
    private static void validateRange(float value, float min, float max, String paramName) {
        if (value < min || value > max) {
            throw new IllegalArgumentException(
                    paramName + " must be between " + min + " and " + max + ", got: " + value
            );
        }
    }

    /**
     * Создает BiomeGenerationData для конкретного типа ландшафта
     */
    public static BiomeGenerationData forLandscape(LandscapeCategory category) {
        return switch (category) {
            case MOUNTAIN -> MOUNTAIN;
            case OCEAN -> OCEAN;
            case PLAINS -> PLAINS;
            case HILLS -> DEFAULT.withHeightModifier(0.5f);
            case RIVER -> OCEAN.withHeightModifier(-0.8f);
        };
    }

    /**
     * Категории ландшафта
     */
    public enum LandscapeCategory {
        MOUNTAIN,
        HILLS,
        PLAINS,
        RIVER,
        OCEAN
    }
}
