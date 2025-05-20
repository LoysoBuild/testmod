package com.test_project.combat.combo;

import java.util.List;

public class Combo {
    private final String id;
    private final List<String> animations;
    private final String finisherAbility;
    private final String requiredAdvancement; // null если всегда доступно

    public Combo(String id, List<String> animations, String finisherAbility, String requiredAdvancement) {
        this.id = id;
        this.animations = animations;
        this.finisherAbility = finisherAbility;
        this.requiredAdvancement = requiredAdvancement;
    }

    public String getId() { return id; }
    public List<String> getAnimations() { return animations; }
    public String getFinisherAbility() { return finisherAbility; }
    public String getRequiredAdvancement() { return requiredAdvancement; }
}