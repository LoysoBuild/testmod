package com.test_project.faction;

import com.test_project.faction.FactionAttachments;
import com.test_project.faction.FactionPlayerData;
import net.minecraft.world.entity.player.Player;
import net.minecraft.network.chat.Component;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;

@EventBusSubscriber(modid = "mainmod")
public class FactionTestHandler {
    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post event) {
        Player player = event.getEntity();
        FactionPlayerData data = player.getData(FactionAttachments.FACTION_DATA.get());
        if (data != null && player.tickCount % 20 == 0) {
            data.addReputation("gondor", 1);
            player.sendSystemMessage(Component.literal(
                    "Reputation for gondor: " + data.getReputation("gondor"))
            );
        }
    }

}
