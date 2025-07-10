package com.test_project.world.biomes;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import java.util.NavigableMap;
import java.util.TreeMap;
import java.util.Map;
import java.util.Optional;

/**
 * ИСПРАВЛЕНО: Использует современные коллекции Java и NeoForge 1.21.1 блоки
 * Класс для маппинга углов наклонов к блокам поверхности
 */
public class SlopeMap {
    private final NavigableMap<Float, Block> slopeData = new TreeMap<>();
    private Block defaultBlock = Blocks.STONE;

    /**
     * Создает пустой SlopeMap с блоком по умолчанию
     */
    public SlopeMap() {
        this(Blocks.STONE);
    }

    /**
     * Создает SlopeMap с указанным блоком по умолчанию
     */
    public SlopeMap(Block defaultBlock) {
        this.defaultBlock = validateBlock(defaultBlock);
    }

    /**
     * Добавляет маппинг наклона к блоку
     */
    public SlopeMap addSlopeData(float angle, Block block) {
        validateAngle(angle);
        validateBlock(block);
        slopeData.put(angle, block);
        return this;
    }

    /**
     * Добавляет несколько маппингов наклонов
     */
    public SlopeMap addSlopeData(Map<Float, Block> slopes) {
        slopes.forEach(this::addSlopeData);
        return this;
    }

    /**
     * Получает блок для указанного наклона
     */
    public Block getBlockForSlope(float angle) {
        validateAngle(angle);
        var entry = slopeData.floorEntry(angle);
        return entry != null ? entry.getValue() : defaultBlock;
    }

    /**
     * Получает блок для указанного наклона с fallback
     */
    public Block getBlockForSlope(float angle, Block fallback) {
        validateAngle(angle);
        var entry = slopeData.floorEntry(angle);
        return entry != null ? entry.getValue() : (fallback != null ? fallback : defaultBlock);
    }

    /**
     * Проверяет, есть ли маппинг для указанного наклона
     */
    public boolean hasMapping(float angle) {
        validateAngle(angle);
        return slopeData.containsKey(angle);
    }

    /**
     * Получает ближайший наклон к указанному
     */
    public Optional<Float> getClosestSlope(float angle) {
        validateAngle(angle);

        var floor = slopeData.floorKey(angle);
        var ceiling = slopeData.ceilingKey(angle);

        if (floor == null && ceiling == null) {
            return Optional.empty();
        }

        if (floor == null) return Optional.of(ceiling);
        if (ceiling == null) return Optional.of(floor);

        return Optional.of(Math.abs(angle - floor) <= Math.abs(angle - ceiling) ? floor : ceiling);
    }

    /**
     * Получает все наклоны в указанном диапазоне
     */
    public NavigableMap<Float, Block> getSlopesInRange(float minAngle, float maxAngle) {
        validateAngle(minAngle);
        validateAngle(maxAngle);

        if (minAngle > maxAngle) {
            throw new IllegalArgumentException("minAngle must be <= maxAngle");
        }

        return slopeData.subMap(minAngle, true, maxAngle, true);
    }

    /**
     * Удаляет маппинг наклона
     */
    public SlopeMap removeSlope(float angle) {
        validateAngle(angle);
        slopeData.remove(angle);
        return this;
    }

    /**
     * Очищает все маппинги
     */
    public SlopeMap clear() {
        slopeData.clear();
        return this;
    }

    /**
     * Устанавливает блок по умолчанию
     */
    public SlopeMap setDefaultBlock(Block block) {
        this.defaultBlock = validateBlock(block);
        return this;
    }

    /**
     * Получает блок по умолчанию
     */
    public Block getDefaultBlock() {
        return defaultBlock;
    }

    /**
     * Получает копию всех данных наклонов
     */
    public NavigableMap<Float, Block> getSlopeData() {
        return new TreeMap<>(slopeData);
    }

    /**
     * Проверяет, пуст ли SlopeMap
     */
    public boolean isEmpty() {
        return slopeData.isEmpty();
    }

    /**
     * Получает количество маппингов
     */
    public int size() {
        return slopeData.size();
    }

    /**
     * Получает минимальный наклон
     */
    public Optional<Float> getMinSlope() {
        return slopeData.isEmpty() ? Optional.empty() : Optional.of(slopeData.firstKey());
    }

    /**
     * Получает максимальный наклон
     */
    public Optional<Float> getMaxSlope() {
        return slopeData.isEmpty() ? Optional.empty() : Optional.of(slopeData.lastKey());
    }

    /**
     * Создает копию SlopeMap
     */
    public SlopeMap copy() {
        SlopeMap copy = new SlopeMap(defaultBlock);
        copy.slopeData.putAll(this.slopeData);
        return copy;
    }

    /**
     * Объединяет с другим SlopeMap (приоритет у текущего)
     */
    public SlopeMap merge(SlopeMap other) {
        SlopeMap merged = this.copy();
        other.slopeData.forEach(merged.slopeData::putIfAbsent);
        return merged;
    }

    /**
     * Валидация угла наклона
     */
    private static void validateAngle(float angle) {
        if (angle < 0 || angle > 90) {
            throw new IllegalArgumentException("Angle must be between 0 and 90 degrees, got: " + angle);
        }
    }

    /**
     * Валидация блока
     */
    private static Block validateBlock(Block block) {
        if (block == null) {
            throw new IllegalArgumentException("Block cannot be null");
        }
        return block;
    }

    /**
     * Создает стандартный SlopeMap для равнинного биома
     */
    public static SlopeMap forPlains() {
        return new SlopeMap(Blocks.GRASS_BLOCK)
                .addSlopeData(0.0f, Blocks.GRASS_BLOCK)
                .addSlopeData(10.0f, Blocks.DIRT)
                .addSlopeData(30.0f, Blocks.STONE);
    }

    /**
     * Создает стандартный SlopeMap для горного биома
     */
    public static SlopeMap forMountains() {
        return new SlopeMap(Blocks.STONE)
                .addSlopeData(0.0f, Blocks.GRASS_BLOCK)
                .addSlopeData(20.0f, Blocks.STONE)
                .addSlopeData(45.0f, Blocks.COBBLESTONE)
                .addSlopeData(70.0f, Blocks.GRAVEL);
    }

    /**
     * Создает стандартный SlopeMap для пустынного биома
     */
    public static SlopeMap forDesert() {
        return new SlopeMap(Blocks.SAND)
                .addSlopeData(0.0f, Blocks.SAND)
                .addSlopeData(15.0f, Blocks.SANDSTONE)
                .addSlopeData(40.0f, Blocks.STONE);
    }

    /**
     * Создает стандартный SlopeMap для болотного биома
     */
    public static SlopeMap forSwamp() {
        return new SlopeMap(Blocks.MUD)
                .addSlopeData(0.0f, Blocks.MUD)
                .addSlopeData(5.0f, Blocks.CLAY)
                .addSlopeData(20.0f, Blocks.DIRT)
                .addSlopeData(45.0f, Blocks.STONE);
    }

    /**
     * Создает стандартный SlopeMap для снежного биома
     */
    public static SlopeMap forSnowy() {
        return new SlopeMap(Blocks.SNOW_BLOCK)
                .addSlopeData(0.0f, Blocks.SNOW_BLOCK)
                .addSlopeData(10.0f, Blocks.DIRT)
                .addSlopeData(30.0f, Blocks.STONE)
                .addSlopeData(60.0f, Blocks.PACKED_ICE);
    }

    /**
     * Строитель для создания сложных SlopeMap
     */
    public static class Builder {
        private final SlopeMap slopeMap;

        public Builder() {
            this.slopeMap = new SlopeMap();
        }

        public Builder(Block defaultBlock) {
            this.slopeMap = new SlopeMap(defaultBlock);
        }

        public Builder slope(float angle, Block block) {
            slopeMap.addSlopeData(angle, block);
            return this;
        }

        public Builder flat(Block block) {
            return slope(0.0f, block);
        }

        public Builder gentle(Block block) {
            return slope(15.0f, block);
        }

        public Builder moderate(Block block) {
            return slope(30.0f, block);
        }

        public Builder steep(Block block) {
            return slope(60.0f, block);
        }

        public Builder cliff(Block block) {
            return slope(80.0f, block);
        }

        public Builder defaultBlock(Block block) {
            slopeMap.setDefaultBlock(block);
            return this;
        }

        public SlopeMap build() {
            return slopeMap.copy();
        }
    }

    /**
     * Создает новый Builder
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * Создает новый Builder с блоком по умолчанию
     */
    public static Builder builder(Block defaultBlock) {
        return new Builder(defaultBlock);
    }

    @Override
    public String toString() {
        return "SlopeMap{" +
                "slopes=" + slopeData.size() +
                ", default=" + defaultBlock +
                '}';
    }
}
