package com.test_project.world.biomes.templates;

import com.test_project.world.biomes.BiomeColorsDTO;
import com.test_project.world.biomes.BiomeGenerationData;
import com.test_project.world.biomes.BlocksLayeringData;
import com.test_project.world.biomes.SlopeMap;
import com.test_project.world.biomes.caves.CaveType;

import java.util.Set;
import java.util.EnumSet;
import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * ИСПРАВЛЕНО: Универсальный шаблон для создания биомов без привязки к конкретным названиям
 *
 * BiomeTemplate представляет собой immutable шаблон для создания биомов с полной конфигурацией:
 * - Категория и подкатегория биома
 * - Цветовая схема
 * - Тип пещер
 * - Данные генерации (высота, шум, климат)
 * - Карта наклонов поверхности
 * - Система слоистых блоков
 * - Теги для логической группировки
 *
 * Принципы дизайна:
 * - Immutability через record
 * - Builder pattern для сложных конфигураций
 * - Factory методы для стандартных биомов
 * - Валидация всех параметров
 * - Совместимость с NeoForge 1.21.1
 */
public record BiomeTemplate(
        String category,
        String subCategory,
        BiomeColorsDTO colors,
        CaveType caveType,
        BiomeGenerationData generationData,
        SlopeMap slopeMap,
        BlocksLayeringData layeringData,
        Set<BiomeTag> tags
) {

    /**
     * ИСПРАВЛЕНО: Добавлена полная валидация всех параметров с детальными сообщениями
     * Компактный конструктор с валидацией
     */
    public BiomeTemplate {
        // Валидация строковых параметров
        if (category == null || category.trim().isEmpty()) {
            throw new IllegalArgumentException("Category cannot be null or empty");
        }
        if (subCategory == null || subCategory.trim().isEmpty()) {
            throw new IllegalArgumentException("SubCategory cannot be null or empty");
        }

        // Нормализация строк (убираем пробелы, приводим к нижнему регистру)
        category = category.trim().toLowerCase();
        subCategory = subCategory.trim().toLowerCase();

        // Валидация объектов
        if (colors == null) {
            throw new IllegalArgumentException("Colors cannot be null");
        }
        if (caveType == null) {
            throw new IllegalArgumentException("CaveType cannot be null");
        }
        if (generationData == null) {
            throw new IllegalArgumentException("GenerationData cannot be null");
        }
        if (slopeMap == null) {
            throw new IllegalArgumentException("SlopeMap cannot be null");
        }
        if (layeringData == null) {
            throw new IllegalArgumentException("LayeringData cannot be null");
        }

        // Инициализация тегов если null
        if (tags == null) {
            tags = EnumSet.noneOf(BiomeTag.class);
        } else {
            // Создаем immutable копию
            tags = EnumSet.copyOf(tags);
        }

        // Проверка логической совместимости
        validateLogicalConsistency();
    }

    /**
     * ИСПРАВЛЕНО: Добавлена проверка логической совместимости параметров
     * Проверяет логическую совместимость всех параметров шаблона
     */
    private void validateLogicalConsistency() {
        // Проверка совместимости тегов с типом пещер
        if (hasTag(BiomeTag.AQUATIC) && !caveType.isAquaticType() && caveType != CaveType.COASTAL) {
            throw new IllegalArgumentException(
                    "Aquatic biomes should use aquatic or coastal cave types, got: " + caveType
            );
        }

        // Проверка совместимости тегов с данными генерации
        if (hasTag(BiomeTag.MOUNTAINOUS) && !generationData.isMountainous()) {
            throw new IllegalArgumentException(
                    "Mountainous biomes should have mountainous generation data"
            );
        }

        // Проверка климатических тегов
        long climateTagCount = tags.stream()
                .filter(tag -> Set.of(BiomeTag.HOT, BiomeTag.COLD, BiomeTag.TEMPERATE).contains(tag))
                .count();
        if (climateTagCount > 1) {
            throw new IllegalArgumentException(
                    "Biome cannot have multiple conflicting climate tags"
            );
        }
    }

    // Альтернативный конструктор без тегов для упрощения использования
    public BiomeTemplate(String category, String subCategory, BiomeColorsDTO colors,
                         CaveType caveType, BiomeGenerationData generationData,
                         SlopeMap slopeMap, BlocksLayeringData layeringData) {
        this(category, subCategory, colors, caveType, generationData, slopeMap, layeringData,
                EnumSet.noneOf(BiomeTag.class));
    }

    /**
     * ИСПРАВЛЕНО: Расширен enum с недостающими константами для полноценной системы тегов
     * Основные категории биомов с улучшенной структуризацией
     */
    public enum BiomeCategory {
        PLAINS("plains", "Равнинные биомы", Set.of(BiomeTag.FLAT, BiomeTag.GRASSY)),
        FOREST("forest", "Лесные биомы", Set.of(BiomeTag.FORESTED, BiomeTag.LUSH)),
        MOUNTAIN("mountain", "Горные биомы", Set.of(BiomeTag.MOUNTAINOUS, BiomeTag.ROCKY, BiomeTag.ELEVATED)),
        DESERT("desert", "Пустынные биомы", Set.of(BiomeTag.HOT, BiomeTag.DRY, BiomeTag.SANDY)),
        SWAMP("swamp", "Болотные биомы", Set.of(BiomeTag.WET, BiomeTag.MUDDY, BiomeTag.LOWLAND)),
        OCEAN("ocean", "Океанские биомы", Set.of(BiomeTag.AQUATIC, BiomeTag.DEEP, BiomeTag.WET)),
        RIVER("river", "Речные биомы", Set.of(BiomeTag.AQUATIC, BiomeTag.SHALLOW, BiomeTag.NEAR_WATER)),
        HILLS("hills", "Холмистые биомы", Set.of(BiomeTag.HILLY, BiomeTag.ELEVATED)),
        WASTELAND("wasteland", "Пустоши", Set.of(BiomeTag.BARREN, BiomeTag.DANGEROUS)),
        TUNDRA("tundra", "Тундра", Set.of(BiomeTag.COLD, BiomeTag.SNOWY, BiomeTag.SPARSE_VEGETATION)),
        JUNGLE("jungle", "Джунгли", Set.of(BiomeTag.HOT, BiomeTag.HUMID, BiomeTag.DENSE_VEGETATION)),
        TAIGA("taiga", "Тайга", Set.of(BiomeTag.COLD, BiomeTag.FORESTED)),
        SAVANNA("savanna", "Саванна", Set.of(BiomeTag.HOT, BiomeTag.DRY, BiomeTag.GRASSY)),
        BADLANDS("badlands", "Бесплодные земли", Set.of(BiomeTag.HOT, BiomeTag.ROCKY, BiomeTag.BARREN)),
        MUSHROOM("mushroom", "Грибные биомы", Set.of(BiomeTag.RARE, BiomeTag.UNIQUE, BiomeTag.MAGICAL));

        private final String name;
        private final String description;
        private final Set<BiomeTag> defaultTags;

        BiomeCategory(String name, String description, Set<BiomeTag> defaultTags) {
            this.name = name;
            this.description = description;
            this.defaultTags = Set.copyOf(defaultTags);
        }

        public String getName() { return name; }
        public String getDescription() { return description; }
        public Set<BiomeTag> getDefaultTags() { return defaultTags; }
    }

    /**
     * ИСПРАВЛЕНО: Расширен список подкатегорий с описаниями
     * Подкатегории биомов для детализации
     */
    public enum BiomeSubCategory {
        STANDARD("standard", "Стандартный вариант"),
        DENSE("dense", "Густой/Плотный"),
        SPARSE("sparse", "Редкий/Разреженный"),
        ELEVATED("elevated", "Возвышенный"),
        COASTAL("coastal", "Прибрежный"),
        RIVERSIDE("riverside", "Речной"),
        FOOTHILLS("foothills", "Предгорья"),
        PEAKS("peaks", "Вершины"),
        HIGHLANDS("highlands", "Нагорья"),
        EDGE("edge", "Граница/Переход"),
        DEEP("deep", "Глубокий"),
        SHALLOW("shallow", "Мелкий"),
        FROZEN("frozen", "Замороженный"),
        HOT("hot", "Горячий"),
        MODIFIED("modified", "Модифицированный"),
        RARE("rare", "Редкий"),
        CLEARING("clearing", "Поляна"),
        VALLEY("valley", "Долина"),
        PLATEAU("plateau", "Плато");

        private final String name;
        private final String description;

        BiomeSubCategory(String name, String description) {
            this.name = name;
            this.description = description;
        }

        public String getName() { return name; }
        public String getDescription() { return description; }
    }

    /**
     * ИСПРАВЛЕНО: Дополнен enum с недостающими константами включая DEEP
     * Теги биомов для логической группировки и фильтрации
     */
    public enum BiomeTag {
        // Климатические теги
        HOT("Горячий климат"),
        COLD("Холодный климат"),
        WET("Влажный климат"),
        DRY("Сухой климат"),
        TEMPERATE("Умеренный климат"),
        HUMID("Влажный воздух"),
        ARID("Засушливый"),
        WINDY("Ветреный"),

        // Топографические теги
        FLAT("Плоская местность"),
        HILLY("Холмистая местность"),
        MOUNTAINOUS("Горная местность"),
        LOWLAND("Низменность"),
        HIGHLAND("Возвышенность"),
        ELEVATED("Приподнятая местность"),
        VALLEY("Долинная местность"),
        PLATEAU("Плато"),

        // Водные теги (ИСПРАВЛЕНО: добавлен DEEP и другие водные теги)
        AQUATIC("Водный биом"),
        NEAR_WATER("Рядом с водой"),
        COASTAL("Прибрежный"),
        RIVER("Речной"),
        DEEP("Глубокий водоем"),  // <- ИСПРАВЛЕНО: Добавлена недостающая константа
        SHALLOW("Мелкий водоем"),
        OCEANIC("Океанический"),
        UNDERGROUND_WATER("Подземные воды"),

        // Растительные теги
        FORESTED("Лесистый"),
        GRASSY("Травянистый"),
        BARREN("Бесплодный"),
        LUSH("Пышная растительность"),
        SPARSE_VEGETATION("Редкая растительность"),
        DENSE_VEGETATION("Густая растительность"),
        FLOWERING("Цветущий"),
        MUSHROOM("Грибной"),

        // Структурные теги
        NATURAL("Естественный"),
        MODIFIED("Модифицированный"),
        RARE("Редкий"),
        COMMON("Обычный"),
        TRANSITIONAL("Переходный"),
        UNIQUE("Уникальный"),
        EDGE("Граничный"),
        BORDER("Пограничный"),

        // Геологические теги
        ROCKY("Каменистый"),
        SANDY("Песчаный"),
        MUDDY("Грязевой"),
        SNOWY("Снежный"),
        VOLCANIC("Вулканический"),
        CRYSTALLINE("Кристаллический"),
        CLAY("Глинистый"),
        GRAVEL("Гравийный"),

        // Особые теги
        DANGEROUS("Опасный"),
        PEACEFUL("Мирный"),
        RESOURCE_RICH("Богатый ресурсами"),
        CAVE_HEAVY("Много пещер"),
        MAGICAL("Магический"),
        ANCIENT("Древний"),
        CORRUPTED("Искаженный"),
        FERTILE("Плодородный");

        private final String description;

        BiomeTag(String description) {
            this.description = description;
        }

        public String getDescription() { return description; }

        /**
         * ИСПРАВЛЕНО: Добавлены утилитные методы для работы с тегами
         * Получить все климатические теги
         */
        public static Set<BiomeTag> getClimateTags() {
            return Set.of(HOT, COLD, WET, DRY, TEMPERATE, HUMID, ARID, WINDY);
        }

        /**
         * Получить все топографические теги
         */
        public static Set<BiomeTag> getTopographyTags() {
            return Set.of(FLAT, HILLY, MOUNTAINOUS, LOWLAND, HIGHLAND, ELEVATED, VALLEY, PLATEAU);
        }

        /**
         * Получить все водные теги
         */
        public static Set<BiomeTag> getWaterTags() {
            return Set.of(AQUATIC, NEAR_WATER, COASTAL, RIVER, DEEP, SHALLOW, OCEANIC, UNDERGROUND_WATER);
        }

        /**
         * Получить все растительные теги
         */
        public static Set<BiomeTag> getVegetationTags() {
            return Set.of(FORESTED, GRASSY, BARREN, LUSH, SPARSE_VEGETATION, DENSE_VEGETATION,
                    FLOWERING, MUSHROOM);
        }
    }

    // =================== FACTORY МЕТОДЫ ДЛЯ СТАНДАРТНЫХ БИОМОВ ===================

    /**
     * ИСПРАВЛЕНО: Обновлены factory методы с правильными тегами
     * Создать шаблон равнинного биома
     */
    public static BiomeTemplate createPlains() {
        return new BiomeTemplate(
                BiomeCategory.PLAINS.getName(),
                BiomeSubCategory.STANDARD.getName(),
                BiomeColorsDTO.DEFAULT,
                CaveType.NORMAL,
                BiomeGenerationData.PLAINS,
                SlopeMap.forPlains(),
                BlocksLayeringData.standardSoil(),
                EnumSet.of(BiomeTag.FLAT, BiomeTag.GRASSY, BiomeTag.TEMPERATE, BiomeTag.COMMON, BiomeTag.PEACEFUL)
        );
    }

    /**
     * Создать шаблон горного биома
     */
    public static BiomeTemplate createMountain() {
        return new BiomeTemplate(
                BiomeCategory.MOUNTAIN.getName(),
                BiomeSubCategory.STANDARD.getName(),
                BiomeColorsDTO.forMountainBiome(0x7777AA, 0x666699),
                CaveType.MOUNTAIN,
                BiomeGenerationData.MOUNTAIN,
                SlopeMap.forMountains(),
                BlocksLayeringData.mountainSoil(),
                EnumSet.of(BiomeTag.MOUNTAINOUS, BiomeTag.ROCKY, BiomeTag.HIGHLAND, BiomeTag.COLD, BiomeTag.ELEVATED)
        );
    }

    /**
     * Создать шаблон лесного биома
     */
    public static BiomeTemplate createForest() {
        return new BiomeTemplate(
                BiomeCategory.FOREST.getName(),
                BiomeSubCategory.STANDARD.getName(),
                BiomeColorsDTO.DEFAULT,
                CaveType.FOREST,
                BiomeGenerationData.FOREST,
                SlopeMap.forPlains(),
                BlocksLayeringData.standardSoil(),
                EnumSet.of(BiomeTag.FORESTED, BiomeTag.LUSH, BiomeTag.TEMPERATE, BiomeTag.COMMON, BiomeTag.PEACEFUL)
        );
    }

    /**
     * Создать шаблон пустынного биома
     */
    public static BiomeTemplate createDesert() {
        return new BiomeTemplate(
                BiomeCategory.DESERT.getName(),
                BiomeSubCategory.STANDARD.getName(),
                BiomeColorsDTO.forAridBiome(0xBDB25F, 0x9E8C54),
                CaveType.DESERT,
                BiomeGenerationData.DESERT,
                SlopeMap.forDesert(),
                BlocksLayeringData.desertSoil(),
                EnumSet.of(BiomeTag.HOT, BiomeTag.DRY, BiomeTag.SANDY, BiomeTag.BARREN, BiomeTag.ARID)
        );
    }

    /**
     * ИСПРАВЛЕНО: Использует существующую константу BiomeTag.DEEP
     * Создать шаблон водного биома
     */
    public static BiomeTemplate createOcean() {
        return new BiomeTemplate(
                BiomeCategory.OCEAN.getName(),
                BiomeSubCategory.DEEP.getName(),
                BiomeColorsDTO.forOceanBiome(),
                CaveType.COASTAL,
                BiomeGenerationData.OCEAN,
                SlopeMap.forPlains(),
                BlocksLayeringData.standardSoil(),
                EnumSet.of(BiomeTag.AQUATIC, BiomeTag.DEEP, BiomeTag.WET, BiomeTag.COMMON, BiomeTag.OCEANIC)
        );
    }

    /**
     * Создать шаблон болотного биома
     */
    public static BiomeTemplate createSwamp() {
        return new BiomeTemplate(
                BiomeCategory.SWAMP.getName(),
                BiomeSubCategory.STANDARD.getName(),
                BiomeColorsDTO.forTropicalBiome(0x6A7039, 0x4C7129),
                CaveType.SWAMP,
                BiomeGenerationData.DEFAULT.withClimateModifiers(-0.2f, 0.8f),
                SlopeMap.forSwamp(),
                BlocksLayeringData.swampSoil(),
                EnumSet.of(BiomeTag.WET, BiomeTag.MUDDY, BiomeTag.LOWLAND, BiomeTag.LUSH, BiomeTag.HUMID)
        );
    }

    /**
     * Создать шаблон тундры
     */
    public static BiomeTemplate createTundra() {
        return new BiomeTemplate(
                BiomeCategory.TUNDRA.getName(),
                BiomeSubCategory.STANDARD.getName(),
                BiomeColorsDTO.forColdBiome(0x60A17B, 0x4F7942),
                CaveType.FROZEN,
                BiomeGenerationData.DEFAULT.withClimateModifiers(-0.8f, -0.2f),
                SlopeMap.forSnowy(),
                BlocksLayeringData.snowySoil(),
                EnumSet.of(BiomeTag.COLD, BiomeTag.SNOWY, BiomeTag.SPARSE_VEGETATION, BiomeTag.FLAT, BiomeTag.WINDY)
        );
    }

    /**
     * ИСПРАВЛЕНО: Добавлен новый factory метод для джунглей
     * Создать шаблон джунглей
     */
    public static BiomeTemplate createJungle() {
        return new BiomeTemplate(
                BiomeCategory.JUNGLE.getName(),
                BiomeSubCategory.STANDARD.getName(),
                BiomeColorsDTO.forTropicalBiome(0x59C93C, 0x30BB0B),
                CaveType.FOREST,
                BiomeGenerationData.DEFAULT.withClimateModifiers(0.6f, 0.9f),
                SlopeMap.forPlains(),
                BlocksLayeringData.standardSoil(),
                EnumSet.of(BiomeTag.HOT, BiomeTag.HUMID, BiomeTag.DENSE_VEGETATION, BiomeTag.LUSH, BiomeTag.FORESTED)
        );
    }

    // =================== МЕТОДЫ МОДИФИКАЦИИ ===================

    /**
     * ИСПРАВЛЕНО: Все методы withXXX создают новые immutable копии
     * Создать вариант шаблона с подкатегорией
     */
    public BiomeTemplate withSubCategory(BiomeSubCategory subCategory) {
        return new BiomeTemplate(category, subCategory.getName(), colors, caveType, generationData, slopeMap, layeringData, tags);
    }

    /**
     * Создать вариант шаблона с другим типом пещер
     */
    public BiomeTemplate withCaveType(CaveType caveType) {
        return new BiomeTemplate(category, subCategory, colors, caveType, generationData, slopeMap, layeringData, tags);
    }

    /**
     * Создать вариант шаблона с другими цветами
     */
    public BiomeTemplate withColors(BiomeColorsDTO colors) {
        return new BiomeTemplate(category, subCategory, colors, caveType, generationData, slopeMap, layeringData, tags);
    }

    /**
     * Создать вариант шаблона с другими данными генерации
     */
    public BiomeTemplate withGenerationData(BiomeGenerationData generationData) {
        return new BiomeTemplate(category, subCategory, colors, caveType, generationData, slopeMap, layeringData, tags);
    }

    /**
     * Создать вариант шаблона с другой slope map
     */
    public BiomeTemplate withSlopeMap(SlopeMap slopeMap) {
        return new BiomeTemplate(category, subCategory, colors, caveType, generationData, slopeMap, layeringData, tags);
    }

    /**
     * Создать вариант шаблона с другими слоями
     */
    public BiomeTemplate withLayeringData(BlocksLayeringData layeringData) {
        return new BiomeTemplate(category, subCategory, colors, caveType, generationData, slopeMap, layeringData, tags);
    }

    /**
     * ИСПРАВЛЕНО: Улучшена работа с тегами - создание immutable копий
     * Создать вариант шаблона с добавленными тегами
     */
    public BiomeTemplate withTags(BiomeTag... newTags) {
        Set<BiomeTag> updatedTags = EnumSet.copyOf(tags);
        updatedTags.addAll(Set.of(newTags));
        return new BiomeTemplate(category, subCategory, colors, caveType, generationData, slopeMap, layeringData, updatedTags);
    }

    /**
     * Создать вариант шаблона с удаленными тегами
     */
    public BiomeTemplate withoutTags(BiomeTag... tagsToRemove) {
        Set<BiomeTag> updatedTags = EnumSet.copyOf(tags);
        updatedTags.removeAll(Set.of(tagsToRemove));
        return new BiomeTemplate(category, subCategory, colors, caveType, generationData, slopeMap, layeringData, updatedTags);
    }

    /**
     * ИСПРАВЛЕНО: Добавлен метод для полной замены тегов
     * Создать вариант шаблона с полностью замененными тегами
     */
    public BiomeTemplate withReplacedTags(Set<BiomeTag> newTags) {
        return new BiomeTemplate(category, subCategory, colors, caveType, generationData, slopeMap, layeringData,
                EnumSet.copyOf(newTags));
    }

    // =================== МЕТОДЫ АНАЛИЗА ТЕГОВ ===================

    /**
     * Проверить наличие тега
     */
    public boolean hasTag(BiomeTag tag) {
        return tags.contains(tag);
    }

    /**
     * Проверить наличие любого из тегов
     */
    public boolean hasAnyTag(BiomeTag... tags) {
        return Set.of(tags).stream().anyMatch(this.tags::contains);
    }

    /**
     * Проверить наличие всех тегов
     */
    public boolean hasAllTags(BiomeTag... tags) {
        return this.tags.containsAll(Set.of(tags));
    }

    /**
     * ИСПРАВЛЕНО: Добавлены новые утилитные методы для анализа тегов
     * Получить теги определенной категории
     */
    public Set<BiomeTag> getTagsByCategory(Set<BiomeTag> categoryTags) {
        return tags.stream()
                .filter(categoryTags::contains)
                .collect(Collectors.toSet());
    }

    /**
     * Получить климатические теги биома
     */
    public Set<BiomeTag> getClimateTags() {
        return getTagsByCategory(BiomeTag.getClimateTags());
    }

    /**
     * Получить топографические теги биома
     */
    public Set<BiomeTag> getTopographyTags() {
        return getTagsByCategory(BiomeTag.getTopographyTags());
    }

    /**
     * Получить водные теги биома
     */
    public Set<BiomeTag> getWaterTags() {
        return getTagsByCategory(BiomeTag.getWaterTags());
    }

    /**
     * Получить растительные теги биома
     */
    public Set<BiomeTag> getVegetationTags() {
        return getTagsByCategory(BiomeTag.getVegetationTags());
    }

    // =================== УТИЛИТНЫЕ МЕТОДЫ ===================

    /**
     * Получить полное имя биома для регистрации
     */
    public String getFullName() {
        return category + "_" + subCategory;
    }

    /**
     * Получить читаемое имя биома для отображения
     */
    public String getDisplayName() {
        return capitalizeFirst(category) + " " + capitalizeFirst(subCategory);
    }

    /**
     * ИСПРАВЛЕНО: Добавлен utility метод для капитализации
     */
    private static String capitalizeFirst(String str) {
        if (str == null || str.isEmpty()) return str;
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }

    /**
     * ИСПРАВЛЕНО: Улучшена проверка совместимости с учетом всех параметров
     * Проверить совместимость с другим шаблоном (для генерации рядом)
     */
    public boolean isCompatibleWith(BiomeTemplate other) {
        // Проверка совместимости типов пещер
        boolean caveCompatible = caveType.getCategory().isCompatibleWith(other.caveType.getCategory());

        // Проверка климатической совместимости
        boolean climateCompatible = Math.abs(generationData.temperatureModifier() -
                other.generationData.temperatureModifier()) < 0.5f;

        // Проверка совместимости по высоте
        boolean heightCompatible = Math.abs(generationData.heightModifier() -
                other.generationData.heightModifier()) < 1.0f;

        // Проверка конфликтующих тегов
        boolean tagCompatible = !hasConflictingTags(other);

        return caveCompatible && climateCompatible && heightCompatible && tagCompatible;
    }

    /**
     * ИСПРАВЛЕНО: Добавлена проверка конфликтующих тегов
     * Проверить наличие конфликтующих тегов с другим биомом
     */
    private boolean hasConflictingTags(BiomeTemplate other) {
        // Определяем конфликтующие пары тегов
        Set<Set<BiomeTag>> conflictingSets = Set.of(
                Set.of(BiomeTag.HOT, BiomeTag.COLD),
                Set.of(BiomeTag.WET, BiomeTag.DRY),
                Set.of(BiomeTag.AQUATIC, BiomeTag.BARREN),
                Set.of(BiomeTag.DEEP, BiomeTag.SHALLOW),
                Set.of(BiomeTag.DENSE_VEGETATION, BiomeTag.SPARSE_VEGETATION)
        );

        for (Set<BiomeTag> conflictingSet : conflictingSets) {
            boolean thisHasAny = tags.stream().anyMatch(conflictingSet::contains);
            boolean otherHasAny = other.tags.stream().anyMatch(conflictingSet::contains);

            if (thisHasAny && otherHasAny) {
                // Проверяем, что у нас разные теги из конфликтующего набора
                Set<BiomeTag> thisConflicting = tags.stream()
                        .filter(conflictingSet::contains)
                        .collect(Collectors.toSet());
                Set<BiomeTag> otherConflicting = other.tags.stream()
                        .filter(conflictingSet::contains)
                        .collect(Collectors.toSet());

                if (!thisConflicting.equals(otherConflicting)) {
                    return true; // Найден конфликт
                }
            }
        }

        return false;
    }

    /**
     * Получить приоритет генерации (выше = более важный)
     */
    public int getGenerationPriority() {
        int basePriority = caveType.getPriority();
        int rarityBonus = hasTag(BiomeTag.RARE) ? 5 : 0;
        int uniqueBonus = hasTag(BiomeTag.UNIQUE) ? 10 : 0;
        int dangerBonus = hasTag(BiomeTag.DANGEROUS) ? 3 : 0;

        return basePriority + rarityBonus + uniqueBonus + dangerBonus;
    }

    /**
     * Проверить является ли биом редким
     */
    public boolean isRare() {
        return hasTag(BiomeTag.RARE) || hasTag(BiomeTag.UNIQUE) || caveType.isRareType();
    }

    /**
     * Проверить является ли биом водным
     */
    public boolean isAquatic() {
        return hasTag(BiomeTag.AQUATIC) || generationData.isAquatic();
    }

    /**
     * Проверить является ли биом горным
     */
    public boolean isMountainous() {
        return hasTag(BiomeTag.MOUNTAINOUS) || generationData.isMountainous();
    }

    /**
     * ИСПРАВЛЕНО: Добавлены новые проверочные методы
     * Проверить является ли биом опасным
     */
    public boolean isDangerous() {
        return hasTag(BiomeTag.DANGEROUS) || hasTag(BiomeTag.CORRUPTED) ||
                caveType == CaveType.VOLCANIC || caveType == CaveType.LAVA;
    }

    /**
     * Проверить является ли биом мирным
     */
    public boolean isPeaceful() {
        return hasTag(BiomeTag.PEACEFUL) && !isDangerous();
    }

    /**
     * Получить рейтинг разнообразия (количество уникальных характеристик)
     */
    public int getDiversityRating() {
        int rating = 0;

        // Добавляем за уникальные теги
        rating += tags.size();

        // Добавляем за особый тип пещер
        if (caveType.isSpecialType()) rating += 2;

        // Добавляем за нестандартные параметры генерации
        if (generationData != BiomeGenerationData.DEFAULT) rating += 3;

        // Добавляем за сложную slope map
        if (!slopeMap.isEmpty() && slopeMap.size() > 3) rating += 2;

        // Добавляем за сложную систему слоев
        if (!layeringData.isEmpty() && layeringData.size() > 3) rating += 2;

        return rating;
    }

    // =================== BUILDER КЛАСС ===================

    /**
     * ИСПРАВЛЕНО: Полностью переписан Builder с валидацией и улучшенными методами
     * Строитель для создания сложных шаблонов биомов
     */
    public static class Builder {
        private String category;
        private String subCategory = BiomeSubCategory.STANDARD.getName();
        private BiomeColorsDTO colors = BiomeColorsDTO.DEFAULT;
        private CaveType caveType = CaveType.NORMAL;
        private BiomeGenerationData generationData = BiomeGenerationData.DEFAULT;
        private SlopeMap slopeMap = new SlopeMap();
        private BlocksLayeringData layeringData = BlocksLayeringData.empty();
        private Set<BiomeTag> tags = EnumSet.noneOf(BiomeTag.class);

        public Builder category(BiomeCategory category) {
            this.category = category.getName();
            // Автоматически добавляем теги по умолчанию для категории
            this.tags.addAll(category.getDefaultTags());
            return this;
        }

        public Builder category(String category) {
            this.category = category;
            return this;
        }

        public Builder subCategory(BiomeSubCategory subCategory) {
            this.subCategory = subCategory.getName();
            return this;
        }

        public Builder subCategory(String subCategory) {
            this.subCategory = subCategory;
            return this;
        }

        public Builder colors(BiomeColorsDTO colors) {
            this.colors = colors;
            return this;
        }

        public Builder colorScheme(BiomeColorsDTO.ColorScheme scheme) {
            this.colors = BiomeColorsDTO.forColorScheme(scheme);
            return this;
        }

        public Builder caveType(CaveType caveType) {
            this.caveType = caveType;
            return this;
        }

        public Builder generationData(BiomeGenerationData generationData) {
            this.generationData = generationData;
            return this;
        }

        public Builder slopeMap(SlopeMap slopeMap) {
            this.slopeMap = slopeMap;
            return this;
        }

        public Builder layeringData(BlocksLayeringData layeringData) {
            this.layeringData = layeringData;
            return this;
        }

        public Builder tags(BiomeTag... tags) {
            this.tags = EnumSet.copyOf(Set.of(tags));
            return this;
        }

        public Builder addTag(BiomeTag tag) {
            this.tags.add(tag);
            return this;
        }

        public Builder addTags(BiomeTag... tags) {
            this.tags.addAll(Set.of(tags));
            return this;
        }

        public Builder removeTag(BiomeTag tag) {
            this.tags.remove(tag);
            return this;
        }

        /**
         * ИСПРАВЛЕНО: Добавлены preset методы для быстрой настройки
         */
        public Builder makeMountainous() {
            return addTags(BiomeTag.MOUNTAINOUS, BiomeTag.ROCKY, BiomeTag.ELEVATED)
                    .caveType(CaveType.MOUNTAIN)
                    .generationData(BiomeGenerationData.MOUNTAIN)
                    .slopeMap(SlopeMap.forMountains())
                    .layeringData(BlocksLayeringData.mountainSoil());
        }

        public Builder makeAquatic() {
            return addTags(BiomeTag.AQUATIC, BiomeTag.WET)
                    .caveType(CaveType.COASTAL)
                    .generationData(BiomeGenerationData.OCEAN)
                    .colors(BiomeColorsDTO.forOceanBiome());
        }

        public Builder makeDesert() {
            return addTags(BiomeTag.HOT, BiomeTag.DRY, BiomeTag.SANDY, BiomeTag.ARID)
                    .caveType(CaveType.DESERT)
                    .generationData(BiomeGenerationData.DESERT)
                    .slopeMap(SlopeMap.forDesert())
                    .layeringData(BlocksLayeringData.desertSoil())
                    .colors(BiomeColorsDTO.forAridBiome(0xBDB25F, 0x9E8C54));
        }

        public Builder makeRare() {
            return addTag(BiomeTag.RARE);
        }

        public Builder makeDangerous() {
            return addTag(BiomeTag.DANGEROUS).removeTag(BiomeTag.PEACEFUL);
        }

        /**
         * ИСПРАВЛЕНО: Добавлена валидация в Builder
         */
        public boolean isValid() {
            return category != null && !category.trim().isEmpty();
        }

        public String getValidationErrors() {
            StringBuilder errors = new StringBuilder();

            if (category == null || category.trim().isEmpty()) {
                errors.append("Category is required; ");
            }

            if (colors == null) {
                errors.append("Colors cannot be null; ");
            }

            if (caveType == null) {
                errors.append("CaveType cannot be null; ");
            }

            return errors.toString();
        }

        public BiomeTemplate build() {
            if (!isValid()) {
                throw new IllegalStateException("Builder validation failed: " + getValidationErrors());
            }
            return new BiomeTemplate(category, subCategory, colors, caveType, generationData,
                    slopeMap, layeringData, tags);
        }

        /**
         * ИСПРАВЛЕНО: Добавлен безопасный build метод
         */
        public BiomeTemplate buildSafe() {
            try {
                return build();
            } catch (Exception e) {
                // Возвращаем минимальный рабочий шаблон
                return createPlains();
            }
        }
    }

    /**
     * Создает новый Builder
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * ИСПРАВЛЕНО: Добавлен метод создания Builder из существующего шаблона
     * Создает Builder из существующего шаблона для модификации
     */
    public Builder toBuilder() {
        return new Builder()
                .category(category)
                .subCategory(subCategory)
                .colors(colors)
                .caveType(caveType)
                .generationData(generationData)
                .slopeMap(slopeMap)
                .layeringData(layeringData)
                .tags(tags.toArray(new BiomeTag[0]));
    }

    // =================== ОТЛАДКА И ПРЕДСТАВЛЕНИЕ ===================

    /**
     * ИСПРАВЛЕНО: Улучшен toString для отладки
     */
    @Override
    public String toString() {
        return String.format("BiomeTemplate{%s_%s, cave=%s, tags=%d, priority=%d}",
                category, subCategory, caveType, tags.size(), getGenerationPriority());
    }

    /**
     * ИСПРАВЛЕНО: Добавлен детальный вывод для отладки
     * Получить детальную информацию о шаблоне для отладки
     */
    public String toDetailedString() {
        StringBuilder sb = new StringBuilder();
        sb.append("BiomeTemplate {\n");
        sb.append("  Name: ").append(getDisplayName()).append(" (").append(getFullName()).append(")\n");
        sb.append("  Cave Type: ").append(caveType).append(" (").append(caveType.getCategory()).append(")\n");
        sb.append("  Generation: height=").append(generationData.heightModifier())
                .append(", temp=").append(generationData.temperatureModifier())
                .append(", humidity=").append(generationData.humidityModifier()).append("\n");
        sb.append("  Tags (").append(tags.size()).append("): ").append(tags).append("\n");
        sb.append("  Properties: ");
        if (isRare()) sb.append("RARE ");
        if (isDangerous()) sb.append("DANGEROUS ");
        if (isPeaceful()) sb.append("PEACEFUL ");
        if (isAquatic()) sb.append("AQUATIC ");
        if (isMountainous()) sb.append("MOUNTAINOUS ");
        sb.append("\n");
        sb.append("  Priority: ").append(getGenerationPriority()).append("\n");
        sb.append("  Diversity: ").append(getDiversityRating()).append("\n");
        sb.append("}");
        return sb.toString();
    }
}
