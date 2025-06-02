package com.test_project.items.weapone.feature;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;

public class LifeStealFeature implements WeaponFeature {

    private final float healAmount;
    private final float healChance;
    private final boolean onlyFromPlayers;

    public LifeStealFeature() {
        this(2.0f, 1.0f, false); // Дефолт: 2 HP, 100% шанс, от всех мобов
    }

    public LifeStealFeature(float healAmount, float healChance, boolean onlyFromPlayers) {
        this.healAmount = healAmount;
        this.healChance = healChance;
        this.onlyFromPlayers = onlyFromPlayers;
    }

    @Override
    public String getId() {
        return "lifesteal";
    }

    @Override
    public String getDisplayName() {
        return "Похищение жизни";
    }

    @Override
    public String getDescription() {
        return "Восстанавливает " + healAmount + " HP при убийстве противника";
    }

    @Override
    public void onKill(LivingEntity attacker, LivingEntity target, ItemStack stack) {
        if (!(attacker instanceof ServerPlayer player)) return;

        // Проверяем, нужно ли лечиться только от игроков
        if (onlyFromPlayers && !(target instanceof Player)) return;

        // Проверяем шанс срабатывания
        if (player.getRandom().nextFloat() > healChance) return;

        // Проверяем, не полное ли здоровье
        if (player.getHealth() >= player.getMaxHealth()) return;

        // Восстанавливаем здоровье
        float newHealth = Math.min(player.getMaxHealth(), player.getHealth() + healAmount);
        player.setHealth(newHealth);

        // Эффекты и уведомления
        playLifeStealEffects(player, target);
        sendLifeStealMessage(player, healAmount);
    }

    @Override
    public void onAttack(LivingEntity attacker, LivingEntity target, ItemStack stack) {
        // Можно добавить небольшое лечение при обычной атаке (опционально)
        if (!(attacker instanceof ServerPlayer player)) return;
        if (player.getRandom().nextFloat() > 0.1f) return; // 10% шанс при атаке

        float minorHeal = healAmount * 0.2f; // 20% от основного лечения
        if (player.getHealth() < player.getMaxHealth()) {
            player.heal(minorHeal);

            // Небольшие эффекты
            if (player.level() instanceof ServerLevel serverLevel) {
                serverLevel.sendParticles(ParticleTypes.HEART,
                        player.getX(), player.getY() + 1.5, player.getZ(),
                        2, 0.3, 0.3, 0.3, 0.1);
            }
        }
    }

    private void playLifeStealEffects(ServerPlayer player, LivingEntity target) {
        // Звуковой эффект
        player.level().playSound(null,
                player.getX(), player.getY(), player.getZ(),
                SoundEvents.PLAYER_LEVELUP,
                SoundSource.PLAYERS,
                0.5f, 1.5f);

        // Визуальные эффекты
        if (player.level() instanceof ServerLevel serverLevel) {
            // Частицы сердечек вокруг игрока
            serverLevel.sendParticles(ParticleTypes.HEART,
                    player.getX(), player.getY() + 1, player.getZ(),
                    8, 0.5, 0.5, 0.5, 0.1);

            // Красные частицы от цели к игроку (эффект высасывания жизни)
            double dx = player.getX() - target.getX();
            double dy = player.getY() + 1 - (target.getY() + 1);
            double dz = player.getZ() - target.getZ();
            double distance = Math.sqrt(dx * dx + dy * dy + dz * dz);

            int steps = (int) (distance * 2);
            for (int i = 0; i < steps; i++) {
                double progress = (double) i / steps;
                double x = target.getX() + dx * progress;
                double y = target.getY() + 1 + dy * progress;
                double z = target.getZ() + dz * progress;

                serverLevel.sendParticles(ParticleTypes.DAMAGE_INDICATOR,
                        x, y, z, 1, 0.1, 0.1, 0.1, 0);
            }
        }

        // Временный эффект регенерации
        player.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 40, 0));
    }

    private void sendLifeStealMessage(ServerPlayer player, float healedAmount) {
        Component message = Component.literal(
                "§c❤ Похищение жизни! §f+" + String.format("%.1f", healedAmount) + " HP"
        );
        player.sendSystemMessage(message);
    }

    @Override
    public int getCooldown() {
        return 20; // 1 секунда кулдаун между срабатываниями
    }

    // Геттеры для настройки
    public float getHealAmount() {
        return healAmount;
    }

    public float getHealChance() {
        return healChance;
    }

    public boolean isOnlyFromPlayers() {
        return onlyFromPlayers;
    }

    // Статические методы для создания разных вариантов
    public static LifeStealFeature createMinor() {
        return new LifeStealFeature(1.0f, 0.5f, false); // Слабое похищение жизни
    }

    public static LifeStealFeature createMajor() {
        return new LifeStealFeature(4.0f, 1.0f, false); // Сильное похищение жизни
    }

    public static LifeStealFeature createPvPOnly() {
        return new LifeStealFeature(3.0f, 1.0f, true); // Только от игроков
    }
}
