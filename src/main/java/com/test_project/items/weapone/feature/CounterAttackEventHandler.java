package com.test_project.items.weapone.feature;


import com.test_project.combat.PlayerCombatSettings;
import com.test_project.combat.combo.CombatEventHandler;
import com.test_project.combat.stance.StanceType;
import com.test_project.items.weapone.AbstractWeapon;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.player.Player;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.util.RandomSource;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingShieldBlockEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;

@EventBusSubscriber
public class CounterAttackEventHandler {

    // Константы для настройки контратаки
    private static final float COUNTER_CHANCE_BASE = 0.25f; // 25% базовый шанс
    private static final float COUNTER_CHANCE_DEFENSE_STANCE = 0.4f; // 40% в защитной стойке
    private static final int COUNTER_COOLDOWN_TICKS = 60; // 3 секунды кулдаун

    // Хранение кулдаунов контратак
    private static final java.util.Map<java.util.UUID, Long> counterCooldowns = new java.util.HashMap<>();

    @SubscribeEvent(priority = EventPriority.NORMAL)
    public static void onLivingShieldBlock(LivingShieldBlockEvent event) {
        LivingEntity defender = event.getEntity();
        if (!(defender instanceof ServerPlayer defenderPlayer)) return;

        ItemStack mainHand = defender.getMainHandItem();
        if (mainHand.isEmpty()) return;

        // Проверяем, является ли предмет оружием с контратакой
        if (mainHand.getItem() instanceof AbstractWeapon weapon) {
            if (hasCounterAttackFeature(weapon)) {
                DamageSource damageSource = event.getDamageSource();
                if (damageSource.getEntity() instanceof LivingEntity attacker && attacker.isAlive()) {
                    // Успешное блокирование щитом - высокий шанс контратаки
                    attemptCounterAttack(weapon, defenderPlayer, attacker, mainHand, 0.8f);
                }
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void onLivingIncomingDamage(LivingIncomingDamageEvent event) {
        LivingEntity defender = event.getEntity();
        if (!(defender instanceof ServerPlayer defenderPlayer)) return;

        ItemStack mainHand = defender.getMainHandItem();
        if (mainHand.isEmpty()) return;

        // Проверяем контратаку при получении урона (без щита)
        if (mainHand.getItem() instanceof AbstractWeapon weapon) {
            if (hasCounterAttackFeature(weapon)) {
                DamageSource damageSource = event.getSource();
                if (damageSource.getEntity() instanceof LivingEntity attacker && attacker.isAlive()) {
                    // Получение урона без щита - обычный шанс контратаки
                    float chance = getCounterChance(defenderPlayer);
                    attemptCounterAttack(weapon, defenderPlayer, attacker, mainHand, chance);
                }
            }
        }
    }

    private static boolean hasCounterAttackFeature(AbstractWeapon weapon) {
        try {
            // Используем новый метод hasFeature из AbstractWeapon
            return weapon.hasFeature("counterattack") ||
                    weapon.hasFeature("counter_attack") ||
                    weapon.hasFeature("parry");
        } catch (Exception e) {
            // Fallback к старому методу
            return weapon.getFeatureSet() != null &&
                    weapon.getFeatureSet().getAll().stream()
                            .anyMatch(feature -> {
                                String featureId = feature.getId().toLowerCase();
                                return featureId.contains("counterattack") ||
                                        featureId.contains("counter") ||
                                        featureId.contains("parry");
                            });
        }
    }

    private static float getCounterChance(ServerPlayer player) {
        PlayerCombatSettings settings = CombatEventHandler.getSettings(player);

        // Базовый шанс зависит от стойки
        float baseChance = settings.getCurrentStance() == StanceType.DEFENSE
                ? COUNTER_CHANCE_DEFENSE_STANCE
                : COUNTER_CHANCE_BASE;

        // Модификаторы шанса
        float finalChance = baseChance;

        // Бонус если игрок в бою
        if (settings.isInCombat()) {
            finalChance += 0.1f; // +10% в бою
        }

        // Штраф за низкое здоровье
        float healthPercent = player.getHealth() / player.getMaxHealth();
        if (healthPercent < 0.3f) {
            finalChance -= 0.15f; // -15% при низком здоровье
        }

        return Math.max(0.05f, Math.min(0.95f, finalChance)); // Ограничиваем 5%-95%
    }

    private static void attemptCounterAttack(AbstractWeapon weapon, ServerPlayer defender,
                                             LivingEntity attacker, ItemStack weaponStack, float chance) {
        // Проверяем кулдаун
        if (isOnCooldown(defender)) {
            return;
        }

        // Проверяем шанс срабатывания
        RandomSource random = defender.getRandom();
        if (random.nextFloat() > chance) {
            return; // Контратака не сработала
        }

        executeCounterAttack(weapon, defender, attacker, weaponStack);
        setCooldown(defender);
    }

    private static void executeCounterAttack(AbstractWeapon weapon, ServerPlayer defender,
                                             LivingEntity attacker, ItemStack weaponStack) {
        try {
            // Вызываем контратаку через оружие
            weapon.triggerCounterAttack(defender, attacker, weaponStack);

            // Применяем дополнительные эффекты
            applyCounterEffects(defender, attacker);

            // Визуальные и звуковые эффекты
            playCounterAttackEffects(defender, attacker);

            // Уведомления
            sendCounterAttackMessage(defender, attacker);

            // Обновляем боевые настройки
            PlayerCombatSettings settings = CombatEventHandler.getSettings(defender);
            settings.enterCombat();
            settings.advanceCombo(); // Контратака продвигает комбо

            // Логирование для отладки
            System.out.println("Counter-attack triggered: " + defender.getName().getString() +
                    " vs " + attacker.getName().getString());

        } catch (Exception e) {
            System.err.println("Error executing counter-attack: " + e.getMessage());
        }
    }

    private static void applyCounterEffects(ServerPlayer defender, LivingEntity attacker) {
        // Эффекты для атакующего
        attacker.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 40, 1)); // Замедление
        attacker.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 60, 0)); // Слабость

        // Эффекты для защитника
        defender.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 40, 0)); // Сопротивление урону
        defender.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 60, 0)); // Регенерация

        // Отбрасывание атакующего
        double knockbackStrength = 1.2;
        double dx = attacker.getX() - defender.getX();
        double dz = attacker.getZ() - defender.getZ();
        double distance = Math.sqrt(dx * dx + dz * dz);
        if (distance > 0) {
            attacker.push(dx / distance * knockbackStrength, 0.2, dz / distance * knockbackStrength);
        }
    }

    private static void playCounterAttackEffects(LivingEntity defender, LivingEntity attacker) {
        // Звуковые эффекты
        defender.level().playSound(null,
                defender.getX(), defender.getY(), defender.getZ(),
                SoundEvents.PLAYER_ATTACK_CRIT,
                SoundSource.PLAYERS,
                1.0f, 1.3f);

        defender.level().playSound(null,
                attacker.getX(), attacker.getY(), attacker.getZ(),
                SoundEvents.PLAYER_HURT,
                SoundSource.PLAYERS,
                0.8f, 0.9f);

        // Визуальные эффекты частиц
        if (defender.level() instanceof ServerLevel serverLevel) {
            // Частицы успешной контратаки вокруг защитника
            serverLevel.sendParticles(ParticleTypes.ENCHANTED_HIT,
                    defender.getX(), defender.getY() + 1, defender.getZ(),
                    12, 0.6, 0.6, 0.6, 0.1);

            // Частицы удара по атакующему
            serverLevel.sendParticles(ParticleTypes.CRIT,
                    attacker.getX(), attacker.getY() + 1, attacker.getZ(),
                    8, 0.4, 0.4, 0.4, 0.05);

            // Линия частиц между защитником и атакующим
            createParticleLine(serverLevel, defender, attacker);
        }
    }

    private static void createParticleLine(ServerLevel level, LivingEntity from, LivingEntity to) {
        double dx = to.getX() - from.getX();
        double dy = to.getY() + 1 - (from.getY() + 1);
        double dz = to.getZ() - from.getZ();
        double distance = Math.sqrt(dx * dx + dy * dy + dz * dz);

        int steps = (int) (distance * 3); // 3 частицы на блок
        for (int i = 0; i < steps; i++) {
            double progress = (double) i / steps;
            double x = from.getX() + dx * progress;
            double y = from.getY() + 1 + dy * progress;
            double z = from.getZ() + dz * progress;

            level.sendParticles(ParticleTypes.SWEEP_ATTACK,
                    x, y, z, 1, 0, 0, 0, 0);
        }
    }

    private static void sendCounterAttackMessage(ServerPlayer defender, LivingEntity attacker) {
        // Сообщение защитнику
        Component defenderMessage = Component.literal(
                "§6⚡ Контратака! §fВы парировали атаку " + attacker.getName().getString()
        );
        defender.sendSystemMessage(defenderMessage);

        // Сообщение атакующему (если это игрок)
        if (attacker instanceof ServerPlayer attackerPlayer) {
            Component attackerMessage = Component.literal(
                    "§c💥 Вас контратаковал §f" + defender.getName().getString()
            );
            attackerPlayer.sendSystemMessage(attackerMessage);
        }
    }

    // === СИСТЕМА КУЛДАУНОВ ===

    private static boolean isOnCooldown(ServerPlayer player) {
        Long lastCounter = counterCooldowns.get(player.getUUID());
        if (lastCounter == null) return false;

        long currentTime = player.level().getGameTime();
        return (currentTime - lastCounter) < COUNTER_COOLDOWN_TICKS;
    }

    private static void setCooldown(ServerPlayer player) {
        counterCooldowns.put(player.getUUID(), player.level().getGameTime());
    }

    public static int getRemainingCooldown(ServerPlayer player) {
        Long lastCounter = counterCooldowns.get(player.getUUID());
        if (lastCounter == null) return 0;

        long currentTime = player.level().getGameTime();
        long elapsed = currentTime - lastCounter;
        return Math.max(0, (int) (COUNTER_COOLDOWN_TICKS - elapsed));
    }

    // === ПУБЛИЧНЫЕ УТИЛИТАРНЫЕ МЕТОДЫ ===

    /**
     * Проверяет, может ли игрок выполнить контратаку
     */
    public static boolean canPerformCounterAttack(Player player) {
        if (!(player instanceof ServerPlayer serverPlayer)) return false;
        if (isOnCooldown(serverPlayer)) return false;

        ItemStack mainHand = player.getMainHandItem();
        if (mainHand.isEmpty()) return false;

        if (mainHand.getItem() instanceof AbstractWeapon weapon) {
            return hasCounterAttackFeature(weapon);
        }
        return false;
    }

    /**
     * Принудительно выполняет контратаку (для использования в других местах кода)
     */
    public static void forceCounterAttack(Player defender, LivingEntity attacker) {
        if (!(defender instanceof ServerPlayer serverPlayer)) return;

        ItemStack mainHand = defender.getMainHandItem();
        if (mainHand.getItem() instanceof AbstractWeapon weapon && hasCounterAttackFeature(weapon)) {
            executeCounterAttack(weapon, serverPlayer, attacker, mainHand);
            setCooldown(serverPlayer);
        }
    }

    /**
     * Очищает данные игрока при выходе
     */
    public static void clearPlayerData(Player player) {
        counterCooldowns.remove(player.getUUID());
    }

    /**
     * Получить шанс контратаки для игрока (для UI)
     */
    public static float getCounterChanceForDisplay(Player player) {
        if (player instanceof ServerPlayer serverPlayer) {
            return getCounterChance(serverPlayer) * 100; // В процентах
        }
        return 0;
    }
}
