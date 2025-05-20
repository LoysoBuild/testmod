package com.test_project.combat.geko;

import com.test_project.combat.PlayerCombatSettings;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.capabilities.EntityCapability;

public class CombatCapabilities {
    public static final EntityCapability<PlayerCombatSettings, Void> PLAYER_COMBAT =
            EntityCapability.createVoid(
                     ResourceLocation.fromNamespaceAndPath("mainmod", "player_combat"),
                    PlayerCombatSettings.class
            );
}
