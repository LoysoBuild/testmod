package com.test_project.combat.combo;

import com.mojang.logging.LogUtils;
import com.test_project.combat.PlayerCombatSettings;
import com.test_project.combat.stance.StanceType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.network.chat.Component;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import org.slf4j.Logger;

import java.util.HashMap;
import java.util.Map;

@EventBusSubscriber
public final class CombatEventHandler {

    private static final Logger LOGGER = LogUtils.getLogger();
    private static final Map<Player, PlayerCombatSettings> PLAYER_SETTINGS = new HashMap<>();
    private static final Map<Player, Integer> PLAYER_COMBO_INDEX = new HashMap<>();

    public static PlayerCombatSettings getSettings(Player player) {
        return PLAYER_SETTINGS.computeIfAbsent(player, p -> {
            PlayerCombatSettings settings = new PlayerCombatSettings();
            settings.setCombo(StanceType.ATTACK, "sword_smah_combo");
            settings.setCombo(StanceType.DEFENSE, "sword_smah_combo");
            return settings;
        });
    }

    // Обработка кулдауна между переключениями стоек
    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post event) {
        if (!event.getEntity().level().isClientSide) {
            PlayerCombatSettings settings = getSettings(event.getEntity());
            settings.tickCooldown();
        }
    }

    // Усиление/ослабление урона в зависимости от стойки + логгирование
    @SubscribeEvent
    public static void onLivingDamagePre(LivingDamageEvent.Pre event) {
        float oldDamage = event.getNewDamage();
        float newDamage = oldDamage;
        float delta = 0f;

        // Усиление урона в атакующей стойке
        if (event.getSource() != null && event.getSource().getEntity() instanceof Player attacker) {
            PlayerCombatSettings attackerSettings = getSettings(attacker);
            if (attackerSettings.getCurrentStance() == StanceType.ATTACK) {
                newDamage = oldDamage * 1.10f;
                delta = newDamage - oldDamage;
                event.setNewDamage(newDamage);
                // Сообщение игроку-атакующему
                attacker.sendSystemMessage(Component.literal(
                        "Вы нанесли " + String.format("%.2f", newDamage) +
                                " урона (+" + String.format("%.2f", delta) + " от стойки атаки)"
                ));
                LOGGER.info("{} dealt {} damage (+{} from ATTACK stance) to {}",
                        attacker.getName().getString(),
                        String.format("%.2f", newDamage),
                        String.format("%.2f", delta),
                        event.getEntity().getName().getString()
                );
            }
        }

        // Снижение урона в защитной стойке
        if (event.getEntity() instanceof Player defender) {
            PlayerCombatSettings defenderSettings = getSettings(defender);
            if (defenderSettings.getCurrentStance() == StanceType.DEFENSE) {
                float beforeReduction = event.getNewDamage();
                newDamage = beforeReduction * 0.90f;
                delta = beforeReduction - newDamage;
                event.setNewDamage(newDamage);
                // Сообщение игроку-защитнику
                defender.sendSystemMessage(Component.literal(
                        "Вы получили " + String.format("%.2f", newDamage) +
                                " урона (−" + String.format("%.2f", delta) + " от стойки защиты)"
                ));
                LOGGER.info("{} received {} damage (−{} from DEFENSE stance) from {}",
                        defender.getName().getString(),
                        String.format("%.2f", newDamage),
                        String.format("%.2f", delta),
                        event.getSource() != null && event.getSource().getEntity() != null
                                ? event.getSource().getEntity().getName().getString()
                                : "unknown"
                );
            }
        }
    }

    // Комбо-логика (оставляем на Post, т.к. не требуется модификация урона)
    @SubscribeEvent
    public static void onLivingDamageCombo(LivingDamageEvent.Post event) {
        if (!(event.getSource() != null && event.getSource().getEntity() instanceof Player player)) return;
        if (event.getEntity() == player) return;

        PlayerCombatSettings settings = getSettings(player);
        StanceType stance = settings.getCurrentStance();

        String comboId = settings.getCombo(stance);
        Combo combo = ComboRegistry.get(comboId);
        if (combo == null || combo.getAnimations().isEmpty()) return;

        int index = PLAYER_COMBO_INDEX.getOrDefault(player, 0);

        if (index < combo.getAnimations().size()) {
            String animation = combo.getAnimations().get(index);
            // TODO: Вызовите проигрывание анимации через Player Animator API
            index++;
            PLAYER_COMBO_INDEX.put(player, index);

            if (index >= combo.getAnimations().size()) {
                String ability = combo.getFinisherAbility();
                if (ability != null && event.getEntity() instanceof LivingEntity target) {
                    if ("stun".equals(ability)) {
                        target.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 40, 1));
                    }
                }
                PLAYER_COMBO_INDEX.put(player, 0);
            }
        } else {
            PLAYER_COMBO_INDEX.put(player, 0);
        }
    }
}
