package com.test_project.skills;

import java.util.List;

public class Skill {
    public enum ReputationType { WORLD, FACTION }

    private final String id;
    private final String name;
    private final List<String> prerequisites;
    private final int cost;
    private final ReputationType reputationType;
    private final String factionId; // null для мировых навыков

    public Skill(String id, String name, List<String> prerequisites, int cost, ReputationType type, String factionId) {
        this.id = id;
        this.name = name;
        this.prerequisites = prerequisites;
        this.cost = cost;
        this.reputationType = type;
        this.factionId = factionId;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public List<String> getPrerequisites() { return prerequisites; }
    public int getCost() { return cost; }
    public ReputationType getReputationType() { return reputationType; }
    public String getFactionId() { return factionId; }
}
