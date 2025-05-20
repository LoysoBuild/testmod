package com.test_project.skills;

import java.util.HashSet;
import java.util.Set;

public class PlayerSkillData {
    private final Set<String> unlockedSkills = new HashSet<>();

    public boolean hasSkill(String skillId) { return unlockedSkills.contains(skillId); }
    public void unlockSkill(String skillId) { unlockedSkills.add(skillId); }
    public Set<String> getUnlockedSkills() { return unlockedSkills; }
}
