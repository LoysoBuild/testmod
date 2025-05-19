package com.test_project.combat.combo;

import com.test_project.combat.PlayerCombatSettings;
import com.test_project.combat.stance.StanceType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;

import java.util.HashMap;
import java.util.Map;

@EventBusSubscriber
public final class CombatEventHandler {
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

    @SubscribeEvent
    public static void onLivingDamage(LivingDamageEvent.Post event) {
        if (!(event.getSource().getEntity() instanceof Player player)) return;
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
