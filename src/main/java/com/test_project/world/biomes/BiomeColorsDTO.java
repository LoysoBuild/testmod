package com.test_project.world.biomes;

public record BiomeColorsDTO(
        int skyColor,
        int fogColor,
        int waterColor,
        int waterFogColor,
        int grassColor,
        int foliageColor
) {
    // Базовые цветовые константы
    public static final int DEFAULT_SKY = 0x78A7FF;        // 7907327
    public static final int DEFAULT_FOG = 0xC0D8FF;        // 12638463
    public static final int DEFAULT_WATER = 0x3F76E4;      // 4159204
    public static final int DEFAULT_WATER_FOG = 0x050533;  // 329011
    public static final int DEFAULT_GRASS = 0x91BD59;      // 9551193
    public static final int DEFAULT_FOLIAGE = 0x77AB2F;    // 7842607

    // Универсальные цветовые схемы
    public static final int TEMPERATE_SKY = 0x78A7FF;      // Умеренное небо
    public static final int HIGHLAND_SKY = 0x7D93FF;       // Горное небо
    public static final int WATER_SKY = 0x7B85FF;          // Водное небо
    public static final int ARID_SKY = 0x6E85FF;           // Засушливое небо
    public static final int COLD_SKY = 0x87CEEB;           // Холодное небо
    public static final int TROPICAL_SKY = 0x87CEEB;       // Тропическое небо

    public static final int ARID_FOG = 0xC4D1FF;           // Засушливый туман
    public static final int COLD_FOG = 0xE0E6FF;           // Холодный туман
    public static final int TROPICAL_FOG = 0xB8E0D2;       // Тропический туман

    // Водные цвета
    public static final int SHORE_WATER = 0x3F6124;        // Прибрежная вода
    public static final int COAST_WATER = 0x3F6044;        // Береговая вода
    public static final int OCEAN_WATER = 0x3C5416;        // Океанская вода
    public static final int OCEAN_WATER_FOG = 0x233C53;    // Океанский туман

    // Растительность
    public static final int ARID_GRASS = 0xBDB25F;         // Засушливая трава
    public static final int ARID_FOLIAGE = 0x9E8C54;       // Засушливая листва
    public static final int COLD_GRASS = 0x60A17B;         // Холодная трава
    public static final int COLD_FOLIAGE = 0x4F7942;       // Холодная листва
    public static final int TROPICAL_GRASS = 0x4F7942;     // Тропическая трава
    public static final int TROPICAL_FOLIAGE = 0x174A14;   // Тропическая листва

    public static final BiomeColorsDTO DEFAULT = new BiomeColorsDTO(
            DEFAULT_SKY, DEFAULT_FOG, DEFAULT_WATER,
            DEFAULT_WATER_FOG, DEFAULT_GRASS, DEFAULT_FOLIAGE
    );

    // Валидация в компактном конструкторе
    public BiomeColorsDTO {
        validateColorRange(skyColor, "skyColor");
        validateColorRange(fogColor, "fogColor");
        validateColorRange(waterColor, "waterColor");
        validateColorRange(waterFogColor, "waterFogColor");
        validateColorRange(grassColor, "grassColor");
        validateColorRange(foliageColor, "foliageColor");
    }

    /**
     * Создает новый BiomeColorsDTO с измененным цветом неба
     */
    public BiomeColorsDTO withSkyColor(int color) {
        return new BiomeColorsDTO(color, fogColor, waterColor, waterFogColor, grassColor, foliageColor);
    }

    /**
     * Создает новый BiomeColorsDTO с измененным цветом воды
     */
    public BiomeColorsDTO withWaterColor(int color) {
        return new BiomeColorsDTO(skyColor, fogColor, color, waterFogColor, grassColor, foliageColor);
    }

    /**
     * Создает новый BiomeColorsDTO с измененными цветами травы и листвы
     */
    public BiomeColorsDTO withVegetationColors(int grassColor, int foliageColor) {
        return new BiomeColorsDTO(skyColor, fogColor, waterColor, waterFogColor, grassColor, foliageColor);
    }

    /**
     * Создает новый BiomeColorsDTO с измененными цветами неба и тумана
     */
    public BiomeColorsDTO withAtmosphereColors(int skyColor, int fogColor) {
        return new BiomeColorsDTO(skyColor, fogColor, waterColor, waterFogColor, grassColor, foliageColor);
    }

    /**
     * Создает BiomeColorsDTO для водных биомов
     */
    public static BiomeColorsDTO forWaterBiome(int waterColor, int waterFogColor) {
        return new BiomeColorsDTO(WATER_SKY, DEFAULT_FOG, waterColor, waterFogColor, DEFAULT_GRASS, DEFAULT_FOLIAGE);
    }

    /**
     * Создает BiomeColorsDTO для горных биомов
     */
    public static BiomeColorsDTO forMountainBiome(int grassColor, int foliageColor) {
        return new BiomeColorsDTO(HIGHLAND_SKY, DEFAULT_FOG, DEFAULT_WATER, DEFAULT_WATER_FOG, grassColor, foliageColor);
    }

    /**
     * Создает BiomeColorsDTO для засушливых биомов
     */
    public static BiomeColorsDTO forAridBiome(int grassColor, int foliageColor) {
        return new BiomeColorsDTO(ARID_SKY, ARID_FOG, DEFAULT_WATER, DEFAULT_WATER_FOG, grassColor, foliageColor);
    }

    /**
     * Создает BiomeColorsDTO для холодных биомов
     */
    public static BiomeColorsDTO forColdBiome(int grassColor, int foliageColor) {
        return new BiomeColorsDTO(COLD_SKY, COLD_FOG, DEFAULT_WATER, DEFAULT_WATER_FOG, grassColor, foliageColor);
    }

    /**
     * Создает BiomeColorsDTO для тропических биомов
     */
    public static BiomeColorsDTO forTropicalBiome(int grassColor, int foliageColor) {
        return new BiomeColorsDTO(TROPICAL_SKY, TROPICAL_FOG, DEFAULT_WATER, DEFAULT_WATER_FOG, grassColor, foliageColor);
    }

    /**
     * Создает BiomeColorsDTO для океанских биомов
     */
    public static BiomeColorsDTO forOceanBiome() {
        return new BiomeColorsDTO(WATER_SKY, DEFAULT_FOG, OCEAN_WATER, OCEAN_WATER_FOG, DEFAULT_GRASS, DEFAULT_FOLIAGE);
    }

    /**
     * Создает BiomeColorsDTO для прибрежных биомов
     */
    public static BiomeColorsDTO forCoastalBiome() {
        return new BiomeColorsDTO(WATER_SKY, DEFAULT_FOG, SHORE_WATER, DEFAULT_WATER_FOG, DEFAULT_GRASS, DEFAULT_FOLIAGE);
    }

    /**
     * Создает BiomeColorsDTO с готовыми цветовыми схемами
     */
    public static BiomeColorsDTO forColorScheme(ColorScheme scheme) {
        return switch (scheme) {
            case TEMPERATE -> DEFAULT;
            case ARID -> forAridBiome(ARID_GRASS, ARID_FOLIAGE);
            case COLD -> forColdBiome(COLD_GRASS, COLD_FOLIAGE);
            case TROPICAL -> forTropicalBiome(TROPICAL_GRASS, TROPICAL_FOLIAGE);
            case MOUNTAIN -> forMountainBiome(DEFAULT_GRASS, DEFAULT_FOLIAGE);
            case OCEAN -> forOceanBiome();
            case COASTAL -> forCoastalBiome();
        };
    }

    /**
     * Валидация цветового значения
     */
    private static void validateColorRange(int color, String paramName) {
        if (color < 0 || color > 0xFFFFFF) {
            throw new IllegalArgumentException(paramName + " must be a valid RGB color (0x000000 to 0xFFFFFF)");
        }
    }

    /**
     * Проверяет схожесть цветовых схем
     */
    public boolean isSimilarTo(BiomeColorsDTO other, int tolerance) {
        return Math.abs(skyColor - other.skyColor) <= tolerance &&
                Math.abs(waterColor - other.waterColor) <= tolerance &&
                Math.abs(grassColor - other.grassColor) <= tolerance;
    }

    /**
     * Возвращает доминирующий цвет биома (для отладки)
     */
    public int getDominantColor() {
        return (skyColor + waterColor + grassColor) / 3;
    }

    /**
     * Предустановленные цветовые схемы
     */
    public enum ColorScheme {
        TEMPERATE,
        ARID,
        COLD,
        TROPICAL,
        MOUNTAIN,
        OCEAN,
        COASTAL
    }
}
