package com.test_project.worldrep;

import net.minecraft.world.entity.player.Player;

public class WorldReputationProvider {
    public static WorldReputation get(Player player) {
        return player.getData(ModAttachments.WORLD_REPUTATION.get());
    }
}
