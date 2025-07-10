package com.test_project.world.biomes.caves;

import java.util.Set;
import java.util.EnumSet;

/**
 * ИСПРАВЛЕНО: Убраны все специфические названия биомов, заменены на универсальные типы
 * Универсальные типы пещер для различных биомов
 */
public enum CaveType {
    NORMAL("normal", "Обычные пещеры", CaveCategory.STANDARD),
    MOUNTAIN("mountain", "Горные пещеры", CaveCategory.ELEVATED),
    HIGHLAND("highland", "Высокогорные пещеры", CaveCategory.ELEVATED),
    FOREST("forest", "Лесные пещеры", CaveCategory.ORGANIC),
    DESERT("desert", "Пустынные пещеры", CaveCategory.HARSH),
    VOLCANIC("volcanic", "Вулканические пещеры", CaveCategory.HARSH),
    FROZEN("frozen", "Замороженные пещеры", CaveCategory.EXTREME),
    SWAMP("swamp", "Болотные пещеры", CaveCategory.ORGANIC),
    COASTAL("coastal", "Прибрежные пещеры", CaveCategory.AQUATIC),
    DEEP("deep", "Глубокие пещеры", CaveCategory.EXTREME),
    CRYSTAL("crystal", "Кристальные пещеры", CaveCategory.EXTREME),
    LAVA("lava", "Лавовые пещеры", CaveCategory.HARSH);

    private final String name;
    private final String displayName;
    private final CaveCategory category;

    CaveType(String name, String displayName, CaveCategory category) {
        this.name = name;
        this.displayName = displayName;
        this.category = category;
    }

    public String getName() {
        return name;
    }

    public String getDisplayName() {
        return displayName;
    }

    public CaveCategory getCategory() {
        return category;
    }

    /**
     * Получить тип пещеры по имени с fallback на NORMAL
     */
    public static CaveType fromName(String name) {
        if (name == null || name.isEmpty()) {
            return NORMAL;
        }

        return switch (name.toLowerCase().trim()) {
            case "mountain" -> MOUNTAIN;
            case "highland" -> HIGHLAND;
            case "forest" -> FOREST;
            case "desert" -> DESERT;
            case "volcanic" -> VOLCANIC;
            case "frozen" -> FROZEN;
            case "swamp" -> SWAMP;
            case "coastal" -> COASTAL;
            case "deep" -> DEEP;
            case "crystal" -> CRYSTAL;
            case "lava" -> LAVA;
            default -> NORMAL;
        };
    }

    /**
     * Получить все типы пещер для категории
     */
    public static Set<CaveType> forCategory(CaveCategory category) {
        Set<CaveType> types = EnumSet.noneOf(CaveType.class);
        for (CaveType type : values()) {
            if (type.category == category) {
                types.add(type);
            }
        }
        return types;
    }

    /**
     * Проверить является ли тип пещеры горным
     */
    public boolean isMountainType() {
        return category == CaveCategory.ELEVATED;
    }

    /**
     * Проверить является ли тип пещеры водным
     */
    public boolean isAquaticType() {
        return category == CaveCategory.AQUATIC;
    }

    /**
     * Проверить является ли тип пещеры опасным
     */
    public boolean isHarshType() {
        return category == CaveCategory.HARSH;
    }

    /**
     * Проверить является ли тип пещеры специальным (не обычным)
     */
    public boolean isSpecialType() {
        return this != NORMAL;
    }

    /**
     * Проверить является ли тип пещеры редким
     */
    public boolean isRareType() {
        return category == CaveCategory.EXTREME;
    }

    /**
     * Получить совместимые типы пещер (могут встречаться вместе)
     */
    public Set<CaveType> getCompatibleTypes() {
        return switch (this) {
            case NORMAL -> EnumSet.of(FOREST, MOUNTAIN);
            case MOUNTAIN -> EnumSet.of(NORMAL, HIGHLAND, FROZEN);
            case HIGHLAND -> EnumSet.of(MOUNTAIN, FROZEN, DEEP);
            case FOREST -> EnumSet.of(NORMAL, SWAMP);
            case DESERT -> EnumSet.of(VOLCANIC, LAVA);
            case VOLCANIC -> EnumSet.of(DESERT, LAVA, DEEP);
            case FROZEN -> EnumSet.of(MOUNTAIN, HIGHLAND, CRYSTAL);
            case SWAMP -> EnumSet.of(FOREST, COASTAL);
            case COASTAL -> EnumSet.of(SWAMP, NORMAL);
            case DEEP -> EnumSet.of(VOLCANIC, HIGHLAND, CRYSTAL);
            case CRYSTAL -> EnumSet.of(FROZEN, DEEP);
            case LAVA -> EnumSet.of(VOLCANIC, DESERT);
        };
    }

    /**
     * Получить приоритет типа пещеры для генерации (чем выше, тем важнее)
     */
    public int getPriority() {
        return switch (this) {
            case DEEP -> 6;
            case CRYSTAL -> 5;
            case LAVA -> 5;
            case VOLCANIC -> 4;
            case HIGHLAND -> 3;
            case MOUNTAIN -> 2;
            case FROZEN -> 2;
            case SWAMP -> 1;
            case FOREST -> 1;
            case COASTAL -> 1;
            case DESERT -> 1;
            case NORMAL -> 0;
        };
    }

    /**
     * Получить редкость типа пещеры (0.0 - 1.0)
     */
    public float getRarity() {
        return switch (this) {
            case CRYSTAL -> 0.05f;
            case LAVA -> 0.1f;
            case DEEP -> 0.15f;
            case VOLCANIC -> 0.2f;
            case FROZEN -> 0.25f;
            case HIGHLAND -> 0.3f;
            case SWAMP -> 0.4f;
            case DESERT -> 0.5f;
            case MOUNTAIN -> 0.6f;
            case COASTAL -> 0.7f;
            case FOREST -> 0.8f;
            case NORMAL -> 1.0f;
        };
    }

    /**
     * Получить температуру пещеры (-1.0 холодная, 0.0 нормальная, 1.0 горячая)
     */
    public float getTemperature() {
        return switch (this) {
            case LAVA -> 1.0f;
            case VOLCANIC -> 0.8f;
            case DESERT -> 0.4f;
            case NORMAL -> 0.0f;
            case FOREST -> -0.1f;
            case MOUNTAIN -> -0.2f;
            case COASTAL -> -0.3f;
            case SWAMP -> -0.1f;
            case HIGHLAND -> -0.4f;
            case DEEP -> -0.5f;
            case CRYSTAL -> -0.6f;
            case FROZEN -> -1.0f;
        };
    }

    /**
     * Получить влажность пещеры (-1.0 сухая, 0.0 нормальная, 1.0 влажная)
     */
    public float getHumidity() {
        return switch (this) {
            case SWAMP -> 1.0f;
            case COASTAL -> 0.8f;
            case FOREST -> 0.6f;
            case NORMAL -> 0.0f;
            case MOUNTAIN -> -0.2f;
            case HIGHLAND -> -0.3f;
            case DEEP -> -0.1f;
            case CRYSTAL -> -0.4f;
            case FROZEN -> -0.8f;
            case DESERT -> -0.9f;
            case VOLCANIC -> -0.6f;
            case LAVA -> -1.0f;
        };
    }

    /**
     * Проверить может ли данный тип пещеры генерироваться на указанной высоте
     */
    public boolean canGenerateAtHeight(int height) {
        return switch (this) {
            case HIGHLAND -> height > 100;
            case MOUNTAIN -> height > 70;
            case DEEP -> height < 30;
            case LAVA -> height < 20;
            case COASTAL -> height < 80 && height > 50;
            default -> true;
        };
    }

    /**
     * Категории пещер для группировки
     */
    public enum CaveCategory {
        STANDARD("Стандартные", "Обычные пещеры"),
        ELEVATED("Возвышенные", "Горные и высокогорные пещеры"),
        ORGANIC("Органические", "Лесные и болотные пещеры"),
        HARSH("Суровые", "Пустынные и вулканические пещеры"),
        EXTREME("Экстремальные", "Замороженные, глубокие и кристальные пещеры"),
        AQUATIC("Водные", "Прибрежные пещеры");

        private final String name;
        private final String description;

        CaveCategory(String name, String description) {
            this.name = name;
            this.description = description;
        }

        public String getName() {
            return name;
        }

        public String getDescription() {
            return description;
        }

        /**
         * Получить все типы пещер в этой категории
         */
        public Set<CaveType> getTypes() {
            return CaveType.forCategory(this);
        }

        /**
         * Проверить совместимость с другой категорией
         */
        public boolean isCompatibleWith(CaveCategory other) {
            return switch (this) {
                case STANDARD -> other != EXTREME;
                case ELEVATED -> other == STANDARD || other == EXTREME;
                case ORGANIC -> other == STANDARD || other == AQUATIC;
                case HARSH -> other == EXTREME;
                case EXTREME -> other == ELEVATED || other == HARSH;
                case AQUATIC -> other == ORGANIC || other == STANDARD;
            };
        }
    }
}
