package com.test_project.combat;

import com.mojang.logging.LogUtils;
import com.test_project.combat.stance.StanceType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.fml.common.EventBusSubscriber;
import org.slf4j.Logger;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@EventBusSubscriber
public final class CombatEventHandler {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final Map<UUID, PlayerCombatSettings> PLAYER_SETTINGS = new HashMap<>();
    private static final float ATTACK_STANCE_DAMAGE_MULTIPLIER = 1.15f;
    private static final float DEFENSE_STANCE_DAMAGE_REDUCTION = 0.85f;

    public static PlayerCombatSettings getSettings(Player player) {
        return PLAYER_SETTINGS.computeIfAbsent(player.getUUID(),
                uuid -> new PlayerCombatSettings());
    }

    @SubscribeEvent
    public static void onPlayerLoggedOut(PlayerEvent.PlayerLoggedOutEvent event) {
        PLAYER_SETTINGS.remove(event.getEntity().getUUID());
    }

    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post event) {
        if (event.getEntity().level().isClientSide()) return;

        PlayerCombatSettings settings = getSettings(event.getEntity());
        settings.tickCooldown();
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void onLivingIncomingDamage(LivingIncomingDamageEvent event) {
        if (!(event.getSource().getEntity() instanceof ServerPlayer attacker)) return;
        if (event.getEntity() == attacker) return;

        PlayerCombatSettings settings = getSettings(attacker);
        settings.enterCombat();

        if (event.getEntity() instanceof ServerPlayer defender) {
            PlayerCombatSettings defenderSettings = getSettings(defender);
            defenderSettings.enterCombat();
        }
    }

    @SubscribeEvent(priority = EventPriority.NORMAL)
    public static void onLivingDamage(LivingDamageEvent.Pre event) {
        float originalDamage = event.getNewDamage();
        float finalDamage = originalDamage;
        boolean damageModified = false;

        if (event.getSource().getEntity() instanceof ServerPlayer attacker) {
            PlayerCombatSettings attackerSettings = getSettings(attacker);
            if (attackerSettings.getCurrentStance() == StanceType.ATTACK) {
                finalDamage *= ATTACK_STANCE_DAMAGE_MULTIPLIER;
                damageModified = true;

                attacker.level().playSound(null, attacker.getX(), attacker.getY(), attacker.getZ(),
                        SoundEvents.PLAYER_ATTACK_CRIT, SoundSource.PLAYERS, 0.8f, 1.2f);
            }
        }

        if (event.getEntity() instanceof ServerPlayer defender) {
            PlayerCombatSettings defenderSettings = getSettings(defender);
            if (defenderSettings.getCurrentStance() == StanceType.DEFENSE) {
                finalDamage *= DEFENSE_STANCE_DAMAGE_REDUCTION;
                damageModified = true;

                defender.level().playSound(null, defender.getX(), defender.getY(), defender.getZ(),
                        SoundEvents.SHIELD_BLOCK, SoundSource.PLAYERS, 0.6f, 1.0f);
            }
        }

        if (damageModified) {
            event.setNewDamage(finalDamage);
        }
    }

    public static boolean isPlayerInCombat(Player player) {
        return getSettings(player).isInCombat();
    }
}
