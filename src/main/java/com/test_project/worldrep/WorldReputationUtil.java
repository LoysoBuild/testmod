package com.test_project.worldrep;

import com.test_project.worldrep.ModAttachments;
import com.test_project.worldrep.WorldReputation;
import net.minecraft.world.entity.player.Player;

public class WorldReputationUtil {
    public static int getPlayerWorldReputation(Player player) {
        WorldReputation rep = player.getData(ModAttachments.WORLD_REPUTATION.get());
        return rep.getPoints();
    }
}
