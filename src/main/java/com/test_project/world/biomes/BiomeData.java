package com.test_project.world.biomes;

import com.test_project.world.biomes.caves.CaveType;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;

/**
 * ИСПРАВЛЕНО: Адаптирован под NeoForge 1.21.1 API
 * Основной класс для хранения всех данных биома включая slope map, слои блоков, цвета и тип пещер
 */
public class BiomeData {
    private final ResourceKey<Biome> biomeRegistryKey;
    private final SlopeMap slopeMap;
    private final BlocksLayeringData blocksLayeringData;
    private final BiomeColorsDTO colors;
    private final CaveType caveType;
    private final BiomeGenerationData generationData;

    public BiomeData(ResourceKey<Biome> biomeRegistryKey, SlopeMap slopeMap,
                     BlocksLayeringData blocksLayeringData, BiomeColorsDTO colors) {
        this(biomeRegistryKey, slopeMap, blocksLayeringData, colors, CaveType.NORMAL, BiomeGenerationData.DEFAULT);
    }

    public BiomeData(ResourceKey<Biome> biomeRegistryKey, SlopeMap slopeMap,
                     BlocksLayeringData blocksLayeringData, BiomeColorsDTO colors, CaveType caveType) {
        this(biomeRegistryKey, slopeMap, blocksLayeringData, colors, caveType, BiomeGenerationData.DEFAULT);
    }

    public BiomeData(ResourceKey<Biome> biomeRegistryKey, SlopeMap slopeMap,
                     BlocksLayeringData blocksLayeringData, BiomeColorsDTO colors,
                     CaveType caveType, BiomeGenerationData generationData) {
        this.biomeRegistryKey = validateNotNull(biomeRegistryKey, "biomeRegistryKey");
        this.slopeMap = validateNotNull(slopeMap, "slopeMap");
        this.blocksLayeringData = validateNotNull(blocksLayeringData, "blocksLayeringData");
        this.colors = validateNotNull(colors, "colors");
        this.caveType = validateNotNull(caveType, "caveType");
        this.generationData = validateNotNull(generationData, "generationData");
    }

    // Getters
    public ResourceKey<Biome> getBiomeRegistryKey() { return biomeRegistryKey; }
    public SlopeMap getSlopeMap() { return slopeMap; }
    public BlocksLayeringData getBlocksLayeringData() { return blocksLayeringData; }
    public BiomeColorsDTO getColors() { return colors; }
    public CaveType getCaveType() { return caveType; }
    public BiomeGenerationData getGenerationData() { return generationData; }

    // Builder methods для создания модифицированных копий
    public BiomeData withColors(BiomeColorsDTO colors) {
        return new BiomeData(biomeRegistryKey, slopeMap, blocksLayeringData, colors, caveType, generationData);
    }

    public BiomeData withCaveType(CaveType caveType) {
        return new BiomeData(biomeRegistryKey, slopeMap, blocksLayeringData, colors, caveType, generationData);
    }

    public BiomeData withGenerationData(BiomeGenerationData generationData) {
        return new BiomeData(biomeRegistryKey, slopeMap, blocksLayeringData, colors, caveType, generationData);
    }

    public BiomeData withSlopeMap(SlopeMap slopeMap) {
        return new BiomeData(biomeRegistryKey, slopeMap, blocksLayeringData, colors, caveType, generationData);
    }

    public BiomeData withLayeringData(BlocksLayeringData layeringData) {
        return new BiomeData(biomeRegistryKey, slopeMap, layeringData, colors, caveType, generationData);
    }

    // Утилитные методы
    public boolean isWaterBiome() {
        return colors.waterColor() != BiomeColorsDTO.DEFAULT_WATER ||
                colors.waterFogColor() != BiomeColorsDTO.DEFAULT_WATER_FOG;
    }

    public boolean isMountainBiome() {
        return caveType.isMountainType() || generationData.heightModifier() > 1.0f;
    }

    public boolean isSpecialBiome() {
        return caveType.isSpecialType() || generationData != BiomeGenerationData.DEFAULT;
    }

    // Валидация
    private static <T> T validateNotNull(T object, String paramName) {
        if (object == null) {
            throw new IllegalArgumentException(paramName + " cannot be null");
        }
        return object;
    }

    @Override
    public String toString() {
        return "BiomeData{" +
                "biomeKey=" + biomeRegistryKey.location() +
                ", caveType=" + caveType +
                ", heightModifier=" + generationData.heightModifier() +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BiomeData biomeData = (BiomeData) o;
        return biomeRegistryKey.equals(biomeData.biomeRegistryKey);
    }

    @Override
    public int hashCode() {
        return biomeRegistryKey.hashCode();
    }
}
