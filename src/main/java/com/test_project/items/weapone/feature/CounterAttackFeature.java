package com.test_project.items.weapone.feature;

import com.test_project.combat.PlayerCombatSettings;
import com.test_project.combat.combo.CombatEventHandler;
import com.test_project.combat.stance.StanceType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.util.RandomSource;

public class CounterAttackFeature implements WeaponFeature {

    // Параметры контратаки
    private final float counterDamageMultiplier;
    private final int counterCooldownTicks;
    private final float counterChance;
    private final boolean requiresDefenseStance;

    // Хранение кулдаунов контратак для игроков
    private static final java.util.Map<java.util.UUID, Long> counterCooldowns = new java.util.HashMap<>();

    public CounterAttackFeature() {
        this(1.5f, 100, 0.3f, true); // Дефолтные значения
    }

    public CounterAttackFeature(float damageMultiplier, int cooldownTicks, float chance, boolean requiresDefenseStance) {
        this.counterDamageMultiplier = damageMultiplier;
        this.counterCooldownTicks = cooldownTicks;
        this.counterChance = chance;
        this.requiresDefenseStance = requiresDefenseStance;
    }

    @Override
    public String getId() {
        return "counterattack";
    }

    @Override
    public String getDisplayName() {
        return "Контратака";
    }

    @Override
    public String getDescription() {
        return "Шанс контратаковать при получении урона (" + (counterChance * 100) + "%)";
    }

    @Override
    public void onAttack(LivingEntity attacker, LivingEntity target, ItemStack stack) {
        // Обычная атака - здесь можно добавить дополнительные эффекты
        if (attacker instanceof Player player) {
            PlayerCombatSettings settings = CombatEventHandler.getSettings(player);
            settings.enterCombat();
        }
    }

    @Override
    public void onCounterAttack(LivingEntity defender, LivingEntity attacker, ItemStack stack) {
        if (!(defender instanceof ServerPlayer defenderPlayer)) return;
        if (!attacker.isAlive()) return;

        // Проверяем кулдаун
        if (isOnCooldown(defenderPlayer)) {
            defenderPlayer.sendSystemMessage(Component.literal("§c⏰ Контратака на кулдауне!"));
            return;
        }

        // Проверяем стойку (если требуется)
        if (requiresDefenseStance) {
            PlayerCombatSettings settings = CombatEventHandler.getSettings(defenderPlayer);
            if (settings.getCurrentStance() != StanceType.DEFENSE) {
                return; // Контратака возможна только в защитной стойке
            }
        }

        // Проверяем шанс срабатывания
        RandomSource random = defenderPlayer.getRandom();
        if (random.nextFloat() > counterChance) {
            return; // Контратака не сработала
        }

        // Выполняем контратаку
        executeCounterAttack(defenderPlayer, attacker, stack);

        // Устанавливаем кулдаун
        setCooldown(defenderPlayer);
    }

    private void executeCounterAttack(ServerPlayer defender, LivingEntity attacker, ItemStack weaponStack) {
        try {
            // Рассчитываем урон контратаки
            float baseDamage = getWeaponBaseDamage(weaponStack);
            float counterDamage = baseDamage * counterDamageMultiplier;

            // Создаём источник урона
            DamageSource damageSource = defender.damageSources().playerAttack(defender);

            // Наносим урон
            boolean damageDealt = attacker.hurt(damageSource, counterDamage);

            if (damageDealt) {
                // Применяем дополнительные эффекты
                applyCounterEffects(defender, attacker, weaponStack);

                // Визуальные и звуковые эффекты
                playCounterAttackEffects(defender, attacker);

                // Уведомления
                sendCounterAttackMessages(defender, attacker, counterDamage);

                // Логирование
                System.out.println("Counter-attack executed: " + defender.getName().getString() +
                        " dealt " + counterDamage + " damage to " + attacker.getName().getString());
            }

        } catch (Exception e) {
            System.err.println("Error executing counter-attack: " + e.getMessage());
        }
    }

    private float getWeaponBaseDamage(ItemStack weaponStack) {
        // Получаем базовый урон оружия из атрибутов
        var attributes = weaponStack.getAttributeModifiers();
        return attributes.modifiers().values().stream()
                .filter(modifier -> modifier.attribute().equals(net.minecraft.world.entity.ai.attributes.Attributes.ATTACK_DAMAGE))
                .map(modifier -> (float) modifier.modifier().amount())
                .findFirst()
                .orElse(4.0f); // Дефолтный урон если не найден
    }

    private void applyCounterEffects(ServerPlayer defender, LivingEntity attacker, ItemStack weaponStack) {
        // Эффекты для атакующего
        attacker.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 40, 1)); // Замедление на 2 сек
        attacker.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 60, 0)); // Слабость на 3 сек

        // Эффекты для защитника
        defender.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 40, 0)); // Сопротивление урону
        defender.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 60, 0)); // Регенерация

        // Отбрасывание атакующего
        double knockbackStrength = 1.5;
        double dx = attacker.getX() - defender.getX();
        double dz = attacker.getZ() - defender.getZ();
        double distance = Math.sqrt(dx * dx + dz * dz);
        if (distance > 0) {
            attacker.push(dx / distance * knockbackStrength, 0.3, dz / distance * knockbackStrength);
        }
    }

    private void playCounterAttackEffects(ServerPlayer defender, LivingEntity attacker) {
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

        // Визуальные эффекты
        if (defender.level() instanceof ServerLevel serverLevel) {
            // Частицы вокруг защитника (успешная контратака)
            serverLevel.sendParticles(ParticleTypes.ENCHANTED_HIT,
                    defender.getX(), defender.getY() + 1, defender.getZ(),
                    12, 0.6, 0.6, 0.6, 0.1);

            // Частицы удара по атакующему
            serverLevel.sendParticles(ParticleTypes.CRIT,
                    attacker.getX(), attacker.getY() + 1, attacker.getZ(),
                    8, 0.4, 0.4, 0.4, 0.05);

            // Линия между защитником и атакующим
            createParticleLine(serverLevel, defender, attacker);
        }
    }

    private void createParticleLine(ServerLevel level, LivingEntity from, LivingEntity to) {
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

    private void sendCounterAttackMessages(ServerPlayer defender, LivingEntity attacker, float damage) {
        // Сообщение защитнику
        Component defenderMessage = Component.literal(
                "§6⚡ Контратака! §fВы нанесли §c" + String.format("%.1f", damage) +
                        " §fурона противнику " + attacker.getName().getString()
        );
        defender.sendSystemMessage(defenderMessage);

        // Сообщение атакующему (если это игрок)
        if (attacker instanceof ServerPlayer attackerPlayer) {
            Component attackerMessage = Component.literal(
                    "§c💥 Вас контратаковал §f" + defender.getName().getString() +
                            " §c(-" + String.format("%.1f", damage) + " HP)"
            );
            attackerPlayer.sendSystemMessage(attackerMessage);
        }
    }

    // Методы управления кулдауном

    private boolean isOnCooldown(Player player) {
        Long lastCounter = counterCooldowns.get(player.getUUID());
        if (lastCounter == null) return false;

        long currentTime = player.level().getGameTime();
        return (currentTime - lastCounter) < counterCooldownTicks;
    }

    private void setCooldown(Player player) {
        counterCooldowns.put(player.getUUID(), player.level().getGameTime());
    }

    public int getRemainingCooldown(Player player) {
        Long lastCounter = counterCooldowns.get(player.getUUID());
        if (lastCounter == null) return 0;

        long currentTime = player.level().getGameTime();
        long elapsed = currentTime - lastCounter;
        return Math.max(0, (int) (counterCooldownTicks - elapsed));
    }

    // Утилитарные методы

    public static void clearPlayerData(Player player) {
        counterCooldowns.remove(player.getUUID());
    }

    public float getCounterChance() {
        return counterChance;
    }

    public float getCounterDamageMultiplier() {
        return counterDamageMultiplier;
    }

    public boolean requiresDefenseStance() {
        return requiresDefenseStance;
    }

    // Методы для создания кастомных вариантов контратаки

    public static CounterAttackFeature createPowerfulCounter() {
        return new CounterAttackFeature(2.0f, 200, 0.5f, true); // Мощная контратака
    }

    public static CounterAttackFeature createQuickCounter() {
        return new CounterAttackFeature(1.2f, 60, 0.4f, false); // Быстрая контратака
    }

    public static CounterAttackFeature createMasterCounter() {
        return new CounterAttackFeature(1.8f, 80, 0.6f, true); // Мастерская контратака
    }
}
