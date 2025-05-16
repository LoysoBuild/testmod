package com.test_project.skills;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class FactionSkillData {
    private final Map<String, Set<String>> unlockedFactionSkills = new HashMap<>();

    public boolean hasSkill(String factionId, String skillId) {
        return unlockedFactionSkills.getOrDefault(factionId, Set.of()).contains(skillId);
    }

    public void unlockSkill(String factionId, String skillId) {
        unlockedFactionSkills.computeIfAbsent(factionId, k -> new HashSet<>()).add(skillId);
    }

    public Set<String> getUnlockedSkills(String factionId) {
        return unlockedFactionSkills.getOrDefault(factionId, Set.of());
    }
}
