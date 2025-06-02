package com.test_project.combat.stance;

import com.zigythebird.playeranimatorapi.data.PlayerAnimationData;
import com.zigythebird.playeranimatorapi.data.PlayerParts;
import com.zigythebird.playeranimatorapi.playeranims.PlayerAnimations;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

public class StanceAnimationManager {
    public static void playStance(Player player, StanceType stance) {
        if (player instanceof AbstractClientPlayer clientPlayer) {
            ResourceLocation animId = switch (stance) {
                case ATTACK -> ResourceLocation.fromNamespaceAndPath("mainmod", "sword_attack_idle");
                case DEFENSE -> ResourceLocation.fromNamespaceAndPath("mainmod", "sword_defense_idle");
            };
            PlayerAnimationData data = new PlayerAnimationData(
                    player.getUUID(),
                    animId,
                    PlayerParts.allEnabled,
                    null,
                    0,
                    0,
                    0,
                    0
            );
            PlayerAnimations.playAnimation(clientPlayer, data);
        }
    }
}
