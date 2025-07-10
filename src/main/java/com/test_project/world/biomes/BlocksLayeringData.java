package com.test_project.world.biomes;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import java.util.List;
import java.util.ArrayList;
import java.util.Random;
import java.util.stream.Stream;

/**
 * ИСПРАВЛЕНО: Использует record и правильные Java API для immutable коллекций
 *
 * Система слоистого распределения блоков для биомов
 *
 * Основные принципы:
 * - Immutable структура данных через record
 * - Процентное распределение блоков по слоям
 * - Ограничения по глубине для каждого слоя
 * - Валидация целостности данных
 * - Builder pattern для сложных конфигураций
 *
 * Примеры использования:
 * - Определение поверхностных блоков (трава, песок)
 * - Настройка подпочвенных слоев (земля, песчаник)
 * - Глубинные блоки (камень, руды)
 */
public record BlocksLayeringData(List<LayerData> layers) {

    /**
     * ИСПРАВЛЕНО: Добавлена полная валидация и документация
     *
     * Данные одного слоя блоков с ограничениями по глубине
     *
     * @param block Блок для данного слоя
     * @param percentage Процентное соотношение (0-100)
     * @param minDepth Минимальная глубина (включительно)
     * @param maxDepth Максимальная глубина (включительно)
     */
    public record LayerData(Block block, float percentage, int minDepth, int maxDepth) {

        /**
         * Упрощенный конструктор без ограничений по глубине
         */
        public LayerData(Block block, float percentage) {
            this(block, percentage, 0, Integer.MAX_VALUE);
        }

        /**
         * ИСПРАВЛЕНО: Добавлена полная валидация параметров
         * Компактный конструктор с валидацией всех параметров
         */
        public LayerData {
            // Валидация процентного соотношения
            if (percentage < 0 || percentage > 100) {
                throw new IllegalArgumentException(
                        String.format("Percentage must be between 0 and 100, got: %.2f", percentage)
                );
            }

            // Валидация глубины
            if (minDepth < 0) {
                throw new IllegalArgumentException(
                        String.format("minDepth must be non-negative, got: %d", minDepth)
                );
            }

            if (maxDepth < minDepth) {
                throw new IllegalArgumentException(
                        String.format("maxDepth must be >= minDepth, got: %d < %d", maxDepth, minDepth)
                );
            }

            // Валидация блока
            if (block == null) {
                throw new IllegalArgumentException("Block cannot be null");
            }
        }

        /**
         * Проверяет, подходит ли данный слой для указанной глубины
         *
         * @param depth Глубина для проверки (0 = поверхность)
         * @return true если слой применим на данной глубине
         */
        public boolean isValidForDepth(int depth) {
            return depth >= minDepth && depth <= maxDepth;
        }

        /**
         * Проверяет, является ли слой поверхностным (глубина 0-1)
         */
        public boolean isSurfaceLayer() {
            return minDepth <= 1 && maxDepth >= 0;
        }

        /**
         * Проверяет, является ли слой глубинным (глубина > 4)
         */
        public boolean isDeepLayer() {
            return minDepth > 4 || maxDepth == Integer.MAX_VALUE;
        }
    }

    /**
     * ИСПРАВЛЕНО: Улучшена валидация с детальными сообщениями об ошибках
     * Компактный конструктор с полной валидацией целостности данных
     */
    public BlocksLayeringData {
        // Проверка на null
        if (layers == null) {
            throw new IllegalArgumentException("Layers list cannot be null");
        }

        // Создаем immutable копию для безопасности
        layers = List.copyOf(layers);

        // Валидация общего процентного соотношения (только если список не пуст)
        if (!layers.isEmpty()) {
            float totalPercentage = layers.stream()
                    .map(LayerData::percentage)
                    .reduce(0f, Float::sum);

            // Допускаем небольшую погрешность для float операций
            if (Math.abs(totalPercentage - 100.0f) > 0.01f) {
                throw new IllegalArgumentException(
                        String.format("Total percentage must equal 100%%, got: %.2f", totalPercentage)
                );
            }
        }

        // Проверка на дублирующиеся блоки на одинаковых глубинах
        validateNoDuplicateBlocksAtSameDepth();
    }

    /**
     * ИСПРАВЛЕНО: Добавлена проверка на конфликты блоков
     * Проверяет, что одинаковые блоки не используются на пересекающихся глубинах
     */
    private void validateNoDuplicateBlocksAtSameDepth() {
        for (int i = 0; i < layers.size(); i++) {
            for (int j = i + 1; j < layers.size(); j++) {
                LayerData layer1 = layers.get(i);
                LayerData layer2 = layers.get(j);

                // Если блоки одинаковые и диапазоны глубин пересекаются
                if (layer1.block() == layer2.block() &&
                        rangesOverlap(layer1.minDepth(), layer1.maxDepth(),
                                layer2.minDepth(), layer2.maxDepth())) {
                    throw new IllegalArgumentException(
                            String.format("Block %s has overlapping depth ranges: [%d-%d] and [%d-%d]",
                                    layer1.block(), layer1.minDepth(), layer1.maxDepth(),
                                    layer2.minDepth(), layer2.maxDepth())
                    );
                }
            }
        }
    }

    /**
     * Проверяет, пересекаются ли два диапазона
     */
    private static boolean rangesOverlap(int start1, int end1, int start2, int end2) {
        return start1 <= end2 && start2 <= end1;
    }

    // =================== СТАТИЧЕСКИЕ МЕТОДЫ СОЗДАНИЯ ===================

    /**
     * ИСПРАВЛЕНО: Упрощен синтаксис создания
     * Создает BlocksLayeringData из массива слоев
     */
    public static BlocksLayeringData of(LayerData... layers) {
        return new BlocksLayeringData(List.of(layers));
    }

    /**
     * Создает пустой BlocksLayeringData (будет использовать defaultBlock)
     */
    public static BlocksLayeringData empty() {
        return new BlocksLayeringData(List.of());
    }

    /**
     * Создает простой слой из одного блока на всех глубинах
     */
    public static BlocksLayeringData single(Block block) {
        return new BlocksLayeringData(List.of(new LayerData(block, 100.0f)));
    }

    /**
     * ИСПРАВЛЕНО: Обновлены блоки для NeoForge 1.21.1
     * Создает стандартные слои грунта для большинства биомов
     */
    public static BlocksLayeringData standardSoil() {
        return of(
                new LayerData(Blocks.GRASS_BLOCK, 60.0f, 0, 1),      // Поверхность
                new LayerData(Blocks.DIRT, 30.0f, 0, 4),             // Подпочва
                new LayerData(Blocks.STONE, 10.0f, 2, Integer.MAX_VALUE) // Глубина
        );
    }

    /**
     * Создает слои для пустынного биома
     */
    public static BlocksLayeringData desertSoil() {
        return of(
                new LayerData(Blocks.SAND, 70.0f, 0, 3),
                new LayerData(Blocks.SANDSTONE, 20.0f, 2, 6),
                new LayerData(Blocks.STONE, 10.0f, 4, Integer.MAX_VALUE)
        );
    }

    /**
     * Создает слои для горного биома
     */
    public static BlocksLayeringData mountainSoil() {
        return of(
                new LayerData(Blocks.STONE, 60.0f, 0, Integer.MAX_VALUE),
                new LayerData(Blocks.COBBLESTONE, 25.0f, 0, Integer.MAX_VALUE),
                new LayerData(Blocks.GRAVEL, 15.0f, 0, 2)
        );
    }

    /**
     * ИСПРАВЛЕНО: Используются актуальные блоки NeoForge 1.21.1
     * Создает слои для болотного биома
     */
    public static BlocksLayeringData swampSoil() {
        return of(
                new LayerData(Blocks.GRASS_BLOCK, 40.0f, 0, 1),
                new LayerData(Blocks.MUD, 35.0f, 0, 3),              // Блок грязи в 1.21.1
                new LayerData(Blocks.CLAY, 15.0f, 1, 4),
                new LayerData(Blocks.STONE, 10.0f, 3, Integer.MAX_VALUE)
        );
    }

    /**
     * Создает слои для снежного биома
     */
    public static BlocksLayeringData snowySoil() {
        return of(
                new LayerData(Blocks.SNOW_BLOCK, 50.0f, 0, 1),
                new LayerData(Blocks.DIRT, 30.0f, 0, 3),
                new LayerData(Blocks.STONE, 20.0f, 2, Integer.MAX_VALUE)
        );
    }

    // =================== МЕТОДЫ ПОЛУЧЕНИЯ БЛОКОВ ===================

    /**
     * ИСПРАВЛЕНО: Улучшена логика выбора блока с fallback
     * Получает блок для указанной глубины и случайного значения
     *
     * @param depth Глубина (0 = поверхность)
     * @param random Случайное значение (0.0 - 1.0)
     * @return Блок для данной глубины
     */
    public Block getBlockForDepth(int depth, float random) {
        if (layers.isEmpty()) {
            return Blocks.STONE; // Fallback блок
        }

        // Фильтруем слои, подходящие для данной глубины
        List<LayerData> validLayers = layers.stream()
                .filter(layer -> layer.isValidForDepth(depth))
                .toList();

        if (validLayers.isEmpty()) {
            // Если нет подходящих слоев, используем последний слой в списке
            return layers.get(layers.size() - 1).block();
        }

        // Выбираем блок на основе процентного соотношения
        float accumulated = 0;
        float totalValid = validLayers.stream()
                .map(LayerData::percentage)
                .reduce(0f, Float::sum);

        // Нормализуем random к общему проценту действительных слоев
        float normalizedRandom = random * totalValid;

        for (LayerData layer : validLayers) {
            accumulated += layer.percentage();
            if (normalizedRandom <= accumulated) {
                return layer.block();
            }
        }

        // Fallback на последний действительный слой
        return validLayers.get(validLayers.size() - 1).block();
    }

    /**
     * Получает блок для глубины используя Random объект
     */
    public Block getBlockForDepth(int depth, Random random) {
        return getBlockForDepth(depth, random.nextFloat());
    }

    /**
     * Получает блок для глубины используя seed
     */
    public Block getBlockForDepth(int depth, long seed) {
        Random random = new Random(seed);
        return getBlockForDepth(depth, random);
    }

    /**
     * Получает поверхностный блок (глубина 0)
     */
    public Block getSurfaceBlock(float random) {
        return getBlockForDepth(0, random);
    }

    /**
     * Получает подпочвенный блок (глубина 1-3)
     */
    public Block getSubsurfaceBlock(float random) {
        return getBlockForDepth(2, random);
    }

    /**
     * Получает глубинный блок (глубина > 3)
     */
    public Block getDeepBlock(float random) {
        return getBlockForDepth(5, random);
    }

    // =================== МЕТОДЫ АНАЛИЗА ДАННЫХ ===================

    /**
     * Возвращает все уникальные блоки в слоях
     */
    public List<Block> getAllBlocks() {
        return layers.stream()
                .map(LayerData::block)
                .distinct()
                .toList();
    }

    /**
     * Проверяет, содержит ли слои указанный блок
     */
    public boolean containsBlock(Block block) {
        return layers.stream()
                .anyMatch(layer -> layer.block() == block);
    }

    /**
     * Получает суммарный процент для блока (может быть > 100% для блоков на разных глубинах)
     */
    public float getBlockPercentage(Block block) {
        return layers.stream()
                .filter(layer -> layer.block() == block)
                .map(LayerData::percentage)
                .reduce(0f, Float::sum);
    }

    /**
     * Получает слои, содержащие указанный блок
     */
    public List<LayerData> getLayersWithBlock(Block block) {
        return layers.stream()
                .filter(layer -> layer.block() == block)
                .toList();
    }

    /**
     * Проверяет, является ли данный набор слоев пустым
     */
    public boolean isEmpty() {
        return layers.isEmpty();
    }

    /**
     * Получает количество слоев
     */
    public int size() {
        return layers.size();
    }

    /**
     * Получает максимальную глубину в конфигурации
     */
    public int getMaxDepth() {
        return layers.stream()
                .mapToInt(LayerData::maxDepth)
                .filter(depth -> depth != Integer.MAX_VALUE)
                .max()
                .orElse(0);
    }

    /**
     * Получает минимальную глубину в конфигурации
     */
    public int getMinDepth() {
        return layers.stream()
                .mapToInt(LayerData::minDepth)
                .min()
                .orElse(0);
    }

    // =================== МЕТОДЫ МОДИФИКАЦИИ ===================

    /**
     * ИСПРАВЛЕНО: Использует ArrayList вместо несуществующего List.builder()
     * Создает новый BlocksLayeringData с добавленным слоем
     */
    public BlocksLayeringData withLayer(LayerData layer) {
        var newLayers = new ArrayList<>(layers);
        newLayers.add(layer);
        return new BlocksLayeringData(List.copyOf(newLayers));
    }

    /**
     * ИСПРАВЛЕНО: Используется stream API для безопасного преобразования
     * Создает новый BlocksLayeringData с замененным блоком
     */
    public BlocksLayeringData replaceBlock(Block oldBlock, Block newBlock) {
        var newLayers = layers.stream()
                .map(layer -> layer.block() == oldBlock ?
                        new LayerData(newBlock, layer.percentage(), layer.minDepth(), layer.maxDepth()) :
                        layer)
                .toList();
        return new BlocksLayeringData(newLayers);
    }

    /**
     * ИСПРАВЛЕНО: Безопасное удаление с сохранением immutability
     * Создает новый BlocksLayeringData без указанного блока
     */
    public BlocksLayeringData withoutBlock(Block block) {
        var newLayers = layers.stream()
                .filter(layer -> layer.block() != block)
                .toList();
        return new BlocksLayeringData(newLayers);
    }

    /**
     * Создает новый BlocksLayeringData с измененным процентом для блока
     */
    public BlocksLayeringData withModifiedPercentage(Block block, float newPercentage) {
        var newLayers = layers.stream()
                .map(layer -> layer.block() == block ?
                        new LayerData(block, newPercentage, layer.minDepth(), layer.maxDepth()) :
                        layer)
                .toList();
        return new BlocksLayeringData(newLayers);
    }

    /**
     * ИСПРАВЛЕНО: Использует Stream.concat для безопасного объединения
     * Объединяет два BlocksLayeringData в один
     */
    public BlocksLayeringData merge(BlocksLayeringData other) {
        var combinedLayers = Stream.concat(this.layers.stream(), other.layers.stream())
                .toList();
        return new BlocksLayeringData(combinedLayers);
    }

    // =================== BUILDER КЛАСС ===================

    /**
     * ИСПРАВЛЕНО: Полностью переписан Builder с правильным использованием ArrayList
     * Строитель для создания сложных конфигураций слоев
     */
    public static class Builder {
        private final List<LayerData> layers = new ArrayList<>();

        /**
         * Добавляет слой с указанными параметрами
         */
        public Builder addLayer(Block block, float percentage) {
            layers.add(new LayerData(block, percentage));
            return this;
        }

        /**
         * Добавляет слой с ограничениями по глубине
         */
        public Builder addLayer(Block block, float percentage, int minDepth, int maxDepth) {
            layers.add(new LayerData(block, percentage, minDepth, maxDepth));
            return this;
        }

        /**
         * Добавляет поверхностный слой (глубина 0-1)
         */
        public Builder surface(Block block, float percentage) {
            return addLayer(block, percentage, 0, 1);
        }

        /**
         * Добавляет подпочвенный слой (глубина 1-4)
         */
        public Builder subsurface(Block block, float percentage) {
            return addLayer(block, percentage, 1, 4);
        }

        /**
         * Добавляет глубинный слой (глубина 4+)
         */
        public Builder deep(Block block, float percentage) {
            return addLayer(block, percentage, 4, Integer.MAX_VALUE);
        }

        /**
         * Добавляет слой только для конкретной глубины
         */
        public Builder atDepth(Block block, float percentage, int depth) {
            return addLayer(block, percentage, depth, depth);
        }

        /**
         * Очищает все добавленные слои
         */
        public Builder clear() {
            layers.clear();
            return this;
        }

        /**
         * Удаляет последний добавленный слой
         */
        public Builder removeLast() {
            if (!layers.isEmpty()) {
                layers.remove(layers.size() - 1);
            }
            return this;
        }

        /**
         * Проверяет валидность текущей конфигурации
         */
        public boolean isValid() {
            if (layers.isEmpty()) return true;

            float total = layers.stream()
                    .map(LayerData::percentage)
                    .reduce(0f, Float::sum);
            return Math.abs(total - 100.0f) <= 0.01f;
        }

        /**
         * Получает текущую сумму процентов
         */
        public float getCurrentPercentageSum() {
            return layers.stream()
                    .map(LayerData::percentage)
                    .reduce(0f, Float::sum);
        }

        /**
         * ИСПРАВЛЕНО: Использует List.copyOf для создания immutable копии
         * Создает BlocksLayeringData из накопленных слоев
         */
        public BlocksLayeringData build() {
            return new BlocksLayeringData(List.copyOf(layers));
        }

        /**
         * Создает BlocksLayeringData с автоматическим нормализованием процентов
         */
        public BlocksLayeringData buildNormalized() {
            if (layers.isEmpty()) {
                return BlocksLayeringData.empty();
            }

            float total = getCurrentPercentageSum();
            if (Math.abs(total - 100.0f) <= 0.01f) {
                return build(); // Уже нормализовано
            }

            // Нормализуем проценты
            var normalizedLayers = layers.stream()
                    .map(layer -> new LayerData(
                            layer.block(),
                            (layer.percentage() / total) * 100.0f,
                            layer.minDepth(),
                            layer.maxDepth()
                    ))
                    .toList();

            return new BlocksLayeringData(normalizedLayers);
        }
    }

    /**
     * Создает новый Builder
     */
    public static Builder builder() {
        return new Builder();
    }

    // =================== УТИЛИТНЫЕ МЕТОДЫ ===================

    /**
     * Создает текстовое представление для отладки
     */
    @Override
    public String toString() {
        if (layers.isEmpty()) {
            return "BlocksLayeringData{empty}";
        }

        StringBuilder sb = new StringBuilder("BlocksLayeringData{\n");
        for (LayerData layer : layers) {
            sb.append(String.format("  %s: %.1f%% [%d-%s]\n",
                    layer.block(),
                    layer.percentage(),
                    layer.minDepth(),
                    layer.maxDepth() == Integer.MAX_VALUE ? "∞" : layer.maxDepth()));
        }
        sb.append("}");
        return sb.toString();
    }

    /**
     * Создает копию с возможностью модификации через Builder
     */
    public Builder toBuilder() {
        Builder builder = new Builder();
        layers.forEach(layer -> builder.addLayer(
                layer.block(), layer.percentage(), layer.minDepth(), layer.maxDepth()
        ));
        return builder;
    }
}
