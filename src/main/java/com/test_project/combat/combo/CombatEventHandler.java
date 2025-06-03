package com.test_project.combat.combo;

import com.mojang.logging.LogUtils;
import com.test_project.combat.PlayerCombatSettings;
import com.test_project.combat.stance.StanceType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.ItemStack;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.server.level.ServerLevel;
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
    private static final Map<UUID, Long> LAST_ATTACK_TIME = new HashMap<>();

    private static final float ATTACK_STANCE_DAMAGE_MULTIPLIER = 1.15f;
    private static final float DEFENSE_STANCE_DAMAGE_REDUCTION = 0.85f;
    private static final long COMBO_RESET_TIME = 3000;

    public static PlayerCombatSettings getSettings(Player player) {
        return PLAYER_SETTINGS.computeIfAbsent(player.getUUID(), uuid -> {
            PlayerCombatSettings settings = new PlayerCombatSettings();
            // Если нужно, можно оставить инициализацию комбо (но сейчас она не используется)
            // settings.setCombo(StanceType.ATTACK, "default_attack_combo");
            // settings.setCombo(StanceType.DEFENSE, "default_defense_combo");
            return settings;
        });
    }

    @SubscribeEvent
    public static void onPlayerLoggedOut(PlayerEvent.PlayerLoggedOutEvent event) {
        UUID playerId = event.getEntity().getUUID();
        PLAYER_SETTINGS.remove(playerId);
        LAST_ATTACK_TIME.remove(playerId);
        LOGGER.debug("Cleaned combat data for player: {}", event.getEntity().getName().getString());
    }

    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post event) {
        if (event.getEntity().level().isClientSide) return;

        Player player = event.getEntity();
        PlayerCombatSettings settings = getSettings(player);
        settings.tickCooldown();

        UUID playerId = player.getUUID();
        Long lastAttack = LAST_ATTACK_TIME.get(playerId);
        if (lastAttack != null && System.currentTimeMillis() - lastAttack > COMBO_RESET_TIME) {
            // Если была логика сброса комбо — теперь просто сбрасываем таймер атаки
            settings.resetCombo();
        }

        ItemStack mainHand = player.getMainHandItem();
        if (mainHand.getItem() instanceof com.test_project.items.weapone.AbstractWeapon weapon) {
            String weaponType = weapon.getClass().getSimpleName();
            settings.setLastUsedWeaponType(weaponType);
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void onLivingIncomingDamage(LivingIncomingDamageEvent event) {
        if (!(event.getSource().getEntity() instanceof ServerPlayer attacker)) return;
        if (event.getEntity() == attacker) return;

        PlayerCombatSettings settings = getSettings(attacker);
        settings.enterCombat();

        LAST_ATTACK_TIME.put(attacker.getUUID(), System.currentTimeMillis());

        LOGGER.debug("{} attacked {} with stance: {}",
                attacker.getName().getString(),
                event.getEntity().getName().getString(),
                settings.getCurrentStance()
        );

        if (event.getEntity() instanceof ServerPlayer defender) {
            PlayerCombatSettings defenderSettings = getSettings(defender);
            defenderSettings.enterCombat();

            LOGGER.debug("{} hurt by {} with stance: {}",
                    defender.getName().getString(),
                    attacker.getName().getString(),
                    defenderSettings.getCurrentStance()
            );
        }
    }

    @SubscribeEvent(priority = EventPriority.NORMAL)
    public static void onLivingDamagePre(LivingDamageEvent.Pre event) {
        float originalDamage = event.getNewDamage();
        float finalDamage = originalDamage;
        boolean damageModified = false;

        if (event.getSource().getEntity() instanceof ServerPlayer attacker) {
            PlayerCombatSettings attackerSettings = getSettings(attacker);
            if (attackerSettings.getCurrentStance() == StanceType.ATTACK) {
                finalDamage *= ATTACK_STANCE_DAMAGE_MULTIPLIER;
                damageModified = true;

                float bonus = finalDamage - originalDamage;
                attacker.sendSystemMessage(Component.literal(
                        String.format("§c⚔ Урон: %.1f (+%.1f от стойки атаки)", finalDamage, bonus)
                ));

                attacker.level().playSound(null, attacker.getX(), attacker.getY(), attacker.getZ(),
                        SoundEvents.PLAYER_ATTACK_CRIT, SoundSource.PLAYERS, 0.8f, 1.2f);

                LOGGER.debug("{} dealt enhanced damage: {} (+{} from ATTACK stance)",
                        attacker.getName().getString(),
                        String.format("%.2f", finalDamage),
                        String.format("%.2f", bonus)
                );
            }
        }

        if (event.getEntity() instanceof ServerPlayer defender) {
            PlayerCombatSettings defenderSettings = getSettings(defender);
            if (defenderSettings.getCurrentStance() == StanceType.DEFENSE) {
                float beforeReduction = finalDamage;
                finalDamage *= DEFENSE_STANCE_DAMAGE_REDUCTION;
                float reduction = beforeReduction - finalDamage;
                damageModified = true;

                defender.sendSystemMessage(Component.literal(
                        String.format("§9🛡 Урон: %.1f (-%.1f от стойки защиты)", finalDamage, reduction)
                ));

                defender.level().playSound(null, defender.getX(), defender.getY(), defender.getZ(),
                        SoundEvents.SHIELD_BLOCK, SoundSource.PLAYERS, 0.6f, 1.0f);

                LOGGER.debug("{} received reduced damage: {} (-{} from DEFENSE stance)",
                        defender.getName().getString(),
                        String.format("%.2f", finalDamage),
                        String.format("%.2f", reduction)
                );
            }
        }

        if (damageModified) {
            event.setNewDamage(finalDamage);
        }
    }

    // Обработка комбо после нанесения урона — удалена

    // Утилитарные методы
    public static boolean isPlayerInCombat(Player player) {
        return getSettings(player).isInCombat();
    }

    public static void clearAllData() {
        PLAYER_SETTINGS.clear();
        LAST_ATTACK_TIME.clear();
        LOGGER.info("Cleared all combat data");
    }
}
