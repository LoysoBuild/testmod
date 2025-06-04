package com.test_project.combat.parry;

import net.minecraft.world.entity.player.Player;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.InteractionHand;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ParrySystem {

    // Состояния парирования для каждого игрока
    private static final Map<UUID, ParryState> PARRY_STATES = new HashMap<>();

    // Константы парирования
    private static final int PARRY_WINDOW_TICKS = 10; // 0.5 секунды (10 тиков)
    private static final int COUNTER_WINDOW_TICKS = 20; // 1 секунда для контратаки
    private static final int PARRY_COOLDOWN_TICKS = 40; // 2 секунды кулдауна
    private static final float COUNTER_DAMAGE_MULTIPLIER = 1.3f; // +30% урона

    public static class ParryState {
        public boolean isParrying = false;
        public int parryStartTick = 0;
        public int lastSuccessfulParryTick = 0;
        public int parryCooldownTick = 0;
        public boolean canCounterAttack = false;
        public int counterWindowEndTick = 0;
    }

    /**
     * Активация парирования игроком
     */
    public static void activateParry(ServerPlayer player) {
        UUID playerId = player.getUUID();
        ParryState state = PARRY_STATES.computeIfAbsent(playerId, k -> new ParryState());

        int currentTick = player.tickCount;

        // Проверка кулдауна
        if (currentTick < state.parryCooldownTick) {
            int remainingCooldown = (state.parryCooldownTick - currentTick) / 20;
            player.sendSystemMessage(Component.literal(
                    "§c⏰ Парирование на кулдауне! Осталось: " + remainingCooldown + " сек"
            ));
            return;
        }

        // Проверка наличия подходящего оружия
        ItemStack mainHand = player.getMainHandItem();
        if (!canParryWithItem(mainHand)) {
            player.sendSystemMessage(Component.literal(
                    "§c❌ Нельзя парировать с этим предметом!"
            ));
            return;
        }

        // Активируем парирование
        state.isParrying = true;
        state.parryStartTick = currentTick;
        state.parryCooldownTick = currentTick + PARRY_COOLDOWN_TICKS;

        // Визуальные эффекты активации парирования
        spawnParryActivationEffects(player);

        // Уведомление игроку
        player.sendSystemMessage(Component.literal("§e🛡 Парирование активировано!"));

        // Анимация парирования
        player.swing(InteractionHand.MAIN_HAND);

        System.out.println("[PARRY] Player " + player.getName().getString() + " activated parry");
    }

    /**
     * Попытка парирования входящей атаки
     */
    public static boolean attemptParry(ServerPlayer defender, Player attacker, float incomingDamage) {
        UUID defenderId = defender.getUUID();
        ParryState state = PARRY_STATES.get(defenderId);

        if (state == null || !state.isParrying) {
            return false; // Парирование не активно
        }

        int currentTick = defender.tickCount;
        int timeSinceParryStart = currentTick - state.parryStartTick;

        // Проверка временного окна парирования
        if (timeSinceParryStart > PARRY_WINDOW_TICKS) {
            state.isParrying = false; // Окно парирования закрылось
            return false;
        }

        // Успешное парирование!
        state.isParrying = false;
        state.lastSuccessfulParryTick = currentTick;
        state.canCounterAttack = true;
        state.counterWindowEndTick = currentTick + COUNTER_WINDOW_TICKS;

        // Эффекты успешного парирования
        executeSuccessfulParry(defender, attacker, incomingDamage);

        return true; // Атака парирована
    }

    /**
     * Выполнение эффектов успешного парирования
     */
    private static void executeSuccessfulParry(ServerPlayer defender, Player attacker, float damage) {
        // Визуальные эффекты
        spawnParrySuccessEffects(defender, attacker);

        // Звуковые эффекты
        defender.level().playSound(null, defender.blockPosition(),
                SoundEvents.SHIELD_BLOCK, SoundSource.PLAYERS, 1.0f, 1.2f);

        // Кратковременный эффект сопротивления защитнику
        defender.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 20, 0)); // 1 сек

        // Уведомления
        defender.sendSystemMessage(Component.literal(
                "§a⚡ Парирование успешно! §fОткрыто окно контратаки!"
        ));

        if (attacker instanceof ServerPlayer serverAttacker) {
            serverAttacker.sendSystemMessage(Component.literal(
                    "§c🛡 Ваша атака была парирована игроком " + defender.getName().getString()
            ));
        }

        System.out.println("[PARRY] Successful parry: " + defender.getName().getString() +
                " vs " + attacker.getName().getString());
    }

    /**
     * Проверка и выполнение контратаки
     */
    public static boolean attemptCounterAttack(ServerPlayer attacker, Player target) {
        UUID attackerId = attacker.getUUID();
        ParryState state = PARRY_STATES.get(attackerId);

        if (state == null || !state.canCounterAttack) {
            return false; // Контратака недоступна
        }

        int currentTick = attacker.tickCount;

        // Проверка временного окна контратаки
        if (currentTick > state.counterWindowEndTick) {
            state.canCounterAttack = false; // Окно контратаки закрылось
            return false;
        }

        // Выполняем контратаку
        state.canCounterAttack = false; // Используем контратаку

        // ИСПРАВЛЕНО: Выполняем контратаку в отдельном потоке через сервер
        attacker.getServer().execute(() -> executeCounterAttack(attacker, target));

        return true; // Контратака выполнена
    }

    /**
     * Выполнение контратаки
     */
    private static void executeCounterAttack(ServerPlayer attacker, Player target) {
        // Бонусный урон
        ItemStack weapon = attacker.getMainHandItem();
        float baseDamage = getWeaponDamage(weapon);
        float counterDamage = baseDamage * COUNTER_DAMAGE_MULTIPLIER;

        // Создаем источник урона
        DamageSource damageSource = attacker.damageSources().playerAttack(attacker);
        target.hurt(damageSource, counterDamage);

        // Эффекты контратаки
        spawnCounterAttackEffects(attacker, target);

        // Звук критического удара
        attacker.level().playSound(null, attacker.blockPosition(),
                SoundEvents.PLAYER_ATTACK_CRIT, SoundSource.PLAYERS, 1.0f, 0.8f);

        // Анимация удара
        attacker.swing(InteractionHand.MAIN_HAND);

        // Уведомления
        attacker.sendSystemMessage(Component.literal(
                "§6⚡ Контратака! §fНанесено §c" + String.format("%.1f", counterDamage) + " §fурона"
        ));

        if (target instanceof ServerPlayer serverTarget) {
            serverTarget.sendSystemMessage(Component.literal(
                    "§c💥 Получена контратака от " + attacker.getName().getString() +
                            " §c(-" + String.format("%.1f", counterDamage) + " HP)"
            ));
        }

        System.out.println("[COUNTER] Counter attack: " + attacker.getName().getString() +
                " -> " + target.getName().getString() + " (" + counterDamage + " dmg)");
    }

    /**
     * Проверка, можно ли парировать с данным предметом
     */
    private static boolean canParryWithItem(ItemStack stack) {
        if (stack.isEmpty()) return false;

        // Можно парировать мечами, топорами и щитами
        String itemName = stack.getItem().toString().toLowerCase();
        return itemName.contains("sword") ||
                itemName.contains("axe") ||
                itemName.contains("shield") ||
                stack.getItem() instanceof net.minecraft.world.item.SwordItem ||
                stack.getItem() instanceof net.minecraft.world.item.AxeItem ||
                stack.getItem() instanceof net.minecraft.world.item.ShieldItem;
    }

    /**
     * Получение урона оружия
     */
    private static float getWeaponDamage(ItemStack weapon) {
        if (weapon.isEmpty()) return 1.0f;

        // Базовый урон в зависимости от типа оружия
        if (weapon.getItem() instanceof net.minecraft.world.item.SwordItem sword) {
            return sword.getDamage() + 1.0f; // +1 за базовый урон руки
        } else if (weapon.getItem() instanceof net.minecraft.world.item.AxeItem axe) {
            return axe.getAttackDamage() + 1.0f;
        }

        return 2.0f; // Урон руки
    }

    /**
     * Визуальные эффекты активации парирования
     */
    private static void spawnParryActivationEffects(ServerPlayer player) {
        // Синие частицы вокруг игрока
        for (int i = 0; i < 8; i++) {
            double angle = (i * Math.PI * 2) / 8;
            double x = player.getX() + Math.cos(angle) * 1.5;
            double z = player.getZ() + Math.sin(angle) * 1.5;
            double y = player.getY() + 1.0;

            player.serverLevel().sendParticles(ParticleTypes.ENCHANTED_HIT,
                    x, y, z, 1, 0, 0, 0, 0);
        }
    }

    /**
     * Визуальные эффекты успешного парирования
     */
    private static void spawnParrySuccessEffects(ServerPlayer defender, Player attacker) {
        // Золотые искры между игроками
        double midX = (defender.getX() + attacker.getX()) / 2;
        double midY = (defender.getY() + attacker.getY()) / 2 + 1;
        double midZ = (defender.getZ() + attacker.getZ()) / 2;

        for (int i = 0; i < 15; i++) {
            defender.serverLevel().sendParticles(ParticleTypes.CRIT,
                    midX, midY, midZ, 1,
                    (Math.random() - 0.5) * 2,
                    (Math.random() - 0.5) * 2,
                    (Math.random() - 0.5) * 2, 0.1);
        }

        // Белые частицы вокруг защитника
        for (int i = 0; i < 10; i++) {
            defender.serverLevel().sendParticles(ParticleTypes.CLOUD,
                    defender.getX(), defender.getY() + 1, defender.getZ(), 1,
                    (Math.random() - 0.5) * 1.5,
                    Math.random() * 1.5,
                    (Math.random() - 0.5) * 1.5, 0.05);
        }
    }

    /**
     * Визуальные эффекты контратаки
     */
    private static void spawnCounterAttackEffects(ServerPlayer attacker, Player target) {
        // Красные частицы критического удара
        for (int i = 0; i < 12; i++) {
            attacker.serverLevel().sendParticles(ParticleTypes.DAMAGE_INDICATOR,
                    target.getX(), target.getY() + 1, target.getZ(), 1,
                    (Math.random() - 0.5) * 1,
                    Math.random() * 1,
                    (Math.random() - 0.5) * 1, 0.1);
        }

        // Линия частиц от атакующего к цели
        double dx = target.getX() - attacker.getX();
        double dy = target.getY() - attacker.getY();
        double dz = target.getZ() - attacker.getZ();

        for (int i = 0; i < 8; i++) {
            double progress = i / 8.0;
            double x = attacker.getX() + dx * progress;
            double y = attacker.getY() + 1 + dy * progress;
            double z = attacker.getZ() + dz * progress;

            attacker.serverLevel().sendParticles(ParticleTypes.SWEEP_ATTACK,
                    x, y, z, 1, 0, 0, 0, 0);
        }
    }

    /**
     * Очистка состояния игрока при выходе
     */
    public static void clearPlayerState(UUID playerId) {
        PARRY_STATES.remove(playerId);
    }

    /**
     * Получение состояния парирования игрока
     */
    public static ParryState getParryState(UUID playerId) {
        return PARRY_STATES.get(playerId);
    }

    /**
     * Проверка, может ли игрок контратаковать
     */
    public static boolean canCounterAttack(UUID playerId) {
        ParryState state = PARRY_STATES.get(playerId);
        return state != null && state.canCounterAttack;
    }
}
