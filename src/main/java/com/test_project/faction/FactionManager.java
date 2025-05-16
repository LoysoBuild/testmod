package com.test_project.faction;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Менеджер для управления фракциями и их отношениями.
 */
public class FactionManager {
    // Хранит отношения между парами фракций: (factionId1, factionId2) -> Relation
    private static final Map<FactionPair, FactionRelation> relations = new HashMap<>();

    /**
     * Установить отношение между двумя фракциями.
     */
    public static void setRelation(String factionId1, String factionId2, FactionRelation relation) {
        if (factionId1.equals(factionId2)) return; // Нельзя установить отношение к себе
        relations.put(new FactionPair(factionId1, factionId2), relation);
        relations.put(new FactionPair(factionId2, factionId1), relation); // Симметрично
    }

    /**
     * Получить отношение между двумя фракциями.
     */
    public static FactionRelation getRelation(String factionId1, String factionId2) {
        if (factionId1.equals(factionId2)) return FactionRelation.ALLY;
        return relations.getOrDefault(new FactionPair(factionId1, factionId2), FactionRelation.NEUTRAL);
    }

    /**
     * Вспомогательный класс для пары фракций (без учёта порядка).
     */
    private static class FactionPair {
        private final String id1;
        private final String id2;

        public FactionPair(String id1, String id2) {
            // Порядок не важен: (A, B) == (B, A)
            if (id1.compareTo(id2) < 0) {
                this.id1 = id1;
                this.id2 = id2;
            } else {
                this.id1 = id2;
                this.id2 = id1;
            }
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof FactionPair)) return false;
            FactionPair that = (FactionPair) o;
            return Objects.equals(id1, that.id1) && Objects.equals(id2, that.id2);
        }

        @Override
        public int hashCode() {
            return Objects.hash(id1, id2);
        }
    }
}
