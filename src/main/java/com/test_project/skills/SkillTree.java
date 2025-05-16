package com.test_project.skills;

import java.util.HashMap;
import java.util.Map;

public class SkillTree {
    private final String id;
    private final Map<String, Skill> skills = new HashMap<>();

    public SkillTree(String id) { this.id = id; }

    public void addSkill(Skill skill) { skills.put(skill.getId(), skill); }
    public Skill getSkill(String id) { return skills.get(id); }
    public Map<String, Skill> getAllSkills() { return skills; }
    public String getId() { return id; }
}
