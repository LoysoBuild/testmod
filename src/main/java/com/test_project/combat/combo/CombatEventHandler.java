package com.test_project.combat.combo;

import com.mojang.logging.LogUtils;
import com.test_project.combat.PlayerCombatSettings;
import com.test_project.combat.stance.StanceType;
import com.test_project.combat.stance.S2CPlayStanceAnimationPacket;
import com.test_project.combat.stance.NetworkManager;
import com.test_project.items.weapone.AbstractWeapon;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.ItemStack;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent; // Новое событие
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
    private static final Map<UUID, Integer> PLAYER_COMBO_INDEX = new HashMap<>();
    private static final Map<UUID, Long> LAST_ATTACK_TIME = new HashMap<>();

    private static final float ATTACK_STANCE_DAMAGE_MULTIPLIER = 1.15f;
    private static final float DEFENSE_STANCE_DAMAGE_REDUCTION = 0.85f;
    private static final long COMBO_RESET_TIME = 3000;
    private static final int COMBO_FINISHER_STUN_DURATION = 60;

    public static PlayerCombatSettings getSettings(Player player) {
        return PLAYER_SETTINGS.computeIfAbsent(player.getUUID(), uuid -> {
            PlayerCombatSettings settings = new PlayerCombatSettings();
            settings.setCombo(StanceType.ATTACK, "default_attack_combo");
            settings.setCombo(StanceType.DEFENSE, "default_defense_combo");
            return settings;
        });
    }

    @SubscribeEvent
    public static void onPlayerLoggedOut(PlayerEvent.PlayerLoggedOutEvent event) {
        UUID playerId = event.getEntity().getUUID();
        PLAYER_SETTINGS.remove(playerId);
        PLAYER_COMBO_INDEX.remove(playerId);
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
            PLAYER_COMBO_INDEX.put(playerId, 0);
            settings.resetCombo();
        }

        ItemStack mainHand = player.getMainHandItem();
        if (mainHand.getItem() instanceof AbstractWeapon weapon) {
            String weaponType = weapon.getClass().getSimpleName();
            settings.setLastUsedWeaponType(weaponType);
        }
    }

    // Используем новое событие LivingIncomingDamageEvent вместо LivingAttackEvent
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

        // Обработка входа в бой для защитника
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

    // Модификация урона в зависимости от стойки
    @SubscribeEvent(priority = EventPriority.NORMAL)
    public static void onLivingDamagePre(LivingDamageEvent.Pre event) {
        float originalDamage = event.getNewDamage();
        float finalDamage = originalDamage;
        boolean damageModified = false;

        // Усиление урона для атакующего в стойке атаки
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

        // Снижение урона для защитника в стойке защиты
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

    // Обработка комбо после нанесения урона
    @SubscribeEvent(priority = EventPriority.LOW)
    public static void onLivingDamagePost(LivingDamageEvent.Post event) {
        if (!(event.getSource().getEntity() instanceof ServerPlayer attacker)) return;
        if (event.getEntity() == attacker) return;

        handleComboLogic(attacker, event.getEntity(), event.getNewDamage());
    }

    private static void handleComboLogic(ServerPlayer attacker, LivingEntity target, float damage) {
        PlayerCombatSettings settings = getSettings(attacker);
        StanceType stance = settings.getCurrentStance();
        UUID attackerId = attacker.getUUID();

        String comboId = settings.getCombo(stance);
        Combo combo = ComboRegistry.get(comboId);
        if (combo == null || combo.getAnimations().isEmpty()) {
            LOGGER.warn("No combo found for id: {} (player: {})", comboId, attacker.getName().getString());
            return;
        }

        settings.advanceCombo();
        int currentIndex = PLAYER_COMBO_INDEX.getOrDefault(attackerId, 0);

        if (currentIndex < combo.getAnimations().size()) {
            String animationId = combo.getAnimations().get(currentIndex);

            NetworkManager.sendToPlayer(new S2CPlayStanceAnimationPacket(stance), attacker);

            currentIndex++;
            PLAYER_COMBO_INDEX.put(attackerId, currentIndex);

            attacker.sendSystemMessage(Component.literal(
                    String.format("§e⚡ Комбо %d/%d", currentIndex, combo.getAnimations().size())
            ));

            if (currentIndex >= combo.getAnimations().size()) {
                executeComboFinisher(attacker, target, combo);
                PLAYER_COMBO_INDEX.put(attackerId, 0);
                settings.resetCombo();
            }

            LOGGER.debug("{} executed combo step {}/{} on {}",
                    attacker.getName().getString(), currentIndex, combo.getAnimations().size(),
                    target.getName().getString()
            );
        }
    }

    private static void executeComboFinisher(ServerPlayer attacker, LivingEntity target, Combo combo) {
        String finisherAbility = combo.getFinisherAbility();
        if (finisherAbility == null) return;

        switch (finisherAbility.toLowerCase()) {
            case "stun" -> {
                target.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, COMBO_FINISHER_STUN_DURATION, 2));
                target.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, COMBO_FINISHER_STUN_DURATION, 1));

                attacker.sendSystemMessage(Component.literal("§6✨ Комбо завершено! Противник оглушён!"));

                if (attacker.level() instanceof ServerLevel serverLevel) {
                    serverLevel.sendParticles(ParticleTypes.CRIT,
                            target.getX(), target.getY() + 1, target.getZ(),
                            10, 0.5, 0.5, 0.5, 0.1);
                }

                attacker.level().playSound(null, target.getX(), target.getY(), target.getZ(),
                        SoundEvents.PLAYER_ATTACK_STRONG, SoundSource.PLAYERS, 1.0f, 0.8f);
            }
            case "knockback" -> {
                double knockbackStrength = 2.0;
                double dx = target.getX() - attacker.getX();
                double dz = target.getZ() - attacker.getZ();
                double distance = Math.sqrt(dx * dx + dz * dz);
                if (distance > 0) {
                    target.push(dx / distance * knockbackStrength, 0.5, dz / distance * knockbackStrength);
                }

                attacker.sendSystemMessage(Component.literal("§6✨ Комбо завершено! Мощный отброс!"));
            }
            case "heal" -> {
                attacker.heal(4.0f);
                attacker.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 100, 1));

                attacker.sendSystemMessage(Component.literal("§6✨ Комбо завершено! Восстановление здоровья!"));
            }
            default -> LOGGER.warn("Unknown combo finisher ability: {}", finisherAbility);
        }

        LOGGER.info("{} executed combo finisher '{}' on {}",
                attacker.getName().getString(), finisherAbility, target.getName().getString());
    }

    // Утилитарные методы
    public static void resetPlayerCombo(Player player) {
        UUID playerId = player.getUUID();
        PLAYER_COMBO_INDEX.put(playerId, 0);
        getSettings(player).resetCombo();
        LOGGER.debug("Reset combo for player: {}", player.getName().getString());
    }

    public static int getPlayerComboIndex(Player player) {
        return PLAYER_COMBO_INDEX.getOrDefault(player.getUUID(), 0);
    }

    public static boolean isPlayerInCombat(Player player) {
        return getSettings(player).isInCombat();
    }

    public static void clearAllData() {
        PLAYER_SETTINGS.clear();
        PLAYER_COMBO_INDEX.clear();
        LAST_ATTACK_TIME.clear();
        LOGGER.info("Cleared all combat data");
    }
}
